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

public class TwoLayersFeatureSize
extends FeatureSize {
    private final int field_24155;
    private final int field_24156;
    private final int field_24157;

    public TwoLayersFeatureSize(int i, int j, int k) {
        this(i, j, k, OptionalInt.empty());
    }

    public TwoLayersFeatureSize(int i, int j, int k, OptionalInt optionalInt) {
        super(FeatureSizeType.TWO_LAYERS_FEATURE_SIZE, optionalInt);
        this.field_24155 = i;
        this.field_24156 = j;
        this.field_24157 = k;
    }

    public <T> TwoLayersFeatureSize(Dynamic<T> dynamic) {
        this(dynamic.get("limit").asInt(1), dynamic.get("lower_size").asInt(0), dynamic.get("upper_size").asInt(1), dynamic.get("min_clipped_height").asNumber().map(number -> OptionalInt.of(number.intValue())).orElse(OptionalInt.empty()));
    }

    @Override
    public int method_27378(int i, int j) {
        return j < this.field_24155 ? this.field_24156 : this.field_24157;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("limit"), dynamicOps.createInt(this.field_24155)).put(dynamicOps.createString("lower_size"), dynamicOps.createInt(this.field_24156)).put(dynamicOps.createString("upper_size"), dynamicOps.createInt(this.field_24157));
        return (T)dynamicOps.merge(super.serialize(dynamicOps), dynamicOps.createMap((Map)builder.build()));
    }
}

