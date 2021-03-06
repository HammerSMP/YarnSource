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
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public static final Codec<MineshaftFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").forGetter(arg -> Float.valueOf(arg.probability)), (App)MineshaftFeature.Type.field_24839.fieldOf("type").forGetter(arg -> arg.type)).apply((Applicative)instance, MineshaftFeatureConfig::new));
    public final float probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(float f, MineshaftFeature.Type arg) {
        this.probability = f;
        this.type = arg;
    }
}

