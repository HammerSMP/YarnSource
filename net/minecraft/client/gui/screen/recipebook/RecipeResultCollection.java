/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBook;

@Environment(value=EnvType.CLIENT)
public class RecipeResultCollection {
    private final List<Recipe<?>> recipes;
    private final boolean singleOutput;
    private final Set<Recipe<?>> craftableRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> fittingRecipes = Sets.newHashSet();
    private final Set<Recipe<?>> unlockedRecipes = Sets.newHashSet();

    public RecipeResultCollection(List<Recipe<?>> list) {
        this.recipes = ImmutableList.copyOf(list);
        this.singleOutput = list.size() <= 1 ? true : RecipeResultCollection.method_30295(list);
    }

    private static boolean method_30295(List<Recipe<?>> list) {
        int i = list.size();
        ItemStack lv = list.get(0).getOutput();
        for (int j = 1; j < i; ++j) {
            ItemStack lv2 = list.get(j).getOutput();
            if (ItemStack.areItemsEqualIgnoreDamage(lv, lv2) && ItemStack.areTagsEqual(lv, lv2)) continue;
            return false;
        }
        return true;
    }

    public boolean isInitialized() {
        return !this.unlockedRecipes.isEmpty();
    }

    public void initialize(RecipeBook arg) {
        for (Recipe<?> lv : this.recipes) {
            if (!arg.contains(lv)) continue;
            this.unlockedRecipes.add(lv);
        }
    }

    public void computeCraftables(RecipeFinder arg, int i, int j, RecipeBook arg2) {
        for (Recipe<?> lv : this.recipes) {
            boolean bl;
            boolean bl2 = bl = lv.fits(i, j) && arg2.contains(lv);
            if (bl) {
                this.fittingRecipes.add(lv);
            } else {
                this.fittingRecipes.remove(lv);
            }
            if (bl && arg.findRecipe(lv, null)) {
                this.craftableRecipes.add(lv);
                continue;
            }
            this.craftableRecipes.remove(lv);
        }
    }

    public boolean isCraftable(Recipe<?> arg) {
        return this.craftableRecipes.contains(arg);
    }

    public boolean hasCraftableRecipes() {
        return !this.craftableRecipes.isEmpty();
    }

    public boolean hasFittingRecipes() {
        return !this.fittingRecipes.isEmpty();
    }

    public List<Recipe<?>> getAllRecipes() {
        return this.recipes;
    }

    public List<Recipe<?>> getResults(boolean bl) {
        ArrayList list = Lists.newArrayList();
        Set<Recipe<?>> set = bl ? this.craftableRecipes : this.fittingRecipes;
        for (Recipe<?> lv : this.recipes) {
            if (!set.contains(lv)) continue;
            list.add(lv);
        }
        return list;
    }

    public List<Recipe<?>> getRecipes(boolean bl) {
        ArrayList list = Lists.newArrayList();
        for (Recipe<?> lv : this.recipes) {
            if (!this.fittingRecipes.contains(lv) || this.craftableRecipes.contains(lv) != bl) continue;
            list.add(lv);
        }
        return list;
    }

    public boolean hasSingleOutput() {
        return this.singleOutput;
    }
}

