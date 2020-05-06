/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class AbstractCookingRecipe
implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final float experience;
    protected final int cookTime;

    public AbstractCookingRecipe(RecipeType<?> arg, Identifier arg2, String string, Ingredient arg3, ItemStack arg4, float f, int i) {
        this.type = arg;
        this.id = arg2;
        this.group = string;
        this.input = arg3;
        this.output = arg4;
        this.experience = f;
        this.cookTime = i;
    }

    @Override
    public boolean matches(Inventory arg, World arg2) {
        return this.input.test(arg.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory arg) {
        return this.output.copy();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> lv = DefaultedList.of();
        lv.add(this.input);
        return lv;
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}

