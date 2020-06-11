/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

@Environment(value=EnvType.CLIENT)
public abstract class GeneratorType {
    public static final GeneratorType DEFAULT = new GeneratorType("default"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    private static final GeneratorType FLAT = new GeneratorType("flat"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
        }
    };
    private static final GeneratorType LARGE_BIOMES = new GeneratorType("large_biomes"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    public static final GeneratorType AMPLIFIED = new GeneratorType("amplified"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.AMPLIFIED.getChunkGeneratorType());
        }
    };
    private static final GeneratorType SINGLE_BIOME_SURFACE = new GeneratorType("single_biome_surface"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    private static final GeneratorType SINGLE_BIOME_CAVES = new GeneratorType("single_biome_caves"){

        @Override
        public GeneratorOptions method_29077(RegistryTracker.Modifiable arg, long l, boolean bl, boolean bl2) {
            return new GeneratorOptions(l, bl, bl2, GeneratorOptions.method_29962(DimensionType.method_28517(l), DimensionType::getOverworldCavesDimensionType, this.method_29076(l)));
        }

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.CAVES.getChunkGeneratorType());
        }
    };
    private static final GeneratorType SINGLE_BIOME_FLOATING_ISLANDS = new GeneratorType("single_biome_floating_islands"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.FLOATING_ISLANDS.getChunkGeneratorType());
        }
    };
    private static final GeneratorType DEBUG_ALL_BLOCK_STATES = new GeneratorType("debug_all_block_states"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return DebugChunkGenerator.INSTANCE;
        }
    };
    protected static final List<GeneratorType> VALUES = Lists.newArrayList((Object[])new GeneratorType[]{DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG_ALL_BLOCK_STATES});
    protected static final Map<Optional<GeneratorType>, ScreenProvider> field_25053 = ImmutableMap.of(Optional.of(FLAT), (arg, arg2) -> {
        ChunkGenerator lv = arg2.getChunkGenerator();
        return new CustomizeFlatLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(new GeneratorOptions(arg2.getSeed(), arg2.shouldGenerateStructures(), arg2.hasBonusChest(), GeneratorOptions.method_28608(arg2.getDimensionMap(), new FlatChunkGenerator((FlatChunkGeneratorConfig)arg3)))), lv instanceof FlatChunkGenerator ? ((FlatChunkGenerator)lv).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig());
    }, Optional.of(SINGLE_BIOME_SURFACE), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(GeneratorType.method_29079(arg2, SINGLE_BIOME_SURFACE, arg3)), GeneratorType.method_29083(arg2)), Optional.of(SINGLE_BIOME_CAVES), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(GeneratorType.method_29079(arg2, SINGLE_BIOME_CAVES, arg3)), GeneratorType.method_29083(arg2)), Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(GeneratorType.method_29079(arg2, SINGLE_BIOME_FLOATING_ISLANDS, arg3)), GeneratorType.method_29083(arg2)));
    private final Text translationKey;

    private GeneratorType(String string) {
        this.translationKey = new TranslatableText("generator." + string);
    }

    private static GeneratorOptions method_29079(GeneratorOptions arg, GeneratorType arg2, Biome arg3) {
        ChunkGeneratorType lv4;
        FixedBiomeSource lv = new FixedBiomeSource(arg3);
        if (arg2 == SINGLE_BIOME_CAVES) {
            ChunkGeneratorType lv2 = ChunkGeneratorType.Preset.CAVES.getChunkGeneratorType();
        } else if (arg2 == SINGLE_BIOME_FLOATING_ISLANDS) {
            ChunkGeneratorType lv3 = ChunkGeneratorType.Preset.FLOATING_ISLANDS.getChunkGeneratorType();
        } else {
            lv4 = ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType();
        }
        return new GeneratorOptions(arg.getSeed(), arg.shouldGenerateStructures(), arg.hasBonusChest(), GeneratorOptions.method_28608(arg.getDimensionMap(), new SurfaceChunkGenerator(lv, arg.getSeed(), lv4)));
    }

    private static Biome method_29083(GeneratorOptions arg) {
        return arg.getChunkGenerator().getBiomeSource().method_28443().stream().findFirst().orElse(Biomes.OCEAN);
    }

    public static Optional<GeneratorType> method_29078(GeneratorOptions arg) {
        ChunkGenerator lv = arg.getChunkGenerator();
        if (lv instanceof FlatChunkGenerator) {
            return Optional.of(FLAT);
        }
        if (lv instanceof DebugChunkGenerator) {
            return Optional.of(DEBUG_ALL_BLOCK_STATES);
        }
        return Optional.empty();
    }

    public Text getTranslationKey() {
        return this.translationKey;
    }

    public GeneratorOptions method_29077(RegistryTracker.Modifiable arg, long l, boolean bl, boolean bl2) {
        return new GeneratorOptions(l, bl, bl2, GeneratorOptions.method_28608(DimensionType.method_28517(l), this.method_29076(l)));
    }

    protected abstract ChunkGenerator method_29076(long var1);

    @Environment(value=EnvType.CLIENT)
    public static interface ScreenProvider {
        public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptions var2);
    }
}

