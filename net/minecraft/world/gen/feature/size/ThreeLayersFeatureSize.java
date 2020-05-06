/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature.size;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public class ThreeLayersFeatureSize
extends FeatureSize {
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public ThreeLayersFeatureSize(int i, int j, int k, int l, int m, OptionalInt optionalInt) {
        super(FeatureSizeType.THREE_LAYERS_FEATURE_SIZE, optionalInt);
        this.limit = i;
        this.upperLimit = j;
        this.lowerSize = k;
        this.middleSize = l;
        this.upperSize = m;
    }

    public <T> ThreeLayersFeatureSize(Dynamic<T> dynamic) {
        this(dynamic.get("limit").asInt(1), dynamic.get("upper_limit").asInt(1), dynamic.get("lower_size").asInt(0), dynamic.get("middle_size").asInt(1), dynamic.get("upper_size").asInt(1), dynamic.get("min_clipped_height").asNumber().map(number -> OptionalInt.of(number.intValue())).orElse(OptionalInt.empty()));
    }

    @Override
    public int method_27378(int i, int j) {
        if (j < this.limit) {
            return this.lowerSize;
        }
        if (j >= i - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("limit"), dynamicOps.createInt(this.limit)).put(dynamicOps.createString("upper_limit"), dynamicOps.createInt(this.upperLimit)).put(dynamicOps.createString("lower_size"), dynamicOps.createInt(this.lowerSize)).put(dynamicOps.createString("middle_size"), dynamicOps.createInt(this.middleSize)).put(dynamicOps.createString("upper_size"), dynamicOps.createInt(this.upperSize));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

