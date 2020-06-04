/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeaPickleFeatureConfig
implements FeatureConfig {
    public static final Codec<SeaPickleFeatureConfig> CODEC = Codec.INT.fieldOf("count").xmap(SeaPickleFeatureConfig::new, arg -> arg.count).codec();
    public final int count;

    public SeaPickleFeatureConfig(int i) {
        this.count = i;
    }
}

