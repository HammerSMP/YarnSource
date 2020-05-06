/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public abstract class CuttingRecipe
implements Recipe<Inventory> {
    protected final Ingredient input;
    protected final ItemStack output;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final Identifier id;
    protected final String group;

    public CuttingRecipe(RecipeType<?> arg, RecipeSerializer<?> arg2, Identifier arg3, String string, Ingredient arg4, ItemStack arg5) {
        this.type = arg;
        this.serializer = arg2;
        this.id = arg3;
        this.group = string;
        this.input = arg4;
        this.output = arg5;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public Identifier getId() {
        return this.id;
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
        DefaultedList<Ingredient> lv = DefaultedList.of();
        lv.add(this.input);
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack craft(Inventory arg) {
        return this.output.copy();
    }

    public static class Serializer<T extends CuttingRecipe>
    implements RecipeSerializer<T> {
        final RecipeFactory<T> recipeFactory;

        protected Serializer(RecipeFactory<T> arg) {
            this.recipeFactory = arg;
        }

        @Override
        public T read(Identifier arg, JsonObject jsonObject) {
            Ingredient lv2;
            String string = JsonHelper.getString(jsonObject, "group", "");
            if (JsonHelper.hasArray(jsonObject, "ingredient")) {
                Ingredient lv = Ingredient.fromJson((JsonElement)JsonHelper.getArray(jsonObject, "ingredient"));
            } else {
                lv2 = Ingredient.fromJson((JsonElement)JsonHelper.getObject(jsonObject, "ingredient"));
            }
            String string2 = JsonHelper.getString(jsonObject, "result");
            int i = JsonHelper.getInt(jsonObject, "count");
            ItemStack lv3 = new ItemStack(Registry.ITEM.get(new Identifier(string2)), i);
            return this.recipeFactory.create(arg, string, lv2, lv3);
        }

        @Override
        public T read(Identifier arg, PacketByteBuf arg2) {
            String string = arg2.readString(32767);
            Ingredient lv = Ingredient.fromPacket(arg2);
            ItemStack lv2 = arg2.readItemStack();
            return this.recipeFactory.create(arg, string, lv, lv2);
        }

        @Override
        public void write(PacketByteBuf arg, T arg2) {
            arg.writeString(((CuttingRecipe)arg2).group);
            ((CuttingRecipe)arg2).input.write(arg);
            arg.writeItemStack(((CuttingRecipe)arg2).output);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, PacketByteBuf arg2) {
            return this.read(arg, arg2);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, JsonObject jsonObject) {
            return this.read(arg, jsonObject);
        }

        static interface RecipeFactory<T extends CuttingRecipe> {
            public T create(Identifier var1, String var2, Ingredient var3, ItemStack var4);
        }
    }
}

