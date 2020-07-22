/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TagEntry
extends LeafEntry {
    private final Tag<Item> name;
    private final boolean expand;

    private TagEntry(Tag<Item> name, boolean expand, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(weight, quality, conditions, functions);
        this.name = name;
        this.expand = expand;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.TAG;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        this.name.values().forEach(arg -> lootConsumer.accept(new ItemStack((ItemConvertible)arg)));
    }

    private boolean grow(LootContext context, Consumer<LootChoice> lootChoiceExpander) {
        if (this.test(context)) {
            for (final Item lv : this.name.values()) {
                lootChoiceExpander.accept(new LeafEntry.Choice(){

                    @Override
                    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
                        lootConsumer.accept(new ItemStack(lv));
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean expand(LootContext arg, Consumer<LootChoice> consumer) {
        if (this.expand) {
            return this.grow(arg, consumer);
        }
        return super.expand(arg, consumer);
    }

    public static LeafEntry.Builder<?> builder(Tag<Item> name) {
        return TagEntry.builder((int weight, int quality, LootCondition[] conditions, LootFunction[] functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
    }

    public static class Serializer
    extends LeafEntry.Serializer<TagEntry> {
        @Override
        public void addEntryFields(JsonObject jsonObject, TagEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.addEntryFields(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", ServerTagManagerHolder.getTagManager().getItems().getTagId(arg.name).toString());
            jsonObject.addProperty("expand", Boolean.valueOf(arg.expand));
        }

        @Override
        protected TagEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            Tag<Item> lv2 = ServerTagManagerHolder.getTagManager().getItems().getTag(lv);
            if (lv2 == null) {
                throw new JsonParseException("Can't find tag: " + lv);
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "expand");
            return new TagEntry(lv2, bl, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
            return this.fromJson(entryJson, context, weight, quality, conditions, functions);
        }
    }
}

