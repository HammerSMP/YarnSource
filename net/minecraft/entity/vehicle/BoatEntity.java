/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Dismounting;
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
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BoatEntity
extends Entity {
    private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> BOAT_TYPE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LEFT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> RIGHT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BUBBLE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final float[] paddlePhases = new float[2];
    private float velocityDecay;
    private float ticksUnderwater;
    private float yawVelocity;
    private int field_7708;
    private double x;
    private double y;
    private double z;
    private double boatYaw;
    private double boatPitch;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private double waterLevel;
    private float field_7714;
    private Location location;
    private Location lastLocation;
    private double fallVelocity;
    private boolean onBubbleColumnSurface;
    private boolean bubbleColumnIsDrag;
    private float bubbleWobbleStrength;
    private float bubbleWobble;
    private float lastBubbleWobble;

    public BoatEntity(EntityType<? extends BoatEntity> arg, World arg2) {
        super(arg, arg2);
        this.inanimate = true;
    }

    public BoatEntity(World world, double x, double y, double z) {
        this((EntityType<? extends BoatEntity>)EntityType.BOAT, world);
        this.updatePosition(x, y, z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height;
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(0.0f));
        this.dataTracker.startTracking(BOAT_TYPE, Type.OAK.ordinal());
        this.dataTracker.startTracking(LEFT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(RIGHT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(BUBBLE_WOBBLE_TICKS, 0);
    }

    @Override
    @Nullable
    public Box getHardCollisionBox(Entity collidingEntity) {
        if (collidingEntity.isPushable()) {
            return collidingEntity.getBoundingBox();
        }
        return null;
    }

    @Override
    @Nullable
    public Box getCollisionBox() {
        return this.getBoundingBox();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public double getMountedHeightOffset() {
        return -0.1;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.world.isClient || this.removed) {
            return true;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        this.scheduleVelocityUpdate();
        boolean bl2 = bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
        if (bl || this.getDamageWobbleStrength() > 40.0f) {
            if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                this.dropItem(this.asItem());
            }
            this.remove();
        }
        return true;
    }

    @Override
    public void onBubbleColumnSurfaceCollision(boolean drag) {
        if (!this.world.isClient) {
            this.onBubbleColumnSurface = true;
            this.bubbleColumnIsDrag = drag;
            if (this.getBubbleWobbleTicks() == 0) {
                this.setBubbleWobbleTicks(60);
            }
        }
        this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof BoatEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    public Item asItem() {
        switch (this.getBoatType()) {
            default: {
                return Items.OAK_BOAT;
            }
            case SPRUCE: {
                return Items.SPRUCE_BOAT;
            }
            case BIRCH: {
                return Items.BIRCH_BOAT;
            }
            case JUNGLE: {
                return Items.JUNGLE_BOAT;
            }
            case ACACIA: {
                return Items.ACACIA_BOAT;
            }
            case DARK_OAK: 
        }
        return Items.DARK_OAK_BOAT;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0f);
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.boatYaw = yaw;
        this.boatPitch = pitch;
        this.field_7708 = 10;
    }

    @Override
    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        this.ticksUnderwater = this.location == Location.UNDER_WATER || this.location == Location.UNDER_FLOWING_WATER ? (this.ticksUnderwater += 1.0f) : 0.0f;
        if (!this.world.isClient && this.ticksUnderwater >= 60.0f) {
            this.removeAllPassengers();
        }
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }
        super.tick();
        this.method_7555();
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.getPassengerList().isEmpty() || !(this.getPassengerList().get(0) instanceof PlayerEntity)) {
                this.setPaddleMovings(false, false);
            }
            this.updateVelocity();
            if (this.world.isClient) {
                this.updatePaddles();
                this.world.sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
            }
            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.handleBubbleColumn();
        for (int i = 0; i <= 1; ++i) {
            if (this.isPaddleMoving(i)) {
                SoundEvent lv;
                if (!this.isSilent() && (double)(this.paddlePhases[i] % ((float)Math.PI * 2)) <= 0.7853981852531433 && ((double)this.paddlePhases[i] + (double)0.3926991f) % 6.2831854820251465 >= 0.7853981852531433 && (lv = this.getPaddleSoundEvent()) != null) {
                    Vec3d lv2 = this.getRotationVec(1.0f);
                    double d = i == 1 ? -lv2.z : lv2.z;
                    double e = i == 1 ? lv2.x : -lv2.x;
                    this.world.playSound(null, this.getX() + d, this.getY(), this.getZ() + e, lv, this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                }
                int n = i;
                this.paddlePhases[n] = (float)((double)this.paddlePhases[n] + (double)0.3926991f);
                continue;
            }
            this.paddlePhases[i] = 0.0f;
        }
        this.checkBlockCollision();
        List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (int j = 0; j < list.size(); ++j) {
                Entity lv3 = list.get(j);
                if (lv3.hasPassenger(this)) continue;
                if (bl && this.getPassengerList().size() < 2 && !lv3.hasVehicle() && lv3.getWidth() < this.getWidth() && lv3 instanceof LivingEntity && !(lv3 instanceof WaterCreatureEntity) && !(lv3 instanceof PlayerEntity)) {
                    lv3.startRiding(this);
                    continue;
                }
                this.pushAwayFrom(lv3);
            }
        }
    }

    private void handleBubbleColumn() {
        if (this.world.isClient) {
            int i = this.getBubbleWobbleTicks();
            this.bubbleWobbleStrength = i > 0 ? (this.bubbleWobbleStrength += 0.05f) : (this.bubbleWobbleStrength -= 0.1f);
            this.bubbleWobbleStrength = MathHelper.clamp(this.bubbleWobbleStrength, 0.0f, 1.0f);
            this.lastBubbleWobble = this.bubbleWobble;
            this.bubbleWobble = 10.0f * (float)Math.sin(0.5f * (float)this.world.getTime()) * this.bubbleWobbleStrength;
        } else {
            int j;
            if (!this.onBubbleColumnSurface) {
                this.setBubbleWobbleTicks(0);
            }
            if ((j = this.getBubbleWobbleTicks()) > 0) {
                this.setBubbleWobbleTicks(--j);
                int k = 60 - j - 1;
                if (k > 0 && j == 0) {
                    this.setBubbleWobbleTicks(0);
                    Vec3d lv = this.getVelocity();
                    if (this.bubbleColumnIsDrag) {
                        this.setVelocity(lv.add(0.0, -0.7, 0.0));
                        this.removeAllPassengers();
                    } else {
                        this.setVelocity(lv.x, this.hasPassengerType(PlayerEntity.class) ? 2.7 : 0.6, lv.z);
                    }
                }
                this.onBubbleColumnSurface = false;
            }
        }
    }

    @Nullable
    protected SoundEvent getPaddleSoundEvent() {
        switch (this.checkLocation()) {
            case IN_WATER: 
            case UNDER_WATER: 
            case UNDER_FLOWING_WATER: {
                return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
            }
            case ON_LAND: {
                return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
            }
        }
        return null;
    }

    private void method_7555() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.field_7708 = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.field_7708 <= 0) {
            return;
        }
        double d = this.getX() + (this.x - this.getX()) / (double)this.field_7708;
        double e = this.getY() + (this.y - this.getY()) / (double)this.field_7708;
        double f = this.getZ() + (this.z - this.getZ()) / (double)this.field_7708;
        double g = MathHelper.wrapDegrees(this.boatYaw - (double)this.yaw);
        this.yaw = (float)((double)this.yaw + g / (double)this.field_7708);
        this.pitch = (float)((double)this.pitch + (this.boatPitch - (double)this.pitch) / (double)this.field_7708);
        --this.field_7708;
        this.updatePosition(d, e, f);
        this.setRotation(this.yaw, this.pitch);
    }

    public void setPaddleMovings(boolean leftMoving, boolean rightMoving) {
        this.dataTracker.set(LEFT_PADDLE_MOVING, leftMoving);
        this.dataTracker.set(RIGHT_PADDLE_MOVING, rightMoving);
    }

    @Environment(value=EnvType.CLIENT)
    public float interpolatePaddlePhase(int paddle, float tickDelta) {
        if (this.isPaddleMoving(paddle)) {
            return (float)MathHelper.clampedLerp((double)this.paddlePhases[paddle] - (double)0.3926991f, this.paddlePhases[paddle], tickDelta);
        }
        return 0.0f;
    }

    private Location checkLocation() {
        Location lv = this.getUnderWaterLocation();
        if (lv != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return lv;
        }
        if (this.checkBoatInWater()) {
            return Location.IN_WATER;
        }
        float f = this.method_7548();
        if (f > 0.0f) {
            this.field_7714 = f;
            return Location.ON_LAND;
        }
        return Location.IN_AIR;
    }

    public float method_7544() {
        Box lv = this.getBoundingBox();
        int i = MathHelper.floor(lv.minX);
        int j = MathHelper.ceil(lv.maxX);
        int k = MathHelper.floor(lv.maxY);
        int l = MathHelper.ceil(lv.maxY - this.fallVelocity);
        int m = MathHelper.floor(lv.minZ);
        int n = MathHelper.ceil(lv.maxZ);
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        block0: for (int o = k; o < l; ++o) {
            float f = 0.0f;
            for (int p = i; p < j; ++p) {
                for (int q = m; q < n; ++q) {
                    lv2.set(p, o, q);
                    FluidState lv3 = this.world.getFluidState(lv2);
                    if (lv3.isIn(FluidTags.WATER)) {
                        f = Math.max(f, lv3.getHeight(this.world, lv2));
                    }
                    if (f >= 1.0f) continue block0;
                }
            }
            if (!(f < 1.0f)) continue;
            return (float)lv2.getY() + f;
        }
        return l + 1;
    }

    public float method_7548() {
        Box lv = this.getBoundingBox();
        Box lv2 = new Box(lv.minX, lv.minY - 0.001, lv.minZ, lv.maxX, lv.minY, lv.maxZ);
        int i = MathHelper.floor(lv2.minX) - 1;
        int j = MathHelper.ceil(lv2.maxX) + 1;
        int k = MathHelper.floor(lv2.minY) - 1;
        int l = MathHelper.ceil(lv2.maxY) + 1;
        int m = MathHelper.floor(lv2.minZ) - 1;
        int n = MathHelper.ceil(lv2.maxZ) + 1;
        VoxelShape lv3 = VoxelShapes.cuboid(lv2);
        float f = 0.0f;
        int o = 0;
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p == i || p == j - 1 ? 1 : 0) + (q == m || q == n - 1 ? 1 : 0);
                if (r == 2) continue;
                for (int s = k; s < l; ++s) {
                    if (r > 0 && (s == k || s == l - 1)) continue;
                    lv4.set(p, s, q);
                    BlockState lv5 = this.world.getBlockState(lv4);
                    if (lv5.getBlock() instanceof LilyPadBlock || !VoxelShapes.matchesAnywhere(lv5.getCollisionShape(this.world, lv4).offset(p, s, q), lv3, BooleanBiFunction.AND)) continue;
                    f += lv5.getBlock().getSlipperiness();
                    ++o;
                }
            }
        }
        return f / (float)o;
    }

    private boolean checkBoatInWater() {
        Box lv = this.getBoundingBox();
        int i = MathHelper.floor(lv.minX);
        int j = MathHelper.ceil(lv.maxX);
        int k = MathHelper.floor(lv.minY);
        int l = MathHelper.ceil(lv.minY + 0.001);
        int m = MathHelper.floor(lv.minZ);
        int n = MathHelper.ceil(lv.maxZ);
        boolean bl = false;
        this.waterLevel = Double.MIN_VALUE;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    lv2.set(o, p, q);
                    FluidState lv3 = this.world.getFluidState(lv2);
                    if (!lv3.isIn(FluidTags.WATER)) continue;
                    float f = (float)p + lv3.getHeight(this.world, lv2);
                    this.waterLevel = Math.max((double)f, this.waterLevel);
                    bl |= lv.minY < (double)f;
                }
            }
        }
        return bl;
    }

    @Nullable
    private Location getUnderWaterLocation() {
        Box lv = this.getBoundingBox();
        double d = lv.maxY + 0.001;
        int i = MathHelper.floor(lv.minX);
        int j = MathHelper.ceil(lv.maxX);
        int k = MathHelper.floor(lv.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(lv.minZ);
        int n = MathHelper.ceil(lv.maxZ);
        boolean bl = false;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    lv2.set(o, p, q);
                    FluidState lv3 = this.world.getFluidState(lv2);
                    if (!lv3.isIn(FluidTags.WATER) || !(d < (double)((float)lv2.getY() + lv3.getHeight(this.world, lv2)))) continue;
                    if (lv3.isStill()) {
                        bl = true;
                        continue;
                    }
                    return Location.UNDER_FLOWING_WATER;
                }
            }
        }
        return bl ? Location.UNDER_WATER : null;
    }

    private void updateVelocity() {
        double d = -0.04f;
        double e = this.hasNoGravity() ? 0.0 : (double)-0.04f;
        double f = 0.0;
        this.velocityDecay = 0.05f;
        if (this.lastLocation == Location.IN_AIR && this.location != Location.IN_AIR && this.location != Location.ON_LAND) {
            this.waterLevel = this.getBodyY(1.0);
            this.updatePosition(this.getX(), (double)(this.method_7544() - this.getHeight()) + 0.101, this.getZ());
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallVelocity = 0.0;
            this.location = Location.IN_WATER;
        } else {
            if (this.location == Location.IN_WATER) {
                f = (this.waterLevel - this.getY()) / (double)this.getHeight();
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.UNDER_FLOWING_WATER) {
                e = -7.0E-4;
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.UNDER_WATER) {
                f = 0.01f;
                this.velocityDecay = 0.45f;
            } else if (this.location == Location.IN_AIR) {
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.ON_LAND) {
                this.velocityDecay = this.field_7714;
                if (this.getPrimaryPassenger() instanceof PlayerEntity) {
                    this.field_7714 /= 2.0f;
                }
            }
            Vec3d lv = this.getVelocity();
            this.setVelocity(lv.x * (double)this.velocityDecay, lv.y + e, lv.z * (double)this.velocityDecay);
            this.yawVelocity *= this.velocityDecay;
            if (f > 0.0) {
                Vec3d lv2 = this.getVelocity();
                this.setVelocity(lv2.x, (lv2.y + f * 0.06153846016296973) * 0.75, lv2.z);
            }
        }
    }

    private void updatePaddles() {
        if (!this.hasPassengers()) {
            return;
        }
        float f = 0.0f;
        if (this.pressingLeft) {
            this.yawVelocity -= 1.0f;
        }
        if (this.pressingRight) {
            this.yawVelocity += 1.0f;
        }
        if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
            f += 0.005f;
        }
        this.yaw += this.yawVelocity;
        if (this.pressingForward) {
            f += 0.04f;
        }
        if (this.pressingBack) {
            f -= 0.005f;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.yaw * ((float)Math.PI / 180)) * f, 0.0, MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * f));
        this.setPaddleMovings(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float f = 0.0f;
        float g = (float)((this.removed ? (double)0.01f : this.getMountedHeightOffset()) + passenger.getHeightOffset());
        if (this.getPassengerList().size() > 1) {
            int i = this.getPassengerList().indexOf(passenger);
            f = i == 0 ? 0.2f : -0.6f;
            if (passenger instanceof AnimalEntity) {
                f = (float)((double)f + 0.2);
            }
        }
        Vec3d lv = new Vec3d(f, 0.0, 0.0).rotateY(-this.yaw * ((float)Math.PI / 180) - 1.5707964f);
        passenger.updatePosition(this.getX() + lv.x, this.getY() + (double)g, this.getZ() + lv.z);
        passenger.yaw += this.yawVelocity;
        passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
        this.copyEntityData(passenger);
        if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1) {
            int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
            passenger.setYaw(((AnimalEntity)passenger).bodyYaw + (float)j);
            passenger.setHeadYaw(passenger.getHeadYaw() + (float)j);
        }
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        double e;
        Vec3d lv = BoatEntity.getPassengerDismountOffset(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO, passenger.getWidth(), this.yaw);
        double d = this.getX() + lv.x;
        BlockPos lv2 = new BlockPos(d, this.getBoundingBox().maxY, e = this.getZ() + lv.z);
        BlockPos lv3 = lv2.down();
        if (!this.world.isWater(lv3)) {
            double f = (double)lv2.getY() + this.world.getDismountHeight(lv2);
            double g = (double)lv2.getY() + this.world.getDismountHeight(lv3);
            for (EntityPose lv4 : passenger.getPoses()) {
                Vec3d lv5 = Dismounting.findDismountPos(this.world, d, f, e, passenger, lv4);
                if (lv5 != null) {
                    passenger.setPose(lv4);
                    return lv5;
                }
                Vec3d lv6 = Dismounting.findDismountPos(this.world, d, g, e, passenger, lv4);
                if (lv6 == null) continue;
                passenger.setPose(lv4);
                return lv6;
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    protected void copyEntityData(Entity entity) {
        entity.setYaw(this.yaw);
        float f = MathHelper.wrapDegrees(entity.yaw - this.yaw);
        float g = MathHelper.clamp(f, -105.0f, 105.0f);
        entity.prevYaw += g - f;
        entity.yaw += g - f;
        entity.setHeadYaw(entity.yaw);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void onPassengerLookAround(Entity passenger) {
        this.copyEntityData(passenger);
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putString("Type", this.getBoatType().getName());
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("Type", 8)) {
            this.setBoatType(Type.getType(tag.getString("Type")));
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        if (this.ticksUnderwater < 60.0f) {
            if (!this.world.isClient) {
                return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (this.hasVehicle()) {
            return;
        }
        if (onGround) {
            if (this.fallDistance > 3.0f) {
                if (this.location != Location.ON_LAND) {
                    this.fallDistance = 0.0f;
                    return;
                }
                this.handleFallDamage(this.fallDistance, 1.0f);
                if (!this.world.isClient && !this.removed) {
                    this.remove();
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        for (int i = 0; i < 3; ++i) {
                            this.dropItem(this.getBoatType().getBaseBlock());
                        }
                        for (int j = 0; j < 2; ++j) {
                            this.dropItem(Items.STICK);
                        }
                    }
                }
            }
            this.fallDistance = 0.0f;
        } else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }
    }

    public boolean isPaddleMoving(int paddle) {
        return this.dataTracker.get(paddle == 0 ? LEFT_PADDLE_MOVING : RIGHT_PADDLE_MOVING) != false && this.getPrimaryPassenger() != null;
    }

    public void setDamageWobbleStrength(float wobbleStrength) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(wobbleStrength));
    }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH).floatValue();
    }

    public void setDamageWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
    }

    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    private void setBubbleWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(BUBBLE_WOBBLE_TICKS, wobbleTicks);
    }

    private int getBubbleWobbleTicks() {
        return this.dataTracker.get(BUBBLE_WOBBLE_TICKS);
    }

    @Environment(value=EnvType.CLIENT)
    public float interpolateBubbleWobble(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastBubbleWobble, this.bubbleWobble);
    }

    public void setDamageWobbleSide(int side) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    public void setBoatType(Type type) {
        this.dataTracker.set(BOAT_TYPE, type.ordinal());
    }

    public Type getBoatType() {
        return Type.getType(this.dataTracker.get(BOAT_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2 && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        List<Entity> list = this.getPassengerList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Environment(value=EnvType.CLIENT)
    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public boolean isSubmergedInWater() {
        return this.location == Location.UNDER_WATER || this.location == Location.UNDER_FLOWING_WATER;
    }

    public static enum Type {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String name;
        private final Block baseBlock;

        private Type(Block baseBlock, String name) {
            this.name = name;
            this.baseBlock = baseBlock;
        }

        public String getName() {
            return this.name;
        }

        public Block getBaseBlock() {
            return this.baseBlock;
        }

        public String toString() {
            return this.name;
        }

        public static Type getType(int i) {
            Type[] lvs = Type.values();
            if (i < 0 || i >= lvs.length) {
                i = 0;
            }
            return lvs[i];
        }

        public static Type getType(String string) {
            Type[] lvs = Type.values();
            for (int i = 0; i < lvs.length; ++i) {
                if (!lvs[i].getName().equals(string)) continue;
                return lvs[i];
            }
            return lvs[0];
        }
    }

    public static enum Location {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }
}

