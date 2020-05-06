/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity
extends HostileEntity {
    private int lifeTime;
    private boolean playerSpawned;

    public EndermiteEntity(EntityType<? extends EndermiteEntity> arg, World arg2) {
        super((EntityType<? extends HostileEntity>)arg, arg2);
        this.experiencePoints = 3;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.13f;
    }

    public static DefaultAttributeContainer.Builder createEndermiteAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ENDERMITE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMITE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15f, 1.0f);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.lifeTime = arg.getInt("Lifetime");
        this.playerSpawned = arg.getBoolean("PlayerSpawned");
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Lifetime", this.lifeTime);
        arg.putBoolean("PlayerSpawned", this.playerSpawned);
    }

    @Override
    public void tick() {
        this.bodyYaw = this.yaw;
        super.tick();
    }

    @Override
    public void setYaw(float f) {
        this.yaw = f;
        super.setYaw(f);
    }

    @Override
    public double getHeightOffset() {
        return 0.1;
    }

    public boolean isPlayerSpawned() {
        return this.playerSpawned;
    }

    public void setPlayerSpawned(boolean bl) {
        this.playerSpawned = bl;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        } else {
            if (!this.isPersistent()) {
                ++this.lifeTime;
            }
            if (this.lifeTime >= 2400) {
                this.remove();
            }
        }
    }

    public static boolean canSpawn(EntityType<EndermiteEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        if (EndermiteEntity.canSpawnIgnoreLightLevel(arg, arg2, arg3, arg4, random)) {
            PlayerEntity lv = arg2.getClosestPlayer((double)arg4.getX() + 0.5, (double)arg4.getY() + 0.5, (double)arg4.getZ() + 0.5, 5.0, true);
            return lv == null;
        }
        return false;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }
}

