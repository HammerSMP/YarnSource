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
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeagrassFeatureConfig
implements FeatureConfig {
    public static final Codec<SeagrassFeatureConfig> field_24907 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("count").forGetter(arg -> arg.count), (App)Codec.DOUBLE.fieldOf("probability").forGetter(arg -> arg.tallSeagrassProbability)).apply((Applicative)instance, SeagrassFeatureConfig::new));
    public final int count;
    public final double tallSeagrassProbability;

    public SeagrassFeatureConfig(int i, double d) {
        this.count = i;
        this.tallSeagrassProbability = d;
    }
}

