/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RavagerEntity
extends RaiderEntity {
    private static final Predicate<Entity> IS_NOT_RAVAGER = arg -> arg.isAlive() && !(arg instanceof RavagerEntity);
    private int attackTick;
    private int stunTick;
    private int roarTick;

    public RavagerEntity(EntityType<? extends RavagerEntity> arg, World arg2) {
        super((EntityType<? extends RaiderEntity>)arg, arg2);
        this.stepHeight = 1.0f;
        this.experiencePoints = 20;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new AttackGoal());
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(2, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(4, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, true));
        this.targetSelector.add(4, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
    }

    @Override
    protected void method_20417() {
        boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity) || this.getPrimaryPassenger().getType().isIn(EntityTypeTags.RAIDERS);
        boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
        this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
        this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
        this.goalSelector.setControlEnabled(Goal.Control.TARGET, bl);
    }

    public static DefaultAttributeContainer.Builder createRavagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("AttackTick", this.attackTick);
        arg.putInt("StunTick", this.stunTick);
        arg.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.attackTick = arg.getInt("AttackTick");
        this.stunTick = arg.getInt("StunTick");
        this.roarTick = arg.getInt("RoarTick");
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_RAVAGER_CELEBRATE;
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        return new Navigation(this, arg);
    }

    @Override
    public int getBodyYawSpeed() {
        return 45;
    }

    @Override
    public double getMountedHeightOffset() {
        return 2.1;
    }

    @Override
    public boolean canBeControlledByRider() {
        return !this.isAiDisabled() && this.getPrimaryPassenger() instanceof LivingEntity;
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.isAlive()) {
            return;
        }
        if (this.isImmobile()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        } else {
            double d = this.getTarget() != null ? 0.35 : 0.3;
            double e = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue();
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
        }
        if (this.horizontalCollision && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            boolean bl = false;
            Box lv = this.getBoundingBox().expand(0.2);
            for (BlockPos lv2 : BlockPos.iterate(MathHelper.floor(lv.x1), MathHelper.floor(lv.y1), MathHelper.floor(lv.z1), MathHelper.floor(lv.x2), MathHelper.floor(lv.y2), MathHelper.floor(lv.z2))) {
                BlockState lv3 = this.world.getBlockState(lv2);
                Block lv4 = lv3.getBlock();
                if (!(lv4 instanceof LeavesBlock)) continue;
                bl = this.world.breakBlock(lv2, true, this) || bl;
            }
            if (!bl && this.onGround) {
                this.jump();
            }
        }
        if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
                this.roar();
            }
        }
        if (this.attackTick > 0) {
            --this.attackTick;
        }
        if (this.stunTick > 0) {
            --this.stunTick;
            this.spawnStunnedParticles();
            if (this.stunTick == 0) {
                this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);
                this.roarTick = 20;
            }
        }
    }

    private void spawnStunnedParticles() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double)this.getWidth() * Math.sin(this.bodyYaw * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            double e = this.getY() + (double)this.getHeight() - 0.3;
            double f = this.getZ() + (double)this.getWidth() * Math.cos(this.bodyYaw * ((float)Math.PI / 180)) + (this.random.nextDouble() * 0.6 - 0.3);
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, d, e, f, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean canSee(Entity arg) {
        if (this.stunTick > 0 || this.roarTick > 0) {
            return false;
        }
        return super.canSee(arg);
    }

    @Override
    protected void knockback(LivingEntity arg) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5) {
                this.stunTick = 40;
                this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0f, 1.0f);
                this.world.sendEntityStatus(this, (byte)39);
                arg.pushAwayFrom(this);
            } else {
                this.knockBack(arg);
            }
            arg.velocityModified = true;
        }
    }

    private void roar() {
        if (this.isAlive()) {
            List<Entity> list = this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(4.0), IS_NOT_RAVAGER);
            for (Entity lv : list) {
                if (!(lv instanceof IllagerEntity)) {
                    lv.damage(DamageSource.mob(this), 6.0f);
                }
                this.knockBack(lv);
            }
            Vec3d lv2 = this.getBoundingBox().getCenter();
            for (int i = 0; i < 40; ++i) {
                double d = this.random.nextGaussian() * 0.2;
                double e = this.random.nextGaussian() * 0.2;
                double f = this.random.nextGaussian() * 0.2;
                this.world.addParticle(ParticleTypes.POOF, lv2.x, lv2.y, lv2.z, d, e, f);
            }
        }
    }

    private void knockBack(Entity arg) {
        double d = arg.getX() - this.getX();
        double e = arg.getZ() - this.getZ();
        double f = Math.max(d * d + e * e, 0.001);
        arg.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 4) {
            this.attackTick = 10;
            this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0f, 1.0f);
        } else if (b == 39) {
            this.stunTick = 40;
        }
        super.handleStatus(b);
    }

    @Environment(value=EnvType.CLIENT)
    public int getAttackTick() {
        return this.attackTick;
    }

    @Environment(value=EnvType.CLIENT)
    public int getStunTick() {
        return this.stunTick;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean tryAttack(Entity arg) {
        this.attackTick = 10;
        this.world.sendEntityStatus(this, (byte)4);
        this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0f, 1.0f);
        return super.tryAttack(arg);
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return !arg.containsFluid(this.getBoundingBox());
    }

    @Override
    public void addBonusForWave(int i, boolean bl) {
    }

    @Override
    public boolean canLead() {
        return false;
    }

    static class PathNodeMaker
    extends LandPathNodeMaker {
        private PathNodeMaker() {
        }

        @Override
        protected PathNodeType adjustNodeType(BlockView arg, boolean bl, boolean bl2, BlockPos arg2, PathNodeType arg3) {
            if (arg3 == PathNodeType.LEAVES) {
                return PathNodeType.OPEN;
            }
            return super.adjustNodeType(arg, bl, bl2, arg2, arg3);
        }
    }

    static class Navigation
    extends MobNavigation {
        public Navigation(MobEntity arg, World arg2) {
            super(arg, arg2);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int i) {
            this.nodeMaker = new PathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, i);
        }
    }

    class AttackGoal
    extends MeleeAttackGoal {
        public AttackGoal() {
            super(RavagerEntity.this, 1.0, true);
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity arg) {
            float f = RavagerEntity.this.getWidth() - 0.1f;
            return f * 2.0f * (f * 2.0f) + arg.getWidth();
        }
    }
}

