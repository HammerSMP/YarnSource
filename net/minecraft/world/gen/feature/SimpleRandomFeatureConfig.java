/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SimpleRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<SimpleRandomFeatureConfig> CODEC = ConfiguredFeature.CODEC.listOf().fieldOf("features").xmap(SimpleRandomFeatureConfig::new, arg -> arg.features).codec();
    public final List<ConfiguredFeature<?, ?>> features;

    public SimpleRandomFeatureConfig(List<ConfiguredFeature<?, ?>> list) {
        this.features = list;
    }
}

