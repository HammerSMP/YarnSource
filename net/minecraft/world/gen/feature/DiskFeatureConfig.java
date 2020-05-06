/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DiskFeatureConfig
implements FeatureConfig {
    public final BlockState state;
    public final int radius;
    public final int ySize;
    public final List<BlockState> targets;

    public DiskFeatureConfig(BlockState arg, int i, int j, List<BlockState> list) {
        this.state = arg;
        this.radius = i;
        this.ySize = j;
        this.targets = list;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("state"), (Object)BlockState.serialize(dynamicOps, this.state).getValue(), (Object)dynamicOps.createString("radius"), (Object)dynamicOps.createInt(this.radius), (Object)dynamicOps.createString("y_size"), (Object)dynamicOps.createInt(this.ySize), (Object)dynamicOps.createString("targets"), (Object)dynamicOps.createList(this.targets.stream().map(arg -> BlockState.serialize(dynamicOps, arg).getValue())))));
    }

    public static <T> DiskFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState lv = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int i = dynamic.get("radius").asInt(0);
        int j = dynamic.get("y_size").asInt(0);
        List list = dynamic.get("targets").asList(BlockState::deserialize);
        return new DiskFeatureConfig(lv, i, j, list);
    }
}

