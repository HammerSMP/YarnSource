/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;

public class SkeletonHorseTrapTriggerGoal
extends Goal {
    private final SkeletonHorseEntity skeletonHorse;

    public SkeletonHorseTrapTriggerGoal(SkeletonHorseEntity arg) {
        this.skeletonHorse = arg;
    }

    @Override
    public boolean canStart() {
        return this.skeletonHorse.world.isPlayerInRange(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ(), 10.0);
    }

    @Override
    public void tick() {
        LocalDifficulty lv = this.skeletonHorse.world.getLocalDifficulty(this.skeletonHorse.getBlockPos());
        this.skeletonHorse.setTrapped(false);
        this.skeletonHorse.setTame(true);
        this.skeletonHorse.setBreedingAge(0);
        ((ServerWorld)this.skeletonHorse.world).addLightning(new LightningEntity(this.skeletonHorse.world, this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ(), true));
        SkeletonEntity lv2 = this.getSkeleton(lv, this.skeletonHorse);
        lv2.startRiding(this.skeletonHorse);
        for (int i = 0; i < 3; ++i) {
            HorseBaseEntity lv3 = this.getHorse(lv);
            SkeletonEntity lv4 = this.getSkeleton(lv, lv3);
            lv4.startRiding(lv3);
            lv3.addVelocity(this.skeletonHorse.getRandom().nextGaussian() * 0.5, 0.0, this.skeletonHorse.getRandom().nextGaussian() * 0.5);
        }
    }

    private HorseBaseEntity getHorse(LocalDifficulty arg) {
        SkeletonHorseEntity lv = EntityType.SKELETON_HORSE.create(this.skeletonHorse.world);
        lv.initialize(this.skeletonHorse.world, arg, SpawnType.TRIGGERED, null, null);
        lv.updatePosition(this.skeletonHorse.getX(), this.skeletonHorse.getY(), this.skeletonHorse.getZ());
        lv.timeUntilRegen = 60;
        lv.setPersistent();
        lv.setTame(true);
        lv.setBreedingAge(0);
        lv.world.spawnEntity(lv);
        return lv;
    }

    private SkeletonEntity getSkeleton(LocalDifficulty arg, HorseBaseEntity arg2) {
        SkeletonEntity lv = EntityType.SKELETON.create(arg2.world);
        lv.initialize(arg2.world, arg, SpawnType.TRIGGERED, null, null);
        lv.updatePosition(arg2.getX(), arg2.getY(), arg2.getZ());
        lv.timeUntilRegen = 60;
        lv.setPersistent();
        if (lv.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            lv.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }
        lv.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(lv.getRandom(), lv.getMainHandStack(), (int)(5.0f + arg.getClampedLocalDifficulty() * (float)lv.getRandom().nextInt(18)), false));
        lv.equipStack(EquipmentSlot.HEAD, EnchantmentHelper.enchant(lv.getRandom(), lv.getEquippedStack(EquipmentSlot.HEAD), (int)(5.0f + arg.getClampedLocalDifficulty() * (float)lv.getRandom().nextInt(18)), false));
        lv.world.spawnEntity(lv);
        return lv;
    }
}

