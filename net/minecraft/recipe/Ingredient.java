/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public final class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Entry[] entries;
    private ItemStack[] matchingStacks;
    private IntList ids;

    private Ingredient(Stream<? extends Entry> stream) {
        this.entries = (Entry[])stream.toArray(Entry[]::new);
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack[] getMatchingStacksClient() {
        this.cacheMatchingStacks();
        return this.matchingStacks;
    }

    private void cacheMatchingStacks() {
        if (this.matchingStacks == null) {
            this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(arg -> arg.getStacks().stream()).distinct().toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack arg) {
        if (arg == null) {
            return false;
        }
        this.cacheMatchingStacks();
        if (this.matchingStacks.length == 0) {
            return arg.isEmpty();
        }
        for (ItemStack lv : this.matchingStacks) {
            if (lv.getItem() != arg.getItem()) continue;
            return true;
        }
        return false;
    }

    public IntList getIds() {
        if (this.ids == null) {
            this.cacheMatchingStacks();
            this.ids = new IntArrayList(this.matchingStacks.length);
            for (ItemStack lv : this.matchingStacks) {
                this.ids.add(RecipeFinder.getItemId(lv));
            }
            this.ids.sort((Comparator)IntComparators.NATURAL_COMPARATOR);
        }
        return this.ids;
    }

    public void write(PacketByteBuf arg) {
        this.cacheMatchingStacks();
        arg.writeVarInt(this.matchingStacks.length);
        for (int i = 0; i < this.matchingStacks.length; ++i) {
            arg.writeItemStack(this.matchingStacks[i]);
        }
    }

    public JsonElement toJson() {
        if (this.entries.length == 1) {
            return this.entries[0].toJson();
        }
        JsonArray jsonArray = new JsonArray();
        for (Entry lv : this.entries) {
            jsonArray.add((JsonElement)lv.toJson());
        }
        return jsonArray;
    }

    public boolean isEmpty() {
        return !(this.entries.length != 0 || this.matchingStacks != null && this.matchingStacks.length != 0 || this.ids != null && !this.ids.isEmpty());
    }

    private static Ingredient ofEntries(Stream<? extends Entry> stream) {
        Ingredient lv = new Ingredient(stream);
        return lv.entries.length == 0 ? EMPTY : lv;
    }

    public static Ingredient ofItems(ItemConvertible ... args) {
        return Ingredient.method_26964(Arrays.stream(args).map(ItemStack::new));
    }

    @Environment(value=EnvType.CLIENT)
    public static Ingredient ofStacks(ItemStack ... args) {
        return Ingredient.method_26964(Arrays.stream(args));
    }

    public static Ingredient method_26964(Stream<ItemStack> stream) {
        return Ingredient.ofEntries(stream.filter(arg -> !arg.isEmpty()).map(arg -> new StackEntry((ItemStack)arg)));
    }

    public static Ingredient fromTag(Tag<Item> arg) {
        return Ingredient.ofEntries(Stream.of(new TagEntry(arg)));
    }

    public static Ingredient fromPacket(PacketByteBuf arg) {
        int i = arg.readVarInt();
        return Ingredient.ofEntries(Stream.generate(() -> new StackEntry(arg.readItemStack())).limit(i));
    }

    public static Ingredient fromJson(@Nullable JsonElement jsonElement2) {
        if (jsonElement2 == null || jsonElement2.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (jsonElement2.isJsonObject()) {
            return Ingredient.ofEntries(Stream.of(Ingredient.entryFromJson(jsonElement2.getAsJsonObject())));
        }
        if (jsonElement2.isJsonArray()) {
            JsonArray jsonArray = jsonElement2.getAsJsonArray();
            if (jsonArray.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            return Ingredient.ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> Ingredient.entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    private static Entry entryFromJson(JsonObject jsonObject) {
        if (jsonObject.has("item") && jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (jsonObject.has("item")) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "item"));
            Item lv2 = (Item)Registry.ITEM.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + lv + "'"));
            return new StackEntry(new ItemStack(lv2));
        }
        if (jsonObject.has("tag")) {
            Identifier lv3 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            Tag<Item> lv4 = TagContainers.instance().items().get(lv3);
            if (lv4 == null) {
                throw new JsonSyntaxException("Unknown item tag '" + lv3 + "'");
            }
            return new TagEntry(lv4);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((ItemStack)object);
    }

    static class TagEntry
    implements Entry {
        private final Tag<Item> tag;

        private TagEntry(Tag<Item> arg) {
            this.tag = arg;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            ArrayList list = Lists.newArrayList();
            for (Item lv : this.tag.values()) {
                list.add(new ItemStack(lv));
            }
            return list;
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tag", TagContainers.instance().items().checkId(this.tag).toString());
            return jsonObject;
        }
    }

    static class StackEntry
    implements Entry {
        private final ItemStack stack;

        private StackEntry(ItemStack arg) {
            this.stack = arg;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        @Override
        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getId(this.stack.getItem()).toString());
            return jsonObject;
        }
    }

    static interface Entry {
        public Collection<ItemStack> getStacks();

        public JsonObject toJson();
    }
}

