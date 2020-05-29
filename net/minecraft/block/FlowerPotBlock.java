/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FlowerPotBlock
extends Block {
    private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.newHashMap();
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    private final Block content;

    public FlowerPotBlock(Block arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.content = arg;
        CONTENT_TO_POTTED.put(arg, this);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        boolean bl2;
        ItemStack lv = arg4.getStackInHand(arg5);
        Item lv2 = lv.getItem();
        Block lv3 = lv2 instanceof BlockItem ? CONTENT_TO_POTTED.getOrDefault(((BlockItem)lv2).getBlock(), Blocks.AIR) : Blocks.AIR;
        boolean bl = lv3 == Blocks.AIR;
        boolean bl3 = bl2 = this.content == Blocks.AIR;
        if (bl != bl2) {
            if (bl2) {
                arg2.setBlockState(arg3, lv3.getDefaultState(), 3);
                arg4.incrementStat(Stats.POT_FLOWER);
                if (!arg4.abilities.creativeMode) {
                    lv.decrement(1);
                }
            } else {
                ItemStack lv4 = new ItemStack(this.content);
                if (lv.isEmpty()) {
                    arg4.setStackInHand(arg5, lv4);
                } else if (!arg4.giveItemStack(lv4)) {
                    arg4.dropItem(lv4, false);
                }
                arg2.setBlockState(arg3, Blocks.FLOWER_POT.getDefaultState(), 3);
            }
            return ActionResult.method_29236(arg2.isClient);
        }
        return ActionResult.CONSUME;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        if (this.content == Blocks.AIR) {
            return super.getPickStack(arg, arg2, arg3);
        }
        return new ItemStack(this.content);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN && !arg.canPlaceAt(arg4, arg5)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    public Block getContent() {
        return this.content;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

