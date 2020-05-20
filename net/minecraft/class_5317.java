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
import net.minecraft.class_5284;
import net.minecraft.class_5285;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

@Environment(value=EnvType.CLIENT)
public abstract class class_5317 {
    public static final class_5317 field_25050 = new class_5317("default"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, class_5284.class_5307.OVERWORLD.method_28568());
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
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, true), l, class_5284.class_5307.OVERWORLD.method_28568());
        }
    };
    public static final class_5317 field_25051 = new class_5317("amplified"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(l, false, false), l, class_5284.class_5307.AMPLIFIED.method_28568());
        }
    };
    private static final class_5317 field_25056 = new class_5317("single_biome_surface"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, class_5284.class_5307.OVERWORLD.method_28568());
        }
    };
    private static final class_5317 field_25057 = new class_5317("single_biome_caves"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, class_5284.class_5307.NETHER.method_28568());
        }
    };
    private static final class_5317 field_25058 = new class_5317("single_biome_floating_islands"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return new SurfaceChunkGenerator(new FixedBiomeSource(Biomes.OCEAN), l, class_5284.class_5307.END.method_28568());
        }
    };
    private static final class_5317 field_25059 = new class_5317("debug_all_block_states"){

        @Override
        protected ChunkGenerator method_29076(long l) {
            return DebugChunkGenerator.generator;
        }
    };
    protected static final List<class_5317> field_25052 = Lists.newArrayList((Object[])new class_5317[]{field_25050, field_25054, field_25055, field_25051, field_25056, field_25057, field_25058, field_25059});
    protected static final Map<Optional<class_5317>, class_5293> field_25053 = ImmutableMap.of(Optional.of(field_25054), (arg, arg2) -> {
        ChunkGenerator lv = arg2.method_28032();
        return new CustomizeFlatLevelScreen(arg, arg3 -> arg.field_24588.method_28086(new class_5285(arg2.method_28028(), arg2.method_28029(), arg2.method_28030(), class_5285.method_28608(arg2.method_28609(), new FlatChunkGenerator((FlatChunkGeneratorConfig)arg3)))), lv instanceof FlatChunkGenerator ? ((FlatChunkGenerator)lv).method_28545() : FlatChunkGeneratorConfig.getDefaultConfig());
    }, Optional.of(field_25056), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.field_24588.method_28086(class_5317.method_29079(arg2, field_25056, arg3)), class_5317.method_29083(arg2)), Optional.of(field_25057), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.field_24588.method_28086(class_5317.method_29079(arg2, field_25057, arg3)), class_5317.method_29083(arg2)), Optional.of(field_25058), (arg, arg2) -> new CustomizeBuffetLevelScreen(arg, arg3 -> arg.field_24588.method_28086(class_5317.method_29079(arg2, field_25058, arg3)), class_5317.method_29083(arg2)));
    private final Text field_25060;

    private class_5317(String string) {
        this.field_25060 = new TranslatableText("generator." + string);
    }

    private static class_5285 method_29079(class_5285 arg, class_5317 arg2, Biome arg3) {
        class_5284 lv4;
        FixedBiomeSource lv = new FixedBiomeSource(arg3);
        if (arg2 == field_25057) {
            class_5284 lv2 = class_5284.class_5307.NETHER.method_28568();
        } else if (arg2 == field_25058) {
            class_5284 lv3 = class_5284.class_5307.END.method_28568();
        } else {
            lv4 = class_5284.class_5307.OVERWORLD.method_28568();
        }
        return new class_5285(arg.method_28028(), arg.method_28029(), arg.method_28030(), class_5285.method_28608(arg.method_28609(), new SurfaceChunkGenerator(lv, arg.method_28028(), lv4)));
    }

    private static Biome method_29083(class_5285 arg) {
        return arg.method_28032().getBiomeSource().method_28443().stream().findFirst().orElse(Biomes.OCEAN);
    }

    public static Optional<class_5317> method_29078(class_5285 arg) {
        ChunkGenerator lv = arg.method_28032();
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

    public class_5285 method_29077(long l, boolean bl, boolean bl2) {
        return new class_5285(l, bl, bl2, class_5285.method_28608(DimensionType.method_28517(l), this.method_29076(l)));
    }

    protected abstract ChunkGenerator method_29076(long var1);

    @Environment(value=EnvType.CLIENT)
    public static interface class_5293 {
        public Screen createEditScreen(CreateWorldScreen var1, class_5285 var2);
    }
}

