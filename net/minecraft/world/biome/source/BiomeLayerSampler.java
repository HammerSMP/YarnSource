/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome.source;

import net.minecraft.SharedConstants;
import net.minecraft.class_5458;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CachingLayerSampler sampler;

    public BiomeLayerSampler(LayerFactory<CachingLayerSampler> arg) {
        this.sampler = arg.make();
    }

    private Biome getBiome(int i) {
        Biome lv = (Biome)class_5458.field_25933.get(i);
        if (lv == null) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + i));
            }
            LOGGER.warn("Unknown biome id: ", (Object)i);
            return Biomes.DEFAULT;
        }
        return lv;
    }

    public Biome sample(int i, int j) {
        return this.getBiome(this.sampler.sample(i, j));
    }
}

