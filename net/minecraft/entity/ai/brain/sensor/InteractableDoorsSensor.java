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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class InteractableDoorsSensor
extends Sensor<LivingEntity> {
    @Override
    protected void sense(ServerWorld arg, LivingEntity arg2) {
        RegistryKey<World> lv = arg.method_27983();
        BlockPos lv2 = arg2.getBlockPos();
        ArrayList list = Lists.newArrayList();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos lv3 = lv2.add(i, j, k);
                    if (!arg.getBlockState(lv3).isIn(BlockTags.WOODEN_DOORS)) continue;
                    list.add(GlobalPos.create(lv, lv3));
                }
            }
        }
        Brain<?> lv4 = arg2.getBrain();
        if (!list.isEmpty()) {
            lv4.remember(MemoryModuleType.INTERACTABLE_DOORS, list);
        } else {
            lv4.forget(MemoryModuleType.INTERACTABLE_DOORS);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
    }
}

