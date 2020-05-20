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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;

public class IcePatchFeature
extends Feature<IcePatchFeatureConfig> {
    private final Block ICE = Blocks.PACKED_ICE;

    public IcePatchFeature(Codec<IcePatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, IcePatchFeatureConfig arg5) {
        while (arg.isAir(arg4) && arg4.getY() > 2) {
            arg4 = arg4.down();
        }
        if (!arg.getBlockState(arg4).isOf(Blocks.SNOW_BLOCK)) {
            return false;
        }
        int i = random.nextInt(arg5.radius) + 2;
        boolean j = true;
        for (int k = arg4.getX() - i; k <= arg4.getX() + i; ++k) {
            for (int l = arg4.getZ() - i; l <= arg4.getZ() + i; ++l) {
                int n;
                int m = k - arg4.getX();
                if (m * m + (n = l - arg4.getZ()) * n > i * i) continue;
                for (int o = arg4.getY() - 1; o <= arg4.getY() + 1; ++o) {
                    BlockPos lv = new BlockPos(k, o, l);
                    Block lv2 = arg.getBlockState(lv).getBlock();
                    if (!IcePatchFeature.isDirt(lv2) && lv2 != Blocks.SNOW_BLOCK && lv2 != Blocks.ICE) continue;
                    arg.setBlockState(lv, this.ICE.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}

