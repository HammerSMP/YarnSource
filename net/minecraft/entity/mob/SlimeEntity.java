/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkRandom;

public class SlimeEntity
extends MobEntity
implements Monster {
    private static final TrackedData<Integer> SLIME_SIZE = DataTracker.registerData(SlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public float targetStretch;
    public float stretch;
    public float lastStretch;
    private boolean onGroundLastTick;

    public SlimeEntity(EntityType<? extends SlimeEntity> arg, World arg2) {
        super((EntityType<? extends MobEntity>)arg, arg2);
        this.moveControl = new SlimeMoveControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimmingGoal(this));
        this.goalSelector.add(2, new FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new RandomLookGoal(this));
        this.goalSelector.add(5, new MoveGoal(this));
        this.targetSelector.add(1, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, arg -> Math.abs(arg.getY() - this.getY()) <= 4.0));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SLIME_SIZE, 1);
    }

    protected void setSize(int size, boolean heal) {
        this.dataTracker.set(SLIME_SIZE, size);
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(size * size);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2f + 0.1f * (float)size);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(size);
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
        this.experiencePoints = size;
    }

    public int getSize() {
        return this.dataTracker.get(SLIME_SIZE);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Size", this.getSize() - 1);
        tag.putBoolean("wasOnGround", this.onGroundLastTick);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        int i = tag.getInt("Size");
        if (i < 0) {
            i = 0;
        }
        this.setSize(i + 1, false);
        super.readCustomDataFromTag(tag);
        this.onGroundLastTick = tag.getBoolean("wasOnGround");
    }

    public boolean isSmall() {
        return this.getSize() <= 1;
    }

    protected ParticleEffect getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.stretch += (this.targetStretch - this.stretch) * 0.5f;
        this.lastStretch = this.stretch;
        super.tick();
        if (this.onGround && !this.onGroundLastTick) {
            int i = this.getSize();
            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * ((float)Math.PI * 2);
                float g = this.random.nextFloat() * 0.5f + 0.5f;
                float h = MathHelper.sin(f) * (float)i * 0.5f * g;
                float k = MathHelper.cos(f) * (float)i * 0.5f * g;
                this.world.addParticle(this.getParticles(), this.getX() + (double)h, this.getY(), this.getZ() + (double)k, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            this.targetStretch = -0.5f;
        } else if (!this.onGround && this.onGroundLastTick) {
            this.targetStretch = 1.0f;
        }
        this.onGroundLastTick = this.onGround;
        this.updateStretch();
    }

    protected void updateStretch() {
        this.targetStretch *= 0.6f;
    }

    protected int getTicksUntilNextJump() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.updatePosition(d, e, f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SLIME_SIZE.equals(data)) {
            this.calculateDimensions();
            this.yaw = this.headYaw;
            this.bodyYaw = this.headYaw;
            if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
                this.onSwimmingStart();
            }
        }
        super.onTrackedDataSet(data);
    }

    public EntityType<? extends SlimeEntity> getType() {
        return super.getType();
    }

    @Override
    public void remove() {
        int i = this.getSize();
        if (!this.world.isClient && i > 1 && this.isDead()) {
            Text lv = this.getCustomName();
            boolean bl = this.isAiDisabled();
            float f = (float)i / 4.0f;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);
            for (int l = 0; l < k; ++l) {
                float g = ((float)(l % 2) - 0.5f) * f;
                float h = ((float)(l / 2) - 0.5f) * f;
                SlimeEntity lv2 = this.getType().create(this.world);
                if (this.isPersistent()) {
                    lv2.setPersistent();
                }
                lv2.setCustomName(lv);
                lv2.setAiDisabled(bl);
                lv2.setInvulnerable(this.isInvulnerable());
                lv2.setSize(j, true);
                lv2.refreshPositionAndAngles(this.getX() + (double)g, this.getY() + 0.5, this.getZ() + (double)h, this.random.nextFloat() * 360.0f, 0.0f);
                this.world.spawnEntity(lv2);
            }
        }
        super.remove();
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity && this.canAttack()) {
            this.damage((LivingEntity)entity);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.canAttack()) {
            this.damage(player);
        }
    }

    protected void damage(LivingEntity target) {
        if (this.isAlive()) {
            int i = this.getSize();
            if (this.squaredDistanceTo(target) < 0.6 * (double)i * (0.6 * (double)i) && this.canSee(target) && target.damage(DamageSource.mob(this), this.getDamageAmount())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                this.dealDamage(this, target);
            }
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.625f * dimensions.height;
    }

    protected boolean canAttack() {
        return !this.isSmall() && this.canMoveVoluntarily();
    }

    protected float getDamageAmount() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_SLIME_HURT_SMALL;
        }
        return SoundEvents.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_SLIME_DEATH_SMALL;
        }
        return SoundEvents.ENTITY_SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        if (this.isSmall()) {
            return SoundEvents.ENTITY_SLIME_SQUISH_SMALL;
        }
        return SoundEvents.ENTITY_SLIME_SQUISH;
    }

    @Override
    protected Identifier getLootTableId() {
        return this.getSize() == 1 ? this.getType().getLootTableId() : LootTables.EMPTY;
    }

    public static boolean canSpawn(EntityType<SlimeEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            boolean bl;
            Biome lv = world.getBiome(pos);
            if (lv == Biomes.SWAMP && pos.getY() > 50 && pos.getY() < 70 && random.nextFloat() < 0.5f && random.nextFloat() < world.method_30272() && world.getLightLevel(pos) <= random.nextInt(8)) {
                return SlimeEntity.canMobSpawn(type, world, spawnReason, pos, random);
            }
            if (!(world instanceof ServerWorldAccess)) {
                return false;
            }
            ChunkPos lv2 = new ChunkPos(pos);
            boolean bl2 = bl = ChunkRandom.getSlimeRandom(lv2.x, lv2.z, ((ServerWorldAccess)world).getSeed(), 987234911L).nextInt(10) == 0;
            if (random.nextInt(10) == 0 && bl && pos.getY() < 40) {
                return SlimeEntity.canMobSpawn(type, world, spawnReason, pos, random);
            }
        }
        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f * (float)this.getSize();
    }

    @Override
    public int getLookPitchSpeed() {
        return 0;
    }

    protected boolean makesJumpSound() {
        return this.getSize() > 0;
    }

    @Override
    protected void jump() {
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.x, this.getJumpVelocity(), lv.z);
        this.velocityDirty = true;
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        int i = this.random.nextInt(3);
        if (i < 2 && this.random.nextFloat() < 0.5f * difficulty.getClampedLocalDifficulty()) {
            ++i;
        }
        int j = 1 << i;
        this.setSize(j, true);
        return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
    }

    private float getJumpSoundPitch() {
        float f = this.isSmall() ? 1.4f : 0.8f;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * f;
    }

    protected SoundEvent getJumpSound() {
        return this.isSmall() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(0.255f * (float)this.getSize());
    }

    static class MoveGoal
    extends Goal {
        private final SlimeEntity slime;

        public MoveGoal(SlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.slime.hasVehicle();
        }

        @Override
        public void tick() {
            ((SlimeMoveControl)this.slime.getMoveControl()).move(1.0);
        }
    }

    static class SwimmingGoal
    extends Goal {
        private final SlimeEntity slime;

        public SwimmingGoal(SlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
            slime.getNavigation().setCanSwim(true);
        }

        @Override
        public boolean canStart() {
            return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8f) {
                this.slime.getJumpControl().setActive();
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).move(1.2);
        }
    }

    static class RandomLookGoal
    extends Goal {
        private final SlimeEntity slime;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(SlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.slime.getTarget() == null && (this.slime.onGround || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--this.timer <= 0) {
                this.timer = 40 + this.slime.getRandom().nextInt(60);
                this.targetYaw = this.slime.getRandom().nextInt(360);
            }
            ((SlimeMoveControl)this.slime.getMoveControl()).look(this.targetYaw, false);
        }
    }

    static class FaceTowardTargetGoal
    extends Goal {
        private final SlimeEntity slime;
        private int ticksLeft;

        public FaceTowardTargetGoal(SlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity lv = this.slime.getTarget();
            if (lv == null) {
                return false;
            }
            if (!lv.isAlive()) {
                return false;
            }
            if (lv instanceof PlayerEntity && ((PlayerEntity)lv).abilities.invulnerable) {
                return false;
            }
            return this.slime.getMoveControl() instanceof SlimeMoveControl;
        }

        @Override
        public void start() {
            this.ticksLeft = 300;
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity lv = this.slime.getTarget();
            if (lv == null) {
                return false;
            }
            if (!lv.isAlive()) {
                return false;
            }
            if (lv instanceof PlayerEntity && ((PlayerEntity)lv).abilities.invulnerable) {
                return false;
            }
            return --this.ticksLeft > 0;
        }

        @Override
        public void tick() {
            this.slime.lookAtEntity(this.slime.getTarget(), 10.0f, 10.0f);
            ((SlimeMoveControl)this.slime.getMoveControl()).look(this.slime.yaw, this.slime.canAttack());
        }
    }

    static class SlimeMoveControl
    extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private final SlimeEntity slime;
        private boolean jumpOften;

        public SlimeMoveControl(SlimeEntity slime) {
            super(slime);
            this.slime = slime;
            this.targetYaw = 180.0f * slime.yaw / (float)Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = MoveControl.State.MOVE_TO;
        }

        @Override
        public void tick() {
            this.entity.headYaw = this.entity.yaw = this.changeAngle(this.entity.yaw, this.targetYaw, 90.0f);
            this.entity.bodyYaw = this.entity.yaw;
            if (this.state != MoveControl.State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            this.state = MoveControl.State.WAIT;
            if (this.entity.isOnGround()) {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                if (this.ticksUntilJump-- <= 0) {
                    this.ticksUntilJump = this.slime.getTicksUntilNextJump();
                    if (this.jumpOften) {
                        this.ticksUntilJump /= 3;
                    }
                    this.slime.getJumpControl().setActive();
                    if (this.slime.makesJumpSound()) {
                        this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getJumpSoundPitch());
                    }
                } else {
                    this.slime.sidewaysSpeed = 0.0f;
                    this.slime.forwardSpeed = 0.0f;
                    this.entity.setMovementSpeed(0.0f);
                }
            } else {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            }
        }
    }
}

