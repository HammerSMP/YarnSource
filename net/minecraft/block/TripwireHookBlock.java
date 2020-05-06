/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class TripwireHookBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

    public TripwireHookBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        switch (arg.get(FACING)) {
            default: {
                return WEST_SHAPE;
            }
            case WEST: {
                return EAST_SHAPE;
            }
            case SOUTH: {
                return NORTH_SHAPE;
            }
            case NORTH: 
        }
        return SOUTH_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        Direction lv = arg.get(FACING);
        BlockPos lv2 = arg3.offset(lv.getOpposite());
        BlockState lv3 = arg2.getBlockState(lv2);
        return lv.getAxis().isHorizontal() && lv3.isSideSolidFullSquare(arg2, lv2, lv);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2.getOpposite() == arg.get(FACING) && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction[] lvs;
        BlockState lv = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
        World lv2 = arg.getWorld();
        BlockPos lv3 = arg.getBlockPos();
        for (Direction lv4 : lvs = arg.getPlacementDirections()) {
            Direction lv5;
            if (!lv4.getAxis().isHorizontal() || !(lv = (BlockState)lv.with(FACING, lv5 = lv4.getOpposite())).canPlaceAt(lv2, lv3)) continue;
            return lv;
        }
        return null;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        this.update(arg, arg2, arg3, false, false, -1, null);
    }

    public void update(World arg, BlockPos arg2, BlockState arg3, boolean bl, boolean bl2, int i, @Nullable BlockState arg4) {
        Direction lv = arg3.get(FACING);
        boolean bl3 = arg3.get(ATTACHED);
        boolean bl4 = arg3.get(POWERED);
        boolean bl5 = !bl;
        boolean bl6 = false;
        int j = 0;
        BlockState[] lvs = new BlockState[42];
        for (int k = 1; k < 42; ++k) {
            BlockPos lv2 = arg2.offset(lv, k);
            BlockState lv3 = arg.getBlockState(lv2);
            if (lv3.isOf(Blocks.TRIPWIRE_HOOK)) {
                if (lv3.get(FACING) != lv.getOpposite()) break;
                j = k;
                break;
            }
            if (lv3.isOf(Blocks.TRIPWIRE) || k == i) {
                if (k == i) {
                    lv3 = (BlockState)MoreObjects.firstNonNull((Object)arg4, (Object)lv3);
                }
                boolean bl7 = lv3.get(TripwireBlock.DISARMED) == false;
                boolean bl8 = lv3.get(TripwireBlock.POWERED);
                bl6 |= bl7 && bl8;
                lvs[k] = lv3;
                if (k != i) continue;
                arg.getBlockTickScheduler().schedule(arg2, this, 10);
                bl5 &= bl7;
                continue;
            }
            lvs[k] = null;
            bl5 = false;
        }
        BlockState lv4 = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl5)).with(POWERED, bl6 &= (bl5 &= j > 1));
        if (j > 0) {
            BlockPos lv5 = arg2.offset(lv, j);
            Direction lv6 = lv.getOpposite();
            arg.setBlockState(lv5, (BlockState)lv4.with(FACING, lv6), 3);
            this.updateNeighborsOnAxis(arg, lv5, lv6);
            this.playSound(arg, lv5, bl5, bl6, bl3, bl4);
        }
        this.playSound(arg, arg2, bl5, bl6, bl3, bl4);
        if (!bl) {
            arg.setBlockState(arg2, (BlockState)lv4.with(FACING, lv), 3);
            if (bl2) {
                this.updateNeighborsOnAxis(arg, arg2, lv);
            }
        }
        if (bl3 != bl5) {
            for (int l = 1; l < j; ++l) {
                BlockPos lv7 = arg2.offset(lv, l);
                BlockState lv8 = lvs[l];
                if (lv8 == null) continue;
                arg.setBlockState(lv7, (BlockState)lv8.with(ATTACHED, bl5), 3);
                if (arg.getBlockState(lv7).isAir()) continue;
            }
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.update(arg2, arg3, arg, false, true, -1, null);
    }

    private void playSound(World arg, BlockPos arg2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (bl2 && !bl4) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4f, 0.6f);
        } else if (!bl2 && bl4) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4f, 0.5f);
        } else if (bl && !bl3) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4f, 0.7f);
        } else if (!bl && bl3) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4f, 1.2f / (arg.random.nextFloat() * 0.2f + 0.9f));
        }
    }

    private void updateNeighborsOnAxis(World arg, BlockPos arg2, Direction arg3) {
        arg.updateNeighborsAlways(arg2, this);
        arg.updateNeighborsAlways(arg2.offset(arg3.getOpposite()), this);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        boolean bl2 = arg.get(ATTACHED);
        boolean bl3 = arg.get(POWERED);
        if (bl2 || bl3) {
            this.update(arg2, arg3, arg, true, false, -1, null);
        }
        if (bl3) {
            arg2.updateNeighborsAlways(arg3, this);
            arg2.updateNeighborsAlways(arg3.offset(arg.get(FACING).getOpposite()), this);
        }
        super.onBlockRemoved(arg, arg2, arg3, arg4, bl);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (!arg.get(POWERED).booleanValue()) {
            return 0;
        }
        if (arg.get(FACING) == arg4) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, POWERED, ATTACHED);
    }
}

