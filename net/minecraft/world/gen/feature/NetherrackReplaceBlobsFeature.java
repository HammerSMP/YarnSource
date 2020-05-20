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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NetherrackReplaceBlobsFeatureConfig;

public class NetherrackReplaceBlobsFeature
extends Feature<NetherrackReplaceBlobsFeatureConfig> {
    public NetherrackReplaceBlobsFeature(Codec<NetherrackReplaceBlobsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, NetherrackReplaceBlobsFeatureConfig arg5) {
        Block lv = arg5.target.getBlock();
        BlockPos lv2 = NetherrackReplaceBlobsFeature.method_27107(arg, arg4.mutableCopy().method_27158(Direction.Axis.Y, 1, arg.getHeight() - 1), lv);
        if (lv2 == null) {
            return false;
        }
        Vec3i lv3 = NetherrackReplaceBlobsFeature.method_27108(random, arg5);
        int i = Math.max(lv3.getX(), Math.max(lv3.getY(), lv3.getZ()));
        boolean bl = false;
        for (BlockPos lv4 : BlockPos.iterateOutwards(lv2, lv3.getX(), lv3.getY(), lv3.getZ())) {
            if (lv4.getManhattanDistance(lv2) > i) break;
            BlockState lv5 = arg.getBlockState(lv4);
            if (!lv5.isOf(lv)) continue;
            this.setBlockState(arg, lv4, arg5.state);
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

    private static Vec3i method_27108(Random random, NetherrackReplaceBlobsFeatureConfig arg) {
        return new Vec3i(arg.minReachPos.getX() + random.nextInt(arg.maxReachPos.getX() - arg.minReachPos.getX() + 1), arg.minReachPos.getY() + random.nextInt(arg.maxReachPos.getY() - arg.minReachPos.getY() + 1), arg.minReachPos.getZ() + random.nextInt(arg.maxReachPos.getZ() - arg.minReachPos.getZ() + 1));
    }
}

