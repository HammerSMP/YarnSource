/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class IcePatchFeatureConfig
implements FeatureConfig {
    public static final Codec<IcePatchFeatureConfig> field_24884 = Codec.INT.fieldOf("radius").xmap(IcePatchFeatureConfig::new, arg -> arg.radius).codec();
    public final int radius;

    public IcePatchFeatureConfig(int i) {
        this.radius = i;
    }
}

