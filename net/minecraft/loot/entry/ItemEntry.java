/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ItemEntry
extends LeafEntry {
    private final Item item;

    private ItemEntry(Item arg, int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(i, j, args, args2);
        this.item = arg;
    }

    @Override
    public LootPoolEntryType method_29318() {
        return LootPoolEntryTypes.ITEM;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> consumer, LootContext arg) {
        consumer.accept(new ItemStack(this.item));
    }

    public static LeafEntry.Builder<?> builder(ItemConvertible arg) {
        return ItemEntry.builder((int i, int j, LootCondition[] args, LootFunction[] args2) -> new ItemEntry(arg.asItem(), i, j, args, args2));
    }

    public static class Serializer
    extends LeafEntry.Serializer<ItemEntry> {
        public void method_442(JsonObject jsonObject, ItemEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.method_422(jsonObject, arg, jsonSerializationContext);
            Identifier lv = Registry.ITEM.getId(arg.item);
            if (lv == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + arg.item);
            }
            jsonObject.addProperty("name", lv.toString());
        }

        @Override
        protected ItemEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            Item lv = JsonHelper.getItem(jsonObject, "name");
            return new ItemEntry(lv, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

