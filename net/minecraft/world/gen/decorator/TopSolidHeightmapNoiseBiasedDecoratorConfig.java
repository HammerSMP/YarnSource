/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig
implements DecoratorConfig {
    public static final Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> field_24987 = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("noise_to_count_ratio").forGetter(arg -> arg.noiseToCountRatio), (App)Codec.DOUBLE.fieldOf("noise_factor").forGetter(arg -> arg.noiseFactor), (App)Codec.DOUBLE.fieldOf("noise_offset").withDefault((Object)0.0).forGetter(arg -> arg.noiseOffset), (App)Heightmap.Type.field_24772.fieldOf("heightmap").forGetter(arg -> arg.heightmap)).apply((Applicative)instance, TopSolidHeightmapNoiseBiasedDecoratorConfig::new));
    private static final Logger field_24988 = LogManager.getLogger();
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;
    public final Heightmap.Type heightmap;

    public TopSolidHeightmapNoiseBiasedDecoratorConfig(int i, double d, double e, Heightmap.Type arg) {
        this.noiseToCountRatio = i;
        this.noiseFactor = d;
        this.noiseOffset = e;
        this.heightmap = arg;
    }
}

