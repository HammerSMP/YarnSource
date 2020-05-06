/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity
extends ProjectileEntity {
    public double posX;
    public double posY;
    public double posZ;

    protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> arg, double d, double e, double f, double g, double h, double i, World arg2) {
        this(arg, arg2);
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.refreshPosition();
        double j = MathHelper.sqrt(g * g + h * h + i * i);
        if (j != 0.0) {
            this.posX = g / j * 0.1;
            this.posY = h / j * 0.1;
            this.posZ = i / j * 0.1;
        }
    }

    public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> arg, LivingEntity arg2, double d, double e, double f, World arg3) {
        this(arg, arg2.getX(), arg2.getY(), arg2.getZ(), d, e, f, arg3);
        this.setOwner(arg2);
        this.setRotation(arg2.yaw, arg2.pitch);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }
        return d < (e *= 64.0) * e;
    }

    @Override
    public void tick() {
        HitResult lv2;
        Entity lv = this.getOwner();
        if (!this.world.isClient && (lv != null && lv.removed || !this.world.isChunkLoaded(this.getBlockPos()))) {
            this.remove();
            return;
        }
        super.tick();
        if (this.isBurning()) {
            this.setOnFireFor(1);
        }
        if ((lv2 = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.COLLIDER)).getType() != HitResult.Type.MISS) {
            this.onCollision(lv2);
        }
        Vec3d lv3 = this.getVelocity();
        double d = this.getX() + lv3.x;
        double e = this.getY() + lv3.y;
        double f = this.getZ() + lv3.z;
        ProjectileUtil.method_7484(this, 0.2f);
        float g = this.getDrag();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float h = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, d - lv3.x * 0.25, e - lv3.y * 0.25, f - lv3.z * 0.25, lv3.x, lv3.y, lv3.z);
            }
            g = 0.8f;
        }
        this.setVelocity(lv3.add(this.posX, this.posY, this.posZ).multiply(g));
        this.world.addParticle(this.getParticleType(), d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.updatePosition(d, e, f);
    }

    @Override
    protected boolean method_26958(Entity arg) {
        return super.method_26958(arg) && !arg.noClip;
    }

    protected boolean isBurning() {
        return true;
    }

    protected ParticleEffect getParticleType() {
        return ParticleTypes.SMOKE;
    }

    protected float getDrag() {
        return 0.95f;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.put("power", this.toListTag(this.posX, this.posY, this.posZ));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        ListTag lv;
        super.readCustomDataFromTag(arg);
        if (arg.contains("power", 9) && (lv = arg.getList("power", 6)).size() == 3) {
            this.posX = lv.getDouble(0);
            this.posY = lv.getDouble(1);
            this.posZ = lv.getDouble(2);
        }
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public float getTargetingMargin() {
        return 1.0f;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        Entity lv = arg.getAttacker();
        if (lv != null) {
            Vec3d lv2 = lv.getRotationVector();
            this.setVelocity(lv2);
            this.posX = lv2.x * 0.1;
            this.posY = lv2.y * 0.1;
            this.posZ = lv2.z * 0.1;
            this.setOwner(lv);
            return true;
        }
        return false;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity lv = this.getOwner();
        int i = lv == null ? 0 : lv.getEntityId();
        return new EntitySpawnS2CPacket(this.getEntityId(), this.getUuid(), this.getX(), this.getY(), this.getZ(), this.pitch, this.yaw, this.getType(), i, new Vec3d(this.posX, this.posY, this.posZ));
    }
}

