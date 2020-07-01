/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.class_5421;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(value=EnvType.CLIENT)
public enum RecipeBookGroup {
    CRAFTING_SEARCH(new ItemStack(Items.COMPASS)),
    CRAFTING_BUILDING_BLOCKS(new ItemStack(Blocks.BRICKS)),
    CRAFTING_REDSTONE(new ItemStack(Items.REDSTONE)),
    CRAFTING_EQUIPMENT(new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)),
    CRAFTING_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)),
    FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
    FURNACE_FOOD(new ItemStack(Items.PORKCHOP)),
    FURNACE_BLOCKS(new ItemStack(Blocks.STONE)),
    FURNACE_MISC(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD)),
    BLAST_FURNACE_SEARCH(new ItemStack(Items.COMPASS)),
    BLAST_FURNACE_BLOCKS(new ItemStack(Blocks.REDSTONE_ORE)),
    BLAST_FURNACE_MISC(new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS)),
    SMOKER_SEARCH(new ItemStack(Items.COMPASS)),
    SMOKER_FOOD(new ItemStack(Items.PORKCHOP)),
    STONECUTTER(new ItemStack(Items.CHISELED_STONE_BRICKS)),
    SMITHING(new ItemStack(Items.NETHERITE_CHESTPLATE)),
    CAMPFIRE(new ItemStack(Items.PORKCHOP)),
    UNKNOWN(new ItemStack(Items.BARRIER));

    public static final List<RecipeBookGroup> field_25779;
    public static final List<RecipeBookGroup> field_25780;
    public static final List<RecipeBookGroup> field_25781;
    public static final List<RecipeBookGroup> field_25782;
    public static final Map<RecipeBookGroup, List<RecipeBookGroup>> field_25783;
    private final List<ItemStack> icons;

    private RecipeBookGroup(ItemStack ... args) {
        this.icons = ImmutableList.copyOf((Object[])args);
    }

    public static List<RecipeBookGroup> method_30285(class_5421 arg) {
        switch (arg) {
            case CRAFTING: {
                return field_25782;
            }
            case FURNACE: {
                return field_25781;
            }
            case BLAST_FURNACE: {
                return field_25780;
            }
            case SMOKER: {
                return field_25779;
            }
        }
        return ImmutableList.of();
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }

    static {
        field_25779 = ImmutableList.of((Object)((Object)SMOKER_SEARCH), (Object)((Object)SMOKER_FOOD));
        field_25780 = ImmutableList.of((Object)((Object)BLAST_FURNACE_SEARCH), (Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC));
        field_25781 = ImmutableList.of((Object)((Object)FURNACE_SEARCH), (Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC));
        field_25782 = ImmutableList.of((Object)((Object)CRAFTING_SEARCH), (Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE));
        field_25783 = ImmutableMap.of((Object)((Object)CRAFTING_SEARCH), (Object)ImmutableList.of((Object)((Object)CRAFTING_EQUIPMENT), (Object)((Object)CRAFTING_BUILDING_BLOCKS), (Object)((Object)CRAFTING_MISC), (Object)((Object)CRAFTING_REDSTONE)), (Object)((Object)FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)FURNACE_FOOD), (Object)((Object)FURNACE_BLOCKS), (Object)((Object)FURNACE_MISC)), (Object)((Object)BLAST_FURNACE_SEARCH), (Object)ImmutableList.of((Object)((Object)BLAST_FURNACE_BLOCKS), (Object)((Object)BLAST_FURNACE_MISC)), (Object)((Object)SMOKER_SEARCH), (Object)ImmutableList.of((Object)((Object)SMOKER_FOOD)));
    }
}

