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
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class VinesFeature
extends Feature<DefaultFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public VinesFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        BlockPos.Mutable lv = arg3.mutableCopy();
        block0: for (int i = 64; i < 256; ++i) {
            lv.set(arg3);
            lv.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            lv.setY(i);
            if (!arg.isAir(lv)) continue;
            for (Direction lv2 : DIRECTIONS) {
                if (lv2 == Direction.DOWN || !VineBlock.shouldConnectTo(arg, lv, lv2)) continue;
                arg.setBlockState(lv, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(lv2), true), 2);
                continue block0;
            }
        }
        return true;
    }
}

