/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

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
public abstract class class_5317 {
    public static final class_5317 field_25050 = new class_5317("default"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    private static final class_5317 field_25054 = new class_5317("flat"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new FlatChunkGenerator(FlatChunkGeneratorConfig.getDefaultConfig());
        }
    };
    private static final class_5317 field_25055 = new class_5317("large_biomes"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    public static final class_5317 field_25051 = new class_5317("amplified"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, ChunkGeneratorType.Preset.AMPLIFIED.getChunkGeneratorType());
        }
    };
    private static final class_5317 field_25056 = new class_5317("single_biome_surface"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }
    };
    private static final class_5317 field_25057 = new class_5317("single_biome_caves"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
        }
    };
    private static final class_5317 field_25058 = new class_5317("single_biome_floating_islands"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
        }
    };
    private static final class_5317 field_25059 = new class_5317("debug_all_block_states"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return DebugChunkGenerator.INSTANCE;
        }
    };
    protected static final List<class_5317> field_25052 = Lists.newArrayList((Object[])new class_5317[]{field_25050, field_25054, field_25055, field_25051, field_25056, field_25057, field_25058, field_25059});
    protected static final Map<Optional<class_5317>, class_5293> field_25053 = ImmutableMap.of(Optional.of(field_25054), (arg, arg2) -> {
        ChunkGenerator lv = arg2.getChunkGenerator();
        return new CustomizeFlatLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(new GeneratorOptions(arg2.getSeed(), arg2.shouldGenerateStructures(), arg2.hasBonusChest(), GeneratorOptions.method_28608(arg2.getDimensionMap(), new FlatChunkGenerator((FlatChunkGeneratorConfig)arg3)))), lv instanceof FlatChunkGenerator ? ((FlatChunkGenerator)lv).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig());
    }, Optional.of(field_25056), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(class_5317.method_29079(arg2, field_25056, arg3)), class_5317.method_29083(arg2)), Optional.of(field_25057), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(class_5317.method_29079(arg2, field_25057, arg3)), class_5317.method_29083(arg2)), Optional.of(field_25058), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.moreOptionsDialog.setGeneratorOptions(class_5317.method_29079(arg2, field_25058, arg3)), class_5317.method_29083(arg2)));
    private final Text field_25060;

    private class_5317(String string) {
        this.field_25060 = new TranslatableText("generator." + string);
    }

    private static GeneratorOptions method_29079(GeneratorOptions arg, class_5317 arg2, Biome arg3) {
        ChunkGeneratorType lv4;
        FixedBiomeSource lv = new FixedBiomeSource(arg3);
        if (arg2 == field_25057) {
            ChunkGeneratorType lv2 = ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType();
        } else if (arg2 == field_25058) {
            ChunkGeneratorType lv3 = ChunkGeneratorType.Preset.END.getChunkGeneratorType();
        } else {
            lv4 = ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType();
        }
        return new GeneratorOptions(arg.getSeed(), arg.shouldGenerateStructures(), arg.hasBonusChest(), GeneratorOptions.method_28608(arg.getDimensionMap(), new SurfaceChunkGenerator(lv, arg.getSeed(), lv4)));
    }

    private static Biome method_29083(GeneratorOptions arg) {
        return arg.getChunkGenerator().getBiomeSource().method_28443().stream().findFirst().orElse(Biomes.OCEAN);
    }

    public static Optional<class_5317> method_29078(GeneratorOptions arg) {
        ChunkGenerator lv = arg.getChunkGenerator();
        if (lv instanceof FlatChunkGenerator) {
            return Optional.of(field_25054);
        }
        if (lv instanceof DebugChunkGenerator) {
            return Optional.of(field_25059);
        }
        return Optional.empty();
    }

    public Text method_29075() {
        return this.field_25060;
    }

    public GeneratorOptions method_29077(long l, boolean bl, boolean bl2) {
        return new GeneratorOptions(l, bl, bl2, GeneratorOptions.method_28608(DimensionType.method_28517(l), this.method_29076(l)));
    }

    protected abstract ChunkGenerator method_29076(long var1);

    @Environment(value=EnvType.CLIENT)
    public static interface class_5293 {
        public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptions var2);
    }
}

