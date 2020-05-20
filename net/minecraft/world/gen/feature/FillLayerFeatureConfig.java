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
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FillLayerFeatureConfig
implements FeatureConfig {
    public final int height;
    public final BlockState state;

    public FillLayerFeatureConfig(int i, BlockState arg) {
        this.height = i;
        this.state = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("height"), (Object)dynamicOps.createInt(this.height), (Object)dynamicOps.createString("state"), (Object)BlockState.serialize(dynamicOps, this.state).getValue())));
    }

    public static <T> FillLayerFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("height").asInt(0);
        BlockState lv = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new FillLayerFeatureConfig(i, lv);
    }
}
