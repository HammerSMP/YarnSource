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
import net.minecraft.block.IceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock
extends IceBlock {
    public static final IntProperty AGE = Properties.AGE_3;

    public FrostedIceBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.scheduledTick(arg, arg2, arg3, random);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if ((random.nextInt(3) == 0 || this.canMelt(arg2, arg3, 4)) && arg2.getLightLevel(arg3) > 11 - arg.get(AGE) - arg.getOpacity(arg2, arg3) && this.increaseAge(arg, arg2, arg3)) {
            BlockPos.Mutable lv = new BlockPos.Mutable();
            for (Direction lv2 : Direction.values()) {
                lv.set(arg3, lv2);
                BlockState lv3 = arg2.getBlockState(lv);
                if (!lv3.isOf(this) || this.increaseAge(lv3, arg2, lv)) continue;
                arg2.getBlockTickScheduler().schedule(lv, this, MathHelper.nextInt(random, 20, 40));
            }
            return;
        }
        arg2.getBlockTickScheduler().schedule(arg3, this, MathHelper.nextInt(random, 20, 40));
    }

    private boolean increaseAge(BlockState arg, World arg2, BlockPos arg3) {
        int i = arg.get(AGE);
        if (i < 3) {
            arg2.setBlockState(arg3, (BlockState)arg.with(AGE, i + 1), 2);
            return false;
        }
        this.melt(arg, arg2, arg3);
        return true;
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg4 == this && this.canMelt(arg2, arg3, 2)) {
            this.melt(arg, arg2, arg3);
        }
        super.neighborUpdate(arg, arg2, arg3, arg4, arg5, bl);
    }

    private boolean canMelt(BlockView arg, BlockPos arg2, int i) {
        int j = 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.values()) {
            lv.set(arg2, lv2);
            if (!arg.getBlockState(lv).isOf(this) || ++j < i) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }
}

