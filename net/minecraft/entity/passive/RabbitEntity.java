/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;

public class RabbitEntity
extends AnimalEntity {
    private static final TrackedData<Integer> RABBIT_TYPE = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Identifier KILLER_BUNNY = new Identifier("killer_bunny");
    private int jumpTicks;
    private int jumpDuration;
    private boolean lastOnGround;
    private int ticksUntilJump;
    private int moreCarrotTicks;

    public RabbitEntity(EntityType<? extends RabbitEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.jumpControl = new RabbitJumpControl(this);
        this.moveControl = new RabbitMoveControl(this);
        this.setSpeed(0.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(3, new TemptGoal((PathAwareEntity)this, 1.0, Ingredient.ofItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.add(4, new FleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<WolfEntity>(this, WolfEntity.class, 10.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<HostileEntity>(this, HostileEntity.class, 4.0f, 2.2, 2.2));
        this.goalSelector.add(5, new EatCarrotCropGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    @Override
    protected float getJumpVelocity() {
        if (this.horizontalCollision || this.moveControl.isMoving() && this.moveControl.getTargetY() > this.getY() + 0.5) {
            return 0.5f;
        }
        Path lv = this.navigation.getCurrentPath();
        if (lv != null && lv.getCurrentNodeIndex() < lv.getLength()) {
            Vec3d lv2 = lv.getNodePosition(this);
            if (lv2.y > this.getY() + 0.5) {
                return 0.5f;
            }
        }
        if (this.moveControl.getSpeed() <= 0.6) {
            return 0.2f;
        }
        return 0.3f;
    }

    @Override
    protected void jump() {
        double e;
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > 0.0 && (e = RabbitEntity.squaredHorizontalLength(this.getVelocity())) < 0.01) {
            this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
        }
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float getJumpProgress(float f) {
        if (this.jumpDuration == 0) {
            return 0.0f;
        }
        return ((float)this.jumpTicks + f) / (float)this.jumpDuration;
    }

    public void setSpeed(double d) {
        this.getNavigation().setSpeed(d);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), d);
    }

    @Override
    public void setJumping(boolean bl) {
        super.setJumping(bl);
        if (bl) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
        }
    }

    public void startJump() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RABBIT_TYPE, 0);
    }

    @Override
    public void mobTick() {
        if (this.ticksUntilJump > 0) {
            --this.ticksUntilJump;
        }
        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }
        if (this.onGround) {
            RabbitJumpControl lv2;
            LivingEntity lv;
            if (!this.lastOnGround) {
                this.setJumping(false);
                this.scheduleJump();
            }
            if (this.getRabbitType() == 99 && this.ticksUntilJump == 0 && (lv = this.getTarget()) != null && this.squaredDistanceTo(lv) < 16.0) {
                this.lookTowards(lv.getX(), lv.getZ());
                this.moveControl.moveTo(lv.getX(), lv.getY(), lv.getZ(), this.moveControl.getSpeed());
                this.startJump();
                this.lastOnGround = true;
            }
            if (!(lv2 = (RabbitJumpControl)this.jumpControl).isActive()) {
                if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
                    Path lv3 = this.navigation.getCurrentPath();
                    Vec3d lv4 = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
                    if (lv3 != null && lv3.getCurrentNodeIndex() < lv3.getLength()) {
                        lv4 = lv3.getNodePosition(this);
                    }
                    this.lookTowards(lv4.x, lv4.z);
                    this.startJump();
                }
            } else if (!lv2.method_27313()) {
                this.method_6611();
            }
        }
        this.lastOnGround = this.onGround;
    }

    @Override
    public boolean shouldSpawnSprintingParticles() {
        return false;
    }

    private void lookTowards(double d, double e) {
        this.yaw = (float)(MathHelper.atan2(e - this.getZ(), d - this.getX()) * 57.2957763671875) - 90.0f;
    }

    private void method_6611() {
        ((RabbitJumpControl)this.jumpControl).method_27311(true);
    }

    private void method_6621() {
        ((RabbitJumpControl)this.jumpControl).method_27311(false);
    }

    private void doScheduleJump() {
        this.ticksUntilJump = this.moveControl.getSpeed() < 2.2 ? 10 : 1;
    }

    private void scheduleJump() {
        this.doScheduleJump();
        this.method_6621();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    public static DefaultAttributeContainer.Builder createRabbitAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("RabbitType", this.getRabbitType());
        arg.putInt("MoreCarrotTicks", this.moreCarrotTicks);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setRabbitType(arg.getInt("RabbitType"));
        this.moreCarrotTicks = arg.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean tryAttack(Entity arg) {
        if (this.getRabbitType() == 99) {
            this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            return arg.damage(DamageSource.mob(this), 8.0f);
        }
        return arg.damage(DamageSource.mob(this), 3.0f);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        return super.damage(arg, f);
    }

    private boolean isBreedingItem(Item arg) {
        return arg == Items.CARROT || arg == Items.GOLDEN_CARROT || arg == Blocks.DANDELION.asItem();
    }

    @Override
    public RabbitEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        RabbitEntity lv = EntityType.RABBIT.create(arg);
        int i = this.chooseType(arg);
        if (this.random.nextInt(20) != 0) {
            i = arg2 instanceof RabbitEntity && this.random.nextBoolean() ? ((RabbitEntity)arg2).getRabbitType() : this.getRabbitType();
        }
        lv.setRabbitType(i);
        return lv;
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return this.isBreedingItem(arg.getItem());
    }

    public int getRabbitType() {
        return this.dataTracker.get(RABBIT_TYPE);
    }

    public void setRabbitType(int i) {
        if (i == 99) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(8.0);
            this.goalSelector.add(4, new RabbitAttackGoal(this));
            this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
            this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
            this.targetSelector.add(2, new FollowTargetGoal<WolfEntity>((MobEntity)this, WolfEntity.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new TranslatableText(Util.createTranslationKey("entity", KILLER_BUNNY)));
            }
        }
        this.dataTracker.set(RABBIT_TYPE, i);
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        int i = this.chooseType(arg);
        if (arg4 instanceof RabbitData) {
            i = ((RabbitData)arg4).type;
        } else {
            arg4 = new RabbitData(i);
        }
        this.setRabbitType(i);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    private int chooseType(WorldAccess arg) {
        Biome lv = arg.getBiome(this.getBlockPos());
        int i = this.random.nextInt(100);
        if (lv.getPrecipitation() == Biome.Precipitation.SNOW) {
            return i < 80 ? 1 : 3;
        }
        if (lv.getCategory() == Biome.Category.DESERT) {
            return 4;
        }
        return i < 50 ? 0 : (i < 90 ? 5 : 2);
    }

    public static boolean canSpawn(EntityType<RabbitEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        BlockState lv = arg2.getBlockState(arg4.down());
        return (lv.isOf(Blocks.GRASS_BLOCK) || lv.isOf(Blocks.SNOW) || lv.isOf(Blocks.SAND)) && arg2.getBaseLightLevel(arg4, 0) > 8;
    }

    private boolean wantsCarrots() {
        return this.moreCarrotTicks == 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 1) {
            this.spawnSprintingParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.6f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        return this.createChild(arg, arg2);
    }

    static class RabbitAttackGoal
    extends MeleeAttackGoal {
        public RabbitAttackGoal(RabbitEntity arg) {
            super(arg, 1.4, true);
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity arg) {
            return 4.0f + arg.getWidth();
        }
    }

    static class EscapeDangerGoal
    extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
        private final RabbitEntity rabbit;

        public EscapeDangerGoal(RabbitEntity arg, double d) {
            super(arg, d);
            this.rabbit = arg;
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.setSpeed(this.speed);
        }
    }

    static class EatCarrotCropGoal
    extends MoveToTargetPosGoal {
        private final RabbitEntity rabbit;
        private boolean wantsCarrots;
        private boolean hasTarget;

        public EatCarrotCropGoal(RabbitEntity arg) {
            super(arg, 0.7f, 16);
            this.rabbit = arg;
        }

        @Override
        public boolean canStart() {
            if (this.cooldown <= 0) {
                if (!this.rabbit.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    return false;
                }
                this.hasTarget = false;
                this.wantsCarrots = this.rabbit.wantsCarrots();
                this.wantsCarrots = true;
            }
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return this.hasTarget && super.shouldContinue();
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, 10.0f, this.rabbit.getLookPitchSpeed());
            if (this.hasReached()) {
                World lv = this.rabbit.world;
                BlockPos lv2 = this.targetPos.up();
                BlockState lv3 = lv.getBlockState(lv2);
                Block lv4 = lv3.getBlock();
                if (this.hasTarget && lv4 instanceof CarrotsBlock) {
                    Integer integer = lv3.get(CarrotsBlock.AGE);
                    if (integer == 0) {
                        lv.setBlockState(lv2, Blocks.AIR.getDefaultState(), 2);
                        lv.breakBlock(lv2, true, this.rabbit);
                    } else {
                        lv.setBlockState(lv2, (BlockState)lv3.with(CarrotsBlock.AGE, integer - 1), 2);
                        lv.syncWorldEvent(2001, lv2, Block.getRawIdFromState(lv3));
                    }
                    this.rabbit.moreCarrotTicks = 40;
                }
                this.hasTarget = false;
                this.cooldown = 10;
            }
        }

        @Override
        protected boolean isTargetPos(WorldView arg, BlockPos arg2) {
            BlockState lv2;
            Block lv = arg.getBlockState(arg2).getBlock();
            if (lv == Blocks.FARMLAND && this.wantsCarrots && !this.hasTarget && (lv = (lv2 = arg.getBlockState(arg2 = arg2.up())).getBlock()) instanceof CarrotsBlock && ((CarrotsBlock)lv).isMature(lv2)) {
                this.hasTarget = true;
                return true;
            }
            return false;
        }
    }

    static class FleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final RabbitEntity rabbit;

        public FleeGoal(RabbitEntity arg, Class<T> class_, float f, double d, double e) {
            super(arg, class_, f, d, e);
            this.rabbit = arg;
        }

        @Override
        public boolean canStart() {
            return this.rabbit.getRabbitType() != 99 && super.canStart();
        }
    }

    static class RabbitMoveControl
    extends MoveControl {
        private final RabbitEntity rabbit;
        private double rabbitSpeed;

        public RabbitMoveControl(RabbitEntity arg) {
            super(arg);
            this.rabbit = arg;
        }

        @Override
        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
                this.rabbit.setSpeed(0.0);
            } else if (this.isMoving()) {
                this.rabbit.setSpeed(this.rabbitSpeed);
            }
            super.tick();
        }

        @Override
        public void moveTo(double d, double e, double f, double g) {
            if (this.rabbit.isTouchingWater()) {
                g = 1.5;
            }
            super.moveTo(d, e, f, g);
            if (g > 0.0) {
                this.rabbitSpeed = g;
            }
        }
    }

    public class RabbitJumpControl
    extends JumpControl {
        private final RabbitEntity rabbit;
        private boolean field_24091;

        public RabbitJumpControl(RabbitEntity arg2) {
            super(arg2);
            this.rabbit = arg2;
        }

        public boolean isActive() {
            return this.active;
        }

        public boolean method_27313() {
            return this.field_24091;
        }

        public void method_27311(boolean bl) {
            this.field_24091 = bl;
        }

        @Override
        public void tick() {
            if (this.active) {
                this.rabbit.startJump();
                this.active = false;
            }
        }
    }

    public static class RabbitData
    extends PassiveEntity.PassiveData {
        public final int type;

        public RabbitData(int i) {
            super(1.0f);
            this.type = i;
        }
    }
}

