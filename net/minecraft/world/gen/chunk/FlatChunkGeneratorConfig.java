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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5464;
import net.minecraft.class_5470;
import net.minecraft.util.Util;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<FlatChunkGeneratorConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructuresConfig.CODEC.fieldOf("structures").forGetter(FlatChunkGeneratorConfig::getConfig), (App)FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers), (App)Codec.BOOL.fieldOf("lakes").orElse((Object)false).forGetter(arg -> arg.field_24977), (App)Codec.BOOL.fieldOf("features").orElse((Object)false).forGetter(arg -> arg.field_24976), (App)Biome.field_24677.fieldOf("biome").orElseGet(Util.method_29188("Unknown biome, defaulting to plains", ((Logger)LOGGER)::error), () -> () -> Biomes.PLAINS).forGetter(arg -> arg.biome)).apply((Applicative)instance, FlatChunkGeneratorConfig::new)).stable();
    private static final Map<StructureFeature<?>, ConfiguredStructureFeature<?, ?>> STRUCTURE_TO_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(StructureFeature.MINESHAFT, class_5470.MINESHAFT);
        hashMap.put(StructureFeature.VILLAGE, class_5470.VILLAGE_PLAINS);
        hashMap.put(StructureFeature.STRONGHOLD, class_5470.STRONGHOLD);
        hashMap.put(StructureFeature.SWAMP_HUT, class_5470.SWAMP_HUT);
        hashMap.put(StructureFeature.DESERT_PYRAMID, class_5470.DESERT_PYRAMID);
        hashMap.put(StructureFeature.JUNGLE_PYRAMID, class_5470.JUNGLE_PYRAMID);
        hashMap.put(StructureFeature.IGLOO, class_5470.IGLOO);
        hashMap.put(StructureFeature.OCEAN_RUIN, class_5470.OCEAN_RUIN_COLD);
        hashMap.put(StructureFeature.SHIPWRECK, class_5470.SHIPWRECK);
        hashMap.put(StructureFeature.MONUMENT, class_5470.MONUMENT);
        hashMap.put(StructureFeature.END_CITY, class_5470.END_CITY);
        hashMap.put(StructureFeature.MANSION, class_5470.MANSION);
        hashMap.put(StructureFeature.FORTRESS, class_5470.FORTRESS);
        hashMap.put(StructureFeature.PILLAGER_OUTPOST, class_5470.PILLAGER_OUTPOST);
        hashMap.put(StructureFeature.RUINED_PORTAL, class_5470.RUINED_PORTAL);
        hashMap.put(StructureFeature.BASTION_REMNANT, class_5470.BASTION_REMNANT);
    });
    private final StructuresConfig config;
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private Supplier<Biome> biome = () -> Biomes.PLAINS;
    private final BlockState[] layerBlocks = new BlockState[256];
    private boolean hasNoTerrain;
    private boolean field_24976 = false;
    private boolean field_24977 = false;

    public FlatChunkGeneratorConfig(StructuresConfig arg, List<FlatChunkGeneratorLayer> list, boolean bl, boolean bl2, Supplier<Biome> supplier) {
        this(arg);
        if (bl) {
            this.method_28916();
        }
        if (bl2) {
            this.method_28911();
        }
        this.layers.addAll(list);
        this.updateLayerBlocks();
        this.biome = supplier;
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
        lv.setBiome(this.biome.get());
        if (this.field_24976) {
            lv.method_28911();
        }
        if (this.field_24977) {
            lv.method_28916();
        }
        return lv;
    }

    public void method_28911() {
        this.field_24976 = true;
    }

    public void method_28916() {
        this.field_24977 = true;
    }

    public Biome method_28917() {
        boolean bl;
        Biome lv = this.getBiome();
        Biome lv2 = new Biome(new Biome.Settings().surfaceBuilder(lv.getSurfaceBuilder()).precipitation(lv.getPrecipitation()).category(lv.getCategory()).depth(lv.getDepth()).scale(lv.getScale()).temperature(lv.getTemperature()).downfall(lv.getRainfall()).effects(lv.getEffects()).parent(lv.getParent())){};
        if (this.field_24977) {
            lv2.addFeature(GenerationStep.Feature.LAKES, class_5464.LAKE_WATER);
            lv2.addFeature(GenerationStep.Feature.LAKES, class_5464.LAKE_LAVA);
        }
        for (Map.Entry<StructureFeature<?>, StructureConfig> entry : this.config.getStructures().entrySet()) {
            lv2.addStructureFeature(lv.method_28405(STRUCTURE_TO_FEATURES.get(entry.getKey())));
        }
        boolean bl2 = bl = (!this.hasNoTerrain || lv == Biomes.THE_VOID) && this.field_24976;
        if (bl) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> list = lv.method_30357();
            for (int i = 0; i < list.size(); ++i) {
                if (i == GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() || i == GenerationStep.Feature.SURFACE_STRUCTURES.ordinal()) continue;
                List<Supplier<ConfiguredFeature<?, ?>>> list2 = list.get(i);
                for (Supplier<ConfiguredFeature<?, ?>> supplier : list2) {
                    lv2.method_30350(i, supplier);
                }
            }
        }
        BlockState[] lvs = this.getLayerBlocks();
        for (int j = 0; j < lvs.length; ++j) {
            BlockState lv3 = lvs[j];
            if (lv3 == null || Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(lv3)) continue;
            this.layerBlocks[j] = null;
            lv2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(j, lv3)));
        }
        return lv2;
    }

    public StructuresConfig getConfig() {
        return this.config;
    }

    public Biome getBiome() {
        return this.biome.get();
    }

    public void setBiome(Biome arg) {
        this.biome = () -> arg;
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

