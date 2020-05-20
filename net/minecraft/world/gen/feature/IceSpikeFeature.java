/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class IceSpikeFeature
extends Feature<DefaultFeatureConfig> {
    public IceSpikeFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        while (arg.isAir(arg4) && arg4.getY() > 2) {
            arg4 = arg4.down();
        }
        if (!arg.getBlockState(arg4).isOf(Blocks.SNOW_BLOCK)) {
            return false;
        }
        arg4 = arg4.up(random.nextInt(4));
        int i = random.nextInt(4) + 7;
        int j = i / 4 + random.nextInt(2);
        if (j > 1 && random.nextInt(60) == 0) {
            arg4 = arg4.up(10 + random.nextInt(30));
        }
        for (int k = 0; k < i; ++k) {
            float f = (1.0f - (float)k / (float)i) * (float)j;
            int l = MathHelper.ceil(f);
            for (int m = -l; m <= l; ++m) {
                float g = (float)MathHelper.abs(m) - 0.25f;
                for (int n = -l; n <= l; ++n) {
                    float h = (float)MathHelper.abs(n) - 0.25f;
                    if ((m != 0 || n != 0) && g * g + h * h > f * f || (m == -l || m == l || n == -l || n == l) && random.nextFloat() > 0.75f) continue;
                    BlockState lv = arg.getBlockState(arg4.add(m, k, n));
                    Block lv2 = lv.getBlock();
                    if (lv.isAir() || IceSpikeFeature.isDirt(lv2) || lv2 == Blocks.SNOW_BLOCK || lv2 == Blocks.ICE) {
                        this.setBlockState(arg, arg4.add(m, k, n), Blocks.PACKED_ICE.getDefaultState());
                    }
                    if (k == 0 || l <= 1) continue;
                    lv = arg.getBlockState(arg4.add(m, -k, n));
                    lv2 = lv.getBlock();
                    if (!lv.isAir() && !IceSpikeFeature.isDirt(lv2) && lv2 != Blocks.SNOW_BLOCK && lv2 != Blocks.ICE) continue;
                    this.setBlockState(arg, arg4.add(m, -k, n), Blocks.PACKED_ICE.getDefaultState());
                }
            }
        }
        int o = j - 1;
        if (o < 0) {
            o = 0;
        } else if (o > 1) {
            o = 1;
        }
        for (int p = -o; p <= o; ++p) {
            block5: for (int q = -o; q <= o; ++q) {
                BlockPos lv3 = arg4.add(p, -1, q);
                int r = 50;
                if (Math.abs(p) == 1 && Math.abs(q) == 1) {
                    r = random.nextInt(5);
                }
                while (lv3.getY() > 50) {
                    BlockState lv4 = arg.getBlockState(lv3);
                    Block lv5 = lv4.getBlock();
                    if (!lv4.isAir() && !IceSpikeFeature.isDirt(lv5) && lv5 != Blocks.SNOW_BLOCK && lv5 != Blocks.ICE && lv5 != Blocks.PACKED_ICE) continue block5;
                    this.setBlockState(arg, lv3, Blocks.PACKED_ICE.getDefaultState());
                    lv3 = lv3.down();
                    if (--r > 0) continue;
                    lv3 = lv3.down(random.nextInt(5) + 1);
                    r = random.nextInt(5);
                }
            }
        }
        return true;
    }
}

