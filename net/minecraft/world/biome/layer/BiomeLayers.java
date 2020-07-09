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

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long l, ParentedLayer arg, LayerFactory<T> arg2, int i, LongFunction<C> longFunction) {
        LayerFactory<T> lv = arg2;
        for (int j = 0; j < i; ++j) {
            lv = arg.create((LayerSampleContext)longFunction.apply(l + (long)j), lv);
        }
        return lv;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(boolean bl, int i, int j, LongFunction<C> longFunction) {
        LayerFactory lv = ContinentLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1L));
        lv = ScaleLayer.FUZZY.create((LayerSampleContext)longFunction.apply(2000L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)longFunction.apply(2001L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(50L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(70L), lv);
        lv = AddIslandLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L), lv);
        LayerFactory lv2 = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L));
        lv2 = BiomeLayers.stack(2001L, ScaleLayer.NORMAL, lv2, 6, longFunction);
        lv = AddColdClimatesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(3L), lv);
        lv = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L), lv);
        lv = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(2L), lv);
        lv = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(3L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)longFunction.apply(2002L), lv);
        lv = ScaleLayer.NORMAL.create((LayerSampleContext)longFunction.apply(2003L), lv);
        lv = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(4L), lv);
        lv = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(5L), lv);
        lv = AddDeepOceanLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(4L), lv);
        LayerFactory lv3 = lv = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv, 0, longFunction);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, 0, longFunction);
        lv3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(100L), lv3);
        LayerFactory lv4 = lv;
        lv4 = new SetBaseBiomesLayer(bl).create((LayerSampleContext)longFunction.apply(200L), lv4);
        lv4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), lv4);
        lv4 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv4, 2, longFunction);
        lv4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), lv4);
        LayerFactory lv5 = lv3;
        lv5 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv5, 2, longFunction);
        lv4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), lv4, lv5);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, 2, longFunction);
        lv3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, lv3, j, longFunction);
        lv3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1L), lv3);
        lv3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), lv3);
        lv4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), lv4);
        for (int k = 0; k < i; ++k) {
            lv4 = ScaleLayer.NORMAL.create((LayerSampleContext)longFunction.apply(1000 + k), lv4);
            if (k == 0) {
                lv4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(3L), lv4);
            }
            if (k != 1 && i != 1) continue;
            lv4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), lv4);
        }
        lv4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), lv4);
        lv4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(100L), lv4, lv3);
        lv4 = ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(100L), lv4, lv2);
        return lv4;
    }

    public static BiomeLayerSampler build(long l, boolean bl, int i, int j) {
        int k = 25;
        LayerFactory<CachingLayerSampler> lv = BiomeLayers.build(bl, i, j, m -> new CachingLayerContext(25, l, m));
        return new BiomeLayerSampler(lv);
    }

    public static boolean areSimilar(int i, int j) {
        if (i == j) {
            return true;
        }
        Biome lv = (Biome)BuiltinRegistries.BIOME.get(i);
        Biome lv2 = (Biome)BuiltinRegistries.BIOME.get(j);
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

    protected static boolean isOcean(int i) {
        return i == WARM_OCEAN_ID || i == LUKEWARM_OCEAN_ID || i == OCEAN_ID || i == COLD_OCEAN_ID || i == FROZEN_OCEAN_ID || i == DEEP_WARM_OCEAN_ID || i == DEEP_LUKEWARM_OCEAN_ID || i == DEEP_OCEAN_ID || i == DEEP_COLD_OCEAN_ID || i == DEEP_FROZEN_OCEAN_ID;
    }

    protected static boolean isShallowOcean(int i) {
        return i == WARM_OCEAN_ID || i == LUKEWARM_OCEAN_ID || i == OCEAN_ID || i == COLD_OCEAN_ID || i == FROZEN_OCEAN_ID;
    }
}

