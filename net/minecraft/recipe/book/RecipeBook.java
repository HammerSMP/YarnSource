/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe.book;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.util.Identifier;

public class RecipeBook {
    protected final Set<Identifier> recipes = Sets.newHashSet();
    protected final Set<Identifier> toBeDisplayed = Sets.newHashSet();
    private final RecipeBookOptions options = new RecipeBookOptions();

    public void copyFrom(RecipeBook arg) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.options.copyFrom(arg.options);
        this.recipes.addAll(arg.recipes);
        this.toBeDisplayed.addAll(arg.toBeDisplayed);
    }

    public void add(Recipe<?> arg) {
        if (!arg.isIgnoredInRecipeBook()) {
            this.add(arg.getId());
        }
    }

    protected void add(Identifier arg) {
        this.recipes.add(arg);
    }

    public boolean contains(@Nullable Recipe<?> arg) {
        if (arg == null) {
            return false;
        }
        return this.recipes.contains(arg.getId());
    }

    public boolean contains(Identifier arg) {
        return this.recipes.contains(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Recipe<?> arg) {
        this.remove(arg.getId());
    }

    protected void remove(Identifier arg) {
        this.recipes.remove(arg);
        this.toBeDisplayed.remove(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDisplay(Recipe<?> arg) {
        return this.toBeDisplayed.contains(arg.getId());
    }

    public void onRecipeDisplayed(Recipe<?> arg) {
        this.toBeDisplayed.remove(arg.getId());
    }

    public void display(Recipe<?> arg) {
        this.display(arg.getId());
    }

    protected void display(Identifier arg) {
        this.toBeDisplayed.add(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen(RecipeBookCategory arg) {
        return this.options.isGuiOpen(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void setGuiOpen(RecipeBookCategory arg, boolean bl) {
        this.options.setGuiOpen(arg, bl);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(AbstractRecipeScreenHandler<?> arg) {
        return this.isFilteringCraftable(arg.getCategory());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(RecipeBookCategory arg) {
        return this.options.isFilteringCraftable(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public void setFilteringCraftable(RecipeBookCategory arg, boolean bl) {
        this.options.setFilteringCraftable(arg, bl);
    }

    public void setOptions(RecipeBookOptions arg) {
        this.options.copyFrom(arg);
    }

    public RecipeBookOptions getOptions() {
        return this.options.copy();
    }

    public void setCategoryOptions(RecipeBookCategory arg, boolean bl, boolean bl2) {
        this.options.setGuiOpen(arg, bl);
        this.options.setFilteringCraftable(arg, bl2);
    }
}

