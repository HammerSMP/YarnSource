/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    private static final Logger field_25622 = LogManager.getLogger();
    private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup = ImmutableMap.of();
    private List<RecipeResultCollection> field_25778 = ImmutableList.of();

    public void reload(Iterable<Recipe<?>> iterable) {
        Map<RecipeBookGroup, List<List<Recipe<?>>>> map = ClientRecipeBook.method_30283(iterable);
        HashMap map2 = Maps.newHashMap();
        ImmutableList.Builder builder = ImmutableList.builder();
        map.forEach((arg, list) -> {
            List cfr_ignored_0 = (List)map2.put(arg, list.stream().map(RecipeResultCollection::new).peek(((ImmutableList.Builder)builder)::add).collect(ImmutableList.toImmutableList()));
        });
        RecipeBookGroup.field_25783.forEach((arg2, list) -> {
            List cfr_ignored_0 = (List)map2.put(arg2, list.stream().flatMap(arg -> ((List)map2.getOrDefault(arg, ImmutableList.of())).stream()).collect(ImmutableList.toImmutableList()));
        });
        this.resultsByGroup = ImmutableMap.copyOf((Map)map2);
        this.field_25778 = builder.build();
    }

    private static Map<RecipeBookGroup, List<List<Recipe<?>>>> method_30283(Iterable<Recipe<?>> iterable) {
        HashMap map = Maps.newHashMap();
        HashBasedTable table = HashBasedTable.create();
        for (Recipe<?> lv : iterable) {
            if (lv.isIgnoredInRecipeBook()) continue;
            RecipeBookGroup lv2 = ClientRecipeBook.getGroupForRecipe(lv);
            String string = lv.getGroup();
            if (string.isEmpty()) {
                map.computeIfAbsent(lv2, arg -> Lists.newArrayList()).add(ImmutableList.of(lv));
                continue;
            }
            List list = (List)table.get((Object)lv2, (Object)string);
            if (list == null) {
                list = Lists.newArrayList();
                table.put((Object)lv2, (Object)string, (Object)list);
                map.computeIfAbsent(lv2, arg -> Lists.newArrayList()).add(list);
            }
            list.add(lv);
        }
        return map;
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

    public List<RecipeResultCollection> getOrderedResults() {
        return this.field_25778;
    }

    public List<RecipeResultCollection> getResultsForGroup(RecipeBookGroup category) {
        return this.resultsByGroup.getOrDefault((Object)category, Collections.emptyList());
    }
}

