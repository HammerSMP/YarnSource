/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;

public class EmptyEntry
extends LeafEntry {
    private EmptyEntry(int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(i, j, args, args2);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.EMPTY;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> consumer, LootContext arg) {
    }

    public static LeafEntry.Builder<?> Serializer() {
        return EmptyEntry.builder(EmptyEntry::new);
    }

    public static class Serializer
    extends LeafEntry.Serializer<EmptyEntry> {
        @Override
        public EmptyEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return new EmptyEntry(i, j, args, args2);
        }

        @Override
        public /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

