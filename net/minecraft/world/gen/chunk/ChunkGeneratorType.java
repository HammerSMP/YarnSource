/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5308;
import net.minecraft.class_5309;
import net.minecraft.class_5310;
import net.minecraft.class_5311;
import net.minecraft.class_5314;
import net.minecraft.class_5324;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorType {
    public static final Codec<ChunkGeneratorType> field_24780 = RecordCodecBuilder.create(instance -> instance.group((App)class_5311.field_24821.fieldOf("structures").forGetter(ChunkGeneratorType::getConfig), (App)class_5309.field_24804.fieldOf("noise").forGetter(ChunkGeneratorType::method_28559), (App)BlockState.field_24734.fieldOf("default_block").forGetter(ChunkGeneratorType::getDefaultBlock), (App)BlockState.field_24734.fieldOf("default_fluid").forGetter(ChunkGeneratorType::getDefaultFluid), (App)class_5324.method_29229(-20, 276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorType::getBedrockCeilingY), (App)class_5324.method_29229(-20, 276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorType::getBedrockFloorY), (App)class_5324.method_29229(0, 255).fieldOf("sea_level").forGetter(ChunkGeneratorType::method_28561), (App)Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorType::method_28562)).apply((Applicative)instance, ChunkGeneratorType::new));
    public static final Codec<ChunkGeneratorType> field_24781 = Codec.either(Preset.field_24788, field_24780).xmap(either -> (ChunkGeneratorType)either.map(Preset::getChunkGeneratorType, Function.identity()), arg -> arg.field_24787.map(Either::left).orElseGet(() -> Either.right((Object)arg)));
    private final class_5311 config;
    private final class_5309 field_24782;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int bedrockCeilingY;
    private final int bedrockFloorY;
    private final int field_24785;
    private final boolean field_24786;
    private final Optional<Preset> field_24787;

    private ChunkGeneratorType(class_5311 arg, class_5309 arg2, BlockState arg3, BlockState arg4, int i, int j, int k, boolean bl) {
        this(arg, arg2, arg3, arg4, i, j, k, bl, Optional.empty());
    }

    private ChunkGeneratorType(class_5311 arg, class_5309 arg2, BlockState arg3, BlockState arg4, int i, int j, int k, boolean bl, Optional<Preset> optional) {
        this.config = arg;
        this.field_24782 = arg2;
        this.defaultBlock = arg3;
        this.defaultFluid = arg4;
        this.bedrockCeilingY = i;
        this.bedrockFloorY = j;
        this.field_24785 = k;
        this.field_24786 = bl;
        this.field_24787 = optional;
    }

    public class_5311 getConfig() {
        return this.config;
    }

    public class_5309 method_28559() {
        return this.field_24782;
    }

    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }

    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }

    public int getBedrockCeilingY() {
        return this.bedrockCeilingY;
    }

    public int getBedrockFloorY() {
        return this.bedrockFloorY;
    }

    public int method_28561() {
        return this.field_24785;
    }

    @Deprecated
    protected boolean method_28562() {
        return this.field_24786;
    }

    public boolean method_28555(Preset arg) {
        return Objects.equals(this.field_24787, Optional.of(arg));
    }

    public static class Preset {
        private static final Map<Identifier, Preset> BY_ID = Maps.newHashMap();
        public static final Codec<Preset> field_24788 = Identifier.field_25139.flatXmap(arg -> Optional.ofNullable(BY_ID.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + arg))), arg -> DataResult.success((Object)arg.id)).stable();
        public static final Preset OVERWORLD = new Preset("overworld", arg -> Preset.createOverworldType(new class_5311(true), false, arg));
        public static final Preset AMPLIFIED = new Preset("amplified", arg -> Preset.createOverworldType(new class_5311(true), true, arg));
        public static final Preset NETHER = new Preset("nether", arg -> Preset.createCavesType(new class_5311(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), arg));
        public static final Preset END = new Preset("end", arg -> Preset.createIslandsType(new class_5311(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), arg, true));
        public static final Preset CAVES = new Preset("caves", arg -> Preset.createCavesType(new class_5311(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg));
        public static final Preset FLOATING_ISLANDS = new Preset("floating_islands", arg -> Preset.createIslandsType(new class_5311(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg, false));
        private final Text text;
        private final Identifier id;
        private final ChunkGeneratorType chunkGeneratorType;

        public Preset(String string, Function<Preset, ChunkGeneratorType> function) {
            this.id = new Identifier(string);
            this.text = new TranslatableText("generator.noise." + string);
            this.chunkGeneratorType = function.apply(this);
            BY_ID.put(this.id, this);
        }

        public ChunkGeneratorType getChunkGeneratorType() {
            return this.chunkGeneratorType;
        }

        private static ChunkGeneratorType createIslandsType(class_5311 arg, BlockState arg2, BlockState arg3, Preset arg4, boolean bl) {
            return new ChunkGeneratorType(arg, new class_5309(128, new class_5308(2.0, 1.0, 80.0, 160.0), new class_5310(-3000, 64, -46), new class_5310(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl, false), arg2, arg3, -10, -10, 0, true, Optional.of(arg4));
        }

        private static ChunkGeneratorType createCavesType(class_5311 arg, BlockState arg2, BlockState arg3, Preset arg4) {
            HashMap map = Maps.newHashMap(class_5311.field_24822);
            map.put(StructureFeature.RUINED_PORTAL, new class_5314(25, 10, 34222645));
            return new ChunkGeneratorType(new class_5311(Optional.ofNullable(arg.method_28602()), map), new class_5309(128, new class_5308(1.0, 3.0, 80.0, 60.0), new class_5310(120, 3, 0), new class_5310(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), arg2, arg3, 0, 0, 32, true, Optional.of(arg4));
        }

        private static ChunkGeneratorType createOverworldType(class_5311 arg, boolean bl, Preset arg2) {
            double d = 0.9999999814507745;
            return new ChunkGeneratorType(arg, new class_5309(256, new class_5308(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new class_5310(-10, 3, 0), new class_5310(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, bl), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), -10, 0, 63, false, Optional.of(arg2));
        }
    }
}

