/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ConcretePowderBlock
extends FallingBlock {
    private final BlockState hardenedState;

    public ConcretePowderBlock(Block arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.hardenedState = arg.getDefaultState();
    }

    @Override
    public void onLanding(World arg, BlockPos arg2, BlockState arg3, BlockState arg4, FallingBlockEntity arg5) {
        if (ConcretePowderBlock.shouldHarden(arg, arg2, arg4)) {
            arg.setBlockState(arg2, this.hardenedState, 3);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv3;
        BlockPos lv2;
        World lv = arg.getWorld();
        if (ConcretePowderBlock.shouldHarden(lv, lv2 = arg.getBlockPos(), lv3 = lv.getBlockState(lv2))) {
            return this.hardenedState;
        }
        return super.getPlacementState(arg);
    }

    private static boolean shouldHarden(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ConcretePowderBlock.hardensIn(arg3) || ConcretePowderBlock.hardensOnAnySide(arg, arg2);
    }

    private static boolean hardensOnAnySide(BlockView arg, BlockPos arg2) {
        boolean bl = false;
        BlockPos.Mutable lv = arg2.mutableCopy();
        for (Direction lv2 : Direction.values()) {
            BlockState lv3 = arg.getBlockState(lv);
            if (lv2 == Direction.DOWN && !ConcretePowderBlock.hardensIn(lv3)) continue;
            lv.set(arg2, lv2);
            lv3 = arg.getBlockState(lv);
            if (!ConcretePowderBlock.hardensIn(lv3) || lv3.isSideSolidFullSquare(arg, arg2, lv2.getOpposite())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    private static boolean hardensIn(BlockState arg) {
        return arg.getFluidState().matches(FluidTags.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (ConcretePowderBlock.hardensOnAnySide(arg4, arg5)) {
            return this.hardenedState;
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.getTopMaterialColor((BlockView)arg2, (BlockPos)arg3).color;
    }
}

