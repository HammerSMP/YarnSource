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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class OcelotEntity
extends AnimalEntity {
    private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
    private static final TrackedData<Boolean> TRUSTING = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private FleeGoal<PlayerEntity> fleeGoal;
    private OcelotTemptGoal temptGoal;

    public OcelotEntity(EntityType<? extends OcelotEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.updateFleeing();
    }

    private boolean isTrusting() {
        return this.dataTracker.get(TRUSTING);
    }

    private void setTrusting(boolean bl) {
        this.dataTracker.set(TRUSTING, bl);
        this.updateFleeing();
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putBoolean("Trusting", this.isTrusting());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setTrusting(arg.getBoolean("Trusting"));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TRUSTING, false);
    }

    @Override
    protected void initGoals() {
        this.temptGoal = new OcelotTemptGoal(this, 0.6, TAMING_INGREDIENT, true);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, this.temptGoal);
        this.goalSelector.add(7, new PounceAtTargetGoal(this, 0.3f));
        this.goalSelector.add(8, new AttackGoal(this));
        this.goalSelector.add(9, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(10, new WanderAroundFarGoal((MobEntityWithAi)this, 0.8, 1.0000001E-5f));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.add(1, new FollowTargetGoal<ChickenEntity>((MobEntity)this, ChickenEntity.class, false));
        this.targetSelector.add(1, new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    @Override
    public void mobTick() {
        if (this.getMoveControl().isMoving()) {
            double d = this.getMoveControl().getSpeed();
            if (d == 0.6) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (d == 1.33) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isTrusting() && this.age > 2400;
    }

    public static DefaultAttributeContainer.Builder createOcelotAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_OCELOT_AMBIENT;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 900;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_OCELOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_OCELOT_DEATH;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        return arg.damage(DamageSource.mob(this), this.getAttackDamage());
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        return super.damage(arg, f);
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if ((this.temptGoal == null || this.temptGoal.isActive()) && !this.isTrusting() && this.isBreedingItem(lv) && arg.squaredDistanceTo(this) < 9.0) {
            this.eat(arg, lv);
            if (!this.world.isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setTrusting(true);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)41);
                } else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)40);
                }
            }
            return true;
        }
        return super.interactMob(arg, arg2);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 41) {
            this.showEmoteParticle(true);
        } else if (b == 40) {
            this.showEmoteParticle(false);
        } else {
            super.handleStatus(b);
        }
    }

    private void showEmoteParticle(boolean bl) {
        DefaultParticleType lv = ParticleTypes.HEART;
        if (!bl) {
            lv = ParticleTypes.SMOKE;
        }
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(lv, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    protected void updateFleeing() {
        if (this.fleeGoal == null) {
            this.fleeGoal = new FleeGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.remove(this.fleeGoal);
        if (!this.isTrusting()) {
            this.goalSelector.add(4, this.fleeGoal);
        }
    }

    @Override
    public OcelotEntity createChild(PassiveEntity arg) {
        return EntityType.OCELOT.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return TAMING_INGREDIENT.test(arg);
    }

    public static boolean canSpawn(EntityType<OcelotEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return random.nextInt(3) != 0;
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        if (arg.intersectsEntities(this) && !arg.containsFluid(this.getBoundingBox())) {
            BlockPos lv = this.getBlockPos();
            if (lv.getY() < arg.getSeaLevel()) {
                return false;
            }
            BlockState lv2 = arg.getBlockState(lv.down());
            if (lv2.isOf(Blocks.GRASS_BLOCK) || lv2.isIn(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)arg4).setBabyChance(1.0f);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    static class OcelotTemptGoal
    extends TemptGoal {
        private final OcelotEntity ocelot;

        public OcelotTemptGoal(OcelotEntity arg, double d, Ingredient arg2, boolean bl) {
            super((MobEntityWithAi)arg, d, arg2, bl);
            this.ocelot = arg;
        }

        @Override
        protected boolean canBeScared() {
            return super.canBeScared() && !this.ocelot.isTrusting();
        }
    }

    static class FleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final OcelotEntity ocelot;

        public FleeGoal(OcelotEntity arg, Class<T> class_, float f, double d, double e) {
            super(arg, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.ocelot = arg;
        }

        @Override
        public boolean canStart() {
            return !this.ocelot.isTrusting() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.ocelot.isTrusting() && super.shouldContinue();
        }
    }
}

