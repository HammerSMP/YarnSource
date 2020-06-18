/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class PersistentProjectileEntity
extends ProjectileEntity {
    private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int inGroundTime;
    public PickupPermission pickupType = PickupPermission.DISALLOWED;
    public int shake;
    private int life;
    private double damage = 2.0;
    private int punch;
    private SoundEvent sound = this.getHitSound();
    private IntOpenHashSet piercedEntities;
    private List<Entity> piercingKilledEntities;

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> arg, double d, double e, double f, World arg2) {
        this(arg, arg2);
        this.updatePosition(d, e, f);
    }

    protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> arg, LivingEntity arg2, World arg3) {
        this(arg, arg2.getX(), arg2.getEyeY() - (double)0.1f, arg2.getZ(), arg3);
        this.setOwner(arg2);
        if (arg2 instanceof PlayerEntity) {
            this.pickupType = PickupPermission.ALLOWED;
        }
    }

    public void setSound(SoundEvent arg) {
        this.sound = arg;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return d < (e *= 64.0 * PersistentProjectileEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(PROJECTILE_FLAGS, (byte)0);
        this.dataTracker.startTracking(PIERCE_LEVEL, (byte)0);
    }

    @Override
    public void setVelocity(double d, double e, double f, float g, float h) {
        super.setVelocity(d, e, f, g, h);
        this.life = 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.updatePosition(d, e, f);
        this.setRotation(g, h);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        super.setVelocityClient(d, e, f);
        this.life = 0;
    }

    @Override
    public void tick() {
        Vec3d lv8;
        VoxelShape lv4;
        BlockPos lv2;
        BlockState lv3;
        super.tick();
        boolean bl = this.isNoClip();
        Vec3d lv = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            float f = MathHelper.sqrt(PersistentProjectileEntity.squaredHorizontalLength(lv));
            this.yaw = (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875);
            this.pitch = (float)(MathHelper.atan2(lv.y, f) * 57.2957763671875);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
        if (!((lv3 = this.world.getBlockState(lv2 = this.getBlockPos())).isAir() || bl || (lv4 = lv3.getCollisionShape(this.world, lv2)).isEmpty())) {
            Vec3d lv5 = this.getPos();
            for (Box lv6 : lv4.getBoundingBoxes()) {
                if (!lv6.offset(lv2).contains(lv5)) continue;
                this.inGround = true;
                break;
            }
        }
        if (this.shake > 0) {
            --this.shake;
        }
        if (this.isTouchingWaterOrRain()) {
            this.extinguish();
        }
        if (this.inGround && !bl) {
            if (this.inBlockState != lv3 && this.method_26351()) {
                this.method_26352();
            } else if (!this.world.isClient) {
                this.age();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        Vec3d lv7 = this.getPos();
        HitResult lv9 = this.world.rayTrace(new RayTraceContext(lv7, lv8 = lv7.add(lv), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
        if (lv9.getType() != HitResult.Type.MISS) {
            lv8 = lv9.getPos();
        }
        while (!this.removed) {
            EntityHitResult lv10 = this.getEntityCollision(lv7, lv8);
            if (lv10 != null) {
                lv9 = lv10;
            }
            if (lv9 != null && lv9.getType() == HitResult.Type.ENTITY) {
                Entity lv11 = ((EntityHitResult)lv9).getEntity();
                Entity lv12 = this.getOwner();
                if (lv11 instanceof PlayerEntity && lv12 instanceof PlayerEntity && !((PlayerEntity)lv12).shouldDamagePlayer((PlayerEntity)lv11)) {
                    lv9 = null;
                    lv10 = null;
                }
            }
            if (lv9 != null && !bl) {
                this.onCollision(lv9);
                this.velocityDirty = true;
            }
            if (lv10 == null || this.getPierceLevel() <= 0) break;
            lv9 = null;
        }
        lv = this.getVelocity();
        double d = lv.x;
        double e = lv.y;
        double g = lv.z;
        if (this.isCritical()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(ParticleTypes.CRIT, this.getX() + d * (double)i / 4.0, this.getY() + e * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -d, -e + 0.2, -g);
            }
        }
        double h = this.getX() + d;
        double j = this.getY() + e;
        double k = this.getZ() + g;
        float l = MathHelper.sqrt(PersistentProjectileEntity.squaredHorizontalLength(lv));
        this.yaw = bl ? (float)(MathHelper.atan2(-d, -g) * 57.2957763671875) : (float)(MathHelper.atan2(d, g) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(e, l) * 57.2957763671875);
        this.pitch = PersistentProjectileEntity.updateRotation(this.prevPitch, this.pitch);
        this.yaw = PersistentProjectileEntity.updateRotation(this.prevYaw, this.yaw);
        float m = 0.99f;
        float n = 0.05f;
        if (this.isTouchingWater()) {
            for (int o = 0; o < 4; ++o) {
                float p = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, h - d * 0.25, j - e * 0.25, k - g * 0.25, d, e, g);
            }
            m = this.getDragInWater();
        }
        this.setVelocity(lv.multiply(m));
        if (!this.hasNoGravity() && !bl) {
            Vec3d lv13 = this.getVelocity();
            this.setVelocity(lv13.x, lv13.y - (double)0.05f, lv13.z);
        }
        this.updatePosition(h, j, k);
        this.checkBlockCollision();
    }

    private boolean method_26351() {
        return this.inGround && this.world.doesNotCollide(new Box(this.getPos(), this.getPos()).expand(0.06));
    }

    private void method_26352() {
        this.inGround = false;
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        this.life = 0;
    }

    @Override
    public void move(MovementType arg, Vec3d arg2) {
        super.move(arg, arg2);
        if (arg != MovementType.SELF && this.method_26351()) {
            this.method_26352();
        }
    }

    protected void age() {
        ++this.life;
        if (this.life >= 1200) {
            this.remove();
        }
    }

    private void clearPiercingStatus() {
        if (this.piercingKilledEntities != null) {
            this.piercingKilledEntities.clear();
        }
        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        DamageSource damageSourceAsProjectile;
        Entity projectileOwner;
        super.onEntityHit(entityHitResult);
        Entity hitEntity = entityHitResult.getEntity();
        float projectileSpeed = (float)this.getVelocity().length();
        int damageAmount = MathHelper.ceil(MathHelper.clamp((double)projectileSpeed * this.damage, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }
            if (this.piercingKilledEntities == null) {
                this.piercingKilledEntities = Lists.newArrayListWithCapacity((int)5);
            }
            if (this.piercedEntities.size() < this.getPierceLevel() + 1) {
                this.piercedEntities.add(hitEntity.getEntityId());
            } else {
                this.remove();
                return;
            }
        }
        if (this.isCritical()) {
            long halfDamage = this.random.nextInt(damageAmount / 2 + 2);
            damageAmount = (int)Math.min(halfDamage + (long)damageAmount, Integer.MAX_VALUE);
        }
        if ((projectileOwner = this.getOwner()) == null) {
            DamageSource lv3 = DamageSource.arrow(this, this);
        } else {
            damageSourceAsProjectile = DamageSource.arrow(this, projectileOwner);
            if (projectileOwner instanceof LivingEntity) {
                ((LivingEntity)projectileOwner).onAttacking(hitEntity);
            }
        }
        boolean hitEnderman = hitEntity.getType() == EntityType.ENDERMAN;
        int oldFireTicks = hitEntity.getFireTicks();
        if (this.isOnFire() && !hitEnderman) {
            hitEntity.setOnFireFor(5);
        }
        if (hitEntity.damage(damageSourceAsProjectile, damageAmount)) {
            if (hitEnderman) {
                return;
            }
            if (hitEntity instanceof LivingEntity) {
                Vec3d knockback = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.punch * 0.6);
                LivingEntity lv5 = (LivingEntity)hitEntity;
                if (!this.world.isClient && this.getPierceLevel() <= 0) {
                    lv5.setStuckArrowCount(lv5.getStuckArrowCount() + 1);
                }
                if (this.punch > 0 && knockback.lengthSquared() > 0.0) {
                    lv5.addVelocity(knockback.x, 0.1, knockback.z);
                }
                if (!this.world.isClient && projectileOwner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(lv5, projectileOwner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)projectileOwner, lv5);
                }
                this.onHit(lv5);
                if (projectileOwner != null && lv5 != projectileOwner && lv5 instanceof PlayerEntity && projectileOwner instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)projectileOwner).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0f));
                }
                if (!hitEntity.isAlive() && this.piercingKilledEntities != null) {
                    this.piercingKilledEntities.add(lv5);
                }
                if (!this.world.isClient && projectileOwner instanceof ServerPlayerEntity) {
                    ServerPlayerEntity lv7 = (ServerPlayerEntity)projectileOwner;
                    if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
                        Criteria.KILLED_BY_CROSSBOW.trigger(lv7, this.piercingKilledEntities);
                    } else if (!hitEntity.isAlive() && this.isShotFromCrossbow()) {
                        Criteria.KILLED_BY_CROSSBOW.trigger(lv7, Arrays.asList(hitEntity));
                    }
                }
            }
            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0) {
                this.remove();
            }
        } else {
            hitEntity.setFireTicks(oldFireTicks);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.yaw += 180.0f;
            this.prevYaw += 180.0f;
            if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.remove();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        this.inBlockState = this.world.getBlockState(arg.getBlockPos());
        super.onBlockHit(arg);
        Vec3d lv = arg.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(lv);
        Vec3d lv2 = lv.normalize().multiply(0.05f);
        this.setPos(this.getX() - lv2.x, this.getY() - lv2.y, this.getZ() - lv2.z);
        this.playSound(this.getSound(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        this.inGround = true;
        this.shake = 7;
        this.setCritical(false);
        this.setPierceLevel((byte)0);
        this.setSound(SoundEvents.ENTITY_ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.clearPiercingStatus();
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    protected final SoundEvent getSound() {
        return this.sound;
    }

    protected void onHit(LivingEntity arg) {
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d arg, Vec3d arg2) {
        return ProjectileUtil.getEntityCollision(this.world, this, arg, arg2, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::method_26958);
    }

    @Override
    protected boolean method_26958(Entity arg) {
        return super.method_26958(arg) && (this.piercedEntities == null || !this.piercedEntities.contains(arg.getEntityId()));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putShort("life", (short)this.life);
        if (this.inBlockState != null) {
            arg.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
        }
        arg.putByte("shake", (byte)this.shake);
        arg.putBoolean("inGround", this.inGround);
        arg.putByte("pickup", (byte)this.pickupType.ordinal());
        arg.putDouble("damage", this.damage);
        arg.putBoolean("crit", this.isCritical());
        arg.putByte("PierceLevel", this.getPierceLevel());
        arg.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
        arg.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.life = arg.getShort("life");
        if (arg.contains("inBlockState", 10)) {
            this.inBlockState = NbtHelper.toBlockState(arg.getCompound("inBlockState"));
        }
        this.shake = arg.getByte("shake") & 0xFF;
        this.inGround = arg.getBoolean("inGround");
        if (arg.contains("damage", 99)) {
            this.damage = arg.getDouble("damage");
        }
        if (arg.contains("pickup", 99)) {
            this.pickupType = PickupPermission.fromOrdinal(arg.getByte("pickup"));
        } else if (arg.contains("player", 99)) {
            this.pickupType = arg.getBoolean("player") ? PickupPermission.ALLOWED : PickupPermission.DISALLOWED;
        }
        this.setCritical(arg.getBoolean("crit"));
        this.setPierceLevel(arg.getByte("PierceLevel"));
        if (arg.contains("SoundEvent", 8)) {
            this.sound = Registry.SOUND_EVENT.getOrEmpty(new Identifier(arg.getString("SoundEvent"))).orElse(this.getHitSound());
        }
        this.setShotFromCrossbow(arg.getBoolean("ShotFromCrossbow"));
    }

    @Override
    public void setOwner(@Nullable Entity arg) {
        super.setOwner(arg);
        if (arg instanceof PlayerEntity) {
            this.pickupType = ((PlayerEntity)arg).abilities.creativeMode ? PickupPermission.CREATIVE_ONLY : PickupPermission.ALLOWED;
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity arg) {
        boolean bl;
        if (this.world.isClient || !this.inGround && !this.isNoClip() || this.shake > 0) {
            return;
        }
        boolean bl2 = bl = this.pickupType == PickupPermission.ALLOWED || this.pickupType == PickupPermission.CREATIVE_ONLY && arg.abilities.creativeMode || this.isNoClip() && this.getOwner().getUuid() == arg.getUuid();
        if (this.pickupType == PickupPermission.ALLOWED && !arg.inventory.insertStack(this.asItemStack())) {
            bl = false;
        }
        if (bl) {
            arg.sendPickup(this, 1);
            this.remove();
        }
    }

    protected abstract ItemStack asItemStack();

    @Override
    protected boolean canClimb() {
        return false;
    }

    public void setDamage(double d) {
        this.damage = d;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setPunch(int i) {
        this.punch = i;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.13f;
    }

    public void setCritical(boolean bl) {
        this.setProjectileFlag(1, bl);
    }

    public void setPierceLevel(byte b) {
        this.dataTracker.set(PIERCE_LEVEL, b);
    }

    private void setProjectileFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        if (bl) {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~i));
        }
    }

    public boolean isCritical() {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        return (b & 1) != 0;
    }

    public boolean isShotFromCrossbow() {
        byte b = this.dataTracker.get(PROJECTILE_FLAGS);
        return (b & 4) != 0;
    }

    public byte getPierceLevel() {
        return this.dataTracker.get(PIERCE_LEVEL);
    }

    public void applyEnchantmentEffects(LivingEntity arg, float f) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, arg);
        int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, arg);
        this.setDamage((double)(f * 2.0f) + (this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getId() * 0.11f)));
        if (i > 0) {
            this.setDamage(this.getDamage() + (double)i * 0.5 + 0.5);
        }
        if (j > 0) {
            this.setPunch(j);
        }
        if (EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, arg) > 0) {
            this.setOnFireFor(100);
        }
    }

    protected float getDragInWater() {
        return 0.6f;
    }

    public void setNoClip(boolean bl) {
        this.noClip = bl;
        this.setProjectileFlag(2, bl);
    }

    public boolean isNoClip() {
        if (!this.world.isClient) {
            return this.noClip;
        }
        return (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean bl) {
        this.setProjectileFlag(4, bl);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity lv = this.getOwner();
        return new EntitySpawnS2CPacket(this, lv == null ? 0 : lv.getEntityId());
    }

    public static enum PickupPermission {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;


        public static PickupPermission fromOrdinal(int i) {
            if (i < 0 || i > PickupPermission.values().length) {
                i = 0;
            }
            return PickupPermission.values()[i];
        }
    }
}

