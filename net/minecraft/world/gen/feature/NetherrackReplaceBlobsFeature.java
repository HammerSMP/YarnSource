/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.class_5437;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class NetherrackReplaceBlobsFeature
extends Feature<class_5437> {
    public NetherrackReplaceBlobsFeature(Codec<class_5437> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, class_5437 arg4) {
        Block lv = arg4.field_25849.getBlock();
        BlockPos lv2 = NetherrackReplaceBlobsFeature.method_27107(arg, arg3.mutableCopy().method_27158(Direction.Axis.Y, 1, arg.getHeight() - 1), lv);
        if (lv2 == null) {
            return false;
        }
        int i = arg4.method_30405().method_30321(random);
        boolean bl = false;
        for (BlockPos lv3 : BlockPos.iterateOutwards(lv2, i, i, i)) {
            if (lv3.getManhattanDistance(lv2) > i) break;
            BlockState lv4 = arg.getBlockState(lv3);
            if (!lv4.isOf(lv)) continue;
            this.setBlockState(arg, lv3, arg4.field_25850);
            bl = true;
        }
        return bl;
    }

    @Nullable
    private static BlockPos method_27107(WorldAccess arg, BlockPos.Mutable arg2, Block arg3) {
        while (arg2.getY() > 1) {
            BlockState lv = arg.getBlockState(arg2);
            if (lv.isOf(arg3)) {
                return arg2;
            }
            arg2.move(Direction.DOWN);
        }
        return null;
    }
}

