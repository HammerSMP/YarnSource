/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<ProbabilityConfig> CAVE = Carver.register("cave", new CaveCarver(ProbabilityConfig.field_24899, 256));
    public static final Carver<ProbabilityConfig> NETHER_CAVE = Carver.register("nether_cave", new NetherCaveCarver(ProbabilityConfig.field_24899));
    public static final Carver<ProbabilityConfig> CANYON = Carver.register("canyon", new RavineCarver(ProbabilityConfig.field_24899));
    public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = Carver.register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig.field_24899));
    public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = Carver.register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig.field_24899));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
    protected Set<Fluid> carvableFluids = ImmutableSet.of((Object)Fluids.WATER);
    private final Codec<ConfiguredCarver<C>> field_24831;
    protected final int heightLimit;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F arg) {
        return (F)Registry.register(Registry.CARVER, string, arg);
    }

    public Carver(Codec<C> codec, int i) {
        this.heightLimit = i;
        this.field_24831 = codec.fieldOf("config").xmap(arg -> new ConfiguredCarver<CarverConfig>(this, (CarverConfig)arg), arg -> arg.config).codec();
    }

    public Codec<ConfiguredCarver<C>> method_28616() {
        return this.field_24831;
    }

    public int getBranchFactor() {
        return 4;
    }

    protected boolean carveRegion(Chunk arg, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, double g, double h, BitSet bitSet) {
        int t;
        int s;
        int r;
        int q;
        int p;
        Random random = new Random(l + (long)j + (long)k);
        double m = j * 16 + 8;
        double n = k * 16 + 8;
        if (d < m - 16.0 - g * 2.0 || f < n - 16.0 - g * 2.0 || d > m + 16.0 + g * 2.0 || f > n + 16.0 + g * 2.0) {
            return false;
        }
        int o = Math.max(MathHelper.floor(d - g) - j * 16 - 1, 0);
        if (this.isRegionUncarvable(arg, j, k, o, p = Math.min(MathHelper.floor(d + g) - j * 16 + 1, 16), q = Math.max(MathHelper.floor(e - h) - 1, 1), r = Math.min(MathHelper.floor(e + h) + 1, this.heightLimit - 8), s = Math.max(MathHelper.floor(f - g) - k * 16 - 1, 0), t = Math.min(MathHelper.floor(f + g) - k * 16 + 1, 16))) {
            return false;
        }
        boolean bl = false;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int u = o; u < p; ++u) {
            int v = u + j * 16;
            double w = ((double)v + 0.5 - d) / g;
            for (int x = s; x < t; ++x) {
                int y = x + k * 16;
                double z = ((double)y + 0.5 - f) / g;
                if (w * w + z * z >= 1.0) continue;
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                for (int aa = r; aa > q; --aa) {
                    double ab = ((double)aa - 0.5 - e) / h;
                    if (this.isPositionExcluded(w, ab, z, aa)) continue;
                    bl |= this.carveAtPoint(arg, function, bitSet, random, lv, lv2, lv3, i, j, k, v, y, u, aa, x, atomicBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(Chunk arg, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.Mutable arg2, BlockPos.Mutable arg3, BlockPos.Mutable arg4, int i, int j, int k, int l, int m, int n, int o, int p, AtomicBoolean atomicBoolean) {
        int q = n | p << 4 | o << 8;
        if (bitSet.get(q)) {
            return false;
        }
        bitSet.set(q);
        arg2.set(l, o, m);
        BlockState lv = arg.getBlockState(arg2);
        BlockState lv2 = arg.getBlockState(arg3.set(arg2, Direction.UP));
        if (lv.isOf(Blocks.GRASS_BLOCK) || lv.isOf(Blocks.MYCELIUM)) {
            atomicBoolean.set(true);
        }
        if (!this.canCarveBlock(lv, lv2)) {
            return false;
        }
        if (o < 11) {
            arg.setBlockState(arg2, LAVA.getBlockState(), false);
        } else {
            arg.setBlockState(arg2, CAVE_AIR, false);
            if (atomicBoolean.get()) {
                arg4.set(arg2, Direction.DOWN);
                if (arg.getBlockState(arg4).isOf(Blocks.DIRT)) {
                    arg.setBlockState(arg4, function.apply(arg2).getSurfaceConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }

    public abstract boolean carve(Chunk var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

    public abstract boolean shouldCarve(Random var1, int var2, int var3, C var4);

    protected boolean canAlwaysCarveBlock(BlockState arg) {
        return this.alwaysCarvableBlocks.contains(arg.getBlock());
    }

    protected boolean canCarveBlock(BlockState arg, BlockState arg2) {
        return this.canAlwaysCarveBlock(arg) || (arg.isOf(Blocks.SAND) || arg.isOf(Blocks.GRAVEL)) && !arg2.getFluidState().matches(FluidTags.WATER);
    }

    protected boolean isRegionUncarvable(Chunk arg, int i, int j, int k, int l, int m, int n, int o, int p) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int q = k; q < l; ++q) {
            for (int r = o; r < p; ++r) {
                for (int s = m - 1; s <= n + 1; ++s) {
                    if (this.carvableFluids.contains(arg.getFluidState(lv.set(q + i * 16, s, r + j * 16)).getFluid())) {
                        return true;
                    }
                    if (s == n + 1 || this.isOnBoundary(k, l, o, p, q, r)) continue;
                    s = n;
                }
            }
        }
        return false;
    }

    private boolean isOnBoundary(int i, int j, int k, int l, int m, int n) {
        return m == i || m == j - 1 || n == k || n == l - 1;
    }

    protected boolean canCarveBranch(int i, int j, double d, double e, int k, int l, float f) {
        double g = i * 16 + 8;
        double m = d - g;
        double h = j * 16 + 8;
        double n = e - h;
        double o = l - k;
        double p = f + 2.0f + 16.0f;
        return m * m + n * n - o * o <= p * p;
    }

    protected abstract boolean isPositionExcluded(double var1, double var3, double var5, int var7);
}

