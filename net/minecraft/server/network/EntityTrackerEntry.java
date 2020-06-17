/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerWorld world;
    private final Entity entity;
    private final int tickInterval;
    private final boolean alwaysUpdateVelocity;
    private final Consumer<Packet<?>> receiver;
    private long lastX;
    private long lastY;
    private long lastZ;
    private int lastYaw;
    private int lastPitch;
    private int lastHeadPitch;
    private Vec3d velocity = Vec3d.ZERO;
    private int trackingTick;
    private int updatesWithoutVehicle;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean hadVehicle;
    private boolean lastOnGround;

    public EntityTrackerEntry(ServerWorld arg, Entity arg2, int i, boolean bl, Consumer<Packet<?>> consumer) {
        this.world = arg;
        this.receiver = consumer;
        this.entity = arg2;
        this.tickInterval = i;
        this.alwaysUpdateVelocity = bl;
        this.storeEncodedCoordinates();
        this.lastYaw = MathHelper.floor(arg2.yaw * 256.0f / 360.0f);
        this.lastPitch = MathHelper.floor(arg2.pitch * 256.0f / 360.0f);
        this.lastHeadPitch = MathHelper.floor(arg2.getHeadYaw() * 256.0f / 360.0f);
        this.lastOnGround = arg2.isOnGround();
    }

    public void tick() {
        List<Entity> list = this.entity.getPassengerList();
        if (!list.equals(this.lastPassengers)) {
            this.lastPassengers = list;
            this.receiver.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity instanceof ItemFrameEntity && this.trackingTick % 10 == 0) {
            ItemFrameEntity lv = (ItemFrameEntity)this.entity;
            ItemStack lv2 = lv.getHeldItemStack();
            if (lv2.getItem() instanceof FilledMapItem) {
                MapState lv3 = FilledMapItem.getOrCreateMapState(lv2, this.world);
                for (ServerPlayerEntity lv4 : this.world.getPlayers()) {
                    lv3.update(lv4, lv2);
                    Packet<?> lv5 = ((FilledMapItem)lv2.getItem()).createSyncPacket(lv2, this.world, lv4);
                    if (lv5 == null) continue;
                    lv4.networkHandler.sendPacket(lv5);
                }
            }
            this.syncEntityData();
        }
        if (this.trackingTick % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
            if (this.entity.hasVehicle()) {
                boolean bl;
                int i = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                int j = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                boolean bl2 = bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
                if (bl) {
                    this.receiver.accept(new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)i, (byte)j, this.entity.isOnGround()));
                    this.lastYaw = i;
                    this.lastPitch = j;
                }
                this.storeEncodedCoordinates();
                this.syncEntityData();
                this.hadVehicle = true;
            } else {
                Vec3d lv8;
                double d;
                boolean bl4;
                ++this.updatesWithoutVehicle;
                int k = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                int l = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                Vec3d lv6 = this.entity.getPos().subtract(EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ));
                boolean bl2 = lv6.lengthSquared() >= 7.62939453125E-6;
                Packet<ClientPlayPacketListener> lv7 = null;
                boolean bl3 = bl2 || this.trackingTick % 60 == 0;
                boolean bl = bl4 = Math.abs(k - this.lastYaw) >= 1 || Math.abs(l - this.lastPitch) >= 1;
                if (this.trackingTick > 0 || this.entity instanceof PersistentProjectileEntity) {
                    boolean bl5;
                    long m = EntityS2CPacket.encodePacketCoordinate(lv6.x);
                    long n = EntityS2CPacket.encodePacketCoordinate(lv6.y);
                    long o = EntityS2CPacket.encodePacketCoordinate(lv6.z);
                    boolean bl6 = bl5 = m < -32768L || m > 32767L || n < -32768L || n > 32767L || o < -32768L || o > 32767L;
                    if (bl5 || this.updatesWithoutVehicle > 400 || this.hadVehicle || this.lastOnGround != this.entity.isOnGround()) {
                        this.lastOnGround = this.entity.isOnGround();
                        this.updatesWithoutVehicle = 0;
                        lv7 = new EntityPositionS2CPacket(this.entity);
                    } else if (bl3 && bl4 || this.entity instanceof PersistentProjectileEntity) {
                        lv7 = new EntityS2CPacket.RotateAndMoveRelative(this.entity.getEntityId(), (short)m, (short)n, (short)o, (byte)k, (byte)l, this.entity.isOnGround());
                    } else if (bl3) {
                        lv7 = new EntityS2CPacket.MoveRelative(this.entity.getEntityId(), (short)m, (short)n, (short)o, this.entity.isOnGround());
                    } else if (bl4) {
                        lv7 = new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)k, (byte)l, this.entity.isOnGround());
                    }
                }
                if ((this.alwaysUpdateVelocity || this.entity.velocityDirty || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.trackingTick > 0 && ((d = (lv8 = this.entity.getVelocity()).squaredDistanceTo(this.velocity)) > 1.0E-7 || d > 0.0 && lv8.lengthSquared() == 0.0)) {
                    this.velocity = lv8;
                    this.receiver.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.velocity));
                }
                if (lv7 != null) {
                    this.receiver.accept(lv7);
                }
                this.syncEntityData();
                if (bl3) {
                    this.storeEncodedCoordinates();
                }
                if (bl4) {
                    this.lastYaw = k;
                    this.lastPitch = l;
                }
                this.hadVehicle = false;
            }
            int p = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
            if (Math.abs(p - this.lastHeadPitch) >= 1) {
                this.receiver.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)p));
                this.lastHeadPitch = p;
            }
            this.entity.velocityDirty = false;
        }
        ++this.trackingTick;
        if (this.entity.velocityModified) {
            this.sendSyncPacket(new EntityVelocityUpdateS2CPacket(this.entity));
            this.entity.velocityModified = false;
        }
    }

    public void stopTracking(ServerPlayerEntity arg) {
        this.entity.onStoppedTrackingBy(arg);
        arg.onStoppedTracking(this.entity);
    }

    public void startTracking(ServerPlayerEntity arg) {
        this.sendPackets(arg.networkHandler::sendPacket);
        this.entity.onStartedTrackingBy(arg);
        arg.onStartedTracking(this.entity);
    }

    public void sendPackets(Consumer<Packet<?>> consumer) {
        MobEntity lv6;
        if (this.entity.removed) {
            LOGGER.warn("Fetching packet for removed entity " + this.entity);
        }
        Packet<?> lv = this.entity.createSpawnPacket();
        this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
        consumer.accept(lv);
        if (!this.entity.getDataTracker().isEmpty()) {
            consumer.accept(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
        }
        boolean bl = this.alwaysUpdateVelocity;
        if (this.entity instanceof LivingEntity) {
            Collection<EntityAttributeInstance> collection = ((LivingEntity)this.entity).getAttributes().getAttributesToSend();
            if (!collection.isEmpty()) {
                consumer.accept(new EntityAttributesS2CPacket(this.entity.getEntityId(), collection));
            }
            if (((LivingEntity)this.entity).isFallFlying()) {
                bl = true;
            }
        }
        this.velocity = this.entity.getVelocity();
        if (bl && !(lv instanceof MobSpawnS2CPacket)) {
            consumer.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.velocity));
        }
        if (this.entity instanceof LivingEntity) {
            ArrayList list = Lists.newArrayList();
            for (EquipmentSlot lv2 : EquipmentSlot.values()) {
                ItemStack lv3 = ((LivingEntity)this.entity).getEquippedStack(lv2);
                if (lv3.isEmpty()) continue;
                list.add(Pair.of((Object)((Object)lv2), (Object)lv3.copy()));
            }
            if (!list.isEmpty()) {
                consumer.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getEntityId(), list));
            }
        }
        if (this.entity instanceof LivingEntity) {
            LivingEntity lv4 = (LivingEntity)this.entity;
            for (StatusEffectInstance lv5 : lv4.getStatusEffects()) {
                consumer.accept(new EntityStatusEffectS2CPacket(this.entity.getEntityId(), lv5));
            }
        }
        if (!this.entity.getPassengerList().isEmpty()) {
            consumer.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity.hasVehicle()) {
            consumer.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
        }
        if (this.entity instanceof MobEntity && (lv6 = (MobEntity)this.entity).isLeashed()) {
            consumer.accept(new EntityAttachS2CPacket(lv6, lv6.getHoldingEntity()));
        }
    }

    private void syncEntityData() {
        DataTracker lv = this.entity.getDataTracker();
        if (lv.isDirty()) {
            this.sendSyncPacket(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), lv, false));
        }
        if (this.entity instanceof LivingEntity) {
            Set<EntityAttributeInstance> set = ((LivingEntity)this.entity).getAttributes().getTracked();
            if (!set.isEmpty()) {
                this.sendSyncPacket(new EntityAttributesS2CPacket(this.entity.getEntityId(), set));
            }
            set.clear();
        }
    }

    private void storeEncodedCoordinates() {
        this.lastX = EntityS2CPacket.encodePacketCoordinate(this.entity.getX());
        this.lastY = EntityS2CPacket.encodePacketCoordinate(this.entity.getY());
        this.lastZ = EntityS2CPacket.encodePacketCoordinate(this.entity.getZ());
    }

    public Vec3d getLastPos() {
        return EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ);
    }

    private void sendSyncPacket(Packet<?> arg) {
        this.receiver.accept(arg);
        if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(arg);
        }
    }
}

