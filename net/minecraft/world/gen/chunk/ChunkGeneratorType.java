/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.NoiseConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public final class ChunkGeneratorType {
    public static final MapCodec<ChunkGeneratorType> field_24780 = RecordCodecBuilder.mapCodec(instance -> instance.group((App)StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorType::getConfig), (App)NoiseConfig.CODEC.fieldOf("noise").forGetter(ChunkGeneratorType::method_28559), (App)BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorType::getDefaultBlock), (App)BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorType::getDefaultFluid), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorType::getBedrockCeilingY), (App)Codec.intRange((int)-20, (int)276).fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorType::getBedrockFloorY), (App)Codec.intRange((int)0, (int)255).fieldOf("sea_level").forGetter(ChunkGeneratorType::method_28561), (App)Codec.BOOL.fieldOf("disable_mob_generation").forGetter(ChunkGeneratorType::method_28562)).apply((Applicative)instance, ChunkGeneratorType::new));
    public static final Codec<Supplier<ChunkGeneratorType>> field_24781 = RegistryElementCodec.of(Registry.NOISE_SETTINGS_WORLDGEN, field_24780);
    private final StructuresConfig config;
    private final NoiseConfig field_24782;
    private final BlockState defaultBlock;
    private final BlockState defaultFluid;
    private final int bedrockCeilingY;
    private final int bedrockFloorY;
    private final int field_24785;
    private final boolean field_24786;
    private final Optional<Identifier> field_24787;
    public static final ChunkGeneratorType field_26355 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30643(new StructuresConfig(true), false, new Identifier("overworld")));
    public static final ChunkGeneratorType field_26356 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30643(new StructuresConfig(true), true, new Identifier("amplified")));
    public static final ChunkGeneratorType field_26357 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30641(new StructuresConfig(false), Blocks.NETHERRACK.getDefaultState(), Blocks.LAVA.getDefaultState(), new Identifier("nether")));
    public static final ChunkGeneratorType field_26358 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30642(new StructuresConfig(false), Blocks.END_STONE.getDefaultState(), Blocks.AIR.getDefaultState(), new Identifier("end"), true, true));
    public static final ChunkGeneratorType field_26359 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30641(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("caves")));
    public static final ChunkGeneratorType field_26360 = ChunkGeneratorType.method_30644(ChunkGeneratorType.method_30642(new StructuresConfig(false), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), new Identifier("floating_islands"), false, false));

    private ChunkGeneratorType(StructuresConfig config, NoiseConfig arg2, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int k, boolean bl) {
        this(config, arg2, defaultBlock, defaultFluid, i, j, k, bl, Optional.empty());
    }

    private ChunkGeneratorType(StructuresConfig config, NoiseConfig arg2, BlockState defaultBlock, BlockState defaultFluid, int i, int j, int k, boolean bl, Optional<Identifier> optional) {
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

    public boolean method_28555(ChunkGeneratorType arg) {
        return Objects.equals(this.field_24787, arg.field_24787);
    }

    private static ChunkGeneratorType method_30644(ChunkGeneratorType arg) {
        BuiltinRegistries.add(BuiltinRegistries.field_26375, arg.field_24787.orElseThrow(IllegalStateException::new), arg);
        return arg;
    }

    private static ChunkGeneratorType method_30642(StructuresConfig arg, BlockState arg2, BlockState arg3, Identifier arg4, boolean bl, boolean bl2) {
        return new ChunkGeneratorType(arg, new NoiseConfig(128, new NoiseSamplingConfig(2.0, 1.0, 80.0, 160.0), new SlideConfig(-3000, 64, -46), new SlideConfig(-30, 7, 1), 2, 1, 0.0, 0.0, true, false, bl2, false), arg2, arg3, -10, -10, 0, bl, Optional.of(arg4));
    }

    private static ChunkGeneratorType method_30641(StructuresConfig arg, BlockState arg2, BlockState arg3, Identifier arg4) {
        HashMap map = Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
        map.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
        return new ChunkGeneratorType(new StructuresConfig(Optional.ofNullable(arg.getStronghold()), map), new NoiseConfig(128, new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0), new SlideConfig(120, 3, 0), new SlideConfig(320, 4, -1), 1, 2, 0.0, 0.019921875, false, false, false, false), arg2, arg3, 0, 0, 32, false, Optional.of(arg4));
    }

    private static ChunkGeneratorType method_30643(StructuresConfig arg, boolean bl, Identifier arg2) {
        double d = 0.9999999814507745;
        return new ChunkGeneratorType(arg, new NoiseConfig(256, new NoiseSamplingConfig(0.9999999814507745, 0.9999999814507745, 80.0, 160.0), new SlideConfig(-10, 3, 0), new SlideConfig(-30, 0, 0), 1, 2, 1.0, -0.46875, true, true, false, bl), Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), -10, 0, 63, false, Optional.of(arg2));
    }
}

