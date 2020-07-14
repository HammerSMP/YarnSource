/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class HoglinSpecificSensor
extends Sensor<HoglinEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object[])new MemoryModuleType[0]);
    }

    @Override
    protected void sense(ServerWorld arg, HoglinEntity arg2) {
        Brain<HoglinEntity> lv = arg2.getBrain();
        lv.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestWarpedFungus(arg, arg2));
        Optional<Object> optional = Optional.empty();
        int i = 0;
        ArrayList list = Lists.newArrayList();
        List<LivingEntity> list2 = lv.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
        for (LivingEntity lv2 : list2) {
            if (lv2 instanceof PiglinEntity && !lv2.isBaby()) {
                ++i;
                if (!optional.isPresent()) {
                    optional = Optional.of((PiglinEntity)lv2);
                }
            }
            if (!(lv2 instanceof HoglinEntity) || lv2.isBaby()) continue;
            list.add((HoglinEntity)lv2);
        }
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
        lv.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, list);
        lv.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, i);
        lv.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, list.size());
    }

    private Optional<BlockPos> findNearestWarpedFungus(ServerWorld world, HoglinEntity hoglin) {
        return BlockPos.findClosest(hoglin.getBlockPos(), 8, 4, arg2 -> world.getBlockState((BlockPos)arg2).isIn(BlockTags.HOGLIN_REPELLENTS));
    }
}

