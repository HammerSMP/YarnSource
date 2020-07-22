/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipe
implements CraftingRecipe {
    private final Identifier id;
    private final String group;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;

    public ShapelessRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
        this.id = id;
        this.group = group;
        this.output = output;
        this.input = input;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return this.input;
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        RecipeFinder lv = new RecipeFinder();
        int i = 0;
        for (int j = 0; j < arg.size(); ++j) {
            ItemStack lv2 = arg.getStack(j);
            if (lv2.isEmpty()) continue;
            ++i;
            lv.method_20478(lv2, 1);
        }
        return i == this.input.size() && lv.findRecipe(this, null);
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        return this.output.copy();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= this.input.size();
    }

    public static class Serializer
    implements RecipeSerializer<ShapelessRecipe> {
        @Override
        public ShapelessRecipe read(Identifier arg, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            DefaultedList<Ingredient> lv = Serializer.getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
            if (lv.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            }
            if (lv.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            }
            ItemStack lv2 = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new ShapelessRecipe(arg, string, lv2, lv);
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> lv = DefaultedList.of();
            for (int i = 0; i < json.size(); ++i) {
                Ingredient lv2 = Ingredient.fromJson(json.get(i));
                if (lv2.isEmpty()) continue;
                lv.add(lv2);
            }
            return lv;
        }

        @Override
        public ShapelessRecipe read(Identifier arg, PacketByteBuf arg2) {
            String string = arg2.readString(32767);
            int i = arg2.readVarInt();
            DefaultedList<Ingredient> lv = DefaultedList.ofSize(i, Ingredient.EMPTY);
            for (int j = 0; j < lv.size(); ++j) {
                lv.set(j, Ingredient.fromPacket(arg2));
            }
            ItemStack lv2 = arg2.readItemStack();
            return new ShapelessRecipe(arg, string, lv2, lv);
        }

        @Override
        public void write(PacketByteBuf arg, ShapelessRecipe arg2) {
            arg.writeString(arg2.group);
            arg.writeVarInt(arg2.input.size());
            for (Ingredient lv : arg2.input) {
                lv.write(arg);
            }
            arg.writeItemStack(arg2.output);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
            return this.read(id, buf);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
            return this.read(id, json);
        }
    }
}

