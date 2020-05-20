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
import net.minecraft.world.gen.feature.OceanRuinFeature;

public class OceanRuinFeatureConfig
implements FeatureConfig {
    public static final Codec<OceanRuinFeatureConfig> field_24895 = RecordCodecBuilder.create(instance -> instance.group((App)OceanRuinFeature.BiomeType.field_24990.fieldOf("biome_temp").forGetter(arg -> arg.biomeType), (App)Codec.FLOAT.fieldOf("large_probability").forGetter(arg -> Float.valueOf(arg.largeProbability)), (App)Codec.FLOAT.fieldOf("cluster_probability").forGetter(arg -> Float.valueOf(arg.clusterProbability))).apply((Applicative)instance, OceanRuinFeatureConfig::new));
    public final OceanRuinFeature.BiomeType biomeType;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinFeatureConfig(OceanRuinFeature.BiomeType arg, float f, float g) {
        this.biomeType = arg;
        this.largeProbability = f;
        this.clusterProbability = g;
    }
}

