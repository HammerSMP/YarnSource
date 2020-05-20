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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DecoratedFeatureConfig
implements FeatureConfig {
    public static final Codec<DecoratedFeatureConfig> field_24880 = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.field_24833.fieldOf("feature").forGetter(arg -> arg.feature), (App)ConfiguredDecorator.field_24981.fieldOf("decorator").forGetter(arg -> arg.decorator)).apply((Applicative)instance, DecoratedFeatureConfig::new));
    public final ConfiguredFeature<?, ?> feature;
    public final ConfiguredDecorator<?> decorator;

    public DecoratedFeatureConfig(ConfiguredFeature<?, ?> arg, ConfiguredDecorator<?> arg2) {
        this.feature = arg;
        this.decorator = arg2;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId((Feature<?>)this.feature.feature), Registry.DECORATOR.getId(this.decorator.decorator));
    }
}

