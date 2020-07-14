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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorType {
    public static final Codec<ChunkGeneratorType> field_24780 = RecordCodecBuilder.create(instance -> instance.group((App)StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorType::getConfig), (App)NoiseConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorType::method_28559), (App)BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorType::getDefaultBlock), (App)BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorType::getDefaultFluid), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorType::getBedrockCeilingY), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorType::getBedrockFloorY), (App)Codec.intRange((int)0, (int)255).fieldOf("sea_level").forGetter(ChunkGeneratorType::method_28561), (App)Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorType::method_28562)).apply((Applicative)instance, ChunkGeneratorType::new));
    public static final Codec<ChunkGeneratorType> field_24781 = Codec.either(Preset.field_24788, field_24780).xmap(either -> (ChunkGeneratorType)either.map(Preset::getChunkGeneratorType, Function.identity()), arg -> arg.field_24787.map(Either::left).orElseGet(() -> Either.right((Object)arg)));
    private final StructuresConfig config;
    private final NoiseConfig field_24782;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int bedrockCeilingY;
    private final int bedrockFloorY;
    private final int field_24785;
    private final boolean field_24786;
    private final Optional<Preset> field_24787;

    private ChunkGeneratorType(StructuresConfig config, NoiseConfig arg2, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int k, boolean bl) {
        this(config, arg2, defaultBlock, defaultFluid, i, j, k, bl, Optional.empty());
    }

    private ChunkGeneratorType(StructuresConfig config, NoiseConfig arg2, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int k, boolean bl, Optional<Preset> optional) {
        this.config = config;
        this.field_24782 = arg2;
        this.defaultBlock = defaultBlock;
        this.defaultFluid = defaultFluid;
        this.bedrockCeilingY = i;
        this.bedrockFloorY = j;
        this.field_24785 = k;
        this.field_24786 = bl;
        this.field_24787 = optional;
    }

    public StructuresConfig getConfig() {
        return this.config;
    }

    public NoiseConfig method_28559() {
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
        public static final Codec<Preset> field_24788 = Identifier.CODEC.flatXmap(arg -> Optional.ofNullable(BY_ID.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown preset: " + arg))), arg -> DataResult.success((Object)arg.id)).stable();
        public static final Preset OVERWORLD = new Preset("overworld", arg -> Preset.createOverworldType(new StructuresConfig(true), false, arg));
        public static final Preset AMPLIFIED = new Preset("amplified", arg -> Preset.createOverworldType(new StructuresConfig(true), true, arg));
        public static final Preset NETHER = new Preset("nether", arg -> Preset.createCavesType(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), arg));
        public static final Preset END = new Preset("end", arg -> Preset.createIslandsType(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), arg, true, true));
        public static final Preset CAVES = new Preset("caves", arg -> Preset.createCavesType(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg));
        public static final Preset FLOATING_ISLANDS = new Preset("floating_islands", arg -> Preset.createIslandsType(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), arg, false, false));
        private final Text text;
        private final Identifier id;
        private final ChunkGeneratorType chunkGeneratorType;

        public Preset(String id, Function<Preset, ChunkGeneratorType> generatorTypeGetter) {
            this.id = new Identifier(id);
            this.text = new TranslatableText("generator.noise." + id);
            this.chunkGeneratorType = generatorTypeGetter.apply(this);
            BY_ID.put(this.id, this);
        }

        public ChunkGeneratorType getChunkGeneratorType() {
            return this.chunkGeneratorType;
        }

        private static ChunkGeneratorType createIslandsType(StructuresConfig config, BlockState defaultBlock, BlockState defaultFluid, Preset arg4, boolean bl, boolean bl2) {
            return new ChunkGeneratorType(config, new NoiseConfig(128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl2, false), defaultBlock, defaultFluid, -10, -10, 0, bl, Optional.of(arg4));
        }

        private static ChunkGeneratorType createCavesType(StructuresConfig config, BlockState defaultBlock, BlockState defaultFluid, Preset arg4) {
            HashMap map = Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
            map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
            return new ChunkGeneratorType(new StructuresConfig(Optional.ofNullable(config.getStronghold()), map), new NoiseConfig(128, new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0), new SlideConfig(120, 3, 0), new SlideConfig(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), defaultBlock, defaultFluid, 0, 0, 32, false, Optional.of(arg4));
        }

        private static ChunkGeneratorType createOverworldType(StructuresConfig arg, boolean bl, Preset arg2) {
            double d = 0.9999999814507745;
            return new ChunkGeneratorType(arg, new NoiseConfig(256, new NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new SlideConfig(-10, 3, 0), new SlideConfig(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, bl), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), -10, 0, 63, false, Optional.of(arg2));
        }
    }
}

