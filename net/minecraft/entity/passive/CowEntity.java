/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity
extends AnimalEntity {
    public CowEntity(EntityType<? extends CowEntity> arg, World arg2) {
        super((EntityType<? extends AnimalEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.25, Ingredient.ofItems(Items.WHEAT), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createCowAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_COW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COW_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15f, 1.0f);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() == Items.BUCKET && !this.isBaby()) {
            arg.playSound(SoundEvents.ENTITY_COW_MILK, 1.0f, 1.0f);
            if (!arg.abilities.creativeMode) {
                lv.decrement(1);
            }
            if (lv.isEmpty()) {
                arg.setStackInHand(arg2, new ItemStack(Items.MILK_BUCKET));
            } else if (!arg.inventory.insertStack(new ItemStack(Items.MILK_BUCKET))) {
                arg.dropItem(new ItemStack(Items.MILK_BUCKET), false);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(arg, arg2);
    }

    @Override
    public CowEntity createChild(PassiveEntity arg) {
        return EntityType.COW.create(this.world);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        if (this.isBaby()) {
            return arg2.height * 0.95f;
        }
        return 1.3f;
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }
}

