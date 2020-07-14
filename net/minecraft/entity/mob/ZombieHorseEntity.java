/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ZombieHorseEntity
extends HorseBaseEntity {
    public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> arg, World arg2) {
        super((EntityType<? extends HorseBaseEntity>)arg, arg2);
    }

    public static DefaultAttributeContainer.Builder createZombieHorseAttributes() {
        return ZombieHorseEntity.createBaseHorseAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        return EntityType.ZOMBIE_HORSE.create(arg);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack lv = player.getStackInHand(hand);
        if (!this.isTame()) {
            return ActionResult.PASS;
        }
        if (this.isBaby()) {
            return super.interactMob(player, hand);
        }
        if (player.shouldCancelInteraction()) {
            this.openInventory(player);
            return ActionResult.success(this.world.isClient);
        }
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (!lv.isEmpty()) {
            if (lv.getItem() == Items.SADDLE && !this.isSaddled()) {
                this.openInventory(player);
                return ActionResult.success(this.world.isClient);
            }
            ActionResult lv2 = lv.useOnEntity(player, this, hand);
            if (lv2.isAccepted()) {
                return lv2;
            }
        }
        this.putPlayerOnBack(player);
        return ActionResult.success(this.world.isClient);
    }

    @Override
    protected void initCustomGoals() {
    }
}

