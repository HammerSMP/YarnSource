/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class DebugChunkGenerator
extends ChunkGenerator<DebugChunkGeneratorConfig> {
    private static final List<BlockState> BLOCK_STATES = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(arg -> arg.getStateManager().getStates().stream()).collect(Collectors.toList());
    private static final int X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(BLOCK_STATES.size()));
    private static final int Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();

    public DebugChunkGenerator(IWorld arg, BiomeSource arg2, DebugChunkGeneratorConfig arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    public void buildSurface(ChunkRegion arg, Chunk arg2) {
    }

    @Override
    public void carve(BiomeAccess arg, Chunk arg2, GenerationStep.Carver arg3) {
    }

    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

    @Override
    public void generateFeatures(ChunkRegion arg, StructureAccessor arg2) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int i = arg.getCenterChunkX();
        int j = arg.getCenterChunkZ();
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int m = (i << 4) + k;
                int n = (j << 4) + l;
                arg.setBlockState(lv.set(m, 60, n), BARRIER, 2);
                BlockState lv2 = DebugChunkGenerator.getBlockState(m, n);
                if (lv2 == null) continue;
                arg.setBlockState(lv.set(m, 70, n), lv2, 2);
            }
        }
    }

    @Override
    public void populateNoise(IWorld arg, StructureAccessor arg2, Chunk arg3) {
    }

    @Override
    public int getHeight(int i, int j, Heightmap.Type arg) {
        return 0;
    }

    @Override
    public BlockView getColumnSample(int i, int j) {
        return new VerticalBlockSample(new BlockState[0]);
    }

    public static BlockState getBlockState(int i, int j) {
        int k;
        BlockState lv = AIR;
        if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0 && (i /= 2) <= X_SIDE_LENGTH && (j /= 2) <= Z_SIDE_LENGTH && (k = MathHelper.abs(i * X_SIDE_LENGTH + j)) < BLOCK_STATES.size()) {
            lv = BLOCK_STATES.get(k);
        }
        return lv;
    }
}

