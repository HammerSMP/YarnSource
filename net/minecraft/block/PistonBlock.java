/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlock
extends FacingBlock {
    public static final BooleanProperty EXTENDED = Properties.EXTENDED;
    protected static final VoxelShape EXTENDED_EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    protected static final VoxelShape EXTENDED_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EXTENDED_UP_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected static final VoxelShape EXTENDED_DOWN_SHAPE = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    private final boolean sticky;

    public PistonBlock(boolean bl, AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
        this.sticky = bl;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        if (arg.get(EXTENDED).booleanValue()) {
            switch (arg.get(FACING)) {
                case DOWN: {
                    return EXTENDED_DOWN_SHAPE;
                }
                default: {
                    return EXTENDED_UP_SHAPE;
                }
                case NORTH: {
                    return EXTENDED_NORTH_SHAPE;
                }
                case SOUTH: {
                    return EXTENDED_SOUTH_SHAPE;
                }
                case WEST: {
                    return EXTENDED_WEST_SHAPE;
                }
                case EAST: 
            }
            return EXTENDED_EAST_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        if (!arg.isClient) {
            this.tryMove(arg, arg2, arg3);
        }
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (!arg2.isClient) {
            this.tryMove(arg2, arg3, arg);
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        if (!arg2.isClient && arg2.getBlockEntity(arg3) == null) {
            this.tryMove(arg2, arg3, arg);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerLookDirection().getOpposite())).with(EXTENDED, false);
    }

    private void tryMove(World arg, BlockPos arg2, BlockState arg3) {
        Direction lv = arg3.get(FACING);
        boolean bl = this.shouldExtend(arg, arg2, lv);
        if (bl && !arg3.get(EXTENDED).booleanValue()) {
            if (new PistonHandler(arg, arg2, lv, true).calculatePush()) {
                arg.addSyncedBlockEvent(arg2, this, 0, lv.getId());
            }
        } else if (!bl && arg3.get(EXTENDED).booleanValue()) {
            PistonBlockEntity lv5;
            BlockEntity lv4;
            BlockPos lv2 = arg2.offset(lv, 2);
            BlockState lv3 = arg.getBlockState(lv2);
            int i = 1;
            if (lv3.isOf(Blocks.MOVING_PISTON) && lv3.get(FACING) == lv && (lv4 = arg.getBlockEntity(lv2)) instanceof PistonBlockEntity && (lv5 = (PistonBlockEntity)lv4).isExtending() && (lv5.getProgress(0.0f) < 0.5f || arg.getTime() == lv5.getSavedWorldTime() || ((ServerWorld)arg).isInBlockTick())) {
                i = 2;
            }
            arg.addSyncedBlockEvent(arg2, this, i, lv.getId());
        }
    }

    private boolean shouldExtend(World arg, BlockPos arg2, Direction arg3) {
        for (Direction lv : Direction.values()) {
            if (lv == arg3 || !arg.isEmittingRedstonePower(arg2.offset(lv), lv)) continue;
            return true;
        }
        if (arg.isEmittingRedstonePower(arg2, Direction.DOWN)) {
            return true;
        }
        BlockPos lv2 = arg2.up();
        for (Direction lv3 : Direction.values()) {
            if (lv3 == Direction.DOWN || !arg.isEmittingRedstonePower(lv2.offset(lv3), lv3)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean onSyncedBlockEvent(BlockState arg, World arg2, BlockPos arg3, int i, int j) {
        Direction lv = arg.get(FACING);
        if (!arg2.isClient) {
            boolean bl = this.shouldExtend(arg2, arg3, lv);
            if (bl && (i == 1 || i == 2)) {
                arg2.setBlockState(arg3, (BlockState)arg.with(EXTENDED, true), 2);
                return false;
            }
            if (!bl && i == 0) {
                return false;
            }
        }
        if (i == 0) {
            if (!this.move(arg2, arg3, lv, true)) return false;
            arg2.setBlockState(arg3, (BlockState)arg.with(EXTENDED, true), 67);
            arg2.playSound(null, arg3, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, arg2.random.nextFloat() * 0.25f + 0.6f);
            return true;
        } else {
            if (i != 1 && i != 2) return true;
            BlockEntity lv2 = arg2.getBlockEntity(arg3.offset(lv));
            if (lv2 instanceof PistonBlockEntity) {
                ((PistonBlockEntity)lv2).finish();
            }
            BlockState lv3 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, lv)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            arg2.setBlockState(arg3, lv3, 20);
            arg2.setBlockEntity(arg3, PistonExtensionBlock.createBlockEntityPiston((BlockState)this.getDefaultState().with(FACING, Direction.byId(j & 7)), lv, false, true));
            arg2.updateNeighbors(arg3, lv3.getBlock());
            lv3.updateNeighbors(arg2, arg3, 2);
            if (this.sticky) {
                PistonBlockEntity lv7;
                BlockEntity lv6;
                BlockPos lv4 = arg3.add(lv.getOffsetX() * 2, lv.getOffsetY() * 2, lv.getOffsetZ() * 2);
                BlockState lv5 = arg2.getBlockState(lv4);
                boolean bl2 = false;
                if (lv5.isOf(Blocks.MOVING_PISTON) && (lv6 = arg2.getBlockEntity(lv4)) instanceof PistonBlockEntity && (lv7 = (PistonBlockEntity)lv6).getFacing() == lv && lv7.isExtending()) {
                    lv7.finish();
                    bl2 = true;
                }
                if (!bl2) {
                    if (i == 1 && !lv5.isAir() && PistonBlock.isMovable(lv5, arg2, lv4, lv.getOpposite(), false, lv) && (lv5.getPistonBehavior() == PistonBehavior.NORMAL || lv5.isOf(Blocks.PISTON) || lv5.isOf(Blocks.STICKY_PISTON))) {
                        this.move(arg2, arg3, lv, false);
                    } else {
                        arg2.removeBlock(arg3.offset(lv), false);
                    }
                }
            } else {
                arg2.removeBlock(arg3.offset(lv), false);
            }
            arg2.playSound(null, arg3, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f, arg2.random.nextFloat() * 0.15f + 0.6f);
        }
        return true;
    }

    public static boolean isMovable(BlockState arg, World arg2, BlockPos arg3, Direction arg4, boolean bl, Direction arg5) {
        if (arg.isOf(Blocks.OBSIDIAN) || arg.isOf(Blocks.CRYING_OBSIDIAN) || arg.isOf(Blocks.RESPAWN_ANCHOR)) {
            return false;
        }
        if (!arg2.getWorldBorder().contains(arg3)) {
            return false;
        }
        if (arg3.getY() < 0 || arg4 == Direction.DOWN && arg3.getY() == 0) {
            return false;
        }
        if (arg3.getY() > arg2.getHeight() - 1 || arg4 == Direction.UP && arg3.getY() == arg2.getHeight() - 1) {
            return false;
        }
        if (arg.isOf(Blocks.PISTON) || arg.isOf(Blocks.STICKY_PISTON)) {
            if (arg.get(EXTENDED).booleanValue()) {
                return false;
            }
        } else {
            if (arg.getHardness(arg2, arg3) == -1.0f) {
                return false;
            }
            switch (arg.getPistonBehavior()) {
                case BLOCK: {
                    return false;
                }
                case DESTROY: {
                    return bl;
                }
                case PUSH_ONLY: {
                    return arg4 == arg5;
                }
            }
        }
        return !arg.getBlock().hasBlockEntity();
    }

    private boolean move(World arg, BlockPos arg2, Direction arg3, boolean bl) {
        PistonHandler lv2;
        BlockPos lv = arg2.offset(arg3);
        if (!bl && arg.getBlockState(lv).isOf(Blocks.PISTON_HEAD)) {
            arg.setBlockState(lv, Blocks.AIR.getDefaultState(), 20);
        }
        if (!(lv2 = new PistonHandler(arg, arg2, arg3, bl)).calculatePush()) {
            return false;
        }
        HashMap map = Maps.newHashMap();
        List<BlockPos> list = lv2.getMovedBlocks();
        ArrayList list2 = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            BlockPos lv3 = list.get(i);
            BlockState lv4 = arg.getBlockState(lv3);
            list2.add(lv4);
            map.put(lv3, lv4);
        }
        List<BlockPos> list3 = lv2.getBrokenBlocks();
        int j = list.size() + list3.size();
        BlockState[] lvs = new BlockState[j];
        Direction lv5 = bl ? arg3 : arg3.getOpposite();
        for (int k = list3.size() - 1; k >= 0; --k) {
            BlockPos lv6 = list3.get(k);
            BlockState blockState = arg.getBlockState(lv6);
            BlockEntity lv8 = blockState.getBlock().hasBlockEntity() ? arg.getBlockEntity(lv6) : null;
            PistonBlock.dropStacks(blockState, arg, lv6, lv8);
            arg.setBlockState(lv6, Blocks.AIR.getDefaultState(), 18);
            lvs[--j] = blockState;
        }
        for (int l = list.size() - 1; l >= 0; --l) {
            BlockPos lv9 = list.get(l);
            BlockState blockState = arg.getBlockState(lv9);
            lv9 = lv9.offset(lv5);
            map.remove(lv9);
            arg.setBlockState(lv9, (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, arg3), 68);
            arg.setBlockEntity(lv9, PistonExtensionBlock.createBlockEntityPiston((BlockState)list2.get(l), arg3, bl, false));
            lvs[--j] = blockState;
        }
        if (bl) {
            PistonType lv11 = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState lv12 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, arg3)).with(PistonHeadBlock.TYPE, lv11);
            BlockState blockState = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, arg3)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(lv);
            arg.setBlockState(lv, blockState, 68);
            arg.setBlockEntity(lv, PistonExtensionBlock.createBlockEntityPiston(lv12, arg3, true, true));
        }
        BlockState lv14 = Blocks.AIR.getDefaultState();
        for (BlockPos blockPos : map.keySet()) {
            arg.setBlockState(blockPos, lv14, 82);
        }
        for (Map.Entry entry : map.entrySet()) {
            BlockPos lv16 = (BlockPos)entry.getKey();
            BlockState lv17 = (BlockState)entry.getValue();
            lv17.prepare(arg, lv16, 2);
            lv14.updateNeighbors(arg, lv16, 2);
            lv14.prepare(arg, lv16, 2);
        }
        for (int m = list3.size() - 1; m >= 0; --m) {
            BlockState blockState = lvs[j++];
            BlockPos lv19 = list3.get(m);
            blockState.prepare(arg, lv19, 2);
            arg.updateNeighborsAlways(lv19, blockState.getBlock());
        }
        for (int n = list.size() - 1; n >= 0; --n) {
            arg.updateNeighborsAlways(list.get(n), lvs[j++].getBlock());
        }
        if (bl) {
            arg.updateNeighborsAlways(lv, Blocks.PISTON_HEAD);
        }
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
        arg.add(FACING, EXTENDED);
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return arg.get(EXTENDED);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

