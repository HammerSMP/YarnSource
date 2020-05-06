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
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.util.Identifier;

public class RecipeBook {
    protected final Set<Identifier> recipes = Sets.newHashSet();
    protected final Set<Identifier> toBeDisplayed = Sets.newHashSet();
    protected boolean guiOpen;
    protected boolean filteringCraftable;
    protected boolean furnaceGuiOpen;
    protected boolean furnaceFilteringCraftable;
    protected boolean blastFurnaceGuiOpen;
    protected boolean blastFurnaceFilteringCraftable;
    protected boolean smokerGuiOpen;
    protected boolean smokerFilteringCraftable;

    public void copyFrom(RecipeBook arg) {
        this.recipes.clear();
        this.toBeDisplayed.clear();
        this.guiOpen = arg.guiOpen;
        this.filteringCraftable = arg.filteringCraftable;
        this.furnaceGuiOpen = arg.furnaceGuiOpen;
        this.furnaceFilteringCraftable = arg.furnaceFilteringCraftable;
        this.blastFurnaceGuiOpen = arg.blastFurnaceGuiOpen;
        this.blastFurnaceFilteringCraftable = arg.blastFurnaceFilteringCraftable;
        this.smokerGuiOpen = arg.smokerGuiOpen;
        this.smokerFilteringCraftable = arg.smokerFilteringCraftable;
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
    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public void setGuiOpen(boolean bl) {
        this.guiOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable(AbstractRecipeScreenHandler<?> arg) {
        if (arg instanceof FurnaceScreenHandler) {
            return this.furnaceFilteringCraftable;
        }
        if (arg instanceof BlastFurnaceScreenHandler) {
            return this.blastFurnaceFilteringCraftable;
        }
        if (arg instanceof SmokerScreenHandler) {
            return this.smokerFilteringCraftable;
        }
        return this.filteringCraftable;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    public void setFilteringCraftable(boolean bl) {
        this.filteringCraftable = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    public void setFurnaceGuiOpen(boolean bl) {
        this.furnaceGuiOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    public void setFurnaceFilteringCraftable(boolean bl) {
        this.furnaceFilteringCraftable = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }

    public void setBlastFurnaceGuiOpen(boolean bl) {
        this.blastFurnaceGuiOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }

    public void setBlastFurnaceFilteringCraftable(boolean bl) {
        this.blastFurnaceFilteringCraftable = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }

    public void setSmokerGuiOpen(boolean bl) {
        this.smokerGuiOpen = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isSmokerFilteringCraftable() {
        return this.smokerFilteringCraftable;
    }

    public void setSmokerFilteringCraftable(boolean bl) {
        this.smokerFilteringCraftable = bl;
    }
}

