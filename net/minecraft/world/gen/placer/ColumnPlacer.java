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
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class ColumnPlacer
extends BlockPlacer {
    private final int minSize;
    private final int extraSize;

    public ColumnPlacer(int i, int j) {
        super(BlockPlacerType.COLUMN_PLACER);
        this.minSize = i;
        this.extraSize = j;
    }

    public <T> ColumnPlacer(Dynamic<T> dynamic) {
        this(dynamic.get("min_size").asInt(1), dynamic.get("extra_size").asInt(2));
    }

    @Override
    public void method_23403(IWorld arg, BlockPos arg2, BlockState arg3, Random random) {
        BlockPos.Mutable lv = arg2.mutableCopy();
        int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);
        for (int j = 0; j < i; ++j) {
            arg.setBlockState(lv, arg3, 2);
            lv.move(Direction.UP);
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.BLOCK_PLACER_TYPE.getId(this.type).toString()), (Object)dynamicOps.createString("min_size"), (Object)dynamicOps.createInt(this.minSize), (Object)dynamicOps.createString("extra_size"), (Object)dynamicOps.createInt(this.extraSize)))).getValue();
    }
}

