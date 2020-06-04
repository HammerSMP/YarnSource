/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomRandomFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomRandomFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter(arg -> arg.features), (App)Codec.INT.fieldOf("count").withDefault((Object)0).forGetter(arg -> arg.count)).apply((Applicative)instance, RandomRandomFeatureConfig::new));
    public final List<ConfiguredFeature<?, ?>> features;
    public final int count;

    public RandomRandomFeatureConfig(List<ConfiguredFeature<?, ?>> list, int i) {
        this.features = list;
        this.count = i;
    }
}

