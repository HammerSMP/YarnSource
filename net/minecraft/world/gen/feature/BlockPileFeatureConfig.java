/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class BlockPileFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider stateProvider;

    public BlockPileFeatureConfig(BlockStateProvider arg) {
        this.stateProvider = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("state_provider"), this.stateProvider.serialize(dynamicOps));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    public static <T> BlockPileFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockStateProviderType<?> lv = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new BlockPileFeatureConfig((BlockStateProvider)lv.deserialize(dynamic.get("state_provider").orElseEmptyMap()));
    }
}

