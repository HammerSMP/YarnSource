/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BuriedTreasureFeatureConfig
implements FeatureConfig {
    public static final Codec<BuriedTreasureFeatureConfig> CODEC = Codec.FLOAT.xmap(BuriedTreasureFeatureConfig::new, arg -> Float.valueOf(arg.probability));
    public final float probability;

    public BuriedTreasureFeatureConfig(float f) {
        this.probability = f;
    }
}

