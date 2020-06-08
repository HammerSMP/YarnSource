/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem
extends MiningToolItem {
    private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of((Object)Blocks.NETHER_WART_BLOCK, (Object)Blocks.WARPED_WART_BLOCK, (Object)Blocks.HAY_BLOCK, (Object)Blocks.DRIED_KELP_BLOCK, (Object)Blocks.TARGET, (Object)Blocks.SHROOMLIGHT, (Object[])new Block[]{Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES});
    protected static final Map<Block, BlockState> TILLED_BLOCKS = Maps.newHashMap((Map)ImmutableMap.of((Object)Blocks.GRASS_BLOCK, (Object)Blocks.FARMLAND.getDefaultState(), (Object)Blocks.GRASS_PATH, (Object)Blocks.FARMLAND.getDefaultState(), (Object)Blocks.DIRT, (Object)Blocks.FARMLAND.getDefaultState(), (Object)Blocks.COARSE_DIRT, (Object)Blocks.DIRT.getDefaultState()));

    protected HoeItem(ToolMaterial arg, int i, float f, Item.Settings arg2) {
        super(i, f, arg, EFFECTIVE_BLOCKS, arg2);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockState lv3;
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        if (arg.getSide() != Direction.DOWN && lv.getBlockState(lv2.up()).isAir() && (lv3 = TILLED_BLOCKS.get(lv.getBlockState(lv2).getBlock())) != null) {
            PlayerEntity lv4 = arg.getPlayer();
            lv.playSound(lv4, lv2, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!lv.isClient) {
                lv.setBlockState(lv2, lv3, 11);
                if (lv4 != null) {
                    arg.getStack().damage(1, lv4, arg2 -> arg2.sendToolBreakStatus(arg.getHand()));
                }
            }
            return ActionResult.success(lv.isClient);
        }
        return ActionResult.PASS;
    }
}

