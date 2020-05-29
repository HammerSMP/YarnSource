/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DragonEggBlock
extends FallingBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public DragonEggBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        this.teleport(arg, arg2, arg3);
        return ActionResult.method_29236(arg2.isClient);
    }

    @Override
    public void onBlockBreakStart(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        this.teleport(arg, arg2, arg3);
    }

    private void teleport(BlockState arg, World arg2, BlockPos arg3) {
        for (int i = 0; i < 1000; ++i) {
            BlockPos lv = arg3.add(arg2.random.nextInt(16) - arg2.random.nextInt(16), arg2.random.nextInt(8) - arg2.random.nextInt(8), arg2.random.nextInt(16) - arg2.random.nextInt(16));
            if (!arg2.getBlockState(lv).isAir()) continue;
            if (arg2.isClient) {
                for (int j = 0; j < 128; ++j) {
                    double d = arg2.random.nextDouble();
                    float f = (arg2.random.nextFloat() - 0.5f) * 0.2f;
                    float g = (arg2.random.nextFloat() - 0.5f) * 0.2f;
                    float h = (arg2.random.nextFloat() - 0.5f) * 0.2f;
                    double e = MathHelper.lerp(d, (double)lv.getX(), (double)arg3.getX()) + (arg2.random.nextDouble() - 0.5) + 0.5;
                    double k = MathHelper.lerp(d, (double)lv.getY(), (double)arg3.getY()) + arg2.random.nextDouble() - 0.5;
                    double l = MathHelper.lerp(d, (double)lv.getZ(), (double)arg3.getZ()) + (arg2.random.nextDouble() - 0.5) + 0.5;
                    arg2.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                }
            } else {
                arg2.setBlockState(lv, arg, 2);
                arg2.removeBlock(arg3, false);
            }
            return;
        }
    }

    @Override
    protected int getFallDelay() {
        return 5;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

