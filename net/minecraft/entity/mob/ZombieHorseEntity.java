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
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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
    protected SoundEvent getHurtSound(DamageSource arg) {
        super.getHurtSound(arg);
        return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity arg) {
        return EntityType.ZOMBIE_HORSE.create(this.world);
    }

    @Override
    public boolean interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (lv.getItem() instanceof SpawnEggItem) {
            return super.interactMob(arg, arg2);
        }
        if (!this.isTame()) {
            return false;
        }
        if (this.isBaby()) {
            return super.interactMob(arg, arg2);
        }
        if (arg.shouldCancelInteraction()) {
            this.openInventory(arg);
            return true;
        }
        if (this.hasPassengers()) {
            return super.interactMob(arg, arg2);
        }
        if (!lv.isEmpty()) {
            if (!this.isSaddled() && lv.getItem() == Items.SADDLE) {
                this.openInventory(arg);
                return true;
            }
            if (lv.useOnEntity(arg, this, arg2)) {
                return true;
            }
        }
        this.putPlayerOnBack(arg);
        return true;
    }

    @Override
    protected void initCustomGoals() {
    }
}
