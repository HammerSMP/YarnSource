/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.biome.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.AddColdClimatesLayer;
import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddIslandLayer;
import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.SimpleLandNoiseLayer;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayers {
    protected static final int WARM_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.WARM_OCEAN);
    protected static final int LUKEWARM_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.LUKEWARM_OCEAN);
    protected static final int OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.OCEAN);
    protected static final int COLD_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.COLD_OCEAN);
    protected static final int FROZEN_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.FROZEN_OCEAN);
    protected static final int DEEP_WARM_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DEEP_WARM_OCEAN);
    protected static final int DEEP_LUKEWARM_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DEEP_LUKEWARM_OCEAN);
    protected static final int DEEP_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DEEP_OCEAN);
    protected static final int DEEP_COLD_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DEEP_COLD_OCEAN);
    protected static final int DEEP_FROZEN_OCEAN_ID = BuiltinRegistries.BIOME.getRawId(Biomes.DEEP_FROZEN_OCEAN);

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> lv = parent;
        for (int j = 0; j < count; ++j) {
            lv = layer.create((LayerSampleContext)contextProvider.apply(seed + (long)j), lv);
        }
        return lv;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(boolean old, int biomeSize, int riverSize, LongFunction<C> contextProvider) {
        LayerFactory lv = ContinentLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L));
        lv = ScaleLayer.FUZZY.create((LayerSampleContext)contextProvider.apply(2000L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2001L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(50L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(70L), lv);
        lv = AddIslandLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), lv);
        LayerFactory lv2 = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L));
        lv2 = BiomeLayers.stack(2001L, ScaleLayer.NORMAL, lv2, 6, contextProvider);
        lv = AddColdClimatesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), lv);
        lv = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), lv);
        lv = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), lv);
        lv = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2002L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2003L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(4L), lv);
        lv = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(5L), lv);
        lv = AddDeepOceanLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(4L), lv);
        LayerFactory lv3 = lv = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv, 0, contextProvider);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, 0, contextProvider);
        lv3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), lv3);
        LayerFactory lv4 = lv;
        lv4 = new SetBaseBiomesLayer(old).create((LayerSampleContext)contextProvider.apply(200L), lv4);
        lv4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), lv4);
        lv4 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv4, 2, contextProvider);
        lv4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), lv4);
        LayerFactory lv5 = lv3;
        lv5 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv5, 2, contextProvider);
        lv4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), lv4, lv5);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, 2, contextProvider);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, riverSize, contextProvider);
        lv3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), lv3);
        lv3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), lv3);
        lv4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), lv4);
        for (int k = 0; k < biomeSize; ++k) {
            lv4 = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(1000 + k), lv4);
            if (k == 0) {
                lv4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), lv4);
            }
            if (k != 1 && biomeSize != 1) continue;
            lv4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), lv4);
        }
        lv4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), lv4);
        lv4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), lv4, lv3);
        lv4 = ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), lv4, lv2);
        return lv4;
    }

    public static BiomeLayerSampler build(long seed, boolean old, int biomeSize, int riverSize) {
        int k = 25;
        LayerFactory<CachingLayerSampler> lv = BiomeLayers.build(old, biomeSize, riverSize, salt -> new CachingLayerContext(25, seed, salt));
        return new BiomeLayerSampler(lv);
    }

    public static boolean areSimilar(int id1, int id2) {
        if (id1 == id2) {
            return true;
        }
        Biome lv = (Biome)BuiltinRegistries.BIOME.get(id1);
        Biome lv2 = (Biome)BuiltinRegistries.BIOME.get(id2);
        if (lv == null || lv2 == null) {
            return false;
        }
        if (lv == Biomes.WOODED_BADLANDS_PLATEAU || lv == Biomes.BADLANDS_PLATEAU) {
            return lv2 == Biomes.WOODED_BADLANDS_PLATEAU || lv2 == Biomes.BADLANDS_PLATEAU;
        }
        if (lv.getCategory() != Biome.Category.NONE && lv2.getCategory() != Biome.Category.NONE && lv.getCategory() == lv2.getCategory()) {
            return true;
        }
        return lv == lv2;
    }

    protected static boolean isOcean(int id) {
        return id == WARM_OCEAN_ID || id == LUKEWARM_OCEAN_ID || id == OCEAN_ID || id == COLD_OCEAN_ID || id == FROZEN_OCEAN_ID || id == DEEP_WARM_OCEAN_ID || id == DEEP_LUKEWARM_OCEAN_ID || id == DEEP_OCEAN_ID || id == DEEP_COLD_OCEAN_ID || id == DEEP_FROZEN_OCEAN_ID;
    }

    protected static boolean isShallowOcean(int id) {
        return id == WARM_OCEAN_ID || id == LUKEWARM_OCEAN_ID || id == OCEAN_ID || id == COLD_OCEAN_ID || id == FROZEN_OCEAN_ID;
    }
}

