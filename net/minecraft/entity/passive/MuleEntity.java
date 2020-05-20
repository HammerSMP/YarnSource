/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MuleEntity
extends AbstractDonkeyEntity {
    public MuleEntity(EntityType<? extends MuleEntity> arg, World arg2) {
        super((EntityType<? extends AbstractDonkeyEntity>)arg, arg2);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_MULE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_MULE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        super.getHurtSound(arg);
        return SoundEvents.ENTITY_MULE_HURT;
    }

    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public PassiveEntity createChild(PassiveEntity arg) {
        return EntityType.MULE.create(this.world);
    }
}
