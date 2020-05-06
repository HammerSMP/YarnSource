/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.entity.projectile;

import java.util.List;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=FlyingItemEntity.class)})
public class FireworkRocketEntity
extends ProjectileEntity
implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<OptionalInt> SHOOTER_ENTITY_ID = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.FIREWORK_DATA);
    private static final TrackedData<Boolean> SHOT_AT_ANGLE = DataTracker.registerData(FireworkRocketEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int life;
    private int lifeTime;
    private LivingEntity shooter;

    public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    public FireworkRocketEntity(World arg, double d, double e, double f, ItemStack arg2) {
        super((EntityType<? extends ProjectileEntity>)EntityType.FIREWORK_ROCKET, arg);
        this.life = 0;
        this.updatePosition(d, e, f);
        int i = 1;
        if (!arg2.isEmpty() && arg2.hasTag()) {
            this.dataTracker.set(ITEM, arg2.copy());
            i += arg2.getOrCreateSubTag("Fireworks").getByte("Flight");
        }
        this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
        this.lifeTime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public FireworkRocketEntity(World arg, Entity arg2, double d, double e, double f, ItemStack arg3) {
        this(arg, d, e, f, arg3);
        this.setOwner(arg2);
    }

    public FireworkRocketEntity(World arg, ItemStack arg2, LivingEntity arg3) {
        this(arg, arg3, arg3.getX(), arg3.getY(), arg3.getZ(), arg2);
        this.dataTracker.set(SHOOTER_ENTITY_ID, OptionalInt.of(arg3.getEntityId()));
        this.shooter = arg3;
    }

    public FireworkRocketEntity(World arg, ItemStack arg2, double d, double e, double f, boolean bl) {
        this(arg, d, e, f, arg2);
        this.dataTracker.set(SHOT_AT_ANGLE, bl);
    }

    public FireworkRocketEntity(World arg, ItemStack arg2, Entity arg3, double d, double e, double f, boolean bl) {
        this(arg, arg2, d, e, f, bl);
        this.setOwner(arg3);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ITEM, ItemStack.EMPTY);
        this.dataTracker.startTracking(SHOOTER_ENTITY_ID, OptionalInt.empty());
        this.dataTracker.startTracking(SHOT_AT_ANGLE, false);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        return d < 4096.0 && !this.wasShotByEntity();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d, double e, double f) {
        return super.shouldRender(d, e, f) && !this.wasShotByEntity();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.wasShotByEntity()) {
            if (this.shooter == null) {
                this.dataTracker.get(SHOOTER_ENTITY_ID).ifPresent(i -> {
                    Entity lv = this.world.getEntityById(i);
                    if (lv instanceof LivingEntity) {
                        this.shooter = (LivingEntity)lv;
                    }
                });
            }
            if (this.shooter != null) {
                if (this.shooter.isFallFlying()) {
                    Vec3d lv = this.shooter.getRotationVector();
                    double d = 1.5;
                    double e = 0.1;
                    Vec3d lv2 = this.shooter.getVelocity();
                    this.shooter.setVelocity(lv2.add(lv.x * 0.1 + (lv.x * 1.5 - lv2.x) * 0.5, lv.y * 0.1 + (lv.y * 1.5 - lv2.y) * 0.5, lv.z * 0.1 + (lv.z * 1.5 - lv2.z) * 0.5));
                }
                this.updatePosition(this.shooter.getX(), this.shooter.getY(), this.shooter.getZ());
                this.setVelocity(this.shooter.getVelocity());
            }
        } else {
            if (!this.wasShotAtAngle()) {
                this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
            }
            Vec3d lv3 = this.getVelocity();
            this.move(MovementType.SELF, lv3);
            this.setVelocity(lv3);
        }
        HitResult lv4 = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.COLLIDER);
        if (!this.noClip) {
            this.onCollision(lv4);
            this.velocityDirty = true;
        }
        this.method_26962();
        if (this.life == 0 && !this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0f, 1.0f);
        }
        ++this.life;
        if (this.world.isClient && this.life % 2 < 2) {
            this.world.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY() - 0.3, this.getZ(), this.random.nextGaussian() * 0.05, -this.getVelocity().y * 0.5, this.random.nextGaussian() * 0.05);
        }
        if (!this.world.isClient && this.life > this.lifeTime) {
            this.explodeAndRemove();
        }
    }

    private void explodeAndRemove() {
        this.world.sendEntityStatus(this, (byte)17);
        this.explode();
        this.remove();
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        if (this.world.isClient) {
            return;
        }
        this.explodeAndRemove();
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        BlockPos lv = new BlockPos(arg.getBlockPos());
        this.world.getBlockState(lv).onEntityCollision(this.world, lv, this);
        if (!this.world.isClient() && this.hasExplosionEffects()) {
            this.explodeAndRemove();
        }
        super.onBlockHit(arg);
    }

    private boolean hasExplosionEffects() {
        ItemStack lv = this.dataTracker.get(ITEM);
        CompoundTag lv2 = lv.isEmpty() ? null : lv.getSubTag("Fireworks");
        ListTag lv3 = lv2 != null ? lv2.getList("Explosions", 10) : null;
        return lv3 != null && !lv3.isEmpty();
    }

    private void explode() {
        ListTag lv3;
        float f = 0.0f;
        ItemStack lv = this.dataTracker.get(ITEM);
        CompoundTag lv2 = lv.isEmpty() ? null : lv.getSubTag("Fireworks");
        ListTag listTag = lv3 = lv2 != null ? lv2.getList("Explosions", 10) : null;
        if (lv3 != null && !lv3.isEmpty()) {
            f = 5.0f + (float)(lv3.size() * 2);
        }
        if (f > 0.0f) {
            if (this.shooter != null) {
                this.shooter.damage(DamageSource.firework(this, this.getOwner()), 5.0f + (float)(lv3.size() * 2));
            }
            double d = 5.0;
            Vec3d lv4 = this.getPos();
            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5.0));
            for (LivingEntity lv5 : list) {
                if (lv5 == this.shooter || this.squaredDistanceTo(lv5) > 25.0) continue;
                boolean bl = false;
                for (int i = 0; i < 2; ++i) {
                    Vec3d lv6 = new Vec3d(lv5.getX(), lv5.getBodyY(0.5 * (double)i), lv5.getZ());
                    BlockHitResult lv7 = this.world.rayTrace(new RayTraceContext(lv4, lv6, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
                    if (((HitResult)lv7).getType() != HitResult.Type.MISS) continue;
                    bl = true;
                    break;
                }
                if (!bl) continue;
                float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(lv5)) / 5.0);
                lv5.damage(DamageSource.firework(this, this.getOwner()), g);
            }
        }
    }

    private boolean wasShotByEntity() {
        return this.dataTracker.get(SHOOTER_ENTITY_ID).isPresent();
    }

    public boolean wasShotAtAngle() {
        return this.dataTracker.get(SHOT_AT_ANGLE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 17 && this.world.isClient) {
            if (!this.hasExplosionEffects()) {
                for (int i = 0; i < this.random.nextInt(3) + 2; ++i) {
                    this.world.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
                }
            } else {
                ItemStack lv = this.dataTracker.get(ITEM);
                CompoundTag lv2 = lv.isEmpty() ? null : lv.getSubTag("Fireworks");
                Vec3d lv3 = this.getVelocity();
                this.world.addFireworkParticle(this.getX(), this.getY(), this.getZ(), lv3.x, lv3.y, lv3.z, lv2);
            }
        }
        super.handleStatus(b);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Life", this.life);
        arg.putInt("LifeTime", this.lifeTime);
        ItemStack lv = this.dataTracker.get(ITEM);
        if (!lv.isEmpty()) {
            arg.put("FireworksItem", lv.toTag(new CompoundTag()));
        }
        arg.putBoolean("ShotAtAngle", this.dataTracker.get(SHOT_AT_ANGLE));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.life = arg.getInt("Life");
        this.lifeTime = arg.getInt("LifeTime");
        ItemStack lv = ItemStack.fromTag(arg.getCompound("FireworksItem"));
        if (!lv.isEmpty()) {
            this.dataTracker.set(ITEM, lv);
        }
        if (arg.contains("ShotAtAngle")) {
            this.dataTracker.set(SHOT_AT_ANGLE, arg.getBoolean("ShotAtAngle"));
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getStack() {
        ItemStack lv = this.dataTracker.get(ITEM);
        return lv.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : lv;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

