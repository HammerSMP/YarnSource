/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
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

public class ShovelItem
extends MiningToolItem {
    private static final Set<Block> EFFECTIVE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL});
    protected static final Map<Block, BlockState> PATH_BLOCKSTATES = Maps.newHashMap((Map)ImmutableMap.of((Object)Blocks.GRASS_BLOCK, (Object)Blocks.GRASS_PATH.getDefaultState()));

    public ShovelItem(ToolMaterial arg, float f, float g, Item.Settings arg2) {
        super(f, g, arg, EFFECTIVE_BLOCKS, arg2);
    }

    @Override
    public boolean isEffectiveOn(BlockState arg) {
        return arg.isOf(Blocks.SNOW) || arg.isOf(Blocks.SNOW_BLOCK);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        BlockState lv3 = lv.getBlockState(lv2);
        if (arg.getSide() != Direction.DOWN) {
            PlayerEntity lv4 = arg.getPlayer();
            BlockState lv5 = PATH_BLOCKSTATES.get(lv3.getBlock());
            BlockState lv6 = null;
            if (lv5 != null && lv.getBlockState(lv2.up()).isAir()) {
                lv.playSound(lv4, lv2, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                lv6 = lv5;
            } else if (lv3.getBlock() instanceof CampfireBlock && lv3.get(CampfireBlock.LIT).booleanValue()) {
                if (!lv.isClient()) {
                    lv.syncWorldEvent(null, 1009, lv2, 0);
                }
                CampfireBlock.extinguish(lv, lv2, lv3);
                lv6 = (BlockState)lv3.with(CampfireBlock.LIT, false);
            }
            if (lv6 != null) {
                if (!lv.isClient) {
                    lv.setBlockState(lv2, lv6, 11);
                    if (lv4 != null) {
                        arg.getStack().damage(1, lv4, arg2 -> arg2.sendToolBreakStatus(arg.getHand()));
                    }
                }
                return ActionResult.method_29236(lv.isClient);
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
}

