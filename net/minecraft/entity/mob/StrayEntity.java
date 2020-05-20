/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class StrayEntity
extends AbstractSkeletonEntity {
    public StrayEntity(EntityType<? extends StrayEntity> arg, World arg2) {
        super((EntityType<? extends AbstractSkeletonEntity>)arg, arg2);
    }

    public static boolean canSpawn(EntityType<StrayEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return StrayEntity.canSpawnInDark(arg, arg2, arg3, arg4, random) && (arg3 == SpawnReason.SPAWNER || arg2.isSkyVisible(arg4));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.ENTITY_STRAY_STEP;
    }

    @Override
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arg, float f) {
        PersistentProjectileEntity lv = super.createArrowProjectile(arg, f);
        if (lv instanceof ArrowEntity) {
            ((ArrowEntity)lv).addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
        }
        return lv;
    }
}

