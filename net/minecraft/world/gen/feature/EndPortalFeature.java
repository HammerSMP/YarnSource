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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class EndPortalFeature
extends Feature<DefaultFeatureConfig> {
    public static final BlockPos ORIGIN = BlockPos.ORIGIN;
    private final boolean open;

    public EndPortalFeature(boolean bl) {
        super(DefaultFeatureConfig::deserialize);
        this.open = bl;
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        for (BlockPos lv : BlockPos.iterate(new BlockPos(arg4.getX() - 4, arg4.getY() - 1, arg4.getZ() - 4), new BlockPos(arg4.getX() + 4, arg4.getY() + 32, arg4.getZ() + 4))) {
            boolean bl = lv.isWithinDistance(arg4, 2.5);
            if (!bl && !lv.isWithinDistance(arg4, 3.5)) continue;
            if (lv.getY() < arg4.getY()) {
                if (bl) {
                    this.setBlockState(arg, lv, Blocks.BEDROCK.getDefaultState());
                    continue;
                }
                if (lv.getY() >= arg4.getY()) continue;
                this.setBlockState(arg, lv, Blocks.END_STONE.getDefaultState());
                continue;
            }
            if (lv.getY() > arg4.getY()) {
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
            this.setBlockState(arg, arg4.up(i), Blocks.BEDROCK.getDefaultState());
        }
        BlockPos lv2 = arg4.up(2);
        for (Direction lv3 : Direction.Type.HORIZONTAL) {
            this.setBlockState(arg, lv2.offset(lv3), (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, lv3));
        }
        return true;
    }
}

