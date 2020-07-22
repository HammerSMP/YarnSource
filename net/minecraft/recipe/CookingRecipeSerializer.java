/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe>
implements RecipeSerializer<T> {
    private final int cookingTime;
    private final RecipeFactory<T> recipeFactory;

    public CookingRecipeSerializer(RecipeFactory<T> recipeFactory, int cookingTime) {
        this.cookingTime = cookingTime;
        this.recipeFactory = recipeFactory;
    }

    @Override
    public T read(Identifier arg, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonArray jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient lv = Ingredient.fromJson((JsonElement)jsonElement);
        String string2 = JsonHelper.getString(jsonObject, "result");
        Identifier lv2 = new Identifier(string2);
        ItemStack lv3 = new ItemStack(Registry.ITEM.getOrEmpty(lv2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        float f = JsonHelper.getFloat(jsonObject, "experience", 0.0f);
        int i = JsonHelper.getInt(jsonObject, "cookingtime", this.cookingTime);
        return this.recipeFactory.create(arg, string, lv, lv3, f, i);
    }

    @Override
    public T read(Identifier arg, PacketByteBuf arg2) {
        String string = arg2.readString(32767);
        Ingredient lv = Ingredient.fromPacket(arg2);
        ItemStack lv2 = arg2.readItemStack();
        float f = arg2.readFloat();
        int i = arg2.readVarInt();
        return this.recipeFactory.create(arg, string, lv, lv2, f, i);
    }

    @Override
    public void write(PacketByteBuf arg, T arg2) {
        arg.writeString(((AbstractCookingRecipe)arg2).group);
        ((AbstractCookingRecipe)arg2).input.write(arg);
        arg.writeItemStack(((AbstractCookingRecipe)arg2).output);
        arg.writeFloat(((AbstractCookingRecipe)arg2).experience);
        arg.writeVarInt(((AbstractCookingRecipe)arg2).cookTime);
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
        return this.read(id, buf);
    }

    @Override
    public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
        return this.read(id, json);
    }

    static interface RecipeFactory<T extends AbstractCookingRecipe> {
        public T create(Identifier var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
    }
}

