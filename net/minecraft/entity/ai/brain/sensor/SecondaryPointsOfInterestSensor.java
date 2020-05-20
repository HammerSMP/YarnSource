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
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SecondaryPointsOfInterestSensor
extends Sensor<VillagerEntity> {
    public SecondaryPointsOfInterestSensor() {
        super(40);
    }

    @Override
    protected void sense(ServerWorld arg, VillagerEntity arg2) {
        DimensionType lv = arg.method_27983();
        BlockPos lv2 = arg2.getBlockPos();
        ArrayList list = Lists.newArrayList();
        int i = 4;
        for (int j = -4; j <= 4; ++j) {
            for (int k = -2; k <= 2; ++k) {
                for (int l = -4; l <= 4; ++l) {
                    BlockPos lv3 = lv2.add(j, k, l);
                    if (!arg2.getVillagerData().getProfession().getSecondaryJobSites().contains((Object)arg.getBlockState(lv3).getBlock())) continue;
                    list.add(GlobalPos.create(lv, lv3));
                }
            }
        }
        Brain<VillagerEntity> lv4 = arg2.getBrain();
        if (!list.isEmpty()) {
            lv4.remember(MemoryModuleType.SECONDARY_JOB_SITE, list);
        } else {
            lv4.forget(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}

