/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class FlowerFeature<U extends FeatureConfig>
extends Feature<U> {
    public FlowerFeature(Codec<U> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess world, ChunkGenerator arg2, Random random, BlockPos arg3, U arg4) {
        BlockState lv = this.getFlowerState(random, arg3, arg4);
        int i = 0;
        for (int j = 0; j < this.getFlowerAmount(arg4); ++j) {
            BlockPos lv2 = this.getPos(random, arg3, arg4);
            if (!world.isAir(lv2) || lv2.getY() >= 255 || !lv.canPlaceAt(world, lv2) || !this.isPosValid(world, lv2, arg4)) continue;
            world.setBlockState(lv2, lv, 2);
            ++i;
        }
        return i > 0;
    }

    public abstract boolean isPosValid(WorldAccess var1, BlockPos var2, U var3);

    public abstract int getFlowerAmount(U var1);

    public abstract BlockPos getPos(Random var1, BlockPos var2, U var3);

    public abstract BlockState getFlowerState(Random var1, BlockPos var2, U var3);
}

