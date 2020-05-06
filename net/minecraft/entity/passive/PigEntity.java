/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PigEntity
extends AnimalEntity
implements ItemSteerable,
Saddleable {
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private final SaddledComponent saddledComponent;

    public PigEntity(EntityType<? extends PigEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.2, false, BREEDING_INGREDIENT));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createPigAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
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
    public boolean canBeControlledByRider() {
        Entity lv = this.getPrimaryPassenger();
        if (!(lv instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity lv2 = (PlayerEntity)lv;
        return lv2.getMainHandStack().getItem() == Items.CARROT_ON_A_STICK || lv2.getOffHandStack().getItem() == Items.CARROT_ON_A_STICK;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (BOOST_TIME.equals(arg) && this.world.isClient) {
            this.saddledComponent.boost();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(BOOST_TIME, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        this.saddledComponent.toTag(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.saddledComponent.fromTag(arg);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        if (!super.interactMob(arg, arg2)) {
            if (this.isSaddled() && !this.hasPassengers()) {
                if (!this.world.isClient) {
                    arg.startRiding(this);
                }
                return true;
            }
            ItemStack lv = arg.getStackInHand(arg2);
            return lv.getItem() == Items.SADDLE && lv.useOnEntity(arg, this, arg2);
        }
        return true;
    }

    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.SADDLE);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.saddledComponent.isSaddled();
    }

    @Override
    public void saddle(@Nullable SoundCategory arg) {
        this.saddledComponent.setSaddled(true);
        if (arg != null) {
            this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PIG_SADDLE, arg, 0.5f, 1.0f);
        }
    }

    @Override
    public void onStruckByLightning(LightningEntity arg) {
        ZombifiedPiglinEntity lv = EntityType.ZOMBIFIED_PIGLIN.create(this.world);
        lv.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        lv.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        lv.setAiDisabled(this.isAiDisabled());
        lv.setBaby(this.isBaby());
        if (this.hasCustomName()) {
            lv.setCustomName(this.getCustomName());
            lv.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(lv);
        this.remove();
    }

    @Override
    public void travel(Vec3d arg) {
        if (this.travel(this, this.saddledComponent, arg)) {
            double e;
            this.lastLimbDistance = this.limbDistance;
            double d = this.getX() - this.prevX;
            float f = MathHelper.sqrt(d * d + (e = this.getZ() - this.prevZ) * e) * 4.0f;
            if (f > 1.0f) {
                f = 1.0f;
            }
            this.limbDistance += (f - this.limbDistance) * 0.4f;
            this.limbAngle += this.limbDistance;
        }
    }

    @Override
    public float getSaddledSpeed() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.225f;
    }

    @Override
    public void setMovementInput(Vec3d arg) {
        super.travel(arg);
    }

    @Override
    public boolean consumeOnAStickItem() {
        return this.saddledComponent.boost(this.getRandom());
    }

    @Override
    public PigEntity createChild(PassiveEntity arg) {
        return EntityType.PIG.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return BREEDING_INGREDIENT.test(arg);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }
}

