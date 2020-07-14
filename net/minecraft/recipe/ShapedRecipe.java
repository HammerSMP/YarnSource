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

    public ShapedRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.inputs = ingredients;
        this.output = output;
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
    public boolean fits(int width, int height) {
        return width >= this.width && height >= this.height;
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

    private boolean matchesSmall(CraftingInventory inv, int offsetX, int offsetY, boolean bl) {
        for (int k = 0; k < inv.getWidth(); ++k) {
            for (int l = 0; l < inv.getHeight(); ++l) {
                int m = k - offsetX;
                int n = l - offsetY;
                Ingredient lv = Ingredient.EMPTY;
                if (m >= 0 && n >= 0 && m < this.width && n < this.height) {
                    lv = bl ? this.inputs.get(this.width - m - 1 + n * this.width) : this.inputs.get(m + n * this.width);
                }
                if (lv.test(inv.getStack(k + l * inv.getWidth()))) continue;
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

    private static DefaultedList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        DefaultedList<Ingredient> lv = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
        HashSet set = Sets.newHashSet(key.keySet());
        set.remove(" ");
        for (int k = 0; k < pattern.length; ++k) {
            for (int l = 0; l < pattern[k].length(); ++l) {
                String string = pattern[k].substring(l, l + 1);
                Ingredient lv2 = key.get(string);
                if (lv2 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }
                set.remove(string);
                lv.set(l + width * k, lv2);
            }
        }
        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        }
        return lv;
    }

    @VisibleForTesting
    static String[] combinePattern(String ... lines) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < lines.length; ++m) {
            String string = lines[m];
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
        if (lines.length == l) {
            return new String[0];
        }
        String[] strings2 = new String[lines.length - l - k];
        for (int o = 0; o < strings2.length; ++o) {
            strings2[o] = lines[o + k].substring(i, j + 1);
        }
        return strings2;
    }

    private static int findNextIngredient(String pattern) {
        int i;
        for (i = 0; i < pattern.length() && pattern.charAt(i) == ' '; ++i) {
        }
        return i;
    }

    private static int findNextIngredientReverse(String pattern) {
        int i;
        for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == ' '; --i) {
        }
        return i;
    }

    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        }
        if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }
        for (int i = 0; i < strings.length; ++i) {
            String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
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

    private static Map<String, Ingredient> getComponents(JsonObject json) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : json.entrySet()) {
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

    public static ItemStack getItemStack(JsonObject json) {
        String string = JsonHelper.getString(json, "item");
        Item lv = Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        int i = JsonHelper.getInt(json, "count", 1);
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
        public /* synthetic */ Recipe read(Identifier id, PacketByteBuf buf) {
            return this.read(id, buf);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier id, JsonObject json) {
            return this.read(id, json);
        }
    }
}

