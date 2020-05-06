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

public class HugeMushroomFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider capProvider;
    public final BlockStateProvider stemProvider;
    public final int capSize;

    public HugeMushroomFeatureConfig(BlockStateProvider arg, BlockStateProvider arg2, int i) {
        this.capProvider = arg;
        this.stemProvider = arg2;
        this.capSize = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("cap_provider"), this.capProvider.serialize(dynamicOps)).put(dynamicOps.createString("stem_provider"), this.stemProvider.serialize(dynamicOps)).put(dynamicOps.createString("foliage_radius"), dynamicOps.createInt(this.capSize));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    public static <T> HugeMushroomFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockStateProviderType<?> lv = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        BlockStateProviderType<?> lv2 = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new HugeMushroomFeatureConfig((BlockStateProvider)lv.deserialize(dynamic.get("cap_provider").orElseEmptyMap()), (BlockStateProvider)lv2.deserialize(dynamic.get("stem_provider").orElseEmptyMap()), dynamic.get("foliage_radius").asInt(2));
    }
}

