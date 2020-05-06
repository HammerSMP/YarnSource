/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DefaultSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    public DefaultSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, int l, long m, TernarySurfaceConfig arg5) {
        this.generate(random, arg, arg2, i, j, k, d, arg3, arg4, arg5.getTopMaterial(), arg5.getUnderMaterial(), arg5.getUnderwaterMaterial(), l);
    }

    protected void generate(Random random, Chunk arg, Biome arg2, int i, int j, int k, double d, BlockState arg3, BlockState arg4, BlockState arg5, BlockState arg6, BlockState arg7, int l) {
        BlockState lv = arg5;
        BlockState lv2 = arg6;
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        int m = -1;
        int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int o = i & 0xF;
        int p = j & 0xF;
        for (int q = k; q >= 0; --q) {
            lv3.set(o, q, p);
            BlockState lv4 = arg.getBlockState(lv3);
            if (lv4.isAir()) {
                m = -1;
                continue;
            }
            if (!lv4.isOf(arg3.getBlock())) continue;
            if (m == -1) {
                if (n <= 0) {
                    lv = Blocks.AIR.getDefaultState();
                    lv2 = arg3;
                } else if (q >= l - 4 && q <= l + 1) {
                    lv = arg5;
                    lv2 = arg6;
                }
                if (q < l && (lv == null || lv.isAir())) {
                    lv = arg2.getTemperature(lv3.set(i, q, j)) < 0.15f ? Blocks.ICE.getDefaultState() : arg4;
                    lv3.set(o, q, p);
                }
                m = n;
                if (q >= l - 1) {
                    arg.setBlockState(lv3, lv, false);
                    continue;
                }
                if (q < l - 7 - n) {
                    lv = Blocks.AIR.getDefaultState();
                    lv2 = arg3;
                    arg.setBlockState(lv3, arg7, false);
                    continue;
                }
                arg.setBlockState(lv3, lv2, false);
                continue;
            }
            if (m <= 0) continue;
            arg.setBlockState(lv3, lv2, false);
            if (--m != 0 || !lv2.isOf(Blocks.SAND) || n <= 1) continue;
            m = random.nextInt(4) + Math.max(0, q - 63);
            lv2 = lv2.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
        }
    }
}

