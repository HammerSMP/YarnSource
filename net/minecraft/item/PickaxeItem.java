/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;

public class PickaxeItem
extends MiningToolItem {
    private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of((Object)Blocks.ACTIVATOR_RAIL, (Object)Blocks.COAL_ORE, (Object)Blocks.COBBLESTONE, (Object)Blocks.DETECTOR_RAIL, (Object)Blocks.DIAMOND_BLOCK, (Object)Blocks.DIAMOND_ORE, (Object[])new Block[]{Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD});

    protected PickaxeItem(ToolMaterial arg, int i, float f, Item.Settings arg2) {
        super(i, f, arg, EFFECTIVE_BLOCKS, arg2);
    }

    @Override
    public boolean isEffectiveOn(BlockState arg) {
        int i = this.getMaterial().getMiningLevel();
        if (arg.isOf(Blocks.OBSIDIAN) || arg.isOf(Blocks.CRYING_OBSIDIAN) || arg.isOf(Blocks.NETHERITE_BLOCK) || arg.isOf(Blocks.RESPAWN_ANCHOR) || arg.isOf(Blocks.ANCIENT_DEBRIS)) {
            return i >= 3;
        }
        if (arg.isOf(Blocks.DIAMOND_BLOCK) || arg.isOf(Blocks.DIAMOND_ORE) || arg.isOf(Blocks.EMERALD_ORE) || arg.isOf(Blocks.EMERALD_BLOCK) || arg.isOf(Blocks.GOLD_BLOCK) || arg.isOf(Blocks.GOLD_ORE) || arg.isOf(Blocks.REDSTONE_ORE)) {
            return i >= 2;
        }
        if (arg.isOf(Blocks.IRON_BLOCK) || arg.isOf(Blocks.IRON_ORE) || arg.isOf(Blocks.LAPIS_BLOCK) || arg.isOf(Blocks.LAPIS_ORE)) {
            return i >= 1;
        }
        Material lv = arg.getMaterial();
        return lv == Material.STONE || lv == Material.METAL || lv == Material.REPAIR_STATION || arg.isOf(Blocks.NETHER_GOLD_ORE);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack arg, BlockState arg2) {
        Material lv = arg2.getMaterial();
        if (lv == Material.METAL || lv == Material.REPAIR_STATION || lv == Material.STONE) {
            return this.miningSpeed;
        }
        return super.getMiningSpeedMultiplier(arg, arg2);
    }
}

