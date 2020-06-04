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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
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
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
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
    protected boolean inLava;
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
    private boolean field_25154;
    public long trackedX;
    public long trackedY;
    public long trackedZ;
    public boolean ignoreCameraFrustum;
    public boolean velocityDirty;
    public int netherPortalCooldown;
    protected boolean inNetherPortal;
    protected int netherPortalTime;
    protected BlockPos lastNetherPortalPosition;
    protected Vec3d lastNetherPortalDirectionVector;
    protected Direction lastNetherPortalDirection;
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

    public Entity(EntityType<?> arg, World arg2) {
        this.type = arg;
        this.world = arg2;
        this.dimensions = arg.getDimensions();
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
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

    public void updateTrackedPosition(double d, double e, double f) {
        this.trackedX = EntityS2CPacket.encodePacketCoordinate(d);
        this.trackedY = EntityS2CPacket.encodePacketCoordinate(e);
        this.trackedZ = EntityS2CPacket.encodePacketCoordinate(f);
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int i) {
        this.entityId = i;
    }

    public Set<String> getScoreboardTags() {
        return this.scoreboardTags;
    }

    public boolean addScoreboardTag(String string) {
        if (this.scoreboardTags.size() >= 1024) {
            return false;
        }
        return this.scoreboardTags.add(string);
    }

    public boolean removeScoreboardTag(String string) {
        return this.scoreboardTags.remove(string);
    }

    public void kill() {
        this.remove();
    }

    protected abstract void initDataTracker();

    public DataTracker getDataTracker() {
        return this.dataTracker;
    }

    public boolean equals(Object object) {
        if (object instanceof Entity) {
            return ((Entity)object).entityId == this.entityId;
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

    public void setPose(EntityPose arg) {
        this.dataTracker.set(POSE, arg);
    }

    public EntityPose getPose() {
        return this.dataTracker.get(POSE);
    }

    public boolean isInRange(Entity arg, double d) {
        double e = arg.pos.x - this.pos.x;
        double f = arg.pos.y - this.pos.y;
        double g = arg.pos.z - this.pos.z;
        return e * e + f * f + g * g < d * d;
    }

    protected void setRotation(float f, float g) {
        this.yaw = f % 360.0f;
        this.pitch = g % 360.0f;
    }

    public void updatePosition(double d, double e, double f) {
        this.setPos(d, e, f);
        float g = this.dimensions.width / 2.0f;
        float h = this.dimensions.height;
        this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
    }

    protected void refreshPosition() {
        this.updatePosition(this.pos.x, this.pos.y, this.pos.z);
    }

    @Environment(value=EnvType.CLIENT)
    public void changeLookDirection(double d, double e) {
        double f = e * 0.15;
        double g = d * 0.15;
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
                this.fireTicks -= 4;
                if (this.fireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damage(DamageSource.ON_FIRE, 1.0f);
                }
                --this.fireTicks;
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

    protected void tickNetherPortalCooldown() {
        if (this.netherPortalCooldown > 0) {
            --this.netherPortalCooldown;
        }
    }

    public int getMaxNetherPortalTime() {
        return 1;
    }

    protected void setOnFireFromLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15);
        this.damage(DamageSource.LAVA, 4.0f);
    }

    public void setOnFireFor(int i) {
        int j = i * 20;
        if (this instanceof LivingEntity) {
            j = ProtectionEnchantment.transformFireDuration((LivingEntity)this, j);
        }
        if (this.fireTicks < j) {
            this.fireTicks = j;
        }
    }

    public void setFireTicks(int i) {
        this.fireTicks = i;
    }

    public int getFireTicks() {
        return this.fireTicks;
    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void destroy() {
        this.remove();
    }

    public boolean doesNotCollide(double d, double e, double f) {
        return this.doesNotCollide(this.getBoundingBox().offset(d, e, f));
    }

    private boolean doesNotCollide(Box arg) {
        return this.world.doesNotCollide(this, arg) && !this.world.containsFluid(arg);
    }

    public void setOnGround(boolean bl) {
        this.onGround = bl;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(MovementType arg, Vec3d arg2) {
        Vec3d lv;
        if (this.noClip) {
            this.setBoundingBox(this.getBoundingBox().offset(arg2));
            this.moveToBoundingBoxCenter();
            return;
        }
        if (arg == MovementType.PISTON && (arg2 = this.adjustMovementForPiston(arg2)).equals(Vec3d.ZERO)) {
            return;
        }
        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            arg2 = arg2.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }
        if ((lv = this.adjustMovementForCollisions(arg2 = this.adjustMovementForSneaking(arg2, arg))).lengthSquared() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().offset(lv));
            this.moveToBoundingBoxCenter();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rest");
        this.horizontalCollision = !MathHelper.approximatelyEquals(arg2.x, lv.x) || !MathHelper.approximatelyEquals(arg2.z, lv.z);
        this.verticalCollision = arg2.y != lv.y;
        this.onGround = this.verticalCollision && arg2.y < 0.0;
        BlockPos lv2 = this.getLandingPos();
        BlockState lv3 = this.world.getBlockState(lv2);
        this.fall(lv.y, this.onGround, lv3, lv2);
        Vec3d lv4 = this.getVelocity();
        if (arg2.x != lv.x) {
            this.setVelocity(0.0, lv4.y, lv4.z);
        }
        if (arg2.z != lv.z) {
            this.setVelocity(lv4.x, lv4.y, 0.0);
        }
        Block lv5 = lv3.getBlock();
        if (arg2.y != lv.y) {
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
            this.inLava = false;
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
        if (!this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001)) && this.fireTicks <= 0) {
            this.fireTicks = -this.getBurningDuration();
        }
        if (this.isWet() && this.isOnFire()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.fireTicks = -this.getBurningDuration();
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

    protected Vec3d adjustMovementForSneaking(Vec3d arg, MovementType arg2) {
        return arg;
    }

    protected Vec3d adjustMovementForPiston(Vec3d arg) {
        if (arg.lengthSquared() <= 1.0E-7) {
            return arg;
        }
        long l = this.world.getTime();
        if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
        }
        if (arg.x != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.X, arg.x);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
        }
        if (arg.y != 0.0) {
            double e = this.calculatePistonMovementFactor(Direction.Axis.Y, arg.y);
            return Math.abs(e) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, e, 0.0);
        }
        if (arg.z != 0.0) {
            double f = this.calculatePistonMovementFactor(Direction.Axis.Z, arg.z);
            return Math.abs(f) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, 0.0, f);
        }
        return Vec3d.ZERO;
    }

    private double calculatePistonMovementFactor(Direction.Axis arg, double d) {
        int i = arg.ordinal();
        double e = MathHelper.clamp(d + this.pistonMovementDelta[i], -0.51, 0.51);
        d = e - this.pistonMovementDelta[i];
        this.pistonMovementDelta[i] = e;
        return d;
    }

    private Vec3d adjustMovementForCollisions(Vec3d arg2) {
        boolean bl4;
        Box lv = this.getBoundingBox();
        ShapeContext lv2 = ShapeContext.of(this);
        VoxelShape lv3 = this.world.getWorldBorder().asVoxelShape();
        Stream<Object> stream = VoxelShapes.matchesAnywhere(lv3, VoxelShapes.cuboid(lv.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(lv3);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, lv.stretch(arg2), arg -> true);
        ReusableStream<VoxelShape> lv4 = new ReusableStream<VoxelShape>(Stream.concat(stream2, stream));
        Vec3d lv5 = arg2.lengthSquared() == 0.0 ? arg2 : Entity.adjustMovementForCollisions(this, arg2, lv, this.world, lv2, lv4);
        boolean bl = arg2.x != lv5.x;
        boolean bl2 = arg2.y != lv5.y;
        boolean bl3 = arg2.z != lv5.z;
        boolean bl5 = bl4 = this.onGround || bl2 && arg2.y < 0.0;
        if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
            Vec3d lv8;
            Vec3d lv6 = Entity.adjustMovementForCollisions(this, new Vec3d(arg2.x, this.stepHeight, arg2.z), lv, this.world, lv2, lv4);
            Vec3d lv7 = Entity.adjustMovementForCollisions(this, new Vec3d(0.0, this.stepHeight, 0.0), lv.stretch(arg2.x, 0.0, arg2.z), this.world, lv2, lv4);
            if (lv7.y < (double)this.stepHeight && Entity.squaredHorizontalLength(lv8 = Entity.adjustMovementForCollisions(this, new Vec3d(arg2.x, 0.0, arg2.z), lv.offset(lv7), this.world, lv2, lv4).add(lv7)) > Entity.squaredHorizontalLength(lv6)) {
                lv6 = lv8;
            }
            if (Entity.squaredHorizontalLength(lv6) > Entity.squaredHorizontalLength(lv5)) {
                return lv6.add(Entity.adjustMovementForCollisions(this, new Vec3d(0.0, -lv6.y + arg2.y, 0.0), lv.offset(lv6), this.world, lv2, lv4));
            }
        }
        return lv5;
    }

    public static double squaredHorizontalLength(Vec3d arg) {
        return arg.x * arg.x + arg.z * arg.z;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity arg, Vec3d arg2, Box arg3, World arg4, ShapeContext arg5, ReusableStream<VoxelShape> arg6) {
        boolean bl3;
        boolean bl = arg2.x == 0.0;
        boolean bl2 = arg2.y == 0.0;
        boolean bl4 = bl3 = arg2.z == 0.0;
        if (bl && bl2 || bl && bl3 || bl2 && bl3) {
            return Entity.adjustSingleAxisMovementForCollisions(arg2, arg3, arg4, arg5, arg6);
        }
        ReusableStream<VoxelShape> lv = new ReusableStream<VoxelShape>(Stream.concat(arg6.stream(), arg4.getBlockCollisions(arg, arg3.stretch(arg2))));
        return Entity.adjustMovementForCollisions(arg2, arg3, lv);
    }

    public static Vec3d adjustMovementForCollisions(Vec3d arg, Box arg2, ReusableStream<VoxelShape> arg3) {
        boolean bl;
        double d = arg.x;
        double e = arg.y;
        double f = arg.z;
        if (e != 0.0 && (e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, arg2, arg3.stream(), e)) != 0.0) {
            arg2 = arg2.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, arg2, arg3.stream(), f)) != 0.0) {
            arg2 = arg2.offset(0.0, 0.0, f);
        }
        if (d != 0.0) {
            d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, arg2, arg3.stream(), d);
            if (!bl && d != 0.0) {
                arg2 = arg2.offset(d, 0.0, 0.0);
            }
        }
        if (!bl && f != 0.0) {
            f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, arg2, arg3.stream(), f);
        }
        return new Vec3d(d, e, f);
    }

    public static Vec3d adjustSingleAxisMovementForCollisions(Vec3d arg, Box arg2, WorldView arg3, ShapeContext arg4, ReusableStream<VoxelShape> arg5) {
        boolean bl;
        double d = arg.x;
        double e = arg.y;
        double f = arg.z;
        if (e != 0.0 && (e = VoxelShapes.calculatePushVelocity(Direction.Axis.Y, arg2, arg3, e, arg4, arg5.stream())) != 0.0) {
            arg2 = arg2.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, arg2, arg3, f, arg4, arg5.stream())) != 0.0) {
            arg2 = arg2.offset(0.0, 0.0, f);
        }
        if (d != 0.0) {
            d = VoxelShapes.calculatePushVelocity(Direction.Axis.X, arg2, arg3, d, arg4, arg5.stream());
            if (!bl && d != 0.0) {
                arg2 = arg2.offset(d, 0.0, 0.0);
            }
        }
        if (!bl && f != 0.0) {
            f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, arg2, arg3, f, arg4, arg5.stream());
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

    protected void onBlockCollision(BlockState arg) {
    }

    protected void playStepSound(BlockPos arg, BlockState arg2) {
        if (arg2.getMaterial().isLiquid()) {
            return;
        }
        BlockState lv = this.world.getBlockState(arg.up());
        BlockSoundGroup lv2 = lv.isOf(Blocks.SNOW) ? lv.getSoundGroup() : arg2.getSoundGroup();
        this.playSound(lv2.getStepSound(), lv2.getVolume() * 0.15f, lv2.getPitch());
    }

    protected void playSwimSound(float f) {
        this.playSound(this.getSwimSound(), f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected float playFlySound(float f) {
        return 0.0f;
    }

    protected boolean hasWings() {
        return false;
    }

    public void playSound(SoundEvent arg, float f, float g) {
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), arg, this.getSoundCategory(), f, g);
        }
    }

    public boolean isSilent() {
        return this.dataTracker.get(SILENT);
    }

    public void setSilent(boolean bl) {
        this.dataTracker.set(SILENT, bl);
    }

    public boolean hasNoGravity() {
        return this.dataTracker.get(NO_GRAVITY);
    }

    public void setNoGravity(boolean bl) {
        this.dataTracker.set(NO_GRAVITY, bl);
    }

    protected boolean canClimb() {
        return true;
    }

    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
        if (bl) {
            if (this.fallDistance > 0.0f) {
                arg.getBlock().onLandedUpon(this.world, arg2, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        } else if (d < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - d);
        }
    }

    @Nullable
    public Box getCollisionBox() {
        return null;
    }

    public boolean isFireImmune() {
        return this.getType().isFireImmune();
    }

    public boolean handleFallDamage(float f, float g) {
        if (this.hasPassengers()) {
            for (Entity lv : this.getPassengerList()) {
                lv.handleFallDamage(f, g);
            }
        }
        return false;
    }

    public boolean isTouchingWater() {
        return this.touchingWater;
    }

    private boolean isBeingRainedOn() {
        BlockPos lv = this.getBlockPos();
        return this.world.hasRain(lv) || this.world.hasRain(lv.add(0.0, this.dimensions.height, 0.0));
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
        if (this.isTouchingWater()) {
            return true;
        }
        double d = this.world.getDimension().hasCeiling() ? 0.007 : 0.0023333333333333335;
        return this.updateMovementInFluid(FluidTags.LAVA, d);
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
        this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
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
            float j = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            float k = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + (double)j, h + 1.0f, this.getZ() + (double)k, lv2.x, lv2.y - (double)(this.random.nextFloat() * 0.2f), lv2.z);
            ++i;
        }
        int l = 0;
        while ((float)l < 1.0f + this.dimensions.width * 20.0f) {
            float m = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            float n = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double)m, h + 1.0f, this.getZ() + (double)n, lv2.x, lv2.y, lv2.z);
            ++l;
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
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, lv2), this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width, this.getY() + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width, lv3.x * -4.0, 1.5, lv3.z * -4.0);
        }
    }

    public boolean isSubmergedIn(Tag<Fluid> arg) {
        return (double)this.getStandingEyeHeight() < this.getFluidHeight(arg);
    }

    public void setInLava() {
        this.inLava = true;
    }

    public boolean isInLava() {
        return this.inLava;
    }

    public void updateVelocity(float f, Vec3d arg) {
        Vec3d lv = Entity.movementInputToVelocity(arg, f, this.yaw);
        this.setVelocity(this.getVelocity().add(lv));
    }

    private static Vec3d movementInputToVelocity(Vec3d arg, float f, float g) {
        double d = arg.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d lv = (d > 1.0 ? arg.normalize() : arg).multiply(f);
        float h = MathHelper.sin(g * ((float)Math.PI / 180));
        float i = MathHelper.cos(g * ((float)Math.PI / 180));
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

    public void setWorld(World arg) {
        this.world = arg;
    }

    public void updatePositionAndAngles(double d, double e, double f, float g, float h) {
        double i = MathHelper.clamp(d, -3.0E7, 3.0E7);
        double j = MathHelper.clamp(f, -3.0E7, 3.0E7);
        this.prevX = i;
        this.prevY = e;
        this.prevZ = j;
        this.updatePosition(i, e, j);
        this.yaw = g % 360.0f;
        this.pitch = MathHelper.clamp(h, -90.0f, 90.0f) % 360.0f;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void positAfterTeleport(double d, double e, double f) {
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
    }

    public void refreshPositionAndAngles(BlockPos arg, float f, float g) {
        this.refreshPositionAndAngles((double)arg.getX() + 0.5, arg.getY(), (double)arg.getZ() + 0.5, f, g);
    }

    public void refreshPositionAndAngles(double d, double e, double f, float g, float h) {
        this.resetPosition(d, e, f);
        this.yaw = g;
        this.pitch = h;
        this.refreshPosition();
    }

    public void resetPosition(double d, double e, double f) {
        this.setPos(d, e, f);
        this.prevX = d;
        this.prevY = e;
        this.prevZ = f;
        this.lastRenderX = d;
        this.lastRenderY = e;
        this.lastRenderZ = f;
    }

    public float distanceTo(Entity arg) {
        float f = (float)(this.getX() - arg.getX());
        float g = (float)(this.getY() - arg.getY());
        float h = (float)(this.getZ() - arg.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public double squaredDistanceTo(double d, double e, double f) {
        double g = this.getX() - d;
        double h = this.getY() - e;
        double i = this.getZ() - f;
        return g * g + h * h + i * i;
    }

    public double squaredDistanceTo(Entity arg) {
        return this.squaredDistanceTo(arg.getPos());
    }

    public double squaredDistanceTo(Vec3d arg) {
        double d = this.getX() - arg.x;
        double e = this.getY() - arg.y;
        double f = this.getZ() - arg.z;
        return d * d + e * e + f * f;
    }

    public void onPlayerCollision(PlayerEntity arg) {
    }

    public void pushAwayFrom(Entity arg) {
        double e;
        if (this.isConnectedThroughVehicle(arg)) {
            return;
        }
        if (arg.noClip || this.noClip) {
            return;
        }
        double d = arg.getX() - this.getX();
        double f = MathHelper.absMax(d, e = arg.getZ() - this.getZ());
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
            if (!arg.hasPassengers()) {
                arg.addVelocity(d, 0.0, e);
            }
        }
    }

    public void addVelocity(double d, double e, double f) {
        this.setVelocity(this.getVelocity().add(d, e, f));
        this.velocityDirty = true;
    }

    protected void scheduleVelocityUpdate() {
        this.velocityModified = true;
    }

    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        return false;
    }

    public final Vec3d getRotationVec(float f) {
        return this.getRotationVector(this.getPitch(f), this.getYaw(f));
    }

    public float getPitch(float f) {
        if (f == 1.0f) {
            return this.pitch;
        }
        return MathHelper.lerp(f, this.prevPitch, this.pitch);
    }

    public float getYaw(float f) {
        if (f == 1.0f) {
            return this.yaw;
        }
        return MathHelper.lerp(f, this.prevYaw, this.yaw);
    }

    protected final Vec3d getRotationVector(float f, float g) {
        float h = f * ((float)Math.PI / 180);
        float i = -g * ((float)Math.PI / 180);
        float j = MathHelper.cos(i);
        float k = MathHelper.sin(i);
        float l = MathHelper.cos(h);
        float m = MathHelper.sin(h);
        return new Vec3d(k * l, -m, j * l);
    }

    public final Vec3d getOppositeRotationVector(float f) {
        return this.getOppositeRotationVector(this.getPitch(f), this.getYaw(f));
    }

    protected final Vec3d getOppositeRotationVector(float f, float g) {
        return this.getRotationVector(f - 90.0f, g);
    }

    public final Vec3d getCameraPosVec(float f) {
        if (f == 1.0f) {
            return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        }
        double d = MathHelper.lerp((double)f, this.prevX, this.getX());
        double e = MathHelper.lerp((double)f, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
        double g = MathHelper.lerp((double)f, this.prevZ, this.getZ());
        return new Vec3d(d, e, g);
    }

    public HitResult rayTrace(double d, float f, boolean bl) {
        Vec3d lv = this.getCameraPosVec(f);
        Vec3d lv2 = this.getRotationVec(f);
        Vec3d lv3 = lv.add(lv2.x * d, lv2.y * d, lv2.z * d);
        return this.world.rayTrace(new RayTraceContext(lv, lv3, RayTraceContext.ShapeType.OUTLINE, bl ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, this));
    }

    public boolean collides() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void updateKilledAdvancementCriterion(Entity arg, int i, DamageSource arg2) {
        if (arg instanceof ServerPlayerEntity) {
            Criteria.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)arg, this, arg2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d, double e, double f) {
        double g = this.getX() - d;
        double h = this.getY() - e;
        double i = this.getZ() - f;
        double j = g * g + h * h + i * i;
        return this.shouldRender(j);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return d < (e *= 64.0 * renderDistanceMultiplier) * e;
    }

    public boolean saveSelfToTag(CompoundTag arg) {
        String string = this.getSavedEntityId();
        if (this.removed || string == null) {
            return false;
        }
        arg.putString("id", string);
        this.toTag(arg);
        return true;
    }

    public boolean saveToTag(CompoundTag arg) {
        if (this.hasVehicle()) {
            return false;
        }
        return this.saveSelfToTag(arg);
    }

    public CompoundTag toTag(CompoundTag arg) {
        try {
            if (this.vehicle != null) {
                arg.put("Pos", this.toListTag(this.vehicle.getX(), this.vehicle.getY(), this.vehicle.getZ()));
            } else {
                arg.put("Pos", this.toListTag(this.getX(), this.getY(), this.getZ()));
            }
            Vec3d lv = this.getVelocity();
            arg.put("Motion", this.toListTag(lv.x, lv.y, lv.z));
            arg.put("Rotation", this.toListTag(this.yaw, this.pitch));
            arg.putFloat("FallDistance", this.fallDistance);
            arg.putShort("Fire", (short)this.fireTicks);
            arg.putShort("Air", (short)this.getAir());
            arg.putBoolean("OnGround", this.onGround);
            arg.putBoolean("Invulnerable", this.invulnerable);
            arg.putInt("PortalCooldown", this.netherPortalCooldown);
            arg.putUuid("UUID", this.getUuid());
            Text lv2 = this.getCustomName();
            if (lv2 != null) {
                arg.putString("CustomName", Text.Serializer.toJson(lv2));
            }
            if (this.isCustomNameVisible()) {
                arg.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                arg.putBoolean("Silent", this.isSilent());
            }
            if (this.hasNoGravity()) {
                arg.putBoolean("NoGravity", this.hasNoGravity());
            }
            if (this.glowing) {
                arg.putBoolean("Glowing", this.glowing);
            }
            if (!this.scoreboardTags.isEmpty()) {
                ListTag lv3 = new ListTag();
                for (String string : this.scoreboardTags) {
                    lv3.add(StringTag.of(string));
                }
                arg.put("Tags", lv3);
            }
            this.writeCustomDataToTag(arg);
            if (this.hasPassengers()) {
                ListTag lv4 = new ListTag();
                for (Entity lv5 : this.getPassengerList()) {
                    CompoundTag lv6;
                    if (!lv5.saveSelfToTag(lv6 = new CompoundTag())) continue;
                    lv4.add(lv6);
                }
                if (!lv4.isEmpty()) {
                    arg.put("Passengers", lv4);
                }
            }
        }
        catch (Throwable throwable) {
            CrashReport lv7 = CrashReport.create(throwable, "Saving entity NBT");
            CrashReportSection lv8 = lv7.addElement("Entity being saved");
            this.populateCrashReport(lv8);
            throw new CrashException(lv7);
        }
        return arg;
    }

    public void fromTag(CompoundTag arg) {
        try {
            ListTag lv = arg.getList("Pos", 6);
            ListTag lv2 = arg.getList("Motion", 6);
            ListTag lv3 = arg.getList("Rotation", 5);
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
            this.fallDistance = arg.getFloat("FallDistance");
            this.fireTicks = arg.getShort("Fire");
            this.setAir(arg.getShort("Air"));
            this.onGround = arg.getBoolean("OnGround");
            this.invulnerable = arg.getBoolean("Invulnerable");
            this.netherPortalCooldown = arg.getInt("PortalCooldown");
            if (arg.containsUuid("UUID")) {
                this.uuid = arg.getUuid("UUID");
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
            if (arg.contains("CustomName", 8)) {
                this.setCustomName(Text.Serializer.fromJson(arg.getString("CustomName")));
            }
            this.setCustomNameVisible(arg.getBoolean("CustomNameVisible"));
            this.setSilent(arg.getBoolean("Silent"));
            this.setNoGravity(arg.getBoolean("NoGravity"));
            this.setGlowing(arg.getBoolean("Glowing"));
            if (arg.contains("Tags", 9)) {
                this.scoreboardTags.clear();
                ListTag lv4 = arg.getList("Tags", 8);
                int i = Math.min(lv4.size(), 1024);
                for (int j = 0; j < i; ++j) {
                    this.scoreboardTags.add(lv4.getString(j));
                }
            }
            this.readCustomDataFromTag(arg);
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

    protected ListTag toListTag(double ... ds) {
        ListTag lv = new ListTag();
        for (double d : ds) {
            lv.add(DoubleTag.of(d));
        }
        return lv;
    }

    protected ListTag toListTag(float ... fs) {
        ListTag lv = new ListTag();
        for (float f : fs) {
            lv.add(FloatTag.of(f));
        }
        return lv;
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible arg) {
        return this.dropItem(arg, 0);
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible arg, int i) {
        return this.dropStack(new ItemStack(arg), i);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack arg) {
        return this.dropStack(arg, 0.0f);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack arg, float f) {
        if (arg.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        ItemEntity lv = new ItemEntity(this.world, this.getX(), this.getY() + (double)f, this.getZ(), arg);
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
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 8; ++i) {
            int j = MathHelper.floor(this.getY() + (double)(((float)((i >> 0) % 2) - 0.5f) * 0.1f) + (double)this.standingEyeHeight);
            int k = MathHelper.floor(this.getX() + (double)(((float)((i >> 1) % 2) - 0.5f) * this.dimensions.width * 0.8f));
            int l = MathHelper.floor(this.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * this.dimensions.width * 0.8f));
            if (lv.getX() == k && lv.getY() == j && lv.getZ() == l) continue;
            lv.set(k, j, l);
            if (!this.world.getBlockState(lv).shouldSuffocate(this.world, lv)) continue;
            return true;
        }
        return false;
    }

    public boolean interact(PlayerEntity arg, Hand arg2) {
        return false;
    }

    @Nullable
    public Box getHardCollisionBox(Entity arg) {
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

    public void updatePassengerPosition(Entity arg) {
        this.updatePassengerPosition(arg, Entity::updatePosition);
    }

    private void updatePassengerPosition(Entity arg, PositionUpdater arg2) {
        if (!this.hasPassenger(arg)) {
            return;
        }
        double d = this.getY() + this.getMountedHeightOffset() + arg.getHeightOffset();
        arg2.accept(arg, this.getX(), d, this.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    public void onPassengerLookAround(Entity arg) {
    }

    public double getHeightOffset() {
        return 0.0;
    }

    public double getMountedHeightOffset() {
        return (double)this.dimensions.height * 0.75;
    }

    public boolean startRiding(Entity arg) {
        return this.startRiding(arg, false);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLiving() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity arg, boolean bl) {
        Entity lv = arg;
        while (lv.vehicle != null) {
            if (lv.vehicle == this) {
                return false;
            }
            lv = lv.vehicle;
        }
        if (!(bl || this.canStartRiding(arg) && arg.canAddPassenger(this))) {
            return false;
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setPose(EntityPose.STANDING);
        this.vehicle = arg;
        this.vehicle.addPassenger(this);
        return true;
    }

    protected boolean canStartRiding(Entity arg) {
        return !this.isSneaking() && this.ridingCooldown <= 0;
    }

    protected boolean wouldPoseNotCollide(EntityPose arg) {
        return this.world.doesNotCollide(this, this.calculateBoundsForPose(arg).contract(1.0E-7));
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

    protected void addPassenger(Entity arg) {
        if (arg.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.world.isClient && arg instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
            this.passengerList.add(0, arg);
        } else {
            this.passengerList.add(arg);
        }
    }

    protected void removePassenger(Entity arg) {
        if (arg.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList.remove(arg);
        arg.ridingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity arg) {
        return this.getPassengerList().size() < 1;
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.updatePosition(d, e, f);
        this.setRotation(g, h);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float f, int i) {
        this.setHeadYaw(f);
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

    public void setInNetherPortal(BlockPos arg) {
        if (this.netherPortalCooldown > 0) {
            this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
            return;
        }
        if (!this.world.isClient && !arg.equals(this.lastNetherPortalPosition)) {
            this.lastNetherPortalPosition = new BlockPos(arg);
            NetherPortalBlock cfr_ignored_0 = (NetherPortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.Result lv = NetherPortalBlock.findPortal(this.world, this.lastNetherPortalPosition);
            double d = lv.getForwards().getAxis() == Direction.Axis.X ? (double)lv.getFrontTopLeft().getZ() : (double)lv.getFrontTopLeft().getX();
            double e = MathHelper.clamp(Math.abs(MathHelper.getLerpProgress((lv.getForwards().getAxis() == Direction.Axis.X ? this.getZ() : this.getX()) - (double)(lv.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)lv.getWidth())), 0.0, 1.0);
            double f = MathHelper.clamp(MathHelper.getLerpProgress(this.getY() - 1.0, lv.getFrontTopLeft().getY(), lv.getFrontTopLeft().getY() - lv.getHeight()), 0.0, 1.0);
            this.lastNetherPortalDirectionVector = new Vec3d(e, f, 0.0);
            this.lastNetherPortalDirection = lv.getForwards();
        }
        this.inNetherPortal = true;
    }

    protected void tickNetherPortal() {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        int i = this.getMaxNetherPortalTime();
        if (this.inNetherPortal) {
            if (this.world.getServer().isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
                this.world.getProfiler().push("portal");
                this.netherPortalTime = i;
                this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
                RegistryKey<World> lv = this.world.getDimension().isNether() ? World.OVERWORLD : World.NETHER;
                this.changeDimension(lv);
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
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
    }

    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        switch (b) {
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

    public void equipStack(EquipmentSlot arg, ItemStack arg2) {
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

    public void setSneaking(boolean bl) {
        this.setFlag(1, bl);
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

    public void setSprinting(boolean bl) {
        this.setFlag(3, bl);
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

    public void setSwimming(boolean bl) {
        this.setFlag(4, bl);
    }

    public boolean isGlowing() {
        return this.glowing || this.world.isClient && this.getFlag(6);
    }

    public void setGlowing(boolean bl) {
        this.glowing = bl;
        if (!this.world.isClient) {
            this.setFlag(6, this.glowing);
        }
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isInvisibleTo(PlayerEntity arg) {
        if (arg.isSpectator()) {
            return false;
        }
        AbstractTeam lv = this.getScoreboardTeam();
        if (lv != null && arg != null && arg.getScoreboardTeam() == lv && lv.shouldShowFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    @Nullable
    public AbstractTeam getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
    }

    public boolean isTeammate(Entity arg) {
        return this.isTeamPlayer(arg.getScoreboardTeam());
    }

    public boolean isTeamPlayer(AbstractTeam arg) {
        if (this.getScoreboardTeam() != null) {
            return this.getScoreboardTeam().isEqual(arg);
        }
        return false;
    }

    public void setInvisible(boolean bl) {
        this.setFlag(5, bl);
    }

    protected boolean getFlag(int i) {
        return (this.dataTracker.get(FLAGS) & 1 << i) != 0;
    }

    protected void setFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(FLAGS);
        if (bl) {
            this.dataTracker.set(FLAGS, (byte)(b | 1 << i));
        } else {
            this.dataTracker.set(FLAGS, (byte)(b & ~(1 << i)));
        }
    }

    public int getMaxAir() {
        return 300;
    }

    public int getAir() {
        return this.dataTracker.get(AIR);
    }

    public void setAir(int i) {
        this.dataTracker.set(AIR, i);
    }

    public void onStruckByLightning(LightningEntity arg) {
        ++this.fireTicks;
        if (this.fireTicks == 0) {
            this.setOnFireFor(8);
        }
        this.damage(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onBubbleColumnSurfaceCollision(boolean bl) {
        double e;
        Vec3d lv = this.getVelocity();
        if (bl) {
            double d = Math.max(-0.9, lv.y - 0.03);
        } else {
            e = Math.min(1.8, lv.y + 0.1);
        }
        this.setVelocity(lv.x, e, lv.z);
    }

    public void onBubbleColumnCollision(boolean bl) {
        double e;
        Vec3d lv = this.getVelocity();
        if (bl) {
            double d = Math.max(-0.3, lv.y - 0.03);
        } else {
            e = Math.min(0.7, lv.y + 0.06);
        }
        this.setVelocity(lv.x, e, lv.z);
        this.fallDistance = 0.0f;
    }

    public void onKilledOther(LivingEntity arg) {
    }

    protected void pushOutOfBlocks(double d, double e, double f) {
        BlockPos lv = new BlockPos(d, e, f);
        Vec3d lv2 = new Vec3d(d - (double)lv.getX(), e - (double)lv.getY(), f - (double)lv.getZ());
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        Direction lv4 = Direction.UP;
        double g = Double.MAX_VALUE;
        for (Direction lv5 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double i;
            lv3.set(lv, lv5);
            if (this.world.getBlockState(lv3).isFullCube(this.world, lv3)) continue;
            double h = lv2.getComponentAlongAxis(lv5.getAxis());
            double d2 = i = lv5.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
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

    public void slowMovement(BlockState arg, Vec3d arg2) {
        this.fallDistance = 0.0f;
        this.movementMultiplier = arg2;
    }

    private static Text removeClickEvents(Text arg2) {
        MutableText lv = arg2.copy().styled(arg -> arg.withClickEvent(null));
        for (Text lv2 : arg2.getSiblings()) {
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

    public boolean isPartOf(Entity arg) {
        return this == arg;
    }

    public float getHeadYaw() {
        return 0.0f;
    }

    public void setHeadYaw(float f) {
    }

    public void setYaw(float f) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean handleAttack(Entity arg) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.entityId, this.world == null ? "~NULL~" : this.world.toString(), this.getX(), this.getY(), this.getZ());
    }

    public boolean isInvulnerableTo(DamageSource arg) {
        return this.invulnerable && arg != DamageSource.OUT_OF_WORLD && !arg.isSourceCreativePlayer();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean bl) {
        this.invulnerable = bl;
    }

    public void copyPositionAndRotation(Entity arg) {
        this.refreshPositionAndAngles(arg.getX(), arg.getY(), arg.getZ(), arg.yaw, arg.pitch);
    }

    public void copyFrom(Entity arg) {
        CompoundTag lv = arg.toTag(new CompoundTag());
        lv.remove("Dimension");
        this.fromTag(lv);
        this.netherPortalCooldown = arg.netherPortalCooldown;
        this.lastNetherPortalPosition = arg.lastNetherPortalPosition;
        this.lastNetherPortalDirectionVector = arg.lastNetherPortalDirectionVector;
        this.lastNetherPortalDirection = arg.lastNetherPortalDirection;
    }

    @Nullable
    public Entity changeDimension(RegistryKey<World> arg) {
        BlockPos lv10;
        if (this.world.isClient || this.removed) {
            return null;
        }
        this.world.getProfiler().push("changeDimension");
        MinecraftServer minecraftServer = this.getServer();
        RegistryKey<World> lv = this.world.getRegistryKey();
        ServerWorld lv2 = minecraftServer.getWorld(lv);
        ServerWorld lv3 = minecraftServer.getWorld(arg);
        this.detach();
        this.world.getProfiler().push("reposition");
        Vec3d lv4 = this.getVelocity();
        float f = 0.0f;
        if (lv == World.END && arg == World.OVERWORLD) {
            BlockPos lv5 = lv3.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv3.getSpawnPos());
        } else if (arg == World.END) {
            BlockPos lv6 = ServerWorld.field_25144;
        } else {
            double d = this.getX();
            double e = this.getZ();
            DimensionType lv7 = lv2.getDimension();
            DimensionType lv8 = lv3.getDimension();
            double g = 8.0;
            if (!lv7.isShrunk() && lv8.isShrunk()) {
                d /= 8.0;
                e /= 8.0;
            } else if (lv7.isShrunk() && !lv8.isShrunk()) {
                d *= 8.0;
                e *= 8.0;
            }
            double h = Math.min(-2.9999872E7, lv3.getWorldBorder().getBoundWest() + 16.0);
            double i = Math.min(-2.9999872E7, lv3.getWorldBorder().getBoundNorth() + 16.0);
            double j = Math.min(2.9999872E7, lv3.getWorldBorder().getBoundEast() - 16.0);
            double k = Math.min(2.9999872E7, lv3.getWorldBorder().getBoundSouth() - 16.0);
            d = MathHelper.clamp(d, h, j);
            e = MathHelper.clamp(e, i, k);
            Vec3d lv9 = this.getLastNetherPortalDirectionVector();
            lv10 = new BlockPos(d, this.getY(), e);
            BlockPattern.TeleportTarget lv11 = lv3.getPortalForcer().getPortal(lv10, lv4, this.getLastNetherPortalDirection(), lv9.x, lv9.y, this instanceof PlayerEntity);
            if (lv11 == null) {
                return null;
            }
            lv10 = new BlockPos(lv11.pos);
            lv4 = lv11.velocity;
            f = lv11.yaw;
        }
        this.world.getProfiler().swap("reloading");
        Object lv12 = this.getType().create(lv3);
        if (lv12 != null) {
            ((Entity)lv12).copyFrom(this);
            ((Entity)lv12).refreshPositionAndAngles(lv10, ((Entity)lv12).yaw + f, ((Entity)lv12).pitch);
            ((Entity)lv12).setVelocity(lv4);
            lv3.onDimensionChanged((Entity)lv12);
            if (arg == World.END) {
                ServerWorld.method_29200(lv3);
            }
        }
        this.removed = true;
        this.world.getProfiler().pop();
        lv2.resetIdleTimeout();
        lv3.resetIdleTimeout();
        this.world.getProfiler().pop();
        return lv12;
    }

    public boolean canUsePortals() {
        return true;
    }

    public float getEffectiveExplosionResistance(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, FluidState arg5, float f) {
        return f;
    }

    public boolean canExplosionDestroyBlock(Explosion arg, BlockView arg2, BlockPos arg3, BlockState arg4, float f) {
        return true;
    }

    public int getSafeFallDistance() {
        return 3;
    }

    public Vec3d getLastNetherPortalDirectionVector() {
        return this.lastNetherPortalDirectionVector;
    }

    public Direction getLastNetherPortalDirection() {
        return this.lastNetherPortalDirection;
    }

    public boolean canAvoidTraps() {
        return false;
    }

    public void populateCrashReport(CrashReportSection arg) {
        arg.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        arg.add("Entity ID", this.entityId);
        arg.add("Entity Name", () -> this.getName().getString());
        arg.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        arg.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
        Vec3d lv = this.getVelocity();
        arg.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", lv.x, lv.y, lv.z));
        arg.add("Entity's Passengers", () -> this.getPassengerList().toString());
        arg.add("Entity's Vehicle", () -> this.getVehicle().toString());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUuid(UUID uUID) {
        this.uuid = uUID;
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
    public static void setRenderDistanceMultiplier(double d) {
        renderDistanceMultiplier = d;
    }

    @Override
    public Text getDisplayName() {
        return Team.modifyText(this.getScoreboardTeam(), this.getName()).styled(arg -> arg.setHoverEvent(this.getHoverEvent()).withInsertion(this.getUuidAsString()));
    }

    public void setCustomName(@Nullable Text arg) {
        this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(arg));
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

    public void setCustomNameVisible(boolean bl) {
        this.dataTracker.set(NAME_VISIBLE, bl);
    }

    public boolean isCustomNameVisible() {
        return this.dataTracker.get(NAME_VISIBLE);
    }

    public final void teleport(double d, double e, double f) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ChunkPos lv = new ChunkPos(new BlockPos(d, e, f));
        ((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, lv, 0, this.getEntityId());
        this.world.getChunk(lv.x, lv.z);
        this.requestTeleport(d, e, f);
    }

    public void requestTeleport(double d, double e, double f) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld lv = (ServerWorld)this.world;
        this.refreshPositionAndAngles(d, e, f, this.yaw, this.pitch);
        this.streamPassengersRecursively().forEach(arg2 -> {
            lv.checkChunk((Entity)arg2);
            arg2.teleportRequested = true;
            for (Entity lv : arg2.passengerList) {
                arg2.updatePassengerPosition(lv, Entity::positAfterTeleport);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    public void onTrackedDataSet(TrackedData<?> arg) {
        if (POSE.equals(arg)) {
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

    public boolean canBeSpectated(ServerPlayerEntity arg) {
        return true;
    }

    public Box getBoundingBox() {
        return this.entityBounds;
    }

    @Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }

    protected Box calculateBoundsForPose(EntityPose arg) {
        EntityDimensions lv = this.getDimensions(arg);
        float f = lv.width / 2.0f;
        Vec3d lv2 = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
        Vec3d lv3 = new Vec3d(this.getX() + (double)f, this.getY() + (double)lv.height, this.getZ() + (double)f);
        return new Box(lv2, lv3);
    }

    public void setBoundingBox(Box arg) {
        this.entityBounds = arg;
    }

    protected float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.85f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEyeHeight(EntityPose arg) {
        return this.getEyeHeight(arg, this.getDimensions(arg));
    }

    public final float getStandingEyeHeight() {
        return this.standingEyeHeight;
    }

    public boolean equip(int i, ItemStack arg) {
        return false;
    }

    @Override
    public void sendSystemMessage(Text arg, UUID uUID) {
    }

    public World getEntityWorld() {
        return this.world;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    public ActionResult interactAt(PlayerEntity arg, Vec3d arg2, Hand arg3) {
        return ActionResult.PASS;
    }

    public boolean isImmuneToExplosion() {
        return false;
    }

    public void dealDamage(LivingEntity arg, Entity arg2) {
        if (arg2 instanceof LivingEntity) {
            EnchantmentHelper.onUserDamaged((LivingEntity)arg2, arg);
        }
        EnchantmentHelper.onTargetDamaged(arg, arg2);
    }

    public void onStartedTrackingBy(ServerPlayerEntity arg) {
    }

    public void onStoppedTrackingBy(ServerPlayerEntity arg) {
    }

    public float applyRotation(BlockRotation arg) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (arg) {
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

    public float applyMirror(BlockMirror arg) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (arg) {
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

    public boolean method_29240() {
        boolean bl = this.field_25154;
        this.field_25154 = false;
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

    public boolean hasPassenger(Entity arg) {
        for (Entity lv : this.getPassengerList()) {
            if (!lv.equals(arg)) continue;
            return true;
        }
        return false;
    }

    public boolean hasPassengerType(Class<? extends Entity> class_) {
        for (Entity lv : this.getPassengerList()) {
            if (!class_.isAssignableFrom(lv.getClass())) continue;
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

    private void collectPassengers(boolean bl, Set<Entity> set) {
        for (Entity lv : this.getPassengerList()) {
            if (!bl || ServerPlayerEntity.class.isAssignableFrom(lv.getClass())) {
                set.add(lv);
            }
            lv.collectPassengers(bl, set);
        }
    }

    public Entity getRootVehicle() {
        Entity lv = this;
        while (lv.hasVehicle()) {
            lv = lv.getVehicle();
        }
        return lv;
    }

    public boolean isConnectedThroughVehicle(Entity arg) {
        return this.getRootVehicle() == arg.getRootVehicle();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasPassengerDeep(Entity arg) {
        for (Entity lv : this.getPassengerList()) {
            if (lv.equals(arg)) {
                return true;
            }
            if (!lv.hasPassengerDeep(arg)) continue;
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

    protected static Vec3d getPassengerDismountOffset(double d, double e, float f) {
        double g = (d + e + (double)1.0E-5f) / 2.0;
        float h = -MathHelper.sin(f * ((float)Math.PI / 180));
        float i = MathHelper.cos(f * ((float)Math.PI / 180));
        float j = Math.max(Math.abs(h), Math.abs(i));
        return new Vec3d((double)h * g / (double)j, 0.0, (double)i * g / (double)j);
    }

    public Vec3d updatePassengerForDismount(LivingEntity arg) {
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

    public boolean hasPermissionLevel(int i) {
        return this.getPermissionLevel() >= i;
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

    public void lookAt(EntityAnchorArgumentType.EntityAnchor arg, Vec3d arg2) {
        Vec3d lv = arg.positionAt(this);
        double d = arg2.x - lv.x;
        double e = arg2.y - lv.y;
        double f = arg2.z - lv.z;
        double g = MathHelper.sqrt(d * d + f * f);
        this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        this.setHeadYaw(this.yaw);
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
    }

    public boolean updateMovementInFluid(Tag<Fluid> arg, double d) {
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
                    if (!lv4.matches(arg) || !((f = (double)((float)q + lv4.getHeight(this.world, lv3))) >= lv.minY)) continue;
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
            this.setVelocity(this.getVelocity().add(lv2.multiply(d)));
        }
        this.fluidHeight.put(arg, e);
        return bl2;
    }

    public double getFluidHeight(Tag<Fluid> arg) {
        return this.fluidHeight.getDouble(arg);
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

    public EntityDimensions getDimensions(EntityPose arg) {
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

    public void setVelocity(Vec3d arg) {
        this.velocity = arg;
    }

    public void setVelocity(double d, double e, double f) {
        this.setVelocity(new Vec3d(d, e, f));
    }

    public final double getX() {
        return this.pos.x;
    }

    public double offsetX(double d) {
        return this.pos.x + (double)this.getWidth() * d;
    }

    public double getParticleX(double d) {
        return this.offsetX((2.0 * this.random.nextDouble() - 1.0) * d);
    }

    public final double getY() {
        return this.pos.y;
    }

    public double getBodyY(double d) {
        return this.pos.y + (double)this.getHeight() * d;
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

    public double offsetZ(double d) {
        return this.pos.z + (double)this.getWidth() * d;
    }

    public double getParticleZ(double d) {
        return this.offsetZ((2.0 * this.random.nextDouble() - 1.0) * d);
    }

    public void setPos(double d, double e, double f) {
        if (this.pos.x != d || this.pos.y != e || this.pos.z != f) {
            this.pos = new Vec3d(d, e, f);
            int i = MathHelper.floor(d);
            int j = MathHelper.floor(e);
            int k = MathHelper.floor(f);
            if (i != this.blockPos.getX() || j != this.blockPos.getY() || k != this.blockPos.getZ()) {
                this.blockPos = new BlockPos(i, j, k);
            }
            this.field_25154 = true;
        }
    }

    public void checkDespawn() {
    }

    @FunctionalInterface
    public static interface PositionUpdater {
        public void accept(Entity var1, double var2, double var4, double var6);
    }
}

