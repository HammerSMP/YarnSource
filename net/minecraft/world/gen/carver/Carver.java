/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveCarver;
import net.minecraft.world.gen.carver.UnderwaterRavineCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<ProbabilityConfig> CAVE = Carver.register("cave", new CaveCarver(ProbabilityConfig.CODEC, 256));
    public static final Carver<ProbabilityConfig> NETHER_CAVE = Carver.register("nether_cave", new NetherCaveCarver(ProbabilityConfig.CODEC));
    public static final Carver<ProbabilityConfig> CANYON = Carver.register("canyon", new RavineCarver(ProbabilityConfig.CODEC));
    public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = Carver.register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig.CODEC));
    public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = Carver.register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig.CODEC));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
    protected Set<Fluid> carvableFluids = ImmutableSet.of((Object)Fluids.WATER);
    private final Codec<ConfiguredCarver<C>> codec;
    protected final int heightLimit;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F arg) {
        return (F)Registry.register(Registry.CARVER, string, arg);
    }

    public Carver(Codec<C> configCodec, int heightLimit) {
        this.heightLimit = heightLimit;
        this.codec = configCodec.fieldOf("config").xmap(this::method_28614, ConfiguredCarver::method_30378).codec();
    }

    public ConfiguredCarver<C> method_28614(C arg) {
        return new ConfiguredCarver<C>(this, arg);
    }

    public Codec<ConfiguredCarver<C>> getCodec() {
        return this.codec;
    }

    public int getBranchFactor() {
        return 4;
    }

    protected boolean carveRegion(Chunk arg, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, int chunkX, int chunkZ, double x, double y, double z, double yaw, double pitch, BitSet carvingMask) {
        int t;
        int s;
        int r;
        int q;
        int p;
        Random random = new Random(seed + (long)chunkX + (long)chunkZ);
        double m = chunkX * 16 + 8;
        double n = chunkZ * 16 + 8;
        if (x < m - 16.0 - yaw * 2.0 || z < n - 16.0 - yaw * 2.0 || x > m + 16.0 + yaw * 2.0 || z > n + 16.0 + yaw * 2.0) {
            return false;
        }
        int o = Math.max(MathHelper.floor(x - yaw) - chunkX * 16 - 1, 0);
        if (this.isRegionUncarvable(arg, chunkX, chunkZ, o, p = Math.min(MathHelper.floor(x + yaw) - chunkX * 16 + 1, 16), q = Math.max(MathHelper.floor(y - pitch) - 1, 1), r = Math.min(MathHelper.floor(y + pitch) + 1, this.heightLimit - 8), s = Math.max(MathHelper.floor(z - yaw) - chunkZ * 16 - 1, 0), t = Math.min(MathHelper.floor(z + yaw) - chunkZ * 16 + 1, 16))) {
            return false;
        }
        boolean bl = false;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int u = o; u < p; ++u) {
            int v = u + chunkX * 16;
            double w = ((double)v + 0.5 - x) / yaw;
            for (int x2 = s; x2 < t; ++x2) {
                int y2 = x2 + chunkZ * 16;
                double z2 = ((double)y2 + 0.5 - z) / yaw;
                if (w * w + z2 * z2 >= 1.0) continue;
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                for (int aa = r; aa > q; --aa) {
                    double ab = ((double)aa - 0.5 - y) / pitch;
                    if (this.isPositionExcluded(w, ab, z2, aa)) continue;
                    bl |= this.carveAtPoint(arg, posToBiome, carvingMask, random, lv, lv2, lv3, seaLevel, chunkX, chunkZ, v, y2, u, aa, x2, mutableBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable arg2, BlockPos.Mutable arg3, BlockPos.Mutable arg4, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, MutableBoolean mutableBoolean) {
        int q = relativeX | relativeZ << 4 | y << 8;
        if (carvingMask.get(q)) {
            return false;
        }
        carvingMask.set(q);
        arg2.set(x, y, z);
        BlockState lv = chunk.getBlockState(arg2);
        BlockState lv2 = chunk.getBlockState(arg3.set(arg2, Direction.UP));
        if (lv.isOf(Blocks.GRASS_BLOCK) || lv.isOf(Blocks.MYCELIUM)) {
            mutableBoolean.setTrue();
        }
        if (!this.canCarveBlock(lv, lv2)) {
            return false;
        }
        if (y < 11) {
            chunk.setBlockState(arg2, LAVA.getBlockState(), false);
        } else {
            chunk.setBlockState(arg2, CAVE_AIR, false);
            if (mutableBoolean.isTrue()) {
                arg4.set(arg2, Direction.DOWN);
                if (chunk.getBlockState(arg4).isOf(Blocks.DIRT)) {
                    chunk.setBlockState(arg4, posToBiome.apply(arg2).getSurfaceConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }

    public abstract boolean carve(Chunk var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

    public abstract boolean shouldCarve(Random var1, int var2, int var3, C var4);

    protected boolean canAlwaysCarveBlock(BlockState state) {
        return this.alwaysCarvableBlocks.contains(state.getBlock());
    }

    protected boolean canCarveBlock(BlockState state, BlockState stateAbove) {
        return this.canAlwaysCarveBlock(state) || (state.isOf(Blocks.SAND) || state.isOf(Blocks.GRAVEL)) && !stateAbove.getFluidState().isIn(FluidTags.WATER);
    }

    protected boolean isRegionUncarvable(Chunk arg, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int q = relMinX; q < relMaxX; ++q) {
            for (int r = relMinZ; r < relMaxZ; ++r) {
                for (int s = minY - 1; s <= maxY + 1; ++s) {
                    if (this.carvableFluids.contains(arg.getFluidState(lv.set(q + mainChunkX * 16, s, r + mainChunkZ * 16)).getFluid())) {
                        return true;
                    }
                    if (s == maxY + 1 || this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, q, r)) continue;
                    s = maxY;
                }
            }
        }
        return false;
    }

    private boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int x, int z) {
        return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
    }

    protected boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth) {
        double g = mainChunkX * 16 + 8;
        double m = x - g;
        double h = mainChunkZ * 16 + 8;
        double n = z - h;
        double o = branchCount - branch;
        double p = baseWidth + 2.0f + 16.0f;
        return m * m + n * n - o * o <= p * p;
    }

    protected abstract boolean isPositionExcluded(double var1, double var3, double var5, int var7);
}

