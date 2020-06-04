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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DynamicEntry
extends LeafEntry {
    private final Identifier name;

    private DynamicEntry(Identifier arg, int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(i, j, args, args2);
        this.name = arg;
    }

    @Override
    public LootPoolEntryType method_29318() {
        return LootPoolEntryTypes.DYNAMIC;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> consumer, LootContext arg) {
        arg.drop(this.name, consumer);
    }

    public static LeafEntry.Builder<?> builder(Identifier arg) {
        return DynamicEntry.builder((int i, int j, LootCondition[] args, LootFunction[] args2) -> new DynamicEntry(arg, i, j, args, args2));
    }

    public static class Serializer
    extends LeafEntry.Serializer<DynamicEntry> {
        @Override
        public void method_422(JsonObject jsonObject, DynamicEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.method_422(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", arg.name.toString());
        }

        @Override
        protected DynamicEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new DynamicEntry(lv, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

