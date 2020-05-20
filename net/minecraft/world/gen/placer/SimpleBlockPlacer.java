/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.placer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class SimpleBlockPlacer
extends BlockPlacer {
    public SimpleBlockPlacer() {
        super(BlockPlacerType.SIMPLE_BLOCK_PLACER);
    }

    public <T> SimpleBlockPlacer(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void method_23403(WorldAccess arg, BlockPos arg2, BlockState arg3, Random random) {
        arg.setBlockState(arg2, arg3, 2);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString())))).getValue();
    }
}
