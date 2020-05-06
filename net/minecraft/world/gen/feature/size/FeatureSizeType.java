/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;

public class FeatureSizeType<P extends FeatureSize> {
    public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE = FeatureSizeType.register("two_layers_feature_size", TwoLayersFeatureSize::new);
    public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE = FeatureSizeType.register("three_layers_feature_size", ThreeLayersFeatureSize::new);
    private final Function<Dynamic<?>, P> deserializer;

    private static <P extends FeatureSize> FeatureSizeType<P> register(String string, Function<Dynamic<?>, P> function) {
        return Registry.register(Registry.FEATURE_SIZE_TYPE, string, new FeatureSizeType<P>(function));
    }

    private FeatureSizeType(Function<Dynamic<?>, P> function) {
        this.deserializer = function;
    }

    public P method_27381(Dynamic<?> dynamic) {
        return (P)((FeatureSize)this.deserializer.apply(dynamic));
    }
}

