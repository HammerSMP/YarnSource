/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger field_25622 = LogManager.getLogger();
    private final RecipeManager manager;
    private final Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = Maps.newHashMap();
    private final List<RecipeResultCollection> orderedResults = Lists.newArrayList();

    public ClientRecipeBook(RecipeManager arg) {
        this.manager = arg;
    }

    public void reload() {
        this.orderedResults.clear();
        this.resultsByGroup.clear();
        HashBasedTable table = HashBasedTable.create();
        for (Recipe<?> lv : this.manager.values()) {
            RecipeResultCollection lv4;
            if (lv.isIgnoredInRecipeBook()) continue;
            RecipeBookGroup lv2 = ClientRecipeBook.getGroupForRecipe(lv);
            String string = lv.getGroup();
            if (string.isEmpty()) {
                RecipeResultCollection lv3 = this.addGroup(lv2);
            } else {
                lv4 = (RecipeResultCollection)table.get((Object)lv2, (Object)string);
                if (lv4 == null) {
                    lv4 = this.addGroup(lv2);
                    table.put((Object)lv2, (Object)string, (Object)lv4);
                }
            }
            lv4.addRecipe(lv);
        }
    }

    private RecipeResultCollection addGroup(RecipeBookGroup arg2) {
        RecipeResultCollection lv = new RecipeResultCollection();
        this.orderedResults.add(lv);
        this.resultsByGroup.computeIfAbsent(arg2, arg -> Lists.newArrayList()).add(lv);
        if (arg2 == RecipeBookGroup.FURNACE_BLOCKS || arg2 == RecipeBookGroup.FURNACE_FOOD || arg2 == RecipeBookGroup.FURNACE_MISC) {
            this.addGroupResults(RecipeBookGroup.FURNACE_SEARCH, lv);
        } else if (arg2 == RecipeBookGroup.BLAST_FURNACE_BLOCKS || arg2 == RecipeBookGroup.BLAST_FURNACE_MISC) {
            this.addGroupResults(RecipeBookGroup.BLAST_FURNACE_SEARCH, lv);
        } else if (arg2 == RecipeBookGroup.SMOKER_FOOD) {
            this.addGroupResults(RecipeBookGroup.SMOKER_SEARCH, lv);
        } else if (arg2 == RecipeBookGroup.CRAFTING_BUILDING_BLOCKS || arg2 == RecipeBookGroup.CRAFTING_REDSTONE || arg2 == RecipeBookGroup.CRAFTING_EQUIPMENT || arg2 == RecipeBookGroup.CRAFTING_MISC) {
            this.addGroupResults(RecipeBookGroup.SEARCH, lv);
        }
        return lv;
    }

    private void addGroupResults(RecipeBookGroup arg2, RecipeResultCollection arg22) {
        this.resultsByGroup.computeIfAbsent(arg2, arg -> Lists.newArrayList()).add(arg22);
    }

    private static RecipeBookGroup getGroupForRecipe(Recipe<?> arg) {
        RecipeType<?> lv = arg.getType();
        if (lv == RecipeType.CRAFTING) {
            ItemStack lv2 = arg.getOutput();
            ItemGroup lv3 = lv2.getItem().getGroup();
            if (lv3 == ItemGroup.BUILDING_BLOCKS) {
                return RecipeBookGroup.CRAFTING_BUILDING_BLOCKS;
            }
            if (lv3 == ItemGroup.TOOLS || lv3 == ItemGroup.COMBAT) {
                return RecipeBookGroup.CRAFTING_EQUIPMENT;
            }
            if (lv3 == ItemGroup.REDSTONE) {
                return RecipeBookGroup.CRAFTING_REDSTONE;
            }
            return RecipeBookGroup.CRAFTING_MISC;
        }
        if (lv == RecipeType.SMELTING) {
            if (arg.getOutput().getItem().isFood()) {
                return RecipeBookGroup.FURNACE_FOOD;
            }
            if (arg.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.FURNACE_BLOCKS;
            }
            return RecipeBookGroup.FURNACE_MISC;
        }
        if (lv == RecipeType.BLASTING) {
            if (arg.getOutput().getItem() instanceof BlockItem) {
                return RecipeBookGroup.BLAST_FURNACE_BLOCKS;
            }
            return RecipeBookGroup.BLAST_FURNACE_MISC;
        }
        if (lv == RecipeType.SMOKING) {
            return RecipeBookGroup.SMOKER_FOOD;
        }
        if (lv == RecipeType.STONECUTTING) {
            return RecipeBookGroup.STONECUTTER;
        }
        if (lv == RecipeType.CAMPFIRE_COOKING) {
            return RecipeBookGroup.CAMPFIRE;
        }
        if (lv == RecipeType.SMITHING) {
            return RecipeBookGroup.SMITHING;
        }
        Supplier[] arrsupplier = new Supplier[2];
        arrsupplier[0] = () -> Registry.RECIPE_TYPE.getId(arg.getType());
        arrsupplier[1] = arg::getId;
        field_25622.warn("Unknown recipe category: {}/{}", arrsupplier);
        return RecipeBookGroup.UNKNOWN;
    }

    public static List<RecipeBookGroup> getGroups(AbstractRecipeScreenHandler<?> arg) {
        if (arg instanceof CraftingScreenHandler || arg instanceof PlayerScreenHandler) {
            return Lists.newArrayList((Object[])new RecipeBookGroup[]{RecipeBookGroup.SEARCH, RecipeBookGroup.CRAFTING_EQUIPMENT, RecipeBookGroup.CRAFTING_BUILDING_BLOCKS, RecipeBookGroup.CRAFTING_MISC, RecipeBookGroup.CRAFTING_REDSTONE});
        }
        if (arg instanceof FurnaceScreenHandler) {
            return Lists.newArrayList((Object[])new RecipeBookGroup[]{RecipeBookGroup.FURNACE_SEARCH, RecipeBookGroup.FURNACE_FOOD, RecipeBookGroup.FURNACE_BLOCKS, RecipeBookGroup.FURNACE_MISC});
        }
        if (arg instanceof BlastFurnaceScreenHandler) {
            return Lists.newArrayList((Object[])new RecipeBookGroup[]{RecipeBookGroup.BLAST_FURNACE_SEARCH, RecipeBookGroup.BLAST_FURNACE_BLOCKS, RecipeBookGroup.BLAST_FURNACE_MISC});
        }
        if (arg instanceof SmokerScreenHandler) {
            return Lists.newArrayList((Object[])new RecipeBookGroup[]{RecipeBookGroup.SMOKER_SEARCH, RecipeBookGroup.SMOKER_FOOD});
        }
        return Lists.newArrayList();
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup arg) {
        return this.resultsByGroup.getOrDefault((Object)arg, Collections.emptyList());
    }
}

