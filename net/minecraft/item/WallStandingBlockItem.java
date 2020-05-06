/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WallStandingBlockItem
extends BlockItem {
    protected final Block wallBlock;

    public WallStandingBlockItem(Block arg, Block arg2, Item.Settings arg3) {
        super(arg, arg3);
        this.wallBlock = arg2;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext arg) {
        BlockState lv = this.wallBlock.getPlacementState(arg);
        BlockState lv2 = null;
        World lv3 = arg.getWorld();
        BlockPos lv4 = arg.getBlockPos();
        for (Direction lv5 : arg.getPlacementDirections()) {
            BlockState lv6;
            if (lv5 == Direction.UP) continue;
            BlockState blockState = lv6 = lv5 == Direction.DOWN ? this.getBlock().getPlacementState(arg) : lv;
            if (lv6 == null || !lv6.canPlaceAt(lv3, lv4)) continue;
            lv2 = lv6;
            break;
        }
        return lv2 != null && lv3.canPlace(lv2, lv4, ShapeContext.absent()) ? lv2 : null;
    }

    @Override
    public void appendBlocks(Map<Block, Item> map, Item arg) {
        super.appendBlocks(map, arg);
        map.put(this.wallBlock, arg);
    }
}

