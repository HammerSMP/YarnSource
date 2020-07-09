/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_5428;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeaPickleFeatureConfig
implements DecoratorConfig,
FeatureConfig {
    public static final Codec<SeaPickleFeatureConfig> CODEC = class_5428.method_30316(-10, 128, 128).fieldOf("count").xmap(SeaPickleFeatureConfig::new, SeaPickleFeatureConfig::method_30396).codec();
    private final class_5428 count;

    public SeaPickleFeatureConfig(int i) {
        this.count = class_5428.method_30314(i);
    }

    public SeaPickleFeatureConfig(class_5428 arg) {
        this.count = arg;
    }

    public class_5428 method_30396() {
        return this.count;
    }
}

