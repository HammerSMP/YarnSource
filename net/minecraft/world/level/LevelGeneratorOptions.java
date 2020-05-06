/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.DynamicLike
 *  com.mojang.datafixers.OptionalDynamic
 */
package net.minecraft.world.level;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DynamicLike;
import com.mojang.datafixers.OptionalDynamic;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.CheckerboardBiomeSourceConfig;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FloatingIslandsChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class LevelGeneratorOptions {
    private final LevelGeneratorType type;
    private final Dynamic<?> dynamic;
    private final Function<IWorld, ChunkGenerator<?>> chunkGeneratorFactory;

    public LevelGeneratorOptions(LevelGeneratorType arg, Dynamic<?> dynamic, Function<IWorld, ChunkGenerator<?>> function) {
        this.type = arg;
        this.dynamic = dynamic;
        this.chunkGeneratorFactory = function;
    }

    public LevelGeneratorType getType() {
        return this.type;
    }

    public Dynamic<?> getDynamic() {
        return this.dynamic;
    }

    public ChunkGenerator<?> createChunkGenerator(IWorld arg) {
        return this.chunkGeneratorFactory.apply(arg);
    }

    public static LevelGeneratorOptions createDefault(LevelGeneratorType arg, Dynamic<?> dynamic) {
        OverworldChunkGeneratorConfig lv = ChunkGeneratorType.SURFACE.createConfig();
        return new LevelGeneratorOptions(arg, dynamic, arg3 -> {
            VanillaLayeredBiomeSourceConfig lv = BiomeSourceType.VANILLA_LAYERED.getConfig(arg3.getSeed()).setGeneratorType(arg).setGeneratorConfig(lv);
            return ChunkGeneratorType.SURFACE.create((IWorld)arg3, BiomeSourceType.VANILLA_LAYERED.applyConfig(lv), lv);
        });
    }

    public static LevelGeneratorOptions createFlat(LevelGeneratorType arg, Dynamic<?> dynamic) {
        FlatChunkGeneratorConfig lv = FlatChunkGeneratorConfig.fromDynamic(dynamic);
        return new LevelGeneratorOptions(arg, dynamic, arg2 -> {
            FixedBiomeSourceConfig lv = BiomeSourceType.FIXED.getConfig(arg2.getSeed()).setBiome(lv.getBiome());
            return ChunkGeneratorType.FLAT.create((IWorld)arg2, BiomeSourceType.FIXED.applyConfig(lv), lv);
        });
    }

    private static <T> T retrieveFromRegistry(DynamicLike<?> dynamicLike, Registry<T> arg, T object) {
        return (T)dynamicLike.asString().map(Identifier::new).flatMap(arg::getOrEmpty).orElse(object);
    }

    private static LongFunction<BiomeSource> loadBiomeSourceFactory(DynamicLike<?> dynamicLike) {
        BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> lv = LevelGeneratorOptions.retrieveFromRegistry(dynamicLike.get("type"), Registry.BIOME_SOURCE_TYPE, BiomeSourceType.FIXED);
        OptionalDynamic dynamicLike2 = dynamicLike.get("options");
        Stream stream2 = dynamicLike2.get("biomes").asStreamOpt().map(stream -> stream.map(dynamic -> LevelGeneratorOptions.retrieveFromRegistry(dynamic, Registry.BIOME, Biomes.OCEAN))).orElseGet(Stream::empty);
        if (BiomeSourceType.CHECKERBOARD == lv) {
            Biome[] arrbiome;
            int i = dynamicLike2.get("size").asInt(2);
            Biome[] lvs = (Biome[])stream2.toArray(Biome[]::new);
            if (lvs.length > 0) {
                arrbiome = lvs;
            } else {
                Biome[] arrbiome2 = new Biome[1];
                arrbiome = arrbiome2;
                arrbiome2[0] = Biomes.OCEAN;
            }
            Biome[] lvs2 = arrbiome;
            return l -> {
                CheckerboardBiomeSourceConfig lv = BiomeSourceType.CHECKERBOARD.getConfig(l).setBiomes(lvs2).setSize(i);
                return BiomeSourceType.CHECKERBOARD.applyConfig(lv);
            };
        }
        if (BiomeSourceType.VANILLA_LAYERED == lv) {
            return l -> {
                VanillaLayeredBiomeSourceConfig lv = BiomeSourceType.VANILLA_LAYERED.getConfig(l);
                return BiomeSourceType.VANILLA_LAYERED.applyConfig(lv);
            };
        }
        Biome lv2 = stream2.findFirst().orElse(Biomes.OCEAN);
        return l -> {
            FixedBiomeSourceConfig lv = BiomeSourceType.FIXED.getConfig(l).setBiome(lv2);
            return BiomeSourceType.FIXED.applyConfig(lv);
        };
    }

    private static void loadOptions(ChunkGeneratorConfig arg, DynamicLike<?> dynamicLike) {
        BlockState lv = LevelGeneratorOptions.retrieveFromRegistry(dynamicLike.get("default_block"), Registry.BLOCK, Blocks.STONE).getDefaultState();
        arg.setDefaultBlock(lv);
        BlockState lv2 = LevelGeneratorOptions.retrieveFromRegistry(dynamicLike.get("default_fluid"), Registry.BLOCK, Blocks.WATER).getDefaultState();
        arg.setDefaultFluid(lv2);
    }

    private static Function<IWorld, ChunkGenerator<?>> loadChunkGeneratorFactory(DynamicLike<?> dynamicLike, LongFunction<BiomeSource> longFunction) {
        ChunkGeneratorType<OverworldChunkGeneratorConfig, OverworldChunkGenerator> lv = LevelGeneratorOptions.retrieveFromRegistry(dynamicLike.get("type"), Registry.CHUNK_GENERATOR_TYPE, ChunkGeneratorType.SURFACE);
        return LevelGeneratorOptions.loadChunkGeneratorFactory(dynamicLike, lv, longFunction);
    }

    private static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> Function<IWorld, ChunkGenerator<?>> loadChunkGeneratorFactory(DynamicLike<?> dynamicLike, ChunkGeneratorType<C, T> arg, LongFunction<BiomeSource> longFunction) {
        Object lv = arg.createConfig();
        if (arg == ChunkGeneratorType.FLOATING_ISLANDS) {
            FloatingIslandsChunkGeneratorConfig lv2 = (FloatingIslandsChunkGeneratorConfig)lv;
            lv2.withCenter(new BlockPos(0, 64, 0));
        }
        LevelGeneratorOptions.loadOptions(lv, dynamicLike.get("options"));
        return arg3 -> arg.create((IWorld)arg3, (BiomeSource)longFunction.apply(arg3.getSeed()), lv);
    }

    public static LevelGeneratorOptions createBuffet(LevelGeneratorType arg, Dynamic<?> dynamic) {
        LongFunction<BiomeSource> longFunction = LevelGeneratorOptions.loadBiomeSourceFactory(dynamic.get("biome_source"));
        Function<IWorld, ChunkGenerator<?>> function = LevelGeneratorOptions.loadChunkGeneratorFactory(dynamic.get("chunk_generator"), longFunction);
        return new LevelGeneratorOptions(arg, dynamic, function);
    }

    public static LevelGeneratorOptions createDebug(LevelGeneratorType arg2, Dynamic<?> dynamic) {
        return new LevelGeneratorOptions(arg2, dynamic, arg -> {
            FixedBiomeSourceConfig lv = BiomeSourceType.FIXED.getConfig(arg.getSeed()).setBiome(Biomes.PLAINS);
            return ChunkGeneratorType.DEBUG.create((IWorld)arg, BiomeSourceType.FIXED.applyConfig(lv), ChunkGeneratorType.DEBUG.createConfig());
        });
    }
}

