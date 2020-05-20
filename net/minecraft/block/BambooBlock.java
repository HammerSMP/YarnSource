/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BambooBlock
extends Block
implements Fertilizable {
    protected static final VoxelShape SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntProperty AGE = Properties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
    public static final IntProperty STAGE = Properties.STAGE;

    public BambooBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0)).with(LEAVES, BambooLeaves.NONE)).with(STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE, LEAVES, STAGE);
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        VoxelShape lv = arg.get(LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
        Vec3d lv2 = arg.getModelOffset(arg2, arg3);
        return lv.offset(lv2.x, lv2.y, lv2.z);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        Vec3d lv = arg.getModelOffset(arg2, arg3);
        return NO_LEAVES_SHAPE.offset(lv.x, lv.y, lv.z);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv = arg.getWorld().getFluidState(arg.getBlockPos());
        if (!lv.isEmpty()) {
            return null;
        }
        BlockState lv2 = arg.getWorld().getBlockState(arg.getBlockPos().down());
        if (lv2.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (lv2.isOf(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.getDefaultState().with(AGE, 0);
            }
            if (lv2.isOf(Blocks.BAMBOO)) {
                int i = lv2.get(AGE) > 0 ? 1 : 0;
                return (BlockState)this.getDefaultState().with(AGE, i);
            }
            return Blocks.BAMBOO_SAPLING.getDefaultState();
        }
        return null;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.breakBlock(arg3, true);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(STAGE) == 0;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        int i;
        if (arg.get(STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && arg2.isAir(arg3.up()) && arg2.getBaseLightLevel(arg3.up(), 0) >= 9 && (i = this.countBambooBelow(arg2, arg3) + 1) < 16) {
            this.updateLeaves(arg, arg2, arg3, random, i);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return arg2.getBlockState(arg3.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (!arg.canPlaceAt(arg4, arg5)) {
            arg4.getBlockTickScheduler().schedule(arg5, this, 1);
        }
        if (arg2 == Direction.UP && arg3.isOf(Blocks.BAMBOO) && arg3.get(AGE) > arg.get(AGE)) {
            arg4.setBlockState(arg5, (BlockState)arg.method_28493(AGE), 2);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean isFertilizable(BlockView arg, BlockPos arg2, BlockState arg3, boolean bl) {
        int j;
        int i = this.countBambooAbove(arg, arg2);
        return i + (j = this.countBambooBelow(arg, arg2)) + 1 < 16 && arg.getBlockState(arg2.up(i)).get(STAGE) != 1;
    }

    @Override
    public boolean canGrow(World arg, Random random, BlockPos arg2, BlockState arg3) {
        return true;
    }

    @Override
    public void grow(ServerWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        int i = this.countBambooAbove(arg, arg2);
        int j = this.countBambooBelow(arg, arg2);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);
        for (int m = 0; m < l; ++m) {
            BlockPos lv = arg2.up(i);
            BlockState lv2 = arg.getBlockState(lv);
            if (k >= 16 || lv2.get(STAGE) == 1 || !arg.isAir(lv.up())) {
                return;
            }
            this.updateLeaves(lv2, arg, lv, random, k);
            ++i;
            ++k;
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState arg, PlayerEntity arg2, BlockView arg3, BlockPos arg4) {
        if (arg2.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(arg, arg2, arg3, arg4);
    }

    protected void updateLeaves(BlockState arg, World arg2, BlockPos arg3, Random random, int i) {
        BlockState lv = arg2.getBlockState(arg3.down());
        BlockPos lv2 = arg3.down(2);
        BlockState lv3 = arg2.getBlockState(lv2);
        BambooLeaves lv4 = BambooLeaves.NONE;
        if (i >= 1) {
            if (!lv.isOf(Blocks.BAMBOO) || lv.get(LEAVES) == BambooLeaves.NONE) {
                lv4 = BambooLeaves.SMALL;
            } else if (lv.isOf(Blocks.BAMBOO) && lv.get(LEAVES) != BambooLeaves.NONE) {
                lv4 = BambooLeaves.LARGE;
                if (lv3.isOf(Blocks.BAMBOO)) {
                    arg2.setBlockState(arg3.down(), (BlockState)lv.with(LEAVES, BambooLeaves.SMALL), 3);
                    arg2.setBlockState(lv2, (BlockState)lv3.with(LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }
        int j = arg.get(AGE) == 1 || lv3.isOf(Blocks.BAMBOO) ? 1 : 0;
        int k = i >= 11 && random.nextFloat() < 0.25f || i == 15 ? 1 : 0;
        arg2.setBlockState(arg3.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(AGE, j)).with(LEAVES, lv4)).with(STAGE, k), 3);
    }

    protected int countBambooAbove(BlockView arg, BlockPos arg2) {
        int i;
        for (i = 0; i < 16 && arg.getBlockState(arg2.up(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }

    protected int countBambooBelow(BlockView arg, BlockPos arg2) {
        int i;
        for (i = 0; i < 16 && arg.getBlockState(arg2.down(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }
}

