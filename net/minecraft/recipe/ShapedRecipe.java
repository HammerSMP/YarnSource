/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ShapedRecipe
implements CraftingRecipe {
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> inputs;
    private final ItemStack output;
    private final Identifier id;
    private final String group;

    public ShapedRecipe(Identifier arg, String string, int i, int j, DefaultedList<Ingredient> arg2, ItemStack arg3) {
        this.id = arg;
        this.group = string;
        this.width = i;
        this.height = j;
        this.inputs = arg2;
        this.output = arg3;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED;
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
        return this.inputs;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i >= this.width && j >= this.height;
    }

    @Override
    public boolean matches(CraftingInventory arg, World arg2) {
        for (int i = 0; i <= arg.getWidth() - this.width; ++i) {
            for (int j = 0; j <= arg.getHeight() - this.height; ++j) {
                if (this.matchesSmall(arg, i, j, true)) {
                    return true;
                }
                if (!this.matchesSmall(arg, i, j, false)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean matchesSmall(CraftingInventory arg, int i, int j, boolean bl) {
        for (int k = 0; k < arg.getWidth(); ++k) {
            for (int l = 0; l < arg.getHeight(); ++l) {
                int m = k - i;
                int n = l - j;
                Ingredient lv = Ingredient.EMPTY;
                if (m >= 0 && n >= 0 && m < this.width && n < this.height) {
                    lv = bl ? this.inputs.get(this.width - m - 1 + n * this.width) : this.inputs.get(m + n * this.width);
                }
                if (lv.test(arg.getStack(k + l * arg.getWidth()))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingInventory arg) {
        return this.getOutput().copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private static DefaultedList<Ingredient> getIngredients(String[] strings, Map<String, Ingredient> map, int i, int j) {
        DefaultedList<Ingredient> lv = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
        HashSet set = Sets.newHashSet(map.keySet());
        set.remove(" ");
        for (int k = 0; k < strings.length; ++k) {
            for (int l = 0; l < strings[k].length(); ++l) {
                String string = strings[k].substring(l, l + 1);
                Ingredient lv2 = map.get(string);
                if (lv2 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }
                set.remove(string);
                lv.set(l + i * k, lv2);
            }
        }
        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        return lv;
    }

    @VisibleForTesting
    static String[] combinePattern(String ... strings) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < strings.length; ++m) {
            String string = strings[m];
            i = Math.min(i, ShapedRecipe.findNextIngredient(string));
            int n = ShapedRecipe.findNextIngredientReverse(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }
                ++l;
                continue;
            }
            l = 0;
        }
        if (strings.length == l) {
            return new String[0];
        }
        String[] strings2 = new String[strings.length - l - k];
        for (int o = 0; o < strings2.length; ++o) {
            strings2[o] = strings[o + k].substring(i, j + 1);
        }
        return strings2;
    }

    private static int findNextIngredient(String string) {
        int i;
        for (i = 0; i < string.length() && string.charAt(i) == ' '; ++i) {
        }
        return i;
    }

    private static int findNextIngredientReverse(String string) {
        int i;
        for (i = string.length() - 1; i >= 0 && string.charAt(i) == ' '; --i) {
        }
        return i;
    }

    private static String[] getPattern(JsonArray jsonArray) {
        String[] strings = new String[jsonArray.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int i = 0; i < strings.length; ++i) {
            String string = JsonHelper.asString(jsonArray.get(i), "pattern[" + i + "]");
            if (string.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }
            if (i > 0 && strings[0].length() != string.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            strings[i] = string;
        }
        return strings;
    }

    private static Map<String, Ingredient> getComponents(JsonObject jsonObject) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : jsonObject.entrySet()) {
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), Ingredient.fromJson((JsonElement)entry.getValue()));
        }
        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack getItemStack(JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "item");
        Item lv = Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        int i = JsonHelper.getInt(jsonObject, "count", 1);
        return new ItemStack(lv, i);
    }

    public static class Serializer
    implements RecipeSerializer<ShapedRecipe> {
        @Override
        public ShapedRecipe read(Identifier arg, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            Map map = ShapedRecipe.getComponents(JsonHelper.getObject(jsonObject, "key"));
            String[] strings = ShapedRecipe.combinePattern(ShapedRecipe.getPattern(JsonHelper.getArray(jsonObject, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            DefaultedList lv = ShapedRecipe.getIngredients(strings, map, i, j);
            ItemStack lv2 = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new ShapedRecipe(arg, string, i, j, lv, lv2);
        }

        @Override
        public ShapedRecipe read(Identifier arg, PacketByteBuf arg2) {
            int i = arg2.readVarInt();
            int j = arg2.readVarInt();
            String string = arg2.readString(32767);
            DefaultedList<Ingredient> lv = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
            for (int k = 0; k < lv.size(); ++k) {
                lv.set(k, Ingredient.fromPacket(arg2));
            }
            ItemStack lv2 = arg2.readItemStack();
            return new ShapedRecipe(arg, string, i, j, lv, lv2);
        }

        @Override
        public void write(PacketByteBuf arg, ShapedRecipe arg2) {
            arg.writeVarInt(arg2.width);
            arg.writeVarInt(arg2.height);
            arg.writeString(arg2.group);
            for (Ingredient lv : arg2.inputs) {
                lv.write(arg);
            }
            arg.writeItemStack(arg2.output);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, PacketByteBuf arg2) {
            return this.read(arg, arg2);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, JsonObject jsonObject) {
            return this.read(arg, jsonObject);
        }
    }
}

