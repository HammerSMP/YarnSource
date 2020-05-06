/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FallingBlock
extends Block {
    public FallingBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        arg2.getBlockTickScheduler().schedule(arg3, this, this.getFallDelay());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        arg4.getBlockTickScheduler().schedule(arg5, this, this.getFallDelay());
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!FallingBlock.canFallThrough(arg2.getBlockState(arg3.down())) || arg3.getY() < 0) {
            return;
        }
        FallingBlockEntity lv = new FallingBlockEntity(arg2, (double)arg3.getX() + 0.5, arg3.getY(), (double)arg3.getZ() + 0.5, arg2.getBlockState(arg3));
        this.configureFallingBlockEntity(lv);
        arg2.spawnEntity(lv);
    }

    protected void configureFallingBlockEntity(FallingBlockEntity arg) {
    }

    protected int getFallDelay() {
        return 2;
    }

    public static boolean canFallThrough(BlockState arg) {
        Material lv = arg.getMaterial();
        return arg.isAir() || arg.isIn(BlockTags.FIRE) || lv.isLiquid() || lv.isReplaceable();
    }

    public void onLanding(World arg, BlockPos arg2, BlockState arg3, BlockState arg4, FallingBlockEntity arg5) {
    }

    public void onDestroyedOnLanding(World arg, BlockPos arg2, FallingBlockEntity arg3) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        BlockPos lv;
        if (random.nextInt(16) == 0 && FallingBlock.canFallThrough(arg2.getBlockState(lv = arg3.down()))) {
            double d = (double)arg3.getX() + (double)random.nextFloat();
            double e = (double)arg3.getY() - 0.05;
            double f = (double)arg3.getZ() + (double)random.nextFloat();
            arg2.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, arg), d, e, f, 0.0, 0.0, 0.0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState arg, BlockView arg2, BlockPos arg3) {
        return -16777216;
    }
}

