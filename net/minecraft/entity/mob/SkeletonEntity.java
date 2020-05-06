/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity
extends AbstractSkeletonEntity {
    public SkeletonEntity(EntityType<? extends SkeletonEntity> arg, World arg2) {
        super((EntityType<? extends AbstractSkeletonEntity>)arg, arg2);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }

    @Override
    protected void dropEquipment(DamageSource arg, int i, boolean bl) {
        CreeperEntity lv2;
        super.dropEquipment(arg, i, bl);
        Entity lv = arg.getAttacker();
        if (lv instanceof CreeperEntity && (lv2 = (CreeperEntity)lv).shouldDropHead()) {
            lv2.onHeadDropped();
            this.dropItem(Items.SKELETON_SKULL);
        }
    }
}

