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
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableEntry
extends LeafEntry {
    private final Identifier id;

    private LootTableEntry(Identifier arg, int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(i, j, args, args2);
        this.id = arg;
    }

    @Override
    public void drop(Consumer<ItemStack> consumer, LootContext arg) {
        LootTable lv = arg.getSupplier(this.id);
        lv.drop(arg, consumer);
    }

    @Override
    public void check(LootTableReporter arg) {
        if (arg.hasTable(this.id)) {
            arg.report("Table " + this.id + " is recursively called");
            return;
        }
        super.check(arg);
        LootTable lv = arg.getTable(this.id);
        if (lv == null) {
            arg.report("Unknown loot table called " + this.id);
        } else {
            lv.check(arg.withTable("->{" + this.id + "}", this.id));
        }
    }

    public static LeafEntry.Builder<?> builder(Identifier arg) {
        return LootTableEntry.builder((int i, int j, LootCondition[] args, LootFunction[] args2) -> new LootTableEntry(arg, i, j, args, args2));
    }

    public static class Serializer
    extends LeafEntry.Serializer<LootTableEntry> {
        public Serializer() {
            super(new Identifier("loot_table"), LootTableEntry.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, LootTableEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", arg.id.toString());
        }

        @Override
        protected LootTableEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new LootTableEntry(lv, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

