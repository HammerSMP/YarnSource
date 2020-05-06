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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public abstract class FeatureSize {
    protected final FeatureSizeType<?> type;
    private final OptionalInt minClippedHeight;

    public FeatureSize(FeatureSizeType<?> arg, OptionalInt optionalInt) {
        this.type = arg;
        this.minClippedHeight = optionalInt;
    }

    public abstract int method_27378(int var1, int var2);

    public OptionalInt getMinClippedHeight() {
        return this.minClippedHeight;
    }

    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FEATURE_SIZE_TYPE.getId(this.type).toString()));
        this.minClippedHeight.ifPresent(i -> builder.put(dynamicOps.createString("min_clipped_height"), dynamicOps.createInt(i)));
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build())).getValue();
    }
}

