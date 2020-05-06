/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public abstract class AbstractNetherSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private long field_23920;
    private ImmutableMap<BlockState, OctavePerlinNoiseSampler> field_23921 = ImmutableMap.of();
    private ImmutableMap<BlockState, OctavePerlinNoiseSampler> field_23922 = ImmutableMap.of();
    private OctavePerlinNoiseSampler field_23923;

    public AbstractNetherSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        int n = l + 1;
        int o = i & 0xF;
        int p = j & 0xF;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        double e = 0.03125;
        boolean bl = this.field_23923.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        BlockState lv = (BlockState)this.field_23922.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockState lv2 = (BlockState)this.field_23921.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        BlockState lv4 = arg.getBlockState(lv3.set(o, 128, p));
        for (int s = 127; s >= 0; --s) {
            lv3.set(o, s, p);
            BlockState lv5 = arg.getBlockState(lv3);
            if (lv4.isOf(arg3.getBlock()) && (lv5.isAir() || lv5 == arg4)) {
                for (int t = 0; t < q; ++t) {
                    lv3.move(Direction.UP);
                    if (!arg.getBlockState(lv3).isOf(arg3.getBlock())) break;
                    arg.setBlockState(lv3, lv, false);
                }
                lv3.set(o, s, p);
            }
            if ((lv4.isAir() || lv4 == arg4) && lv5.isOf(arg3.getBlock())) {
                for (int u = 0; u < r && arg.getBlockState(lv3).isOf(arg3.getBlock()); ++u) {
                    if (bl && s >= n - 4 && s <= n + 1) {
                        arg.setBlockState(lv3, this.method_27135(), false);
                    } else {
                        arg.setBlockState(lv3, lv2, false);
                    }
                    lv3.move(Direction.DOWN);
                }
            }
            lv4 = lv5;
        }
    }

    @Override
    public void initSeed(long l) {
        if (this.field_23920 != l || this.field_23923 == null || this.field_23921.isEmpty() || this.field_23922.isEmpty()) {
            this.field_23921 = AbstractNetherSurfaceBuilder.method_27131(this.method_27129(), l);
            this.field_23922 = AbstractNetherSurfaceBuilder.method_27131(this.method_27133(), l + (long)this.field_23921.size());
            this.field_23923 = new OctavePerlinNoiseSampler(new ChunkRandom(l + (long)this.field_23921.size() + (long)this.field_23922.size()), (List<Integer>)ImmutableList.of((Object)0));
        }
        this.field_23920 = l;
    }

    private static ImmutableMap<BlockState, OctavePerlinNoiseSampler> method_27131(ImmutableList<BlockState> immutableList, long l) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (BlockState lv : immutableList) {
            builder.put((Object)lv, (Object)new OctavePerlinNoiseSampler(new ChunkRandom(l), (List<Integer>)ImmutableList.of((Object)-4)));
            ++l;
        }
        return builder.build();
    }

    protected abstract ImmutableList<BlockState> method_27129();

    protected abstract ImmutableList<BlockState> method_27133();

    protected abstract BlockState method_27135();
}

