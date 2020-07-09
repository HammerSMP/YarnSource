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
import java.util.function.Supplier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomBooleanFeatureConfig
implements FeatureConfig {
    public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter(arg -> arg.featureTrue), (App)ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter(arg -> arg.featureFalse)).apply((Applicative)instance, RandomBooleanFeatureConfig::new));
    public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
    public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

    public RandomBooleanFeatureConfig(Supplier<ConfiguredFeature<?, ?>> supplier, Supplier<ConfiguredFeature<?, ?>> supplier2) {
        this.featureTrue = supplier;
        this.featureFalse = supplier2;
    }
}

