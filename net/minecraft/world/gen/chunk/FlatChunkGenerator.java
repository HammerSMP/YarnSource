/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

public class FlatChunkGenerator
extends ChunkGenerator {
    private final Biome biome;
    private final PhantomSpawner phantomSpawner = new PhantomSpawner();
    private final CatSpawner catSpawner = new CatSpawner();
    private final FlatChunkGeneratorConfig generatorConfig;

    public FlatChunkGenerator(FlatChunkGeneratorConfig arg) {
        super(new FixedBiomeSource(arg.getBiome()), arg.getConfig());
        this.generatorConfig = arg;
        this.biome = this.getBiome();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ChunkGenerator create(long l) {
        return this;
    }

    /*
     * Could not resolve type clashes
     */
    private Biome getBiome() {
        boolean bl;
        Biome lv = this.generatorConfig.getBiome();
        FlatChunkGeneratorBiome lv2 = new FlatChunkGeneratorBiome(lv.getSurfaceBuilder(), lv.getPrecipitation(), lv.getCategory(), lv.getDepth(), lv.getScale(), lv.getTemperature(), lv.getRainfall(), lv.getEffects(), lv.getParent());
        Map<String, Map<String, String>> map = this.generatorConfig.getStructures();
        for (String string : map.keySet()) {
            Object[] lvs = FlatChunkGeneratorConfig.STRUCTURE_TO_FEATURES.get(string);
            if (lvs == null) continue;
            ConfiguredFeature<?, ?>[] arrconfiguredFeature = lvs;
            int n = arrconfiguredFeature.length;
            for (int i = 0; i < n; ++i) {
                ConfiguredFeature<?, ?> lv3 = arrconfiguredFeature[i];
                lv2.addFeature(FlatChunkGeneratorConfig.FEATURE_TO_GENERATION_STEP.get(lv3), lv3);
                if (!(lv3.feature instanceof StructureFeature)) continue;
                StructureFeature lv4 = (StructureFeature)lv3.feature;
                Object lv5 = lv.getStructureFeatureConfig(lv4);
                Object lv6 = lv5 != null ? lv5 : FlatChunkGeneratorConfig.FEATURE_TO_FEATURE_CONFIG.get(lv3);
                lv2.addStructureFeature(lv4.configure(lv6));
            }
        }
        boolean bl2 = bl = (!this.generatorConfig.hasNoTerrain() || lv == Biomes.THE_VOID) && map.containsKey("decoration");
        if (bl) {
            ArrayList list = Lists.newArrayList();
            list.add(GenerationStep.Feature.UNDERGROUND_STRUCTURES);
            list.add(GenerationStep.Feature.SURFACE_STRUCTURES);
            for (ConfiguredFeature<?, ?> lv7 : GenerationStep.Feature.values()) {
                if (list.contains(lv7)) continue;
                for (ConfiguredFeature<?, ?> lv8 : lv.getFeaturesForStep((GenerationStep.Feature)((Object)lv7))) {
                    lv2.addFeature((GenerationStep.Feature)((Object)lv7), lv8);
                }
            }
        }
        BlockState[] lvs2 = this.generatorConfig.getLayerBlocks();
        for (int i = 0; i < lvs2.length; ++i) {
            BlockState lv9 = lvs2[i];
            if (lv9 == null || Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(lv9)) continue;
            this.generatorConfig.removeLayerBlock(i);
            lv2.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.FILL_LAYER.configure(new FillLayerFeatureConfig(i, lv9)));
        }
        return lv2;
    }

    @Override
    public void buildSurface(ChunkRegion arg, Chunk arg2) {
    }

    @Override
    public int getSpawnHeight() {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        for (int i = 0; i < lvs.length; ++i) {
            BlockState lv;
            BlockState blockState = lv = lvs[i] == null ? Blocks.AIR.getDefaultState() : lvs[i];
            if (Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(lv)) continue;
            return i - 1;
        }
        return lvs.length;
    }

    @Override
    protected Biome getDecorationBiome(BiomeAccess arg, BlockPos arg2) {
        return this.biome;
    }

    @Override
    public boolean hasStructure(StructureFeature<?> arg) {
        return this.biome.hasStructureFeature(arg);
    }

    @Override
    public void populateNoise(WorldAccess arg, StructureAccessor arg2, Chunk arg3) {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        BlockPos.Mutable lv = new BlockPos.Mutable();
        Heightmap lv2 = arg3.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap lv3 = arg3.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        for (int i = 0; i < lvs.length; ++i) {
            BlockState lv4 = lvs[i];
            if (lv4 == null) continue;
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    arg3.setBlockState(lv.set(j, i, k), lv4, false);
                    lv2.trackUpdate(j, i, k, lv4);
                    lv3.trackUpdate(j, i, k, lv4);
                }
            }
        }
    }

    @Override
    public int getHeight(int i, int j, Heightmap.Type arg) {
        BlockState[] lvs = this.generatorConfig.getLayerBlocks();
        for (int k = lvs.length - 1; k >= 0; --k) {
            BlockState lv = lvs[k];
            if (lv == null || !arg.getBlockPredicate().test(lv)) continue;
            return k + 1;
        }
        return 0;
    }

    @Override
    public BlockView getColumnSample(int i, int j) {
        return new VerticalBlockSample((BlockState[])Arrays.stream(this.generatorConfig.getLayerBlocks()).map(arg -> arg == null ? Blocks.AIR.getDefaultState() : arg).toArray(BlockState[]::new));
    }

    @Override
    public void spawnEntities(ServerWorld arg, boolean bl, boolean bl2) {
        this.phantomSpawner.spawn(arg, bl, bl2);
        this.catSpawner.spawn(arg, bl, bl2);
    }

    @Override
    public boolean hasStructure(Biome arg, StructureFeature<? extends FeatureConfig> arg2) {
        return this.biome.hasStructureFeature(arg2);
    }

    @Override
    @Nullable
    public <C extends FeatureConfig> C getStructureConfig(Biome arg, StructureFeature<C> arg2) {
        return this.biome.getStructureFeatureConfig(arg2);
    }

    @Override
    @Nullable
    public BlockPos locateStructure(ServerWorld arg, String string, BlockPos arg2, int i, boolean bl) {
        if (!this.generatorConfig.getStructures().keySet().contains(string.toLowerCase(Locale.ROOT))) {
            return null;
        }
        return super.locateStructure(arg, string, arg2, i, bl);
    }

    class FlatChunkGeneratorBiome
    extends Biome {
        protected FlatChunkGeneratorBiome(ConfiguredSurfaceBuilder<?> arg2, Biome.Precipitation arg3, Biome.Category arg4, float f, float g, float h, float i, BiomeEffects arg5, @Nullable String string) {
            super(new Biome.Settings().surfaceBuilder(arg2).precipitation(arg3).category(arg4).depth(f).scale(g).temperature(h).downfall(i).effects(arg5).parent(string));
        }
    }
}

