/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity
extends ZombieEntity {
    public HuskEntity(EntityType<? extends HuskEntity> arg, World arg2) {
        super((EntityType<? extends ZombieEntity>)arg, arg2);
    }

    public static boolean canSpawn(EntityType<HuskEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        return HuskEntity.canSpawnInDark(arg, arg2, arg3, arg4, random) && (arg3 == SpawnType.SPAWNER || arg2.isSkyVisible(arg4));
    }

    @Override
    protected boolean burnsInDaylight() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HUSK_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_HUSK_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_HUSK_STEP;
    }

    @Override
    public boolean tryAttack(Entity arg) {
        boolean bl = super.tryAttack(arg);
        if (bl && this.getMainHandStack().isEmpty() && arg instanceof LivingEntity) {
            float f = this.world.getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
            ((LivingEntity)arg).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f));
        }
        return bl;
    }

    @Override
    protected boolean canConvertInWater() {
        return true;
    }

    @Override
    protected void convertInWater() {
        this.convertTo(EntityType.ZOMBIE);
        if (!this.isSilent()) {
            this.world.syncWorldEvent(null, 1041, this.getBlockPos(), 0);
        }
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
}

