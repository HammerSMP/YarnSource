/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.AreaHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.class_5459;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity
implements Nameable,
CommandOutput {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger MAX_ENTITY_ID = new AtomicInteger();
    private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
    private static final Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static double renderDistanceMultiplier = 1.0;
    private final EntityType<?> type;
    private int entityId = MAX_ENTITY_ID.incrementAndGet();
    public boolean inanimate;
    private final List<Entity> passengerList = Lists.newArrayList();
    protected int ridingCooldown;
    @Nullable
    private Entity vehicle;
    public boolean teleporting;
    public World world;
    public double prevX;
    public double prevY;
    public double prevZ;
    private Vec3d pos;
    private BlockPos blockPos;
    private Vec3d velocity = Vec3d.ZERO;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    private Box entityBounds = NULL_BOX;
    protected boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean velocityModified;
    protected Vec3d movementMultiplier = Vec3d.ZERO;
    public boolean removed;
    public float prevHorizontalSpeed;
    public float horizontalSpeed;
    public float distanceTraveled;
    public float fallDistance;
    private float nextStepSoundDistance = 1.0f;
    private float nextFlySoundDistance = 1.0f;
    public double lastRenderX;
    public double lastRenderY;
    public double lastRenderZ;
    public float stepHeight;
    public boolean noClip;
    public float pushSpeedReduction;
    protected final Random random = new Random();
    public int age;
    private int fireTicks = -this.getBurningDuration();
    protected boolean touchingWater;
    protected Object2DoubleMap<Tag<Fluid>> fluidHeight = new Object2DoubleArrayMap(2);
    protected boolean submergedInWater;
    @Nullable
    protected Tag<Fluid> field_25599;
    public int timeUntilRegen;
    protected boolean firstUpdate = true;
    protected final DataTracker dataTracker;
    protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
    private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
    public boolean updateNeeded;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    private boolean chunkPosUpdateRequested;
    private Vec3d field_25750;
    public boolean ignoreCameraFrustum;
    public boolean velocityDirty;
    private int netherPortalCooldown;
    protected boolean inNetherPortal;
    protected int netherPortalTime;
    protected BlockPos lastNetherPortalPosition;
    private boolean invulnerable;
    protected UUID uuid = MathHelper.randomUuid(this.random);
    protected String uuidString = this.uuid.toString();
    protected boolean glowing;
    private final Set<String> scoreboardTags = Sets.newHashSet();
    private boolean teleportRequested;
    private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
    private long pistonMovementTick;
    private EntityDimensions dimensions;
    private float standingEyeHeight;

    public Entity(EntityType<?> type, World world) {
        this.type = type;
        this.world = world;
        this.dimensions = type.getDimensions();
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.field_25750 = Vec3d.ZERO;
        this.updatePosition(0.0, 0.0, 0.0);
        this.dataTracker = new DataTracker(this);
        this.dataTracker.startTracking(FLAGS, (byte)0);
        this.dataTracker.startTracking(AIR, this.getMaxAir());
        this.dataTracker.startTracking(NAME_VISIBLE, false);
        this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
        this.dataTracker.startTracking(SILENT, false);
        this.dataTracker.startTracking(NO_GRAVITY, false);
        this.dataTracker.startTracking(POSE, EntityPose.STANDING);
        this.initDataTracker();
        this.standingEyeHeight = this.getEyeHeight(EntityPose.STANDING, this.dimensions);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_30632(BlockPos arg, BlockState arg2) {
        VoxelShape lv = arg2.getCollisionShape(this.world, arg, ShapeContext.of(this));
        VoxelShape lv2 = lv.offset(arg.getX(), arg.getY(), arg.getZ());
        return VoxelShapes.matchesAnywhere(lv2, VoxelShapes.cuboid(this.getBoundingBox()), BooleanBiFunction.AND);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTeamColorValue() {
        AbstractTeam lv = this.getScoreboardTeam();
        if (lv != null && lv.getColor().getColorValue() != null) {
            return lv.getColor().getColorValue();
        }
        return 0xFFFFFF;
    }

    public boolean isSpectator() {
        return false;
    }

    public final void detach() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

    public void updateTrackedPosition(double x, double y, double z) {
        this.method_30228(new Vec3d(x, y, z));
    }

    public void method_30228(Vec3d arg) {
        this.field_25750 = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d method_30227() {
        return this.field_25750;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public Set<String> getScoreboardTags() {
        return this.scoreboardTags;
    }

    public boolean addScoreboardTag(String tag) {
        if (this.scoreboardTags.size() >= 1024) {
            return false;
        }
        return this.scoreboardTags.add(tag);
    }

    public boolean removeScoreboardTag(String tag) {
        return this.scoreboardTags.remove(tag);
    }

    public void kill() {
        this.remove();
    }

    protected abstract void initDataTracker();

    public DataTracker getDataTracker() {
        return this.dataTracker;
    }

    public boolean equals(Object o) {
        if (o instanceof Entity) {
            return ((Entity)o).entityId == this.entityId;
        }
        return false;
    }

    public int hashCode() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    protected void afterSpawn() {
        if (this.world == null) {
            return;
        }
        for (double d = this.getY(); d > 0.0 && d < 256.0; d += 1.0) {
            this.updatePosition(this.getX(), d, this.getZ());
            if (this.world.doesNotCollide(this)) break;
        }
        this.setVelocity(Vec3d.ZERO);
        this.pitch = 0.0f;
    }

    public void remove() {
        this.removed = true;
    }

    public void setPose(EntityPose pose) {
        this.dataTracker.set(POSE, pose);
    }

    public EntityPose getPose() {
        return this.dataTracker.get(POSE);
    }

    public boolean isInRange(Entity other, double radius) {
        double e = other.pos.x - this.pos.x;
        double f = other.pos.y - this.pos.y;
        double g = other.pos.z - this.pos.z;
        return e * e + f * f + g * g < radius * radius;
    }

    protected void setRotation(float yaw, float pitch) {
        this.yaw = yaw % 360.0f;
        this.pitch = pitch % 360.0f;
    }

    public void updatePosition(double x, double y, double z) {
        this.setPos(x, y, z);
        this.setBoundingBox(this.dimensions.method_30231(x, y, z));
    }

    protected void refreshPosition() {
        this.updatePosition(this.pos.x, this.pos.y, this.pos.z);
    }

    @Environment(value=EnvType.CLIENT)
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        double f = cursorDeltaY * 0.15;
        double g = cursorDeltaX * 0.15;
        this.pitch = (float)((double)this.pitch + f);
        this.yaw = (float)((double)this.yaw + g);
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
        this.prevPitch = (float)((double)this.prevPitch + f);
        this.prevYaw = (float)((double)this.prevYaw + g);
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerLookAround(this);
        }
    }

    public void tick() {
        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();
    }

    public void baseTick() {
        this.world.getProfiler().push("entityBaseTick");
        if (this.hasVehicle() && this.getVehicle().removed) {
            this.stopRiding();
        }
        if (this.ridingCooldown > 0) {
            --this.ridingCooldown;
        }
        this.prevHorizontalSpeed = this.horizontalSpeed;
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.tickNetherPortal();
        if (this.shouldSpawnSprintingParticles()) {
            this.spawnSprintingParticles();
        }
        this.updateWaterState();
        this.updateSubmergedInWaterState();
        this.updateSwimming();
        if (this.world.isClient) {
            this.extinguish();
        } else if (this.fireTicks > 0) {
            if (this.isFireImmune()) {
                this.setFireTicks(this.fireTicks - 4);
                if (this.fireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damage(DamageSource.ON_FIRE, 1.0f);
                }
                this.setFireTicks(this.fireTicks - 1);
            }
        }
        if (this.isInLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5f;
        }
        if (this.getY() < -64.0) {
            this.destroy();
        }
        if (!this.world.isClient) {
            this.setFlag(0, this.fireTicks > 0);
        }
        this.firstUpdate = false;
        this.world.getProfiler().pop();
    }

    public void method_30229() {
        this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
    }

    public boolean method_30230() {
        return this.netherPortalCooldown > 0;
    }

    protected void tickNetherPortalCooldown() {
        if (this.method_30230()) {
            --this.netherPortalCooldown;
        }
    }

    public int getMaxNetherPortalTime() {
        return 0;
    }

    protected void setOnFireFromLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15);
        this.damage(DamageSource.LAVA, 4.0f);
    }

    public void setOnFireFor(int seconds) {
        int j = seconds * 20;
        if (this instanceof LivingEntity) {
            j = ProtectionEnchantment.transformFireDuration((LivingEntity)this, j);
        }
        if (this.fireTicks < j) {
            this.setFireTicks(j);
        }
    }

    public void setFireTicks(int ticks) {
        this.fireTicks = ticks;
    }

    public int getFireTicks() {
        return this.fireTicks;
    }

    public void extinguish() {
        this.setFireTicks(0);
    }

    protected void destroy() {
        this.remove();
    }

    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
    }

    private boolean doesNotCollide(Box box) {
        return this.world.doesNotCollide(this, box) && !this.world.containsFluid(box);
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(MovementType type, Vec3d movement) {
        Vec3d lv;
        if (this.noClip) {
            this.setBoundingBox(this.getBoundingBox().offset(movement));
            this.moveToBoundingBoxCenter();
            return;
        }
        if (type == MovementType.PISTON && (movement = this.adjustMovementForPiston(movement)).equals(Vec3d.ZERO)) {
            return;
        }
        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            movement = movement.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }
        if ((lv = this.adjustMovementForCollisions(movement = this.adjustMovementForSneaking(movement, type))).lengthSquared() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().offset(lv));
            this.moveToBoundingBoxCenter();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rest");
        this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, lv.x) || !MathHelper.approximatelyEquals(movement.z, lv.z);
        this.verticalCollision = movement.y != lv.y;
        this.onGround = this.verticalCollision && movement.y < 0.0;
        BlockPos lv2 = this.getLandingPos();
        BlockState lv3 = this.world.getBlockState(lv2);
        this.fall(lv.y, this.onGround, lv3, lv2);
        Vec3d lv4 = this.getVelocity();
        if (movement.x != lv.x) {
            this.setVelocity(0.0, lv4.y, lv4.z);
        }
        if (movement.z != lv.z) {
            this.setVelocity(lv4.x, lv4.y, 0.0);
        }
        Block lv5 = lv3.getBlock();
        if (movement.y != lv.y) {
            lv5.onEntityLand(this.world, this);
        }
        if (this.onGround && !this.bypassesSteppingEffects()) {
            lv5.onSteppedOn(this.world, lv2, this);
        }
        if (this.canClimb() && !this.hasVehicle()) {
            double d = lv.x;
            double e = lv.y;
            double f = lv.z;
            if (!lv5.isIn(BlockTags.CLIMBABLE)) {
                e = 0.0;
            }
            this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(Entity.squaredHorizontalLength(lv)) * 0.6);
            this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
            if (this.distanceTraveled > this.nextStepSoundDistance && !lv3.isAir()) {
                this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                if (this.isTouchingWater()) {
                    Entity lv6 = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
                    float g = lv6 == this ? 0.35f : 0.4f;
                    Vec3d lv7 = lv6.getVelocity();
                    float h = MathHelper.sqrt(lv7.x * lv7.x * (double)0.2f + lv7.y * lv7.y + lv7.z * lv7.z * (double)0.2f) * g;
                    if (h > 1.0f) {
                        h = 1.0f;
                    }
                    this.playSwimSound(h);
                } else {
                    this.playStepSound(lv2, lv3);
                }
            } else if (this.distanceTraveled > this.nextFlySoundDistance && this.hasWings() && lv3.isAir()) {
                this.nextFlySoundDistance = this.playFlySound(this.distanceTraveled);
            }
        }
        try {
            this.checkBlockCollision();
        }
        catch (Throwable throwable) {
            CrashReport lv8 = CrashReport.create(throwable, "Checking entity block collision");
            CrashReportSection lv9 = lv8.addElement("Entity being checked for collision");
            this.populateCrashReport(lv9);
            throw new CrashException(lv8);
        }
        float i = this.getVelocityMultiplier();
        this.setVelocity(this.getVelocity().multiply(i, 1.0, i));
        if (this.world.method_29556(this.getBoundingBox().contract(0.001)).noneMatch(arg -> arg.isIn(BlockTags.FIRE) || arg.isOf(Blocks.LAVA)) && this.fireTicks <= 0) {
            this.setFireTicks(-this.getBurningDuration());
        }
        if (this.isWet() && this.isOnFire()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.setFireTicks(-this.getBurningDuration());
        }
        this.world.getProfiler().pop();
    }

    protected BlockPos getLandingPos() {
        BlockPos lv2;
        BlockState lv3;
        Block lv4;
        int k;
        int j;
        int i = MathHelper.floor(this.pos.x);
        BlockPos lv = new BlockPos(i, j = MathHelper.floor(this.pos.y - (double)0.2f), k = MathHelper.floor(this.pos.z));
        if (this.world.getBlockState(lv).isAir() && ((lv4 = (lv3 = this.world.getBlockState(lv2 = lv.down())).getBlock()).isIn(BlockTags.FENCES) || lv4.isIn(BlockTags.WALLS) || lv4 instanceof FenceGateBlock)) {
            return lv2;
        }
        return lv;
    }

    protected float getJumpVelocityMultiplier() {
        float f = this.world.getBlockState(this.getBlockPos()).getBlock().getJumpVelocityMultiplier();
        float g = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
        return (double)f == 1.0 ? g : f;
    }

    protected float getVelocityMultiplier() {
        Block lv = this.world.getBlockState(this.getBlockPos()).getBlock();
        float f = lv.getVelocityMultiplier();
        if (lv == Blocks.WATER || lv == Blocks.BUBBLE_COLUMN) {
            return f;
        }
        return (double)f == 1.0 ? this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
    }

    protected BlockPos getVelocityAffectingPos() {
        return new BlockPos(this.pos.x, this.getBoundingBox().minY - 0.5000001, this.pos.z);
    }

    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        return movement;
    }

    protected Vec3d adjustMovementForPiston(Vec3d movement) {
        if (movement.lengthSquared() <= 1.0E-7) {
            return movement;
        }
        long l = this.world.getTime();
        if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
        }
        if (movement.x != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.X, movement.x);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
        }
        if (movement.y != 0.0) {
            double e = this.calculatePistonMovementFactor(Direction.Axis.Y, movement.y);
            return Math.abs(e) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, e, 0.0);
        }
        if (movement.z != 0.0) {
            double f = this.calculatePistonMovementFactor(Direction.Axis.Z, movement.z);
            return Math.abs(f) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, 0.0, f);
        }
        return Vec3d.ZERO;
    }

    private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
        int i = axis.ordinal();
        double e = MathHelper.clamp(offsetFactor + this.pistonMovementDelta[i], -0.51, 0.51);
        offsetFactor = e - this.pistonMovementDelta[i];
        this.pistonMovementDelta[i] = e;
        return offsetFactor;
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        boolean bl4;
        Box lv = this.getBoundingBox();
        ShapeContext lv2 = ShapeContext.of(this);
        VoxelShape lv3 = this.world.getWorldBorder().asVoxelShape();
        Stream<Object> stream = VoxelShapes.matchesAnywhere(lv3, VoxelShapes.cuboid(lv.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(lv3);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, lv.stretch(movement), arg -> true);
        ReusableStream<VoxelShape> lv4 = new ReusableStream<VoxelShape>(Stream.concat(stream2, stream));
        Vec3d lv5 = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(this, movement, lv, this.world, lv2, lv4);
        boolean bl = movement.x != lv5.x;
        boolean bl2 = movement.y != lv5.y;
        boolean bl3 = movement.z != lv5.z;
        boolean bl5 = bl4 = this.onGround || bl2 && movement.y < 0.0;
        if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
            Vec3d lv8;
            Vec3d lv6 = Entity.adjustMovementForCollisions(this, new Vec3d(movement.x, this.stepHeight, movement.z), lv, this.world, lv2, lv4);
            Vec3d lv7 = Entity.adjustMovementForCollisions(this, new Vec3d(0.0, this.stepHeight, 0.0), lv.stretch(movement.x, 0.0, movement.z), this.world, lv2, lv4);
            if (lv7.y < (double)this.stepHeight && Entity.squaredHorizontalLength(lv8 = Entity.adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), lv.offset(lv7), this.world, lv2, lv4).add(lv7)) > Entity.squaredHorizontalLength(lv6)) {
                lv6 = lv8;
            }
            if (Entity.squaredHorizontalLength(lv6) > Entity.squaredHorizontalLength(lv5)) {
                return lv6.add(Entity.adjustMovementForCollisions(this, new Vec3d(0.0, -lv6.y + movement.y, 0.0), lv.offset(lv6), this.world, lv2, lv4));
            }
        }
        return lv5;
    }

    public static double squaredHorizontalLength(Vec3d vector) {
        return vector.x * vector.x + vector.z * vector.z;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, ShapeContext context, ReusableStream<VoxelShape> collisions) {
        boolean bl3;
        boolean bl = movement.x == 0.0;
        boolean bl2 = movement.y == 0.0;
        boolean bl4 = bl3 = movement.z == 0.0;
        if (bl && bl2 || bl && bl3 || bl2 && bl3) {
            return Entity.adjustSingleAxisMovementForCollisions(movement, entityBoundingBox, world, context, collisions);
        }
        ReusableStream<VoxelShape> lv = new ReusableStream<VoxelShape>(Stream.concat(collisions.stream(), world.getBlockCollisions(entity, entityBoundingBox.stretch(movement))));
        return Entity.adjustMovementForCollisions(movement, entityBoundingBox, lv);
    }

    public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions) {
        boolean bl;
        double d = movement.x;
        double e = movement.y;
        double f = movement.z;
        if (e != 0.0 && (e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions.stream(), e)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions.stream(), f)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
        }
        if (d != 0.0) {
            d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions.stream(), d);
            if (!bl && d != 0.0) {
                entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
            }
        }
        if (!bl && f != 0.0) {
            f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions.stream(), f);
        }
        return new Vec3d(d, e, f);
    }

    public static Vec3d adjustSingleAxisMovementForCollisions(Vec3d movement, Box entityBoundingBox, WorldView world, ShapeContext context, ReusableStream<VoxelShape> collisions) {
        boolean bl;
        double d = movement.x;
        double e = movement.y;
        double f = movement.z;
        if (e != 0.0 && (e = VoxelShapes.calculatePushVelocity(Direction.Axis.Y, entityBoundingBox, world, e, context, collisions.stream())) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, entityBoundingBox, world, f, context, collisions.stream())) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
        }
        if (d != 0.0) {
            d = VoxelShapes.calculatePushVelocity(Direction.Axis.X, entityBoundingBox, world, d, context, collisions.stream());
            if (!bl && d != 0.0) {
                entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
            }
        }
        if (!bl && f != 0.0) {
            f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, entityBoundingBox, world, f, context, collisions.stream());
        }
        return new Vec3d(d, e, f);
    }

    protected float calculateNextStepSoundDistance() {
        return (int)this.distanceTraveled + 1;
    }

    public void moveToBoundingBoxCenter() {
        Box lv = this.getBoundingBox();
        this.setPos((lv.minX + lv.maxX) / 2.0, lv.minY, (lv.minZ + lv.maxZ) / 2.0);
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_GENERIC_SWIM;
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    protected void checkBlockCollision() {
        Box lv = this.getBoundingBox();
        BlockPos lv2 = new BlockPos(lv.minX + 0.001, lv.minY + 0.001, lv.minZ + 0.001);
        BlockPos lv3 = new BlockPos(lv.maxX - 0.001, lv.maxY - 0.001, lv.maxZ - 0.001);
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        if (this.world.isRegionLoaded(lv2, lv3)) {
            for (int i = lv2.getX(); i <= lv3.getX(); ++i) {
                for (int j = lv2.getY(); j <= lv3.getY(); ++j) {
                    for (int k = lv2.getZ(); k <= lv3.getZ(); ++k) {
                        lv4.set(i, j, k);
                        BlockState lv5 = this.world.getBlockState(lv4);
                        try {
                            lv5.onEntityCollision(this.world, lv4, this);
                            this.onBlockCollision(lv5);
                            continue;
                        }
                        catch (Throwable throwable) {
                            CrashReport lv6 = CrashReport.create(throwable, "Colliding entity with block");
                            CrashReportSection lv7 = lv6.addElement("Block being collided with");
                            CrashReportSection.addBlockInfo(lv7, lv4, lv5);
                            throw new CrashException(lv6);
                        }
                    }
                }
            }
        }
    }

    protected void onBlockCollision(BlockState state) {
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getMaterial().isLiquid()) {
            return;
        }
        BlockState lv = this.world.getBlockState(pos.up());
        BlockSoundGroup lv2 = lv.isOf(Blocks.SNOW) ? lv.getSoundGroup() : state.getSoundGroup();
        this.playSound(lv2.getStepSound(), lv2.getVolume() * 0.15f, lv2.getPitch());
    }

    protected void playSwimSound(float volume) {
        this.playSound(this.getSwimSound(), volume, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected float playFlySound(float distance) {
        return 0.0f;
    }

    protected boolean hasWings() {
        return false;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
        }
    }

    public boolean isSilent() {
        return this.dataTracker.get(SILENT);
    }

    public void setSilent(boolean silent) {
        this.dataTracker.set(SILENT, silent);
    }

    public boolean hasNoGravity() {
        return this.dataTracker.get(NO_GRAVITY);
    }

    public void setNoGravity(boolean noGravity) {
        this.dataTracker.set(NO_GRAVITY, noGravity);
    }

    protected boolean canClimb() {
        return true;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        if (onGround) {
            if (this.fallDistance > 0.0f) {
                landedState.getBlock().onLandedUpon(this.world, landedPosition, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        } else if (heightDifference < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }
    }

    @Nullable
    public Box getCollisionBox() {
        return null;
    }

    public boolean isFireImmune() {
        return this.getType().isFireImmune();
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        if (this.hasPassengers()) {
            for (Entity lv : this.getPassengerList()) {
                lv.handleFallDamage(fallDistance, damageMultiplier);
            }
        }
        return false;
    }

    public boolean isTouchingWater() {
        return this.touchingWater;
    }

    private boolean isBeingRainedOn() {
        BlockPos lv = this.getBlockPos();
        return this.world.hasRain(lv) || this.world.hasRain(new BlockPos((double)lv.getX(), this.getBoundingBox().maxY, (double)lv.getZ()));
    }

    private boolean isInsideBubbleColumn() {
        return this.world.getBlockState(this.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);
    }

    public boolean isTouchingWaterOrRain() {
        return this.isTouchingWater() || this.isBeingRainedOn();
    }

    public boolean isWet() {
        return this.isTouchingWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
    }

    public boolean isInsideWaterOrBubbleColumn() {
        return this.isTouchingWater() || this.isInsideBubbleColumn();
    }

    public boolean isSubmergedInWater() {
        return this.submergedInWater && this.isTouchingWater();
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
        } else {
            this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle());
        }
    }

    protected boolean updateWaterState() {
        this.fluidHeight.clear();
        this.checkWaterState();
        double d = this.world.getDimension().isUltrawarm() ? 0.007 : 0.0023333333333333335;
        boolean bl = this.updateMovementInFluid(FluidTags.LAVA, d);
        return this.isTouchingWater() || bl;
    }

    void checkWaterState() {
        if (this.getVehicle() instanceof BoatEntity) {
            this.touchingWater = false;
        } else if (this.updateMovementInFluid(FluidTags.WATER, 0.014)) {
            if (!this.touchingWater && !this.firstUpdate) {
                this.onSwimmingStart();
            }
            this.fallDistance = 0.0f;
            this.touchingWater = true;
            this.extinguish();
        } else {
            this.touchingWater = false;
        }
    }

    private void updateSubmergedInWaterState() {
        BoatEntity lv2;
        this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
        this.field_25599 = null;
        double d = this.getEyeY() - 0.1111111119389534;
        Entity lv = this.getVehicle();
        if (lv instanceof BoatEntity && !(lv2 = (BoatEntity)lv).isSubmergedInWater() && lv2.getBoundingBox().maxY >= d && lv2.getBoundingBox().minY <= d) {
            return;
        }
        BlockPos lv3 = new BlockPos(this.getX(), d, this.getZ());
        FluidState lv4 = this.world.getFluidState(lv3);
        for (Tag<Fluid> tag : FluidTags.all()) {
            if (!lv4.isIn(tag)) continue;
            double e = (float)lv3.getY() + lv4.getHeight(this.world, lv3);
            if (e > d) {
                this.field_25599 = tag;
            }
            return;
        }
    }

    protected void onSwimmingStart() {
        Entity lv = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
        float f = lv == this ? 0.2f : 0.9f;
        Vec3d lv2 = lv.getVelocity();
        float g = MathHelper.sqrt(lv2.x * lv2.x * (double)0.2f + lv2.y * lv2.y + lv2.z * lv2.z * (double)0.2f) * f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        if ((double)g < 0.25) {
            this.playSound(this.getSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        } else {
            this.playSound(this.getHighSpeedSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        float h = MathHelper.floor(this.getY());
        int i = 0;
        while ((float)i < 1.0f + this.dimensions.width * 20.0f) {
            double d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            double e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + d, h + 1.0f, this.getZ() + e, lv2.x, lv2.y - this.random.nextDouble() * (double)0.2f, lv2.z);
            ++i;
        }
        int j = 0;
        while ((float)j < 1.0f + this.dimensions.width * 20.0f) {
            double k = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            double l = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
            this.world.addParticle(ParticleTypes.SPLASH, this.getX() + k, h + 1.0f, this.getZ() + l, lv2.x, lv2.y, lv2.z);
            ++j;
        }
    }

    protected BlockState getLandingBlockState() {
        return this.world.getBlockState(this.getLandingPos());
    }

    public boolean shouldSpawnSprintingParticles() {
        return this.isSprinting() && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
    }

    protected void spawnSprintingParticles() {
        int k;
        int j;
        int i = MathHelper.floor(this.getX());
        BlockPos lv = new BlockPos(i, j = MathHelper.floor(this.getY() - (double)0.2f), k = MathHelper.floor(this.getZ()));
        BlockState lv2 = this.world.getBlockState(lv);
        if (lv2.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d lv3 = this.getVelocity();
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, lv2), this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width, lv3.x * -4.0, 1.5, lv3.z * -4.0);
        }
    }

    public boolean isSubmergedIn(Tag<Fluid> arg) {
        return this.field_25599 == arg;
    }

    public boolean isInLava() {
        return !this.firstUpdate && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
    }

    public void updateVelocity(float speed, Vec3d movementInput) {
        Vec3d lv = Entity.movementInputToVelocity(movementInput, speed, this.yaw);
        this.setVelocity(this.getVelocity().add(lv));
    }

    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d lv = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float h = MathHelper.sin(yaw * ((float)Math.PI / 180));
        float i = MathHelper.cos(yaw * ((float)Math.PI / 180));
        return new Vec3d(lv.x * (double)i - lv.z * (double)h, lv.y, lv.z * (double)i + lv.x * (double)h);
    }

    public float getBrightnessAtEyes() {
        BlockPos.Mutable lv = new BlockPos.Mutable(this.getX(), 0.0, this.getZ());
        if (this.world.isChunkLoaded(lv)) {
            lv.setY(MathHelper.floor(this.getEyeY()));
            return this.world.getBrightness(lv);
        }
        return 0.0f;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.method_30634(x, y, z);
        this.yaw = yaw % 360.0f;
        this.pitch = MathHelper.clamp(pitch, -90.0f, 90.0f) % 360.0f;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void method_30634(double d, double e, double f) {
        double g = MathHelper.clamp(d, -3.0E7, 3.0E7);
        double h = MathHelper.clamp(f, -3.0E7, 3.0E7);
        this.prevX = g;
        this.prevY = e;
        this.prevZ = h;
        this.updatePosition(g, e, h);
    }

    public void method_29495(Vec3d arg) {
        this.refreshPositionAfterTeleport(arg.x, arg.y, arg.z);
    }

    public void refreshPositionAfterTeleport(double x, double y, double z) {
        this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
    }

    public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
        this.refreshPositionAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
    }

    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.resetPosition(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
        this.refreshPosition();
    }

    public void resetPosition(double x, double y, double z) {
        this.setPos(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.lastRenderX = x;
        this.lastRenderY = y;
        this.lastRenderZ = z;
    }

    public float distanceTo(Entity entity) {
        float f = (float)(this.getX() - entity.getX());
        float g = (float)(this.getY() - entity.getY());
        float h = (float)(this.getZ() - entity.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public double squaredDistanceTo(double x, double y, double z) {
        double g = this.getX() - x;
        double h = this.getY() - y;
        double i = this.getZ() - z;
        return g * g + h * h + i * i;
    }

    public double squaredDistanceTo(Entity entity) {
        return this.squaredDistanceTo(entity.getPos());
    }

    public double squaredDistanceTo(Vec3d vector) {
        double d = this.getX() - vector.x;
        double e = this.getY() - vector.y;
        double f = this.getZ() - vector.z;
        return d * d + e * e + f * f;
    }

    public void onPlayerCollision(PlayerEntity player) {
    }

    public void pushAwayFrom(Entity entity) {
        double e;
        if (this.isConnectedThroughVehicle(entity)) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        double d = entity.getX() - this.getX();
        double f = MathHelper.absMax(d, e = entity.getZ() - this.getZ());
        if (f >= (double)0.01f) {
            f = MathHelper.sqrt(f);
            d /= f;
            e /= f;
            double g = 1.0 / f;
            if (g > 1.0) {
                g = 1.0;
            }
            d *= g;
            e *= g;
            d *= (double)0.05f;
            e *= (double)0.05f;
            d *= (double)(1.0f - this.pushSpeedReduction);
            e *= (double)(1.0f - this.pushSpeedReduction);
            if (!this.hasPassengers()) {
                this.addVelocity(-d, 0.0, -e);
            }
            if (!entity.hasPassengers()) {
                entity.addVelocity(d, 0.0, e);
            }
        }
    }

    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        this.setVelocity(this.getVelocity().add(deltaX, deltaY, deltaZ));
        this.velocityDirty = true;
    }

    protected void scheduleVelocityUpdate() {
        this.velocityModified = true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        return false;
    }

    public final Vec3d getRotationVec(float tickDelta) {
        return this.getRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }

    public float getPitch(float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.pitch;
        }
        return MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
    }

    public float getYaw(float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.yaw;
        }
        return MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
    }

    protected final Vec3d getRotationVector(float pitch, float yaw) {
        float h = pitch * ((float)Math.PI / 180);
        float i = -yaw * ((float)Math.PI / 180);
        float j = MathHelper.cos(i);
        float k = MathHelper.sin(i);
        float l = MathHelper.cos(h);
        float m = MathHelper.sin(h);
        return new Vec3d(k * l, -m, j * l);
    }

    public final Vec3d getOppositeRotationVector(float tickDelta) {
        return this.getOppositeRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }

    protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
        return this.getRotationVector(pitch - 90.0f, yaw);
    }

    public final Vec3d getCameraPosVec(float tickDelta) {
        if (tickDelta == 1.0f) {
            return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        }
        double d = MathHelper.lerp((double)tickDelta, this.prevX, this.getX());
        double e = MathHelper.lerp((double)tickDelta, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
        double g = MathHelper.lerp((double)tickDelta, this.prevZ, this.getZ());
        return new Vec3d(d, e, g);
    }

    public HitResult rayTrace(double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d lv = this.getCameraPosVec(tickDelta);
        Vec3d lv2 = this.getRotationVec(tickDelta);
        Vec3d lv3 = lv.add(lv2.x * maxDistance, lv2.y * maxDistance, lv2.z * maxDistance);
        return this.world.rayTrace(new RayTraceContext(lv, lv3, RayTraceContext.ShapeType.OUTLINE, includeFluids ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, this));
    }

    public boolean collides() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
        if (killer instanceof ServerPlayerEntity) {
            Criteria.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)killer, this, damageSource);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        double g = this.getX() - cameraX;
        double h = this.getY() - cameraY;
        double i = this.getZ() - cameraZ;
        double j = g * g + h * h + i * i;
        return this.shouldRender(j);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double e = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return distance < (e *= 64.0 * renderDistanceMultiplier) * e;
    }

    public boolean saveSelfToTag(CompoundTag tag) {
        String string = this.getSavedEntityId();
        if (this.removed || string == null) {
            return false;
        }
        tag.putString("id", string);
        this.toTag(tag);
        return true;
    }

    public boolean saveToTag(CompoundTag tag) {
        if (this.hasVehicle()) {
            return false;
        }
        return this.saveSelfToTag(tag);
    }

    public CompoundTag toTag(CompoundTag tag) {
        try {
            if (this.vehicle != null) {
                tag.put("Pos", this.toListTag(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            } else {
                tag.put("Pos", this.toListTag(this.getX(), this.getY(), this.getZ()));
            }
            Vec3d lv = this.getVelocity();
            tag.put("Motion", this.toListTag(lv.x, lv.y, lv.z));
            tag.put("Rotation", this.toListTag(this.yaw, this.pitch));
            tag.putFloat("FallDistance", this.fallDistance);
            tag.putShort("Fire", (short)this.fireTicks);
            tag.putShort("Air", (short)this.getAir());
            tag.putBoolean("OnGround", this.onGround);
            tag.putBoolean("Invulnerable", this.invulnerable);
            tag.putInt("PortalCooldown", this.netherPortalCooldown);
            tag.putUuid("UUID", this.getUuid());
            Text lv2 = this.getCustomName();
            if (lv2 != null) {
                tag.putString("CustomName", Text.Serializer.toJson(lv2));
            }
            if (this.isCustomNameVisible()) {
                tag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                tag.putBoolean("Silent", this.isSilent());
            }
            if (this.hasNoGravity()) {
                tag.putBoolean("NoGravity", this.hasNoGravity());
            }
            if (this.glowing) {
                tag.putBoolean("Glowing", this.glowing);
            }
            if (!this.scoreboardTags.isEmpty()) {
                ListTag lv3 = new ListTag();
                for (String string : this.scoreboardTags) {
                    lv3.add(StringTag.of(string));
                }
                tag.put("Tags", lv3);
            }
            this.writeCustomDataToTag(tag);
            if (this.hasPassengers()) {
                ListTag lv4 = new ListTag();
                for (Entity lv5 : this.getPassengerList()) {
                    CompoundTag lv6;
                    if (!lv5.saveSelfToTag(lv6 = new CompoundTag())) continue;
                    lv4.add(lv6);
                }
                if (!lv4.isEmpty()) {
                    tag.put("Passengers", lv4);
                }
            }
        }
        catch (Throwable throwable) {
            CrashReport lv7 = CrashReport.create(throwable, "Saving entity NBT");
            CrashReportSection lv8 = lv7.addElement("Entity being saved");
            this.populateCrashReport(lv8);
            throw new CrashException(lv7);
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        try {
            ListTag lv = tag.getList("Pos", 6);
            ListTag lv2 = tag.getList("Motion", 6);
            ListTag lv3 = tag.getList("Rotation", 5);
            double d = lv2.getDouble(0);
            double e = lv2.getDouble(1);
            double f = lv2.getDouble(2);
            this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
            this.resetPosition(lv.getDouble(0), lv.getDouble(1), lv.getDouble(2));
            this.yaw = lv3.getFloat(0);
            this.pitch = lv3.getFloat(1);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.setHeadYaw(this.yaw);
            this.setYaw(this.yaw);
            this.fallDistance = tag.getFloat("FallDistance");
            this.fireTicks = tag.getShort("Fire");
            this.setAir(tag.getShort("Air"));
            this.onGround = tag.getBoolean("OnGround");
            this.invulnerable = tag.getBoolean("Invulnerable");
            this.netherPortalCooldown = tag.getInt("PortalCooldown");
            if (tag.containsUuid("UUID")) {
                this.uuid = tag.getUuid("UUID");
                this.uuidString = this.uuid.toString();
            }
            if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.yaw) || !Double.isFinite(this.pitch)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.refreshPosition();
            this.setRotation(this.yaw, this.pitch);
            if (tag.contains("CustomName", 8)) {
                String string = tag.getString("CustomName");
                try {
                    this.setCustomName(Text.Serializer.fromJson(string));
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to parse entity custom name {}", (Object)string, (Object)exception);
                }
            }
            this.setCustomNameVisible(tag.getBoolean("CustomNameVisible"));
            this.setSilent(tag.getBoolean("Silent"));
            this.setNoGravity(tag.getBoolean("NoGravity"));
            this.setGlowing(tag.getBoolean("Glowing"));
            if (tag.contains("Tags", 9)) {
                this.scoreboardTags.clear();
                ListTag lv4 = tag.getList("Tags", 8);
                int i = Math.min(lv4.size(), 1024);
                for (int j = 0; j < i; ++j) {
                    this.scoreboardTags.add(lv4.getString(j));
                }
            }
            this.readCustomDataFromTag(tag);
            if (this.shouldSetPositionOnLoad()) {
                this.refreshPosition();
            }
        }
        catch (Throwable throwable) {
            CrashReport lv5 = CrashReport.create(throwable, "Loading entity NBT");
            CrashReportSection lv6 = lv5.addElement("Entity being loaded");
            this.populateCrashReport(lv6);
            throw new CrashException(lv5);
        }
    }

    protected boolean shouldSetPositionOnLoad() {
        return true;
    }

    @Nullable
    protected final String getSavedEntityId() {
        EntityType<?> lv = this.getType();
        Identifier lv2 = EntityType.getId(lv);
        return !lv.isSaveable() || lv2 == null ? null : lv2.toString();
    }

    protected abstract void readCustomDataFromTag(CompoundTag var1);

    protected abstract void writeCustomDataToTag(CompoundTag var1);

    protected ListTag toListTag(double ... values) {
        ListTag lv = new ListTag();
        for (double d : values) {
            lv.add(DoubleTag.of(d));
        }
        return lv;
    }

    protected ListTag toListTag(float ... values) {
        ListTag lv = new ListTag();
        for (float f : values) {
            lv.add(FloatTag.of(f));
        }
        return lv;
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible item) {
        return this.dropItem(item, 0);
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible item, int yOffset) {
        return this.dropStack(new ItemStack(item), yOffset);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack stack) {
        return this.dropStack(stack, 0.0f);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        if (stack.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        ItemEntity lv = new ItemEntity(this.world, this.getX(), this.getY() + (double)yOffset, this.getZ(), stack);
        lv.setToDefaultPickupDelay();
        this.world.spawnEntity(lv);
        return lv;
    }

    public boolean isAlive() {
        return !this.removed;
    }

    public boolean isInsideWall() {
        if (this.noClip) {
            return false;
        }
        float f = 0.1f;
        float g = this.dimensions.width * 0.8f;
        Box lv = Box.method_30048(g, 0.1f, g).offset(this.getX(), this.getEyeY(), this.getZ());
        return this.world.method_30030(this, lv, (arg, arg2) -> arg.shouldSuffocate(this.world, (BlockPos)arg2)).findAny().isPresent();
    }

    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Nullable
    public Box getHardCollisionBox(Entity collidingEntity) {
        return null;
    }

    public void tickRiding() {
        this.setVelocity(Vec3d.ZERO);
        this.tick();
        if (!this.hasVehicle()) {
            return;
        }
        this.getVehicle().updatePassengerPosition(this);
    }

    public void updatePassengerPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::updatePosition);
    }

    private void updatePassengerPosition(Entity passenger, PositionUpdater arg2) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();
        arg2.accept(passenger, this.getX(), d, this.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public void onPassengerLookAround(Entity passenger) {
    }

    public double getHeightOffset() {
        return 0.0;
    }

    public double getMountedHeightOffset() {
        return (double)this.dimensions.height * 0.75;
    }

    public boolean startRiding(Entity entity) {
        return this.startRiding(entity, false);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLiving() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entity, boolean force) {
        Entity lv = entity;
        while (lv.vehicle != null) {
            if (lv.vehicle == this) {
                return false;
            }
            lv = lv.vehicle;
        }
        if (!(force || this.canStartRiding(entity) && entity.canAddPassenger(this))) {
            return false;
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setPose(EntityPose.STANDING);
        this.vehicle = entity;
        this.vehicle.addPassenger(this);
        return true;
    }

    protected boolean canStartRiding(Entity entity) {
        return !this.isSneaking() && this.ridingCooldown <= 0;
    }

    protected boolean wouldPoseNotCollide(EntityPose pose) {
        return this.world.doesNotCollide(this, this.calculateBoundsForPose(pose).contract(1.0E-7));
    }

    public void removeAllPassengers() {
        for (int i = this.passengerList.size() - 1; i >= 0; --i) {
            this.passengerList.get(i).stopRiding();
        }
    }

    public void method_29239() {
        if (this.vehicle != null) {
            Entity lv = this.vehicle;
            this.vehicle = null;
            lv.removePassenger(this);
        }
    }

    public void stopRiding() {
        this.method_29239();
    }

    protected void addPassenger(Entity passenger) {
        if (passenger.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.world.isClient && passenger instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
            this.passengerList.add(0, passenger);
        } else {
            this.passengerList.add(passenger);
        }
    }

    protected void removePassenger(Entity passenger) {
        if (passenger.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList.remove(passenger);
        passenger.ridingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 1;
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.updatePosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
        this.setHeadYaw(yaw);
    }

    public float getTargetingMargin() {
        return 0.0f;
    }

    public Vec3d getRotationVector() {
        return this.getRotationVector(this.pitch, this.yaw);
    }

    public Vec2f getRotationClient() {
        return new Vec2f(this.pitch, this.yaw);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getRotationVecClient() {
        return Vec3d.fromPolar(this.getRotationClient());
    }

    public void setInNetherPortal(BlockPos pos) {
        if (this.method_30230()) {
            this.method_30229();
            return;
        }
        if (!this.world.isClient && !pos.equals(this.lastNetherPortalPosition)) {
            this.lastNetherPortalPosition = pos.toImmutable();
        }
        this.inNetherPortal = true;
    }

    protected void tickNetherPortal() {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        int i = this.getMaxNetherPortalTime();
        ServerWorld lv = (ServerWorld)this.world;
        if (this.inNetherPortal) {
            RegistryKey<World> lv2;
            MinecraftServer minecraftServer = lv.getServer();
            ServerWorld lv3 = minecraftServer.getWorld(lv2 = this.world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER);
            if (lv3 != null && minecraftServer.isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
                this.world.getProfiler().push("portal");
                this.netherPortalTime = i;
                this.method_30229();
                this.moveToWorld(lv3);
                this.world.getProfiler().pop();
            }
            this.inNetherPortal = false;
        } else {
            if (this.netherPortalTime > 0) {
                this.netherPortalTime -= 4;
            }
            if (this.netherPortalTime < 0) {
                this.netherPortalTime = 0;
            }
        }
        this.tickNetherPortalCooldown();
    }

    public int getDefaultNetherPortalCooldown() {
        return 300;
    }

    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
    }

    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        switch (status) {
            case 53: {
                HoneyBlock.addRegularParticles(this);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
    }

    public Iterable<ItemStack> getItemsHand() {
        return EMPTY_STACK_LIST;
    }

    public Iterable<ItemStack> getArmorItems() {
        return EMPTY_STACK_LIST;
    }

    public Iterable<ItemStack> getItemsEquipped() {
        return Iterables.concat(this.getItemsHand(), this.getArmorItems());
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    public boolean isOnFire() {
        boolean bl = this.world != null && this.world.isClient;
        return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(0));
    }

    public boolean hasVehicle() {
        return this.getVehicle() != null;
    }

    public boolean hasPassengers() {
        return !this.getPassengerList().isEmpty();
    }

    public boolean canBeRiddenInWater() {
        return true;
    }

    public void setSneaking(boolean sneaking) {
        this.setFlag(1, sneaking);
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public boolean bypassesSteppingEffects() {
        return this.isSneaking();
    }

    public boolean bypassesLandingEffects() {
        return this.isSneaking();
    }

    public boolean isSneaky() {
        return this.isSneaking();
    }

    public boolean isDescending() {
        return this.isSneaking();
    }

    public boolean isInSneakingPose() {
        return this.getPose() == EntityPose.CROUCHING;
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean sprinting) {
        this.setFlag(3, sprinting);
    }

    public boolean isSwimming() {
        return this.getFlag(4);
    }

    public boolean isInSwimmingPose() {
        return this.getPose() == EntityPose.SWIMMING;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldLeaveSwimmingPose() {
        return this.isInSwimmingPose() && !this.isTouchingWater();
    }

    public void setSwimming(boolean swimming) {
        this.setFlag(4, swimming);
    }

    public boolean isGlowing() {
        return this.glowing || this.world.isClient && this.getFlag(6);
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        if (!this.world.isClient) {
            this.setFlag(6, this.glowing);
        }
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isInvisibleTo(PlayerEntity player) {
        if (player.isSpectator()) {
            return false;
        }
        AbstractTeam lv = this.getScoreboardTeam();
        if (lv != null && player != null && player.getScoreboardTeam() == lv && lv.shouldShowFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    @Nullable
    public AbstractTeam getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
    }

    public boolean isTeammate(Entity other) {
        return this.isTeamPlayer(other.getScoreboardTeam());
    }

    public boolean isTeamPlayer(AbstractTeam team) {
        if (this.getScoreboardTeam() != null) {
            return this.getScoreboardTeam().isEqual(team);
        }
        return false;
    }

    public void setInvisible(boolean invisible) {
        this.setFlag(5, invisible);
    }

    protected boolean getFlag(int index) {
        return (this.dataTracker.get(FLAGS) & 1 << index) != 0;
    }

    protected void setFlag(int index, boolean value) {
        byte b = this.dataTracker.get(FLAGS);
        if (value) {
            this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
        } else {
            this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
        }
    }

    public int getMaxAir() {
        return 300;
    }

    public int getAir() {
        return this.dataTracker.get(AIR);
    }

    public void setAir(int air) {
        this.dataTracker.set(AIR, air);
    }

    public void onStruckByLightning(ServerWorld arg, LightningEntity arg2) {
        this.setFireTicks(this.fireTicks + 1);
        if (this.fireTicks == 0) {
            this.setOnFireFor(8);
        }
        this.damage(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onBubbleColumnSurfaceCollision(boolean drag) {
        double e;
        Vec3d lv = this.getVelocity();
        if (drag) {
            double d = Math.max(-0.9, lv.y - 0.03);
        } else {
            e = Math.min(1.8, lv.y + 0.1);
        }
        this.setVelocity(lv.x, e, lv.z);
    }

    public void onBubbleColumnCollision(boolean drag) {
        double e;
        Vec3d lv = this.getVelocity();
        if (drag) {
            double d = Math.max(-0.3, lv.y - 0.03);
        } else {
            e = Math.min(0.7, lv.y + 0.06);
        }
        this.setVelocity(lv.x, e, lv.z);
        this.fallDistance = 0.0f;
    }

    public void onKilledOther(ServerWorld arg, LivingEntity arg2) {
    }

    protected void pushOutOfBlocks(double x, double y, double z) {
        BlockPos lv = new BlockPos(x, y, z);
        Vec3d lv2 = new Vec3d(x - (double)lv.getX(), y - (double)lv.getY(), z - (double)lv.getZ());
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        Direction lv4 = Direction.UP;
        double g = Double.MAX_VALUE;
        for (Direction lv5 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double i;
            lv3.set(lv, lv5);
            if (this.world.getBlockState(lv3).isFullCube(this.world, lv3)) continue;
            double h = lv2.getComponentAlongAxis(lv5.getAxis());
            double d = i = lv5.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
            if (!(i < g)) continue;
            g = i;
            lv4 = lv5;
        }
        float j = this.random.nextFloat() * 0.2f + 0.1f;
        float k = lv4.getDirection().offset();
        Vec3d lv6 = this.getVelocity().multiply(0.75);
        if (lv4.getAxis() == Direction.Axis.X) {
            this.setVelocity(k * j, lv6.y, lv6.z);
        } else if (lv4.getAxis() == Direction.Axis.Y) {
            this.setVelocity(lv6.x, k * j, lv6.z);
        } else if (lv4.getAxis() == Direction.Axis.Z) {
            this.setVelocity(lv6.x, lv6.y, k * j);
        }
    }

    public void slowMovement(BlockState state, Vec3d multiplier) {
        this.fallDistance = 0.0f;
        this.movementMultiplier = multiplier;
    }

    private static Text removeClickEvents(Text textComponent) {
        MutableText lv = textComponent.copy().setStyle(textComponent.getStyle().withClickEvent(null));
        for (Text lv2 : textComponent.getSiblings()) {
            lv.append(Entity.removeClickEvents(lv2));
        }
        return lv;
    }

    @Override
    public Text getName() {
        Text lv = this.getCustomName();
        if (lv != null) {
            return Entity.removeClickEvents(lv);
        }
        return this.getDefaultName();
    }

    protected Text getDefaultName() {
        return this.type.getName();
    }

    public boolean isPartOf(Entity entity) {
        return this == entity;
    }

    public float getHeadYaw() {
        return 0.0f;
    }

    public void setHeadYaw(float headYaw) {
    }

    public void setYaw(float yaw) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean handleAttack(Entity attacker) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.entityId, this.world == null ? "~NULL~" : this.world.toString(), this.getX(), this.getY(), this.getZ());
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void copyPositionAndRotation(Entity entity) {
        this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
    }

    public void copyFrom(Entity original) {
        CompoundTag lv = original.toTag(new CompoundTag());
        lv.remove("Dimension");
        this.fromTag(lv);
        this.netherPortalCooldown = original.netherPortalCooldown;
        this.lastNetherPortalPosition = original.lastNetherPortalPosition;
    }

    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        if (!(this.world instanceof ServerWorld) || this.removed) {
            return null;
        }
        this.world.getProfiler().push("changeDimension");
        this.detach();
        this.world.getProfiler().push("reposition");
        TeleportTarget lv = this.getTeleportTarget(destination);
        if (lv == null) {
            return null;
        }
        this.world.getProfiler().swap("reloading");
        Object lv2 = this.getType().create(destination);
        if (lv2 != null) {
            ((Entity)lv2).copyFrom(this);
            ((Entity)lv2).refreshPositionAndAngles(lv.position.x, lv.position.y, lv.position.z, lv.yaw, ((Entity)lv2).pitch);
            ((Entity)lv2).setVelocity(lv.velocity);
            destination.onDimensionChanged((Entity)lv2);
            if (destination.getRegistryKey() == World.END) {
                ServerWorld.createEndSpawnPlatform(destination);
            }
        }
        this.method_30076();
        this.world.getProfiler().pop();
        ((ServerWorld)this.world).resetIdleTimeout();
        destination.resetIdleTimeout();
        this.world.getProfiler().pop();
        return lv2;
    }

    protected void method_30076() {
        this.removed = true;
    }

    @Nullable
    protected TeleportTarget getTeleportTarget(ServerWorld destination) {
        boolean bl3;
        boolean bl2;
        boolean bl = this.world.getRegistryKey() == World.END && destination.getRegistryKey() == World.OVERWORLD;
        boolean bl4 = bl2 = destination.getRegistryKey() == World.END;
        if (bl || bl2) {
            BlockPos lv2;
            if (bl2) {
                BlockPos lv = ServerWorld.END_SPAWN_POS;
            } else {
                lv2 = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
            }
            return new TeleportTarget(new Vec3d((double)lv2.getX() + 0.5, lv2.getY(), (double)lv2.getZ() + 0.5), this.getVelocity(), this.yaw, this.pitch);
        }
        boolean bl5 = bl3 = destination.getRegistryKey() == World.NETHER;
        if (this.world.getRegistryKey() != World.NETHER && !bl3) {
            return null;
        }
        WorldBorder lv3 = destination.getWorldBorder();
        double d = Math.max(-2.9999872E7, lv3.getBoundWest() + 16.0);
        double e = Math.max(-2.9999872E7, lv3.getBoundNorth() + 16.0);
        double f = Math.min(2.9999872E7, lv3.getBoundEast() - 16.0);
        double g = Math.min(2.9999872E7, lv3.getBoundSouth() - 16.0);
        double h = Entity.getTeleportationScale(this.world.getDimension(), destination.getDimension());
        BlockPos lv4 = new BlockPos(MathHelper.clamp(this.getX() * h, d, f), this.getY(), MathHelper.clamp(this.getZ() * h, e, g));
        return this.method_30330(destination, lv4, bl3).map(arg22 -> {
            Vec3d lv6;
            Direction.Axis lv5;
            BlockState lv = this.world.getBlockState(this.lastNetherPortalPosition);
            if (lv.contains(Properties.HORIZONTAL_AXIS)) {
                Direction.Axis lv2 = lv.get(Properties.HORIZONTAL_AXIS);
                class_5459.class_5460 lv3 = class_5459.method_30574(this.lastNetherPortalPosition, lv2, 21, Direction.Axis.Y, 21, arg2 -> this.world.getBlockState((BlockPos)arg2) == lv);
                Vec3d lv4 = this.method_30633(lv2, lv3);
            } else {
                lv5 = Direction.Axis.X;
                lv6 = new Vec3d(0.5, 0.0, 0.0);
            }
            return AreaHelper.method_30484(destination, arg22, lv5, lv6, this.getDimensions(this.getPose()), this.getVelocity(), this.yaw, this.pitch);
        }).orElse(null);
    }

    protected Vec3d method_30633(Direction.Axis arg, class_5459.class_5460 arg2) {
        return AreaHelper.method_30494(arg2, arg, this.getPos(), this.getDimensions(this.getPose()));
    }

    protected Optional<class_5459.class_5460> method_30330(ServerWorld arg, BlockPos arg2, boolean bl) {
        return arg.getPortalForcer().method_30483(arg2, bl);
    }

    private static double getTeleportationScale(DimensionType origin, DimensionType destination) {
        boolean bl = origin.isShrunk();
        boolean bl2 = destination.isShrunk();
        if (!bl && bl2) {
            return 0.125;
        }
        if (bl && !bl2) {
            return 8.0;
        }
        return 1.0;
    }

    public boolean canUsePortals() {
        return true;
    }

    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return max;
    }

    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        return true;
    }

    public int getSafeFallDistance() {
        return 3;
    }

    public boolean canAvoidTraps() {
        return false;
    }

    public void populateCrashReport(CrashReportSection section) {
        section.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        section.add("Entity ID", this.entityId);
        section.add("Entity Name", () -> this.getName().getString());
        section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        section.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
        Vec3d lv = this.getVelocity();
        section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", lv.x, lv.y, lv.z));
        section.add("Entity's Passengers", () -> this.getPassengerList().toString());
        section.add("Entity's Vehicle", () -> this.getVehicle().toString());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        this.uuidString = this.uuid.toString();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getUuidAsString() {
        return this.uuidString;
    }

    public String getEntityName() {
        return this.uuidString;
    }

    public boolean canFly() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static double getRenderDistanceMultiplier() {
        return renderDistanceMultiplier;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setRenderDistanceMultiplier(double value) {
        renderDistanceMultiplier = value;
    }

    @Override
    public Text getDisplayName() {
        return Team.modifyText(this.getScoreboardTeam(), this.getName()).styled(arg -> arg.withHoverEvent(this.getHoverEvent()).withInsertion(this.getUuidAsString()));
    }

    public void setCustomName(@Nullable Text name) {
        this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(name));
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.dataTracker.get(CUSTOM_NAME).orElse(null);
    }

    @Override
    public boolean hasCustomName() {
        return this.dataTracker.get(CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean visible) {
        this.dataTracker.set(NAME_VISIBLE, visible);
    }

    public boolean isCustomNameVisible() {
        return this.dataTracker.get(NAME_VISIBLE);
    }

    public final void teleport(double destX, double destY, double destZ) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ChunkPos lv = new ChunkPos(new BlockPos(destX, destY, destZ));
        ((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.field_19347, lv, 0, this.getEntityId());
        this.world.getChunk(lv.x, lv.z);
        this.requestTeleport(destX, destY, destZ);
    }

    public void requestTeleport(double destX, double destY, double destZ) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld lv = (ServerWorld)this.world;
        this.refreshPositionAndAngles(destX, destY, destZ, this.yaw, this.pitch);
        this.streamPassengersRecursively().forEach(arg2 -> {
            lv.checkEntityChunkPos((Entity)arg2);
            arg2.teleportRequested = true;
            for (Entity lv : arg2.passengerList) {
                arg2.updatePassengerPosition(lv, Entity::refreshPositionAfterTeleport);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            this.calculateDimensions();
        }
    }

    public void calculateDimensions() {
        EntityDimensions lv3;
        EntityDimensions lv = this.dimensions;
        EntityPose lv2 = this.getPose();
        this.dimensions = lv3 = this.getDimensions(lv2);
        this.standingEyeHeight = this.getEyeHeight(lv2, lv3);
        if (lv3.width < lv.width) {
            double d = (double)lv3.width / 2.0;
            this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + (double)lv3.height, this.getZ() + d));
            return;
        }
        Box lv4 = this.getBoundingBox();
        this.setBoundingBox(new Box(lv4.minX, lv4.minY, lv4.minZ, lv4.minX + (double)lv3.width, lv4.minY + (double)lv3.height, lv4.minZ + (double)lv3.width));
        if (lv3.width > lv.width && !this.firstUpdate && !this.world.isClient) {
            float f = lv.width - lv3.width;
            this.move(MovementType.SELF, new Vec3d(f, 0.0, f));
        }
    }

    public Direction getHorizontalFacing() {
        return Direction.fromRotation(this.yaw);
    }

    public Direction getMovementDirection() {
        return this.getHorizontalFacing();
    }

    protected HoverEvent getHoverEvent() {
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(this.getType(), this.getUuid(), this.getName()));
    }

    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        return true;
    }

    public Box getBoundingBox() {
        return this.entityBounds;
    }

    @Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }

    protected Box calculateBoundsForPose(EntityPose pos) {
        EntityDimensions lv = this.getDimensions(pos);
        float f = lv.width / 2.0f;
        Vec3d lv2 = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
        Vec3d lv3 = new Vec3d(this.getX() + (double)f, this.getY() + (double)lv.height, this.getZ() + (double)f);
        return new Box(lv2, lv3);
    }

    public void setBoundingBox(Box boundingBox) {
        this.entityBounds = boundingBox;
    }

    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.85f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEyeHeight(EntityPose pose) {
        return this.getEyeHeight(pose, this.getDimensions(pose));
    }

    public final float getStandingEyeHeight() {
        return this.standingEyeHeight;
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    public boolean equip(int slot, ItemStack item) {
        return false;
    }

    @Override
    public void sendSystemMessage(Text message, UUID senderUuid) {
    }

    public World getEntityWorld() {
        return this.world;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        return ActionResult.PASS;
    }

    public boolean isImmuneToExplosion() {
        return false;
    }

    public void dealDamage(LivingEntity attacker, Entity target) {
        if (target instanceof LivingEntity) {
            EnchantmentHelper.onUserDamaged((LivingEntity)target, attacker);
        }
        EnchantmentHelper.onTargetDamaged(attacker, target);
    }

    public void onStartedTrackingBy(ServerPlayerEntity player) {
    }

    public void onStoppedTrackingBy(ServerPlayerEntity player) {
    }

    public float applyRotation(BlockRotation rotation) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 270.0f;
            }
            case CLOCKWISE_90: {
                return f + 90.0f;
            }
        }
        return f;
    }

    public float applyMirror(BlockMirror mirror) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (mirror) {
            case LEFT_RIGHT: {
                return -f;
            }
            case FRONT_BACK: {
                return 180.0f - f;
            }
        }
        return f;
    }

    public boolean entityDataRequiresOperator() {
        return false;
    }

    public boolean teleportRequested() {
        boolean bl = this.teleportRequested;
        this.teleportRequested = false;
        return bl;
    }

    public boolean isChunkPosUpdateRequested() {
        boolean bl = this.chunkPosUpdateRequested;
        this.chunkPosUpdateRequested = false;
        return bl;
    }

    @Nullable
    public Entity getPrimaryPassenger() {
        return null;
    }

    public List<Entity> getPassengerList() {
        if (this.passengerList.isEmpty()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(this.passengerList);
    }

    public boolean hasPassenger(Entity passenger) {
        for (Entity lv : this.getPassengerList()) {
            if (!lv.equals(passenger)) continue;
            return true;
        }
        return false;
    }

    public boolean hasPassengerType(Class<? extends Entity> clazz) {
        for (Entity lv : this.getPassengerList()) {
            if (!clazz.isAssignableFrom(lv.getClass())) continue;
            return true;
        }
        return false;
    }

    public Collection<Entity> getPassengersDeep() {
        HashSet set = Sets.newHashSet();
        for (Entity lv : this.getPassengerList()) {
            set.add(lv);
            lv.collectPassengers(false, set);
        }
        return set;
    }

    public Stream<Entity> streamPassengersRecursively() {
        return Stream.concat(Stream.of(this), this.passengerList.stream().flatMap(Entity::streamPassengersRecursively));
    }

    public boolean hasPlayerRider() {
        HashSet set = Sets.newHashSet();
        this.collectPassengers(true, set);
        return set.size() == 1;
    }

    private void collectPassengers(boolean playersOnly, Set<Entity> output) {
        for (Entity lv : this.getPassengerList()) {
            if (!playersOnly || ServerPlayerEntity.class.isAssignableFrom(lv.getClass())) {
                output.add(lv);
            }
            lv.collectPassengers(playersOnly, output);
        }
    }

    public Entity getRootVehicle() {
        Entity lv = this;
        while (lv.hasVehicle()) {
            lv = lv.getVehicle();
        }
        return lv;
    }

    public boolean isConnectedThroughVehicle(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasPassengerDeep(Entity passenger) {
        for (Entity lv : this.getPassengerList()) {
            if (lv.equals(passenger)) {
                return true;
            }
            if (!lv.hasPassengerDeep(passenger)) continue;
            return true;
        }
        return false;
    }

    public boolean isLogicalSideForUpdatingMovement() {
        Entity lv = this.getPrimaryPassenger();
        if (lv instanceof PlayerEntity) {
            return ((PlayerEntity)lv).isMainPlayer();
        }
        return !this.world.isClient;
    }

    protected static Vec3d getPassengerDismountOffset(double vehicleWidth, double passengerWidth, float passengerYaw) {
        double g = (vehicleWidth + passengerWidth + (double)1.0E-5f) / 2.0;
        float h = -MathHelper.sin(passengerYaw * ((float)Math.PI / 180));
        float i = MathHelper.cos(passengerYaw * ((float)Math.PI / 180));
        float j = Math.max(Math.abs(h), Math.abs(i));
        return new Vec3d((double)h * g / (double)j, 0.0, (double)i * g / (double)j);
    }

    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.NORMAL;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    protected int getBurningDuration() {
        return 1;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this, this.getPos(), this.getRotationClient(), this.world instanceof ServerWorld ? (ServerWorld)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
    }

    protected int getPermissionLevel() {
        return 0;
    }

    public boolean hasPermissionLevel(int permissionLevel) {
        return this.getPermissionLevel() >= permissionLevel;
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return true;
    }

    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        Vec3d lv = anchorPoint.positionAt(this);
        double d = target.x - lv.x;
        double e = target.y - lv.y;
        double f = target.z - lv.z;
        double g = MathHelper.sqrt(d * d + f * f);
        this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        this.setHeadYaw(this.yaw);
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
    }

    public boolean updateMovementInFluid(Tag<Fluid> tag, double d) {
        int n;
        Box lv = this.getBoundingBox().contract(0.001);
        int i = MathHelper.floor(lv.minX);
        int j = MathHelper.ceil(lv.maxX);
        int k = MathHelper.floor(lv.minY);
        int l = MathHelper.ceil(lv.maxY);
        int m = MathHelper.floor(lv.minZ);
        if (!this.world.isRegionLoaded(i, k, m, j, l, n = MathHelper.ceil(lv.maxZ))) {
            return false;
        }
        double e = 0.0;
        boolean bl = this.canFly();
        boolean bl2 = false;
        Vec3d lv2 = Vec3d.ZERO;
        int o = 0;
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int p = i; p < j; ++p) {
            for (int q = k; q < l; ++q) {
                for (int r = m; r < n; ++r) {
                    double f;
                    lv3.set(p, q, r);
                    FluidState lv4 = this.world.getFluidState(lv3);
                    if (!lv4.isIn(tag) || !((f = (double)((float)q + lv4.getHeight(this.world, lv3))) >= lv.minY)) continue;
                    bl2 = true;
                    e = Math.max(f - lv.minY, e);
                    if (!bl) continue;
                    Vec3d lv5 = lv4.getVelocity(this.world, lv3);
                    if (e < 0.4) {
                        lv5 = lv5.multiply(e);
                    }
                    lv2 = lv2.add(lv5);
                    ++o;
                }
            }
        }
        if (lv2.length() > 0.0) {
            if (o > 0) {
                lv2 = lv2.multiply(1.0 / (double)o);
            }
            if (!(this instanceof PlayerEntity)) {
                lv2 = lv2.normalize();
            }
            Vec3d lv6 = this.getVelocity();
            lv2 = lv2.multiply(d * 1.0);
            double g = 0.003;
            if (Math.abs(lv6.x) < 0.003 && Math.abs(lv6.z) < 0.003 && lv2.length() < 0.0045000000000000005) {
                lv2 = lv2.normalize().multiply(0.0045000000000000005);
            }
            this.setVelocity(this.getVelocity().add(lv2));
        }
        this.fluidHeight.put(tag, e);
        return bl2;
    }

    public double getFluidHeight(Tag<Fluid> fluid) {
        return this.fluidHeight.getDouble(fluid);
    }

    public double method_29241() {
        return (double)this.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
    }

    public final float getWidth() {
        return this.dimensions.width;
    }

    public final float getHeight() {
        return this.dimensions.height;
    }

    public abstract Packet<?> createSpawnPacket();

    public EntityDimensions getDimensions(EntityPose pose) {
        return this.type.getDimensions();
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Vec3d getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(double x, double y, double z) {
        this.setVelocity(new Vec3d(x, y, z));
    }

    public final double getX() {
        return this.pos.x;
    }

    public double offsetX(double widthScale) {
        return this.pos.x + (double)this.getWidth() * widthScale;
    }

    public double getParticleX(double widthScale) {
        return this.offsetX((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public final double getY() {
        return this.pos.y;
    }

    public double getBodyY(double heightScale) {
        return this.pos.y + (double)this.getHeight() * heightScale;
    }

    public double getRandomBodyY() {
        return this.getBodyY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.pos.y + (double)this.standingEyeHeight;
    }

    public final double getZ() {
        return this.pos.z;
    }

    public double offsetZ(double widthScale) {
        return this.pos.z + (double)this.getWidth() * widthScale;
    }

    public double getParticleZ(double widthScale) {
        return this.offsetZ((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public void setPos(double x, double y, double z) {
        if (this.pos.x != x || this.pos.y != y || this.pos.z != z) {
            this.pos = new Vec3d(x, y, z);
            int i = MathHelper.floor(x);
            int j = MathHelper.floor(y);
            int k = MathHelper.floor(z);
            if (i != this.blockPos.getX() || j != this.blockPos.getY() || k != this.blockPos.getZ()) {
                this.blockPos = new BlockPos(i, j, k);
            }
            this.chunkPosUpdateRequested = true;
        }
    }

    public void checkDespawn() {
    }

    @FunctionalInterface
    public static interface PositionUpdater {
        public void accept(Entity var1, double var2, double var4, double var6);
    }
}

