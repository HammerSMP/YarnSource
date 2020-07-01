/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.AbstractPiglinEntity;
import net.minecraft.PiglinBrute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class PiglinSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object[])new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void sense(ServerWorld arg, LivingEntity arg2) {
        Brain<?> lv = arg2.getBrain();
        lv.remember(MemoryModuleType.NEAREST_REPELLENT, PiglinSpecificSensor.findSoulFire(arg, arg2));
        Optional<Object> optional = Optional.empty();
        Optional<Object> optional2 = Optional.empty();
        Optional<Object> optional3 = Optional.empty();
        Optional<Object> optional4 = Optional.empty();
        Optional<Object> optional5 = Optional.empty();
        Optional<Object> optional6 = Optional.empty();
        Optional<Object> optional7 = Optional.empty();
        int i = 0;
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        List<LivingEntity> list3 = lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse((List<LivingEntity>)ImmutableList.of());
        for (LivingEntity lv2 : list3) {
            if (lv2 instanceof HoglinEntity) {
                HoglinEntity lv3 = (HoglinEntity)lv2;
                if (lv3.isBaby() && !optional3.isPresent()) {
                    optional3 = Optional.of(lv3);
                    continue;
                }
                if (!lv3.isAdult()) continue;
                ++i;
                if (optional2.isPresent() || !lv3.canBeHunted()) continue;
                optional2 = Optional.of(lv3);
                continue;
            }
            if (lv2 instanceof PiglinBrute) {
                list.add((PiglinBrute)lv2);
                continue;
            }
            if (lv2 instanceof PiglinEntity) {
                PiglinEntity lv4 = (PiglinEntity)lv2;
                if (lv4.isBaby() && !optional4.isPresent()) {
                    optional4 = Optional.of(lv4);
                    continue;
                }
                if (!lv4.method_30236()) continue;
                list.add(lv4);
                continue;
            }
            if (lv2 instanceof PlayerEntity) {
                PlayerEntity lv5 = (PlayerEntity)lv2;
                if (!optional6.isPresent() && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(lv2) && !PiglinBrain.wearsGoldArmor(lv5)) {
                    optional6 = Optional.of(lv5);
                }
                if (optional7.isPresent() || lv5.isSpectator() || !PiglinBrain.isGoldHoldingPlayer(lv5)) continue;
                optional7 = Optional.of(lv5);
                continue;
            }
            if (!optional.isPresent() && (lv2 instanceof WitherSkeletonEntity || lv2 instanceof WitherEntity)) {
                optional = Optional.of((MobEntity)lv2);
                continue;
            }
            if (optional5.isPresent() || !PiglinBrain.isZombified(lv2.getType())) continue;
            optional5 = Optional.of(lv2);
        }
        List<LivingEntity> list4 = lv.getOptionalMemory(MemoryModuleType.MOBS).orElse((List<LivingEntity>)ImmutableList.of());
        for (LivingEntity lv6 : list4) {
            if (!(lv6 instanceof AbstractPiglinEntity) || !((AbstractPiglinEntity)lv6).method_30236()) continue;
            list2.add((AbstractPiglinEntity)lv6);
        }
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, optional2);
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional3);
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, optional5);
        lv.remember(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional6);
        lv.remember(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional7);
        lv.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list2);
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
        lv.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
        lv.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
    }

    private static Optional<BlockPos> findSoulFire(ServerWorld arg, LivingEntity arg22) {
        return BlockPos.findClosest(arg22.getBlockPos(), 8, 4, arg2 -> PiglinSpecificSensor.method_24648(arg, arg2));
    }

    private static boolean method_24648(ServerWorld arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        boolean bl = lv.isIn(BlockTags.PIGLIN_REPELLENTS);
        if (bl && lv.isOf(Blocks.SOUL_CAMPFIRE)) {
            return CampfireBlock.isLitCampfire(lv);
        }
        return bl;
    }
}

