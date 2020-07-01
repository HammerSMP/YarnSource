/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndPortalFeature
extends Feature<DefaultFeatureConfig> {
    public static final BlockPos ORIGIN = BlockPos.ORIGIN;
    private final boolean open;

    public EndPortalFeature(boolean bl) {
        super(DefaultFeatureConfig.CODEC);
        this.open = bl;
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DefaultFeatureConfig arg4) {
        for (BlockPos lv : BlockPos.iterate(new BlockPos(arg3.getX() - 4, arg3.getY() - 1, arg3.getZ() - 4), new BlockPos(arg3.getX() + 4, arg3.getY() + 32, arg3.getZ() + 4))) {
            boolean bl = lv.isWithinDistance(arg3, 2.5);
            if (!bl && !lv.isWithinDistance(arg3, 3.5)) continue;
            if (lv.getY() < arg3.getY()) {
                if (bl) {
                    this.setBlockState(arg, lv, Blocks.BEDROCK.getDefaultState());
                    continue;
                }
                if (lv.getY() >= arg3.getY()) continue;
                this.setBlockState(arg, lv, Blocks.END_STONE.getDefaultState());
                continue;
            }
            if (lv.getY() > arg3.getY()) {
                this.setBlockState(arg, lv, Blocks.AIR.getDefaultState());
                continue;
            }
            if (!bl) {
                this.setBlockState(arg, lv, Blocks.BEDROCK.getDefaultState());
                continue;
            }
            if (this.open) {
                this.setBlockState(arg, new BlockPos(lv), Blocks.END_PORTAL.getDefaultState());
                continue;
            }
            this.setBlockState(arg, new BlockPos(lv), Blocks.AIR.getDefaultState());
        }
        for (int i = 0; i < 4; ++i) {
            this.setBlockState(arg, arg3.up(i), Blocks.BEDROCK.getDefaultState());
        }
        BlockPos lv2 = arg3.up(2);
        for (Direction lv3 : Direction.Type.HORIZONTAL) {
            this.setBlockState(arg, lv2.offset(lv3), (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, lv3));
        }
        return true;
    }
}

