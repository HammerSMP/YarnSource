/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.NumberCodecs;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig), (App)FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers), (App)NumberCodecs.method_29905(Registry.BIOME.fieldOf("biome"), Util.method_29188("Unknown biome, defaulting to plains", ((Logger)LOGGER)::error), () -> Biomes.PLAINS).forGetter(arg -> arg.biome)).apply((Applicative)instance, FlatChunkGeneratorConfig::new)).stable();
    private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState())).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)));
    private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState())).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)));
    private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(StructureFeature.MINESHAFT, DefaultBiomeFeatures.NORMAL_MINESHAFT);
        hashMap.put(StructureFeature.VILLAGE, DefaultBiomeFeatures.PLAINS_VILLAGE);
        hashMap.put(StructureFeature.STRONGHOLD, DefaultBiomeFeatures.STRONGHOLD);
        hashMap.put(StructureFeature.SWAMP_HUT, DefaultBiomeFeatures.SWAMP_HUT);
        hashMap.put(StructureFeature.DESERT_PYRAMID, DefaultBiomeFeatures.DESERT_PYRAMID);
        hashMap.put(StructureFeature.JUNGLE_PYRAMID, DefaultBiomeFeatures.JUNGLE_PYRAMID);
        hashMap.put(StructureFeature.IGLOO, DefaultBiomeFeatures.IGLOO);
        hashMap.put(StructureFeature.OCEAN_RUIN, DefaultBiomeFeatures.COLD_OCEAN_RUIN);
        hashMap.put(StructureFeature.SHIPWRECK, DefaultBiomeFeatures.SUNKEN_SHIPWRECK);
        hashMap.put(StructureFeature.MONUMENT, DefaultBiomeFeatures.MONUMENT);
        hashMap.put(StructureFeature.END_CITY, DefaultBiomeFeatures.END_CITY);
        hashMap.put(StructureFeature.MANSION, DefaultBiomeFeatures.MANSION);
        hashMap.put(StructureFeature.FORTRESS, DefaultBiomeFeatures.FORTRESS);
        hashMap.put(StructureFeature.PILLAGER_OUTPOST, DefaultBiomeFeatures.PILLAGER_OUTPOST);
        hashMap.put(StructureFeature.RUINED_PORTAL, DefaultBiomeFeatures.STANDARD_RUINED_PORTAL);
        hashMap.put(StructureFeature.BASTION_REMNANT, DefaultBiomeFeatures.BASTION_REMNANT);
    });
    private final StructuresConfig config;
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private Biome biome;
    private final BlockState[] layerBlocks = new BlockState[256];
    private boolean hasNoTerrain;
    private boolean field_24976 = false;
    private boolean field_24977 = false;

    public FlatChunkGeneratorConfig(StructuresConfig arg, List<FlatChunkGeneratorLayer> list, Biome arg2) {
        this(arg);
        this.layers.addAll(list);
        this.updateLayerBlocks();
        this.biome = arg2;
    }

    public FlatChunkGeneratorConfig(StructuresConfig arg) {
        this.config = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public FlatChunkGeneratorConfig method_28912(StructuresConfig arg) {
        return this.method_29965(this.layers, arg);
    }

    @Environment(value=EnvType.CLIENT)
    public FlatChunkGeneratorConfig method_29965(List<FlatChunkGeneratorLayer> list, StructuresConfig arg) {
        FlatChunkGeneratorConfig lv = new FlatChunkGeneratorConfig(arg);
        for (FlatChunkGeneratorLayer lv2 : list) {
            lv.layers.add(new FlatChunkGeneratorLayer(lv2.getThickness(), lv2.getBlockState().getBlock()));
            lv.updateLayerBlocks();
        }
        lv.setBiome(this.biome);
        if (this.field_24976) {
            lv.method_28911();
        }
        if (this.field_24977) {
            lv.method_28916();
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public void method_28911() {
        this.field_24976 = true;
    }

    @Environment(value=EnvType.CLIENT)
    public void method_28916() {
        this.field_24977 = true;
    }

    public Biome method_28917() {
        boolean bl;
        Biome lv = this.getBiome();
        Biome lv2 = new Biome(new Biome.Settings().surfaceBuilder(lv.getSurfaceBuilder()).precipitation(lv.getPrecipitation()).category(lv.getCategory()).depth(lv.getDepth()).scale(lv.getScale()).temperature(lv.getTemperature()).downfall(lv.getRainfall()).effects(lv.getEffects()).parent(lv.getParent())){};
        if (this.field_24977) {
            lv2.addFeature(GenerationStep.Feature.LAKES, WATER_LAKE);
            lv2.addFeature(GenerationStep.Feature.LAKES, LAVA_LAKE);
        }
        for (Map.Entry<StructureFeature<?>, StructureConfig> entry : this.config.getStructures().entrySet()) {
            lv2.addStructureFeature(lv.method_28405(STRUCTURE_TO_FEATURES.get(entry.getKey())));
        }
        boolean bl2 = bl = (!this.hasNoTerrain || lv == Biomes.THE_VOID) && this.field_24976;
        if (bl) {
            ArrayList list = Lists.newArrayList();
            list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            list.add(GenerationStep.Feature.SURFACE_STRUCTURES);
            for (GenerationStep.Feature lv3 : GenerationStep.Feature.values()) {
                if (list.contains(lv3)) continue;
                for (ConfiguredFeature<?, ?> lv4 : lv.getFeaturesForStep(lv3)) {
                    lv2.addFeature(lv3, lv4);
                }
            }
        }
        BlockState[] lvs = this.getLayerBlocks();
        for (int i = 0; i < lvs.length; ++i) {
            BlockState lv5 = lvs[i];
            if (lv5 == null || Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(lv5)) continue;
            this.layerBlocks[i] = null;
            lv2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, lv5)));
        }
        return lv2;
    }

    public StructuresConfig getConfig() {
        return this.config;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void setBiome(Biome arg) {
        this.biome = arg;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public BlockState[] getLayerBlocks() {
        return this.layerBlocks;
    }

    public void updateLayerBlocks() {
        Arrays.fill(this.layerBlocks, 0, this.layerBlocks.length, null);
        int i = 0;
        for (FlatChunkGeneratorLayer lv : this.layers) {
            lv.setStartY(i);
            i += lv.getThickness();
        }
        this.hasNoTerrain = true;
        for (FlatChunkGeneratorLayer lv2 : this.layers) {
            for (int j = lv2.getStartY(); j < lv2.getStartY() + lv2.getThickness(); ++j) {
                BlockState lv3 = lv2.getBlockState();
                if (lv3.isOf(Blocks.AIR)) continue;
                this.hasNoTerrain = false;
                this.layerBlocks[j] = lv3;
            }
        }
    }

    public static FlatChunkGeneratorConfig getDefaultConfig() {
        StructuresConfig lv = new StructuresConfig(Optional.of(StructuresConfig.DEFAULT_STRONGHOLD), Maps.newHashMap((Map)ImmutableMap.of(StructureFeature.VILLAGE, (Object)StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.VILLAGE))));
        FlatChunkGeneratorConfig lv2 = new FlatChunkGeneratorConfig(lv);
        lv2.setBiome(Biomes.PLAINS);
        lv2.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        lv2.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
        lv2.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
        lv2.updateLayerBlocks();
        return lv2;
    }
}

