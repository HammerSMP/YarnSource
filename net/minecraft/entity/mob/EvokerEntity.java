/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EvokerEntity
extends SpellcastingIllagerEntity {
    private SheepEntity wololoTarget;

    public EvokerEntity(EntityType<? extends EvokerEntity> arg, World arg2) {
        super((EntityType<? extends SpellcastingIllagerEntity>)arg, arg2);
        this.experiencePoints = 10;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(2, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(4, new SummonVexGoal());
        this.goalSelector.add(5, new ConjureFangsGoal());
        this.goalSelector.add(6, new WololoGoal());
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, false));
    }

    public static DefaultAttributeContainer.Builder createEvokerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof VexEntity) {
            return this.isTeammate(((VexEntity)other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    private void setWololoTarget(@Nullable SheepEntity sheep) {
        this.wololoTarget = sheep;
    }

    @Nullable
    private SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }

    public class WololoGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate convertibleSheepPredicate;

        public WololoGoal() {
            super(EvokerEntity.this);
            this.convertibleSheepPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().setPredicate(arg -> ((SheepEntity)arg).getColor() == DyeColor.BLUE);
        }

        @Override
        public boolean canStart() {
            if (EvokerEntity.this.getTarget() != null) {
                return false;
            }
            if (EvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (EvokerEntity.this.age < this.startTime) {
                return false;
            }
            if (!EvokerEntity.this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            }
            List<SheepEntity> list = EvokerEntity.this.world.getTargets(SheepEntity.class, this.convertibleSheepPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            EvokerEntity.this.setWololoTarget(list.get(EvokerEntity.this.random.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }

        @Override
        public void stop() {
            super.stop();
            EvokerEntity.this.setWololoTarget(null);
        }

        @Override
        protected void castSpell() {
            SheepEntity lv = EvokerEntity.this.getWololoTarget();
            if (lv != null && lv.isAlive()) {
                lv.setColor(DyeColor.RED);
            }
        }

        @Override
        protected int getInitialCooldown() {
            return 40;
        }

        @Override
        protected int getSpellTicks() {
            return 60;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.WOLOLO;
        }
    }

    class SummonVexGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeVexPredicate;

        private SummonVexGoal() {
            super(EvokerEntity.this);
            this.closeVexPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }
            int i = EvokerEntity.this.world.getTargets(VexEntity.class, this.closeVexPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
            return EvokerEntity.this.random.nextInt(8) + 1 > i;
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 340;
        }

        @Override
        protected void castSpell() {
            for (int i = 0; i < 3; ++i) {
                BlockPos lv = EvokerEntity.this.getBlockPos().add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
                VexEntity lv2 = EntityType.VEX.create(EvokerEntity.this.world);
                lv2.refreshPositionAndAngles(lv, 0.0f, 0.0f);
                lv2.initialize((ServerWorld)EvokerEntity.this.world, EvokerEntity.this.world.getLocalDifficulty(lv), SpawnReason.MOB_SUMMONED, null, null);
                lv2.setOwner(EvokerEntity.this);
                lv2.setBounds(lv);
                lv2.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
                EvokerEntity.this.world.spawnEntity(lv2);
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
        }
    }

    class ConjureFangsGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private ConjureFangsGoal() {
            super(EvokerEntity.this);
        }

        @Override
        protected int getSpellTicks() {
            return 40;
        }

        @Override
        protected int startTimeDelay() {
            return 100;
        }

        @Override
        protected void castSpell() {
            LivingEntity lv = EvokerEntity.this.getTarget();
            double d = Math.min(lv.getY(), EvokerEntity.this.getY());
            double e = Math.max(lv.getY(), EvokerEntity.this.getY()) + 1.0;
            float f = (float)MathHelper.atan2(lv.getZ() - EvokerEntity.this.getZ(), lv.getX() - EvokerEntity.this.getX());
            if (EvokerEntity.this.squaredDistanceTo(lv) < 9.0) {
                for (int i = 0; i < 5; ++i) {
                    float g = f + (float)i * (float)Math.PI * 0.4f;
                    this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
                }
                for (int j = 0; j < 8; ++j) {
                    float h = f + (float)j * (float)Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(h) * 2.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(h) * 2.5, d, e, h, 3);
                }
            } else {
                for (int k = 0; k < 16; ++k) {
                    double l = 1.25 * (double)(k + 1);
                    int m = 1 * k;
                    this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(f) * l, EvokerEntity.this.getZ() + (double)MathHelper.sin(f) * l, d, e, f, m);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
            BlockPos lv = new BlockPos(x, y, z);
            boolean bl = false;
            double j = 0.0;
            do {
                BlockState lv4;
                VoxelShape lv5;
                BlockPos lv2;
                BlockState lv3;
                if (!(lv3 = EvokerEntity.this.world.getBlockState(lv2 = lv.down())).isSideSolidFullSquare(EvokerEntity.this.world, lv2, Direction.UP)) continue;
                if (!EvokerEntity.this.world.isAir(lv) && !(lv5 = (lv4 = EvokerEntity.this.world.getBlockState(lv)).getCollisionShape(EvokerEntity.this.world, lv)).isEmpty()) {
                    j = lv5.getMax(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((lv = lv.down()).getY() >= MathHelper.floor(maxY) - 1);
            if (bl) {
                EvokerEntity.this.world.spawnEntity(new EvokerFangsEntity(EvokerEntity.this.world, x, (double)lv.getY() + j, z, yaw, warmup, EvokerEntity.this));
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.FANGS;
        }
    }

    class LookAtTargetOrWololoTarget
    extends SpellcastingIllagerEntity.LookAtTargetGoal {
        private LookAtTargetOrWololoTarget() {
            super(EvokerEntity.this);
        }

        @Override
        public void tick() {
            if (EvokerEntity.this.getTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), EvokerEntity.this.getBodyYawSpeed(), EvokerEntity.this.getLookPitchSpeed());
            } else if (EvokerEntity.this.getWololoTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getWololoTarget(), EvokerEntity.this.getBodyYawSpeed(), EvokerEntity.this.getLookPitchSpeed());
            }
        }
    }
}

