/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PandaEntity
extends AnimalEntity {
    private static final TrackedData<Integer> ASK_FOR_BAMBOO_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SNEEZE_PROGRESS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EATING_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> MAIN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> HIDDEN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> PANDA_FLAGS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TargetPredicate ASK_FOR_BAMBOO_TARGET = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
    private boolean shouldGetRevenge;
    private boolean shouldAttack;
    public int playingTicks;
    private Vec3d playingJump;
    private float scaredAnimationProgress;
    private float lastScaredAnimationProgress;
    private float lieOnBackAnimationProgress;
    private float lastLieOnBackAnimationProgress;
    private float rollOverAnimationProgress;
    private float lastRollOverAnimationProgress;
    private LookAtEntityGoal lookAtPlayerGoal;
    private static final Predicate<ItemEntity> IS_FOOD = arg -> {
        Item lv = arg.getStack().getItem();
        return (lv == Blocks.BAMBOO.asItem() || lv == Blocks.CAKE.asItem()) && arg.isAlive() && !arg.cannotPickup();
    };

    public PandaEntity(EntityType<? extends PandaEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.moveControl = new PandaMoveControl(this);
        if (!this.isBaby()) {
            this.setCanPickUpLoot(true);
        }
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        if (!this.getEquippedStack(lv).isEmpty()) {
            return false;
        }
        return lv == EquipmentSlot.MAINHAND && super.canPickUp(arg);
    }

    public int getAskForBambooTicks() {
        return this.dataTracker.get(ASK_FOR_BAMBOO_TICKS);
    }

    public void setAskForBambooTicks(int i) {
        this.dataTracker.set(ASK_FOR_BAMBOO_TICKS, i);
    }

    public boolean isSneezing() {
        return this.hasPandaFlag(2);
    }

    public boolean isScared() {
        return this.hasPandaFlag(8);
    }

    public void setScared(boolean bl) {
        this.setPandaFlag(8, bl);
    }

    public boolean isLyingOnBack() {
        return this.hasPandaFlag(16);
    }

    public void setLyingOnBack(boolean bl) {
        this.setPandaFlag(16, bl);
    }

    public boolean isEating() {
        return this.dataTracker.get(EATING_TICKS) > 0;
    }

    public void setEating(boolean bl) {
        this.dataTracker.set(EATING_TICKS, bl ? 1 : 0);
    }

    private int getEatingTicks() {
        return this.dataTracker.get(EATING_TICKS);
    }

    private void setEatingTicks(int i) {
        this.dataTracker.set(EATING_TICKS, i);
    }

    public void setSneezing(boolean bl) {
        this.setPandaFlag(2, bl);
        if (!bl) {
            this.setSneezeProgress(0);
        }
    }

    public int getSneezeProgress() {
        return this.dataTracker.get(SNEEZE_PROGRESS);
    }

    public void setSneezeProgress(int i) {
        this.dataTracker.set(SNEEZE_PROGRESS, i);
    }

    public Gene getMainGene() {
        return Gene.byId(this.dataTracker.get(MAIN_GENE).byteValue());
    }

    public void setMainGene(Gene arg) {
        if (arg.getId() > 6) {
            arg = Gene.createRandom(this.random);
        }
        this.dataTracker.set(MAIN_GENE, (byte)arg.getId());
    }

    public Gene getHiddenGene() {
        return Gene.byId(this.dataTracker.get(HIDDEN_GENE).byteValue());
    }

    public void setHiddenGene(Gene arg) {
        if (arg.getId() > 6) {
            arg = Gene.createRandom(this.random);
        }
        this.dataTracker.set(HIDDEN_GENE, (byte)arg.getId());
    }

    public boolean isPlaying() {
        return this.hasPandaFlag(4);
    }

    public void setPlaying(boolean bl) {
        this.setPandaFlag(4, bl);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ASK_FOR_BAMBOO_TICKS, 0);
        this.dataTracker.startTracking(SNEEZE_PROGRESS, 0);
        this.dataTracker.startTracking(MAIN_GENE, (byte)0);
        this.dataTracker.startTracking(HIDDEN_GENE, (byte)0);
        this.dataTracker.startTracking(PANDA_FLAGS, (byte)0);
        this.dataTracker.startTracking(EATING_TICKS, 0);
    }

    private boolean hasPandaFlag(int i) {
        return (this.dataTracker.get(PANDA_FLAGS) & i) != 0;
    }

    private void setPandaFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(PANDA_FLAGS);
        if (bl) {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b & ~i));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putString("MainGene", this.getMainGene().getName());
        arg.putString("HiddenGene", this.getHiddenGene().getName());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setMainGene(Gene.byName(arg.getString("MainGene")));
        this.setHiddenGene(Gene.byName(arg.getString("HiddenGene")));
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity arg) {
        PandaEntity lv = EntityType.PANDA.create(this.world);
        if (arg instanceof PandaEntity) {
            lv.initGenes(this, (PandaEntity)arg);
        }
        lv.resetAttributes();
        return lv;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new ExtinguishFireGoal(this, 2.0));
        this.goalSelector.add(2, new PandaMateGoal(this, 1.0));
        this.goalSelector.add(3, new AttackGoal(this, (double)1.2f, true));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.0, Ingredient.ofItems(Blocks.BAMBOO.asItem()), false));
        this.goalSelector.add(6, new PandaFleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.0, 2.0));
        this.goalSelector.add(6, new PandaFleeGoal<HostileEntity>(this, HostileEntity.class, 4.0f, 2.0, 2.0));
        this.goalSelector.add(7, new PickUpFoodGoal());
        this.goalSelector.add(8, new LieOnBackGoal(this));
        this.goalSelector.add(8, new SneezeGoal(this));
        this.lookAtPlayerGoal = new LookAtEntityGoal(this, PlayerEntity.class, 6.0f);
        this.goalSelector.add(9, this.lookAtPlayerGoal);
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.goalSelector.add(12, new PlayGoal(this));
        this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new PandaRevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createPandaAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    public Gene getProductGene() {
        return Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
    }

    public boolean isLazy() {
        return this.getProductGene() == Gene.LAZY;
    }

    public boolean isWorried() {
        return this.getProductGene() == Gene.WORRIED;
    }

    public boolean isPlayful() {
        return this.getProductGene() == Gene.PLAYFUL;
    }

    public boolean isWeak() {
        return this.getProductGene() == Gene.WEAK;
    }

    @Override
    public boolean isAttacking() {
        return this.getProductGene() == Gene.AGGRESSIVE;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity arg) {
        return false;
    }

    @Override
    public boolean tryAttack(Entity arg) {
        this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0f, 1.0f);
        if (!this.isAttacking()) {
            this.shouldAttack = true;
        }
        return super.tryAttack(arg);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.world.isThundering() && !this.isTouchingWater()) {
                this.setScared(true);
                this.setEating(false);
            } else if (!this.isEating()) {
                this.setScared(false);
            }
        }
        if (this.getTarget() == null) {
            this.shouldGetRevenge = false;
            this.shouldAttack = false;
        }
        if (this.getAskForBambooTicks() > 0) {
            if (this.getTarget() != null) {
                this.lookAtEntity(this.getTarget(), 90.0f, 90.0f);
            }
            if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
                this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0f, 1.0f);
            }
            this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
        }
        if (this.isSneezing()) {
            this.setSneezeProgress(this.getSneezeProgress() + 1);
            if (this.getSneezeProgress() > 20) {
                this.setSneezing(false);
                this.sneeze();
            } else if (this.getSneezeProgress() == 1) {
                this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0f, 1.0f);
            }
        }
        if (this.isPlaying()) {
            this.updatePlaying();
        } else {
            this.playingTicks = 0;
        }
        if (this.isScared()) {
            this.pitch = 0.0f;
        }
        this.updateScaredAnimation();
        this.updateEatingAnimation();
        this.updateLieOnBackAnimation();
        this.updateRollOverAnimation();
    }

    public boolean isScaredByThunderstorm() {
        return this.isWorried() && this.world.isThundering();
    }

    private void updateEatingAnimation() {
        if (!this.isEating() && this.isScared() && !this.isScaredByThunderstorm() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.setEating(true);
        } else if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || !this.isScared()) {
            this.setEating(false);
        }
        if (this.isEating()) {
            this.playEatingAnimation();
            if (!this.world.isClient && this.getEatingTicks() > 80 && this.random.nextInt(20) == 1) {
                if (this.getEatingTicks() > 100 && this.canEat(this.getEquippedStack(EquipmentSlot.MAINHAND))) {
                    if (!this.world.isClient) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    this.setScared(false);
                }
                this.setEating(false);
                return;
            }
            this.setEatingTicks(this.getEatingTicks() + 1);
        }
    }

    private void playEatingAnimation() {
        if (this.getEatingTicks() % 5 == 0) {
            this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            for (int i = 0; i < 6; ++i) {
                Vec3d lv = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
                lv = lv.rotateX(-this.pitch * ((float)Math.PI / 180));
                lv = lv.rotateY(-this.yaw * ((float)Math.PI / 180));
                double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
                Vec3d lv2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
                lv2 = lv2.rotateY(-this.bodyYaw * ((float)Math.PI / 180));
                lv2 = lv2.add(this.getX(), this.getEyeY() + 1.0, this.getZ());
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getEquippedStack(EquipmentSlot.MAINHAND)), lv2.x, lv2.y, lv2.z, lv.x, lv.y + 0.05, lv.z);
            }
        }
    }

    private void updateScaredAnimation() {
        this.lastScaredAnimationProgress = this.scaredAnimationProgress;
        this.scaredAnimationProgress = this.isScared() ? Math.min(1.0f, this.scaredAnimationProgress + 0.15f) : Math.max(0.0f, this.scaredAnimationProgress - 0.19f);
    }

    private void updateLieOnBackAnimation() {
        this.lastLieOnBackAnimationProgress = this.lieOnBackAnimationProgress;
        this.lieOnBackAnimationProgress = this.isLyingOnBack() ? Math.min(1.0f, this.lieOnBackAnimationProgress + 0.15f) : Math.max(0.0f, this.lieOnBackAnimationProgress - 0.19f);
    }

    private void updateRollOverAnimation() {
        this.lastRollOverAnimationProgress = this.rollOverAnimationProgress;
        this.rollOverAnimationProgress = this.isPlaying() ? Math.min(1.0f, this.rollOverAnimationProgress + 0.15f) : Math.max(0.0f, this.rollOverAnimationProgress - 0.19f);
    }

    @Environment(value=EnvType.CLIENT)
    public float getScaredAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getLieOnBackAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRollOverAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
    }

    private void updatePlaying() {
        ++this.playingTicks;
        if (this.playingTicks > 32) {
            this.setPlaying(false);
            return;
        }
        if (!this.world.isClient) {
            Vec3d lv = this.getVelocity();
            if (this.playingTicks == 1) {
                float f = this.yaw * ((float)Math.PI / 180);
                float g = this.isBaby() ? 0.1f : 0.2f;
                this.playingJump = new Vec3d(lv.x + (double)(-MathHelper.sin(f) * g), 0.0, lv.z + (double)(MathHelper.cos(f) * g));
                this.setVelocity(this.playingJump.add(0.0, 0.27, 0.0));
            } else if ((float)this.playingTicks == 7.0f || (float)this.playingTicks == 15.0f || (float)this.playingTicks == 23.0f) {
                this.setVelocity(0.0, this.onGround ? 0.27 : lv.y, 0.0);
            } else {
                this.setVelocity(this.playingJump.x, lv.y, this.playingJump.z);
            }
        }
    }

    private void sneeze() {
        Vec3d lv = this.getVelocity();
        this.world.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180)), this.getEyeY() - (double)0.1f, this.getZ() + (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180)), lv.x, 0.0, lv.z);
        this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0f, 1.0f);
        List<PandaEntity> list = this.world.getNonSpectatingEntities(PandaEntity.class, this.getBoundingBox().expand(10.0));
        for (PandaEntity lv2 : list) {
            if (lv2.isBaby() || !lv2.onGround || lv2.isTouchingWater() || !lv2.isIdle()) continue;
            lv2.jump();
        }
        if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SLIME_BALL);
        }
    }

    @Override
    protected void loot(ItemEntity arg) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && IS_FOOD.test(arg)) {
            this.method_27964(arg);
            ItemStack lv = arg.getStack();
            this.equipStack(EquipmentSlot.MAINHAND, lv);
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(arg, lv.getCount());
            arg.remove();
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        this.setScared(false);
        return super.damage(arg, f);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setMainGene(Gene.createRandom(this.random));
        this.setHiddenGene(Gene.createRandom(this.random));
        this.resetAttributes();
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)arg4).setBabyChance(0.2f);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    public void initGenes(PandaEntity arg, @Nullable PandaEntity arg2) {
        if (arg2 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(arg.getRandomGene());
                this.setHiddenGene(Gene.createRandom(this.random));
            } else {
                this.setMainGene(Gene.createRandom(this.random));
                this.setHiddenGene(arg.getRandomGene());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(arg.getRandomGene());
            this.setHiddenGene(arg2.getRandomGene());
        } else {
            this.setMainGene(arg2.getRandomGene());
            this.setHiddenGene(arg.getRandomGene());
        }
        if (this.random.nextInt(32) == 0) {
            this.setMainGene(Gene.createRandom(this.random));
        }
        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(Gene.createRandom(this.random));
        }
    }

    private Gene getRandomGene() {
        if (this.random.nextBoolean()) {
            return this.getMainGene();
        }
        return this.getHiddenGene();
    }

    public void resetAttributes() {
        if (this.isWeak()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.07f);
        }
    }

    private void stop() {
        if (!this.isTouchingWater()) {
            this.setForwardSpeed(0.0f);
            this.getNavigation().stop();
            this.setScared(true);
        }
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() instanceof SpawnEggItem) {
            return super.interactMob(arg, arg2);
        }
        if (this.isScaredByThunderstorm()) {
            return false;
        }
        if (this.isLyingOnBack()) {
            this.setLyingOnBack(false);
            return true;
        }
        if (this.isBreedingItem(lv)) {
            if (this.getTarget() != null) {
                this.shouldGetRevenge = true;
            }
            if (this.isBaby()) {
                this.eat(arg, lv);
                this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1f), true);
            } else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
                this.eat(arg, lv);
                this.lovePlayer(arg);
            } else if (!(this.world.isClient || this.isScared() || this.isTouchingWater())) {
                this.stop();
                this.setEating(true);
                ItemStack lv2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
                if (!lv2.isEmpty() && !arg.abilities.creativeMode) {
                    this.dropStack(lv2);
                }
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(lv.getItem(), 1));
                this.eat(arg, lv);
            } else {
                return false;
            }
            arg.swingHand(arg2, true);
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isAttacking()) {
            return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
        }
        if (this.isWorried()) {
            return SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT;
        }
        return SoundEvents.ENTITY_PANDA_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem() == Blocks.BAMBOO.asItem();
    }

    private boolean canEat(ItemStack arg) {
        return this.isBreedingItem(arg) || arg.getItem() == Blocks.CAKE.asItem();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PANDA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PANDA_HURT;
    }

    public boolean isIdle() {
        return !this.isLyingOnBack() && !this.isScaredByThunderstorm() && !this.isEating() && !this.isPlaying() && !this.isScared();
    }

    static class ExtinguishFireGoal
    extends EscapeDangerGoal {
        private final PandaEntity panda;

        public ExtinguishFireGoal(PandaEntity arg, double d) {
            super(arg, d);
            this.panda = arg;
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isOnFire()) {
                return false;
            }
            BlockPos lv = this.locateClosestWater(this.mob.world, this.mob, 5, 4);
            if (lv != null) {
                this.targetX = lv.getX();
                this.targetY = lv.getY();
                this.targetZ = lv.getZ();
                return true;
            }
            return this.findTarget();
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.isScared()) {
                this.panda.getNavigation().stop();
                return false;
            }
            return super.shouldContinue();
        }
    }

    static class PandaRevengeGoal
    extends RevengeGoal {
        private final PandaEntity panda;

        public PandaRevengeGoal(PandaEntity arg, Class<?> ... args) {
            super(arg, args);
            this.panda = arg;
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.shouldGetRevenge || this.panda.shouldAttack) {
                this.panda.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }

        @Override
        protected void setMobEntityTarget(MobEntity arg, LivingEntity arg2) {
            if (arg instanceof PandaEntity && ((PandaEntity)arg).isAttacking()) {
                arg.setTarget(arg2);
            }
        }
    }

    static class LieOnBackGoal
    extends Goal {
        private final PandaEntity panda;
        private int nextLieOnBackAge;

        public LieOnBackGoal(PandaEntity arg) {
            this.panda = arg;
        }

        @Override
        public boolean canStart() {
            return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.isIdle() && this.panda.random.nextInt(400) == 1;
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.isTouchingWater() || !this.panda.isLazy() && this.panda.random.nextInt(600) == 1) {
                return false;
            }
            return this.panda.random.nextInt(2000) != 1;
        }

        @Override
        public void start() {
            this.panda.setLyingOnBack(true);
            this.nextLieOnBackAge = 0;
        }

        @Override
        public void stop() {
            this.panda.setLyingOnBack(false);
            this.nextLieOnBackAge = this.panda.age + 200;
        }
    }

    class PickUpFoodGoal
    extends Goal {
        private int startAge;

        public PickUpFoodGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.startAge > PandaEntity.this.age || PandaEntity.this.isBaby() || PandaEntity.this.isTouchingWater() || !PandaEntity.this.isIdle() || PandaEntity.this.getAskForBambooTicks() > 0) {
                return false;
            }
            List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), IS_FOOD);
            return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public boolean shouldContinue() {
            if (PandaEntity.this.isTouchingWater() || !PandaEntity.this.isLazy() && PandaEntity.this.random.nextInt(600) == 1) {
                return false;
            }
            return PandaEntity.this.random.nextInt(2000) != 1;
        }

        @Override
        public void tick() {
            if (!PandaEntity.this.isScared() && !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.stop();
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), IS_FOOD);
            if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            } else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.stop();
            }
            this.startAge = 0;
        }

        @Override
        public void stop() {
            ItemStack lv = PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!lv.isEmpty()) {
                PandaEntity.this.dropStack(lv);
                PandaEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
                this.startAge = PandaEntity.this.age + i * 20;
            }
            PandaEntity.this.setScared(false);
        }
    }

    static class PandaFleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final PandaEntity panda;

        public PandaFleeGoal(PandaEntity arg, Class<T> arg2, float f, double d, double e) {
            super(arg, arg2, f, d, e, EntityPredicates.EXCEPT_SPECTATOR::test);
            this.panda = arg;
        }

        @Override
        public boolean canStart() {
            return this.panda.isWorried() && this.panda.isIdle() && super.canStart();
        }
    }

    class PandaMateGoal
    extends AnimalMateGoal {
        private final PandaEntity panda;
        private int nextAskPlayerForBambooAge;

        public PandaMateGoal(PandaEntity arg2, double d) {
            super(arg2, d);
            this.panda = arg2;
        }

        @Override
        public boolean canStart() {
            if (super.canStart() && this.panda.getAskForBambooTicks() == 0) {
                if (!this.isBambooClose()) {
                    if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                        this.panda.setAskForBambooTicks(32);
                        this.nextAskPlayerForBambooAge = this.panda.age + 600;
                        if (this.panda.canMoveVoluntarily()) {
                            PlayerEntity lv = this.world.getClosestPlayer(ASK_FOR_BAMBOO_TARGET, this.panda);
                            this.panda.lookAtPlayerGoal.setTarget(lv);
                        }
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean isBambooClose() {
            BlockPos lv = this.panda.getBlockPos();
            BlockPos.Mutable lv2 = new BlockPos.Mutable();
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int k = 0;
                    while (k <= j) {
                        int l;
                        int n = l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            lv2.set(lv, k, i, l);
                            if (this.world.getBlockState(lv2).isOf(Blocks.BAMBOO)) {
                                return true;
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                }
            }
            return false;
        }
    }

    static class SneezeGoal
    extends Goal {
        private final PandaEntity panda;

        public SneezeGoal(PandaEntity arg) {
            this.panda = arg;
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() || !this.panda.isIdle()) {
                return false;
            }
            if (this.panda.isWeak() && this.panda.random.nextInt(500) == 1) {
                return true;
            }
            return this.panda.random.nextInt(6000) == 1;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            this.panda.setSneezing(true);
        }
    }

    static class PlayGoal
    extends Goal {
        private final PandaEntity panda;

        public PlayGoal(PandaEntity arg) {
            this.panda = arg;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() && !this.panda.isPlayful() || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.isIdle()) {
                return false;
            }
            float f = this.panda.yaw * ((float)Math.PI / 180);
            int i = 0;
            int j = 0;
            float g = -MathHelper.sin(f);
            float h = MathHelper.cos(f);
            if ((double)Math.abs(g) > 0.5) {
                i = (int)((float)i + g / Math.abs(g));
            }
            if ((double)Math.abs(h) > 0.5) {
                j = (int)((float)j + h / Math.abs(h));
            }
            if (this.panda.world.getBlockState(this.panda.getBlockPos().add(i, -1, j)).isAir()) {
                return true;
            }
            if (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1) {
                return true;
            }
            return this.panda.random.nextInt(500) == 1;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            this.panda.setPlaying(true);
        }

        @Override
        public boolean canStop() {
            return false;
        }
    }

    static class LookAtEntityGoal
    extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
        private final PandaEntity panda;

        public LookAtEntityGoal(PandaEntity arg, Class<? extends LivingEntity> arg2, float f) {
            super(arg, arg2, f);
            this.panda = arg;
        }

        public void setTarget(LivingEntity arg) {
            this.target = arg;
        }

        @Override
        public boolean shouldContinue() {
            return this.target != null && super.shouldContinue();
        }

        @Override
        public boolean canStart() {
            if (this.mob.getRandom().nextFloat() >= this.chance) {
                return false;
            }
            if (this.target == null) {
                this.target = this.targetType == PlayerEntity.class ? this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetType, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().expand(this.range, 3.0, this.range));
            }
            return this.panda.isIdle() && this.target != null;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                super.tick();
            }
        }
    }

    static class AttackGoal
    extends MeleeAttackGoal {
        private final PandaEntity panda;

        public AttackGoal(PandaEntity arg, double d, boolean bl) {
            super(arg, d, bl);
            this.panda = arg;
        }

        @Override
        public boolean canStart() {
            return this.panda.isIdle() && super.canStart();
        }
    }

    static class PandaMoveControl
    extends MoveControl {
        private final PandaEntity panda;

        public PandaMoveControl(PandaEntity arg) {
            super(arg);
            this.panda = arg;
        }

        @Override
        public void tick() {
            if (!this.panda.isIdle()) {
                return;
            }
            super.tick();
        }
    }

    public static enum Gene {
        NORMAL(0, "normal", false),
        LAZY(1, "lazy", false),
        WORRIED(2, "worried", false),
        PLAYFUL(3, "playful", false),
        BROWN(4, "brown", true),
        WEAK(5, "weak", true),
        AGGRESSIVE(6, "aggressive", false);

        private static final Gene[] VALUES;
        private final int id;
        private final String name;
        private final boolean recessive;

        private Gene(int j, String string2, boolean bl) {
            this.id = j;
            this.name = string2;
            this.recessive = bl;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public boolean isRecessive() {
            return this.recessive;
        }

        private static Gene getProductGene(Gene arg, Gene arg2) {
            if (arg.isRecessive()) {
                if (arg == arg2) {
                    return arg;
                }
                return NORMAL;
            }
            return arg;
        }

        public static Gene byId(int i) {
            if (i < 0 || i >= VALUES.length) {
                i = 0;
            }
            return VALUES[i];
        }

        public static Gene byName(String string) {
            for (Gene lv : Gene.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            return NORMAL;
        }

        public static Gene createRandom(Random random) {
            int i = random.nextInt(16);
            if (i == 0) {
                return LAZY;
            }
            if (i == 1) {
                return WORRIED;
            }
            if (i == 2) {
                return PLAYFUL;
            }
            if (i == 4) {
                return AGGRESSIVE;
            }
            if (i < 9) {
                return WEAK;
            }
            if (i < 11) {
                return BROWN;
            }
            return NORMAL;
        }

        static {
            VALUES = (Gene[])Arrays.stream(Gene.values()).sorted(Comparator.comparingInt(Gene::getId)).toArray(Gene[]::new);
        }
    }
}

