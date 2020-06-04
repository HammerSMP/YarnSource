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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter(arg -> arg.featureTrue), (App)ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter(arg -> arg.featureFalse)).apply((Applicative)instance, RandomBooleanFeatureConfig::new));
    public final ConfiguredFeature<?, ?> featureTrue;
    public final ConfiguredFeature<?, ?> featureFalse;

    public RandomBooleanFeatureConfig(ConfiguredFeature<?, ?> arg, ConfiguredFeature<?, ?> arg2) {
        this.featureTrue = arg;
        this.featureFalse = arg2;
    }
}

