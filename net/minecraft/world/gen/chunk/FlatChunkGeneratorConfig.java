/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.chunk;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGeneratorConfig
extends ChunkGeneratorConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> MINESHAFT = Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> VILLAGE = Feature.VILLAGE.configure(new StructurePoolFeatureConfig("village/plains/town_centers", 6));
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> STRONGHOLD = Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> SWAMP_HUT = Feature.SWAMP_HUT.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> DESERT_PYRAMID = Feature.DESERT_PYRAMID.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> IGLOO = Feature.IGLOO.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> SHIPWRECK = Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false));
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> OCEAN_MONUMENT = Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> END_CITY = Feature.END_CITY.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> WOODLAND_MANSION = Feature.WOODLAND_MANSION.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> NETHER_BRIDGE = Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> field_24017 = Feature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig());
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> OCEAN_RUIN = Feature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.1f));
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST.configure(FeatureConfig.DEFAULT);
    private static final ConfiguredFeature<?, ? extends StructureFeature<?>> field_24422 = Feature.BASTION_REMNANT.configure(new BastionRemnantFeatureConfig((Map<String, Integer>)BastionRemnantGenerator.START_POOLS_TO_SIZES));
    private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState())).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)));
    private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState())).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)));
    public static final Map<ConfiguredFeature<?, ?>, GenerationStep.Feature> FEATURE_TO_GENERATION_STEP = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MINESHAFT, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(VILLAGE, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(STRONGHOLD, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(SWAMP_HUT, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(DESERT_PYRAMID, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(JUNGLE_TEMPLE, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(IGLOO, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(field_24017, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(SHIPWRECK, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(OCEAN_RUIN, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(WATER_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
        hashMap.put(LAVA_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
        hashMap.put(END_CITY, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(WOODLAND_MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(NETHER_BRIDGE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(OCEAN_MONUMENT, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(PILLAGER_OUTPOST, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(field_24422, GenerationStep.Feature.SURFACE_STRUCTURES);
    });
    public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURE_TO_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("mineshaft", new ConfiguredFeature[]{MINESHAFT});
        hashMap.put("village", new ConfiguredFeature[]{VILLAGE});
        hashMap.put("stronghold", new ConfiguredFeature[]{STRONGHOLD});
        hashMap.put("biome_1", new ConfiguredFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_TEMPLE, IGLOO, OCEAN_RUIN, SHIPWRECK});
        hashMap.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT});
        hashMap.put("lake", new ConfiguredFeature[]{WATER_LAKE});
        hashMap.put("lava_lake", new ConfiguredFeature[]{LAVA_LAKE});
        hashMap.put("endcity", new ConfiguredFeature[]{END_CITY});
        hashMap.put("mansion", new ConfiguredFeature[]{WOODLAND_MANSION});
        hashMap.put("fortress", new ConfiguredFeature[]{NETHER_BRIDGE});
        hashMap.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST});
        hashMap.put("ruined_portal", new ConfiguredFeature[]{field_24017});
        hashMap.put("bastion_remnant", new ConfiguredFeature[]{field_24422});
    });
    public static final Map<ConfiguredFeature<?, ? extends StructureFeature<?>>, FeatureConfig> FEATURE_TO_FEATURE_CONFIG = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        hashMap.put(VILLAGE, new StructurePoolFeatureConfig("village/plains/town_centers", 6));
        hashMap.put(STRONGHOLD, FeatureConfig.DEFAULT);
        hashMap.put(SWAMP_HUT, FeatureConfig.DEFAULT);
        hashMap.put(DESERT_PYRAMID, FeatureConfig.DEFAULT);
        hashMap.put(JUNGLE_TEMPLE, FeatureConfig.DEFAULT);
        hashMap.put(IGLOO, FeatureConfig.DEFAULT);
        hashMap.put(OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
        hashMap.put(SHIPWRECK, new ShipwreckFeatureConfig(false));
        hashMap.put(OCEAN_MONUMENT, FeatureConfig.DEFAULT);
        hashMap.put(END_CITY, FeatureConfig.DEFAULT);
        hashMap.put(WOODLAND_MANSION, FeatureConfig.DEFAULT);
        hashMap.put(NETHER_BRIDGE, FeatureConfig.DEFAULT);
        hashMap.put(PILLAGER_OUTPOST, FeatureConfig.DEFAULT);
        hashMap.put(field_24422, new BastionRemnantFeatureConfig((Map<String, Integer>)BastionRemnantGenerator.START_POOLS_TO_SIZES));
    });
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private final Map<String, Map<String, String>> structures = Maps.newHashMap();
    private Biome biome;
    private final BlockState[] layerBlocks = new BlockState[256];
    private boolean hasNoTerrain;
    private int groundHeight;

    @Nullable
    public static Block parseBlock(String string) {
        try {
            Identifier lv = new Identifier(string);
            return Registry.BLOCK.getOrEmpty(lv).orElse(null);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.warn("Invalid blockstate: {}", (Object)string, (Object)illegalArgumentException);
            return null;
        }
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void setBiome(Biome arg) {
        this.biome = arg;
    }

    public Map<String, Map<String, String>> getStructures() {
        return this.structures;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public void updateLayerBlocks() {
        int i = 0;
        for (FlatChunkGeneratorLayer lv : this.layers) {
            lv.setStartY(i);
            i += lv.getThickness();
        }
        this.groundHeight = 0;
        this.hasNoTerrain = true;
        int j = 0;
        for (FlatChunkGeneratorLayer lv2 : this.layers) {
            for (int k = lv2.getStartY(); k < lv2.getStartY() + lv2.getThickness(); ++k) {
                BlockState lv3 = lv2.getBlockState();
                if (lv3.isOf(Blocks.AIR)) continue;
                this.hasNoTerrain = false;
                this.layerBlocks[k] = lv3;
            }
            if (lv2.getBlockState().isOf(Blocks.AIR)) {
                j += lv2.getThickness();
                continue;
            }
            this.groundHeight += lv2.getThickness() + j;
            j = 0;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.layers.size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(this.layers.get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(Registry.BIOME.getId(this.biome));
        stringBuilder.append(";");
        if (!this.structures.isEmpty()) {
            int j = 0;
            for (Map.Entry<String, Map<String, String>> entry : this.structures.entrySet()) {
                if (j++ > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
                Map<String, String> map = entry.getValue();
                if (map.isEmpty()) continue;
                stringBuilder.append("(");
                int k = 0;
                for (Map.Entry<String, String> entry2 : map.entrySet()) {
                    if (k++ > 0) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append(entry2.getKey());
                    stringBuilder.append("=");
                    stringBuilder.append(entry2.getValue());
                }
                stringBuilder.append(")");
            }
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    @Environment(value=EnvType.CLIENT)
    private static FlatChunkGeneratorLayer parseLayerString(String string, int i) {
        void lv2;
        int k;
        String[] strings = string.split("\\*", 2);
        if (strings.length == 2) {
            try {
                int j = Math.max(Integer.parseInt(strings[0]), 0);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)numberFormatException.getMessage());
                return null;
            }
        } else {
            k = 1;
        }
        int l = Math.min(i + k, 256);
        int m = l - i;
        try {
            Block lv = FlatChunkGeneratorConfig.parseBlock(strings[strings.length - 1]);
        }
        catch (Exception exception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            return null;
        }
        if (lv2 == null) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)strings[strings.length - 1]);
            return null;
        }
        FlatChunkGeneratorLayer lv3 = new FlatChunkGeneratorLayer(m, (Block)lv2);
        lv3.setStartY(i);
        return lv3;
    }

    @Environment(value=EnvType.CLIENT)
    private static List<FlatChunkGeneratorLayer> parseLayersString(String string) {
        ArrayList list = Lists.newArrayList();
        String[] strings = string.split(",");
        int i = 0;
        for (String string2 : strings) {
            FlatChunkGeneratorLayer lv = FlatChunkGeneratorConfig.parseLayerString(string2, i);
            if (lv == null) {
                return Collections.emptyList();
            }
            list.add(lv);
            i += lv.getThickness();
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createList(this.layers.stream().map(arg -> dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("height"), (Object)dynamicOps.createInt(arg.getThickness()), (Object)dynamicOps.createString("block"), (Object)dynamicOps.createString(Registry.BLOCK.getId(arg.getBlockState().getBlock()).toString())))));
        Object object2 = dynamicOps.createMap(this.structures.entrySet().stream().map(entry2 -> Pair.of((Object)dynamicOps.createString(((String)entry2.getKey()).toLowerCase(Locale.ROOT)), (Object)dynamicOps.createMap(((Map)entry2.getValue()).entrySet().stream().map(entry -> Pair.of((Object)dynamicOps.createString((String)entry.getKey()), (Object)dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("layers"), (Object)object, (Object)dynamicOps.createString("biome"), (Object)dynamicOps.createString(Registry.BIOME.getId(this.biome).toString()), (Object)dynamicOps.createString("structures"), (Object)object2)));
    }

    public static FlatChunkGeneratorConfig fromDynamic(Dynamic<?> dynamic2) {
        FlatChunkGeneratorConfig lv = ChunkGeneratorType.FLAT.createConfig();
        List list = dynamic2.get("layers").asList(dynamic -> Pair.of((Object)dynamic.get("height").asInt(1), (Object)FlatChunkGeneratorConfig.parseBlock(dynamic.get("block").asString(""))));
        if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        List list2 = list.stream().map(pair -> new FlatChunkGeneratorLayer((Integer)pair.getFirst(), (Block)pair.getSecond())).collect(Collectors.toList());
        if (list2.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        lv.getLayers().addAll(list2);
        lv.updateLayerBlocks();
        lv.setBiome(Registry.BIOME.get(new Identifier(dynamic2.get("biome").asString(""))));
        dynamic2.get("structures").flatMap(Dynamic::getMapValues).ifPresent(map -> map.keySet().forEach(dynamic -> dynamic.asString().map(string -> lv.getStructures().put((String)string, Maps.newHashMap()))));
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static FlatChunkGeneratorConfig fromString(String string) {
        Iterator iterator = Splitter.on((char)';').split((CharSequence)string).iterator();
        if (!iterator.hasNext()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        FlatChunkGeneratorConfig lv = ChunkGeneratorType.FLAT.createConfig();
        List<FlatChunkGeneratorLayer> list = FlatChunkGeneratorConfig.parseLayersString((String)iterator.next());
        if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        lv.getLayers().addAll(list);
        lv.updateLayerBlocks();
        Biome lv2 = Biomes.PLAINS;
        if (iterator.hasNext()) {
            try {
                Identifier lv3 = new Identifier((String)iterator.next());
                lv2 = Registry.BIOME.getOrEmpty(lv3).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + lv3));
            }
            catch (Exception exception) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            }
        }
        lv.setBiome(lv2);
        if (iterator.hasNext()) {
            String[] strings;
            for (String string2 : strings = ((String)iterator.next()).toLowerCase(Locale.ROOT).split(",")) {
                String[] strings3;
                String[] strings2 = string2.split("\\(", 2);
                if (strings2[0].isEmpty()) continue;
                lv.addStructure(strings2[0]);
                if (strings2.length <= 1 || !strings2[1].endsWith(")") || strings2[1].length() <= 1) continue;
                for (String string3 : strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ")) {
                    String[] strings4 = string3.split("=", 2);
                    if (strings4.length != 2) continue;
                    lv.setStructureOption(strings2[0], strings4[0], strings4[1]);
                }
            }
        } else {
            lv.getStructures().put("village", Maps.newHashMap());
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    private void addStructure(String string) {
        HashMap map = Maps.newHashMap();
        this.structures.put(string, map);
    }

    @Environment(value=EnvType.CLIENT)
    private void setStructureOption(String string, String string2, String string3) {
        this.structures.get(string).put(string2, string3);
        if ("village".equals(string) && "distance".equals(string2)) {
            this.villageDistance = MathHelper.parseInt(string3, this.villageDistance, 9);
        }
        if ("biome_1".equals(string) && "distance".equals(string2)) {
            this.templeDistance = MathHelper.parseInt(string3, this.templeDistance, 9);
        }
        if ("stronghold".equals(string)) {
            if ("distance".equals(string2)) {
                this.strongholdDistance = MathHelper.parseInt(string3, this.strongholdDistance, 1);
            } else if ("count".equals(string2)) {
                this.strongholdCount = MathHelper.parseInt(string3, this.strongholdCount, 1);
            } else if ("spread".equals(string2)) {
                this.strongholdSpread = MathHelper.parseInt(string3, this.strongholdSpread, 1);
            }
        }
        if ("oceanmonument".equals(string)) {
            if ("separation".equals(string2)) {
                this.oceanMonumentSeparation = MathHelper.parseInt(string3, this.oceanMonumentSeparation, 1);
            } else if ("spacing".equals(string2)) {
                this.oceanMonumentSpacing = MathHelper.parseInt(string3, this.oceanMonumentSpacing, 1);
            }
        }
        if ("endcity".equals(string) && "distance".equals(string2)) {
            this.endCityDistance = MathHelper.parseInt(string3, this.endCityDistance, 1);
        }
        if ("mansion".equals(string) && "distance".equals(string2)) {
            this.mansionDistance = MathHelper.parseInt(string3, this.mansionDistance, 1);
        }
    }

    public static FlatChunkGeneratorConfig getDefaultConfig() {
        FlatChunkGeneratorConfig lv = ChunkGeneratorType.FLAT.createConfig();
        lv.setBiome(Biomes.PLAINS);
        lv.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        lv.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
        lv.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
        lv.updateLayerBlocks();
        lv.getStructures().put("village", Maps.newHashMap());
        return lv;
    }

    public boolean hasNoTerrain() {
        return this.hasNoTerrain;
    }

    public BlockState[] getLayerBlocks() {
        return this.layerBlocks;
    }

    public void removeLayerBlock(int i) {
        this.layerBlocks[i] = null;
    }
}

