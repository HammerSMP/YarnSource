/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
    private final World world;
    private final BlockPos posFrom;
    private final boolean retracted;
    private final BlockPos posTo;
    private final Direction motionDirection;
    private final List<BlockPos> movedBlocks = Lists.newArrayList();
    private final List<BlockPos> brokenBlocks = Lists.newArrayList();
    private final Direction pistonDirection;

    public PistonHandler(World arg, BlockPos arg2, Direction arg3, boolean bl) {
        this.world = arg;
        this.posFrom = arg2;
        this.pistonDirection = arg3;
        this.retracted = bl;
        if (bl) {
            this.motionDirection = arg3;
            this.posTo = arg2.offset(arg3);
        } else {
            this.motionDirection = arg3.getOpposite();
            this.posTo = arg2.offset(arg3, 2);
        }
    }

    public boolean calculatePush() {
        this.movedBlocks.clear();
        this.brokenBlocks.clear();
        BlockState lv = this.world.getBlockState(this.posTo);
        if (!PistonBlock.isMovable(lv, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
            if (this.retracted && lv.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(this.posTo);
                return true;
            }
            return false;
        }
        if (!this.tryMove(this.posTo, this.motionDirection)) {
            return false;
        }
        for (int i = 0; i < this.movedBlocks.size(); ++i) {
            BlockPos lv2 = this.movedBlocks.get(i);
            if (!PistonHandler.isBlockSticky(this.world.getBlockState(lv2).getBlock()) || this.canMoveAdjacentBlock(lv2)) continue;
            return false;
        }
        return true;
    }

    private static boolean isBlockSticky(Block arg) {
        return arg == Blocks.SLIME_BLOCK || arg == Blocks.HONEY_BLOCK;
    }

    private static boolean isAdjacentBlockStuck(Block arg, Block arg2) {
        if (arg == Blocks.HONEY_BLOCK && arg2 == Blocks.SLIME_BLOCK) {
            return false;
        }
        if (arg == Blocks.SLIME_BLOCK && arg2 == Blocks.HONEY_BLOCK) {
            return false;
        }
        return PistonHandler.isBlockSticky(arg) || PistonHandler.isBlockSticky(arg2);
    }

    private boolean tryMove(BlockPos arg, Direction arg2) {
        BlockState lv = this.world.getBlockState(arg);
        Block lv2 = lv.getBlock();
        if (lv.isAir()) {
            return true;
        }
        if (!PistonBlock.isMovable(lv, this.world, arg, this.motionDirection, false, arg2)) {
            return true;
        }
        if (arg.equals(this.posFrom)) {
            return true;
        }
        if (this.movedBlocks.contains(arg)) {
            return true;
        }
        int i = 1;
        if (i + this.movedBlocks.size() > 12) {
            return false;
        }
        while (PistonHandler.isBlockSticky(lv2)) {
            BlockPos lv3 = arg.offset(this.motionDirection.getOpposite(), i);
            Block lv4 = lv2;
            lv = this.world.getBlockState(lv3);
            lv2 = lv.getBlock();
            if (lv.isAir() || !PistonHandler.isAdjacentBlockStuck(lv4, lv2) || !PistonBlock.isMovable(lv, this.world, lv3, this.motionDirection, false, this.motionDirection.getOpposite()) || lv3.equals(this.posFrom)) break;
            if (++i + this.movedBlocks.size() <= 12) continue;
            return false;
        }
        int j = 0;
        for (int k = i - 1; k >= 0; --k) {
            this.movedBlocks.add(arg.offset(this.motionDirection.getOpposite(), k));
            ++j;
        }
        int l = 1;
        do {
            BlockPos lv5;
            int m;
            if ((m = this.movedBlocks.indexOf(lv5 = arg.offset(this.motionDirection, l))) > -1) {
                this.setMovedBlocks(j, m);
                for (int n = 0; n <= m + j; ++n) {
                    BlockPos lv6 = this.movedBlocks.get(n);
                    if (!PistonHandler.isBlockSticky(this.world.getBlockState(lv6).getBlock()) || this.canMoveAdjacentBlock(lv6)) continue;
                    return false;
                }
                return true;
            }
            lv = this.world.getBlockState(lv5);
            if (lv.isAir()) {
                return true;
            }
            if (!PistonBlock.isMovable(lv, this.world, lv5, this.motionDirection, true, this.motionDirection) || lv5.equals(this.posFrom)) {
                return false;
            }
            if (lv.getPistonBehavior() == PistonBehavior.DESTROY) {
                this.brokenBlocks.add(lv5);
                return true;
            }
            if (this.movedBlocks.size() >= 12) {
                return false;
            }
            this.movedBlocks.add(lv5);
            ++j;
            ++l;
        } while (true);
    }

    private void setMovedBlocks(int i, int j) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        list.addAll(this.movedBlocks.subList(0, j));
        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - i, this.movedBlocks.size()));
        list3.addAll(this.movedBlocks.subList(j, this.movedBlocks.size() - i));
        this.movedBlocks.clear();
        this.movedBlocks.addAll(list);
        this.movedBlocks.addAll(list2);
        this.movedBlocks.addAll(list3);
    }

    private boolean canMoveAdjacentBlock(BlockPos arg) {
        BlockState lv = this.world.getBlockState(arg);
        for (Direction lv2 : Direction.values()) {
            BlockPos lv3;
            BlockState lv4;
            if (lv2.getAxis() == this.motionDirection.getAxis() || !PistonHandler.isAdjacentBlockStuck((lv4 = this.world.getBlockState(lv3 = arg.offset(lv2))).getBlock(), lv.getBlock()) || this.tryMove(lv3, lv2)) continue;
            return false;
        }
        return true;
    }

    public List<BlockPos> getMovedBlocks() {
        return this.movedBlocks;
    }

    public List<BlockPos> getBrokenBlocks() {
        return this.brokenBlocks;
    }
}

