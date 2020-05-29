/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureEntry;

public class RandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.apply2(RandomFeatureConfig::new, (App)RandomFeatureEntry.CODEC.listOf().fieldOf("features").forGetter(arg -> arg.features), (App)ConfiguredFeature.field_24833.fieldOf("default").forGetter(arg -> arg.defaultFeature)));
    public final List<RandomFeatureEntry<?>> features;
    public final ConfiguredFeature<?, ?> defaultFeature;

    public RandomFeatureConfig(List<RandomFeatureEntry<?>> list, ConfiguredFeature<?, ?> arg) {
        this.features = list;
        this.defaultFeature = arg;
    }
}

