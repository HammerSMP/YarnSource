/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CountConfig
implements DecoratorConfig,
FeatureConfig {
    public static final Codec<CountConfig> CODEC = UniformIntDistribution.createValidatedCodec(-10, 128, 128).fieldOf("count").xmap(CountConfig::new, CountConfig::method_30396).codec();
    private final UniformIntDistribution count;

    public CountConfig(int count) {
        this.count = UniformIntDistribution.of(count);
    }

    public CountConfig(UniformIntDistribution arg) {
        this.count = arg;
    }

    public UniformIntDistribution method_30396() {
        return this.count;
    }
}
