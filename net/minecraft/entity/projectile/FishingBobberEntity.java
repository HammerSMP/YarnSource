/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class FishingBobberEntity
extends ProjectileEntity {
    private final Random velocityRandom = new Random();
    private boolean caughtFish;
    private int outOfOpenWaterTicks;
    private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CAUGHT_FISH = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int removalTimer;
    private int hookCountdown;
    private int waitCountdown;
    private int fishTravelCountdown;
    private float fishAngle;
    private boolean inOpenWater = true;
    private Entity hookedEntity;
    private State state = State.FLYING;
    private final int luckOfTheSeaLevel;
    private final int lureLevel;

    private FishingBobberEntity(World arg, PlayerEntity arg2, int i, int j) {
        super((EntityType<? extends ProjectileEntity>)EntityType.FISHING_BOBBER, arg);
        this.ignoreCameraFrustum = true;
        this.setOwner(arg2);
        arg2.fishHook = this;
        this.luckOfTheSeaLevel = Math.max(0, i);
        this.lureLevel = Math.max(0, j);
    }

    @Environment(value=EnvType.CLIENT)
    public FishingBobberEntity(World arg, PlayerEntity arg2, double d, double e, double f) {
        this(arg, arg2, 0, 0);
        this.updatePosition(d, e, f);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
    }

    public FishingBobberEntity(PlayerEntity arg, World arg2, int i, int j) {
        this(arg2, arg, i, j);
        float f = arg.pitch;
        float g = arg.yaw;
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float k = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float l = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float m = MathHelper.sin(-f * ((float)Math.PI / 180));
        double d = arg.getX() - (double)k * 0.3;
        double e = arg.getEyeY();
        double n = arg.getZ() - (double)h * 0.3;
        this.refreshPositionAndAngles(d, e, n, g, f);
        Vec3d lv = new Vec3d(-k, MathHelper.clamp(-(m / l), -5.0f, 5.0f), -h);
        double o = lv.length();
        lv = lv.multiply(0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045, 0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045);
        this.setVelocity(lv);
        this.yaw = (float)(MathHelper.atan2(lv.x, lv.z) * 57.2957763671875);
        this.pitch = (float)(MathHelper.atan2(lv.y, MathHelper.sqrt(FishingBobberEntity.squaredHorizontalLength(lv))) * 57.2957763671875);
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
        this.getDataTracker().startTracking(CAUGHT_FISH, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (HOOK_ENTITY_ID.equals(arg)) {
            int i = this.getDataTracker().get(HOOK_ENTITY_ID);
            Entity entity = this.hookedEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
        }
        if (CAUGHT_FISH.equals(arg)) {
            this.caughtFish = this.getDataTracker().get(CAUGHT_FISH);
            if (this.caughtFish) {
                this.setVelocity(this.getVelocity().x, -0.4f * MathHelper.nextFloat(this.velocityRandom, 0.6f, 1.0f), this.getVelocity().z);
            }
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = 64.0;
        return d < 4096.0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
    }

    @Override
    public void tick() {
        boolean bl;
        this.velocityRandom.setSeed(this.getUuid().getLeastSignificantBits() ^ this.world.getTime());
        super.tick();
        PlayerEntity lv = this.getOwner();
        if (lv == null) {
            this.remove();
            return;
        }
        if (!this.world.isClient && this.removeIfInvalid(lv)) {
            return;
        }
        if (this.onGround) {
            ++this.removalTimer;
            if (this.removalTimer >= 1200) {
                this.remove();
                return;
            }
        } else {
            this.removalTimer = 0;
        }
        float f = 0.0f;
        BlockPos lv2 = this.getBlockPos();
        FluidState lv3 = this.world.getFluidState(lv2);
        if (lv3.matches(FluidTags.WATER)) {
            f = lv3.getHeight(this.world, lv2);
        }
        boolean bl2 = bl = f > 0.0f;
        if (this.state == State.FLYING) {
            if (this.hookedEntity != null) {
                this.setVelocity(Vec3d.ZERO);
                this.state = State.HOOKED_IN_ENTITY;
                return;
            }
            if (bl) {
                this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
                this.state = State.BOBBING;
                return;
            }
            this.checkForCollision();
        } else {
            if (this.state == State.HOOKED_IN_ENTITY) {
                if (this.hookedEntity != null) {
                    if (this.hookedEntity.removed) {
                        this.hookedEntity = null;
                        this.state = State.FLYING;
                    } else {
                        this.updatePosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8), this.hookedEntity.getZ());
                    }
                }
                return;
            }
            if (this.state == State.BOBBING) {
                Vec3d lv4 = this.getVelocity();
                double d = this.getY() + lv4.y - (double)lv2.getY() - (double)f;
                if (Math.abs(d) < 0.01) {
                    d += Math.signum(d) * 0.1;
                }
                this.setVelocity(lv4.x * 0.9, lv4.y - d * (double)this.random.nextFloat() * 0.2, lv4.z * 0.9);
                this.inOpenWater = this.hookCountdown > 0 || this.fishTravelCountdown > 0 ? this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(lv2) : true;
                if (bl) {
                    this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
                    if (this.caughtFish) {
                        this.setVelocity(this.getVelocity().add(0.0, -0.1 * (double)this.velocityRandom.nextFloat() * (double)this.velocityRandom.nextFloat(), 0.0));
                    }
                    if (!this.world.isClient) {
                        this.tickFishingLogic(lv2);
                    }
                } else {
                    this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
                }
            }
        }
        if (!lv3.matches(FluidTags.WATER)) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.method_26962();
        if (this.state == State.FLYING && (this.onGround || this.horizontalCollision)) {
            this.setVelocity(Vec3d.ZERO);
        }
        double e = 0.92;
        this.setVelocity(this.getVelocity().multiply(0.92));
        this.refreshPosition();
    }

    private boolean removeIfInvalid(PlayerEntity arg) {
        boolean bl2;
        ItemStack lv = arg.getMainHandStack();
        ItemStack lv2 = arg.getOffHandStack();
        boolean bl = lv.getItem() == Items.FISHING_ROD;
        boolean bl3 = bl2 = lv2.getItem() == Items.FISHING_ROD;
        if (arg.removed || !arg.isAlive() || !bl && !bl2 || this.squaredDistanceTo(arg) > 1024.0) {
            this.remove();
            return true;
        }
        return false;
    }

    private void checkForCollision() {
        HitResult lv = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.COLLIDER);
        this.onCollision(lv);
    }

    @Override
    protected boolean method_26958(Entity arg) {
        return super.method_26958(arg) || arg.isAlive() && arg instanceof ItemEntity;
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        if (!this.world.isClient) {
            this.hookedEntity = arg.getEntity();
            this.updateHookedEntityId();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        super.onBlockHit(arg);
        this.setVelocity(this.getVelocity().normalize().multiply(arg.squaredDistanceTo(this)));
    }

    private void updateHookedEntityId() {
        this.getDataTracker().set(HOOK_ENTITY_ID, this.hookedEntity.getEntityId() + 1);
    }

    private void tickFishingLogic(BlockPos arg) {
        ServerWorld lv = (ServerWorld)this.world;
        int i = 1;
        BlockPos lv2 = arg.up();
        if (this.random.nextFloat() < 0.25f && this.world.hasRain(lv2)) {
            ++i;
        }
        if (this.random.nextFloat() < 0.5f && !this.world.isSkyVisible(lv2)) {
            --i;
        }
        if (this.hookCountdown > 0) {
            --this.hookCountdown;
            if (this.hookCountdown <= 0) {
                this.waitCountdown = 0;
                this.fishTravelCountdown = 0;
                this.getDataTracker().set(CAUGHT_FISH, false);
            }
        } else if (this.fishTravelCountdown > 0) {
            this.fishTravelCountdown -= i;
            if (this.fishTravelCountdown > 0) {
                double j;
                double e;
                this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0);
                float f = this.fishAngle * ((float)Math.PI / 180);
                float g = MathHelper.sin(f);
                float h = MathHelper.cos(f);
                double d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1f);
                BlockState lv3 = lv.getBlockState(new BlockPos(d, (e = (double)((float)MathHelper.floor(this.getY()) + 1.0f)) - 1.0, j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1f)));
                if (lv3.isOf(Blocks.WATER)) {
                    if (this.random.nextFloat() < 0.15f) {
                        lv.spawnParticles(ParticleTypes.BUBBLE, d, e - (double)0.1f, j, 1, g, 0.1, h, 0.0);
                    }
                    float k = g * 0.04f;
                    float l = h * 0.04f;
                    lv.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, l, 0.01, -k, 1.0);
                    lv.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, -l, 0.01, k, 1.0);
                }
            } else {
                this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
                double m = this.getY() + 0.5;
                lv.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                lv.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0f + this.getWidth() * 20.0f), this.getWidth(), 0.0, this.getWidth(), 0.2f);
                this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
                this.getDataTracker().set(CAUGHT_FISH, true);
            }
        } else if (this.waitCountdown > 0) {
            this.waitCountdown -= i;
            float n = 0.15f;
            if (this.waitCountdown < 20) {
                n = (float)((double)n + (double)(20 - this.waitCountdown) * 0.05);
            } else if (this.waitCountdown < 40) {
                n = (float)((double)n + (double)(40 - this.waitCountdown) * 0.02);
            } else if (this.waitCountdown < 60) {
                n = (float)((double)n + (double)(60 - this.waitCountdown) * 0.01);
            }
            if (this.random.nextFloat() < n) {
                double s;
                double r;
                float o = MathHelper.nextFloat(this.random, 0.0f, 360.0f) * ((float)Math.PI / 180);
                float p = MathHelper.nextFloat(this.random, 25.0f, 60.0f);
                double q = this.getX() + (double)(MathHelper.sin(o) * p * 0.1f);
                BlockState lv4 = lv.getBlockState(new BlockPos(q, (r = (double)((float)MathHelper.floor(this.getY()) + 1.0f)) - 1.0, s = this.getZ() + (double)(MathHelper.cos(o) * p * 0.1f)));
                if (lv4.isOf(Blocks.WATER)) {
                    lv.spawnParticles(ParticleTypes.SPLASH, q, r, s, 2 + this.random.nextInt(2), 0.1f, 0.0, 0.1f, 0.0);
                }
            }
            if (this.waitCountdown <= 0) {
                this.fishAngle = MathHelper.nextFloat(this.random, 0.0f, 360.0f);
                this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
            }
        } else {
            this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
            this.waitCountdown -= this.lureLevel * 20 * 5;
        }
    }

    private boolean isOpenOrWaterAround(BlockPos arg) {
        PositionType lv = PositionType.INVALID;
        for (int i = -1; i <= 2; ++i) {
            PositionType lv2 = this.getPositionType(arg.add(-2, i, -2), arg.add(2, i, 2));
            switch (lv2) {
                case INVALID: {
                    return false;
                }
                case ABOVE_WATER: {
                    if (lv != PositionType.INVALID) break;
                    return false;
                }
                case INSIDE_WATER: {
                    if (lv != PositionType.ABOVE_WATER) break;
                    return false;
                }
            }
            lv = lv2;
        }
        return true;
    }

    private PositionType getPositionType(BlockPos arg3, BlockPos arg22) {
        return BlockPos.stream(arg3, arg22).map(this::getPositionType).reduce((arg, arg2) -> arg == arg2 ? arg : PositionType.INVALID).orElse(PositionType.INVALID);
    }

    private PositionType getPositionType(BlockPos arg) {
        BlockState lv = this.world.getBlockState(arg);
        if (lv.isAir() || lv.isOf(Blocks.LILY_PAD)) {
            return PositionType.ABOVE_WATER;
        }
        FluidState lv2 = lv.getFluidState();
        if (lv2.matches(FluidTags.WATER) && lv2.isStill() && lv.getCollisionShape(this.world, arg).isEmpty()) {
            return PositionType.INSIDE_WATER;
        }
        return PositionType.INVALID;
    }

    public boolean isInOpenWater() {
        return this.inOpenWater;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
    }

    public int use(ItemStack arg) {
        PlayerEntity lv = this.getOwner();
        if (this.world.isClient || lv == null) {
            return 0;
        }
        int i = 0;
        if (this.hookedEntity != null) {
            this.pullHookedEntity();
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)lv, arg, this, Collections.emptyList());
            this.world.sendEntityStatus(this, (byte)31);
            i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
        } else if (this.hookCountdown > 0) {
            LootContext.Builder lv2 = new LootContext.Builder((ServerWorld)this.world).put(LootContextParameters.POSITION, this.getBlockPos()).put(LootContextParameters.TOOL, arg).put(LootContextParameters.THIS_ENTITY, this).setRandom(this.random).setLuck((float)this.luckOfTheSeaLevel + lv.getLuck());
            LootTable lv3 = this.world.getServer().getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
            List<ItemStack> list = lv3.getDrops(lv2.build(LootContextTypes.FISHING));
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)lv, arg, this, list);
            for (ItemStack lv4 : list) {
                ItemEntity lv5 = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), lv4);
                double d = lv.getX() - this.getX();
                double e = lv.getY() - this.getY();
                double f = lv.getZ() - this.getZ();
                double g = 0.1;
                lv5.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
                this.world.spawnEntity(lv5);
                lv.world.spawnEntity(new ExperienceOrbEntity(lv.world, lv.getX(), lv.getY() + 0.5, lv.getZ() + 0.5, this.random.nextInt(6) + 1));
                if (!lv4.getItem().isIn(ItemTags.FISHES)) continue;
                lv.increaseStat(Stats.FISH_CAUGHT, 1);
            }
            i = 1;
        }
        if (this.onGround) {
            i = 2;
        }
        this.remove();
        return i;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
            this.pullHookedEntity();
        }
        super.handleStatus(b);
    }

    protected void pullHookedEntity() {
        Entity lv = this.getOwner();
        if (lv == null) {
            return;
        }
        Vec3d lv2 = new Vec3d(lv.getX() - this.getX(), lv.getY() - this.getY(), lv.getZ() - this.getZ()).multiply(0.1);
        this.hookedEntity.setVelocity(this.hookedEntity.getVelocity().add(lv2));
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    public void remove() {
        super.remove();
        PlayerEntity lv = this.getOwner();
        if (lv != null) {
            lv.fishHook = null;
        }
    }

    @Override
    @Nullable
    public PlayerEntity getOwner() {
        Entity lv = this.getOwner();
        return lv instanceof PlayerEntity ? (PlayerEntity)lv : null;
    }

    @Nullable
    public Entity getHookedEntity() {
        return this.hookedEntity;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity lv = this.getOwner();
        return new EntitySpawnS2CPacket(this, lv == null ? this.getEntityId() : lv.getEntityId());
    }

    static enum PositionType {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;

    }

    static enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;

    }
}

