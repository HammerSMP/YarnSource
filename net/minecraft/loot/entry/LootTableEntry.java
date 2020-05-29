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
import net.minecraft.class_5338;
import net.minecraft.class_5341;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootTableEntry
extends LeafEntry {
    private final Identifier id;

    private LootTableEntry(Identifier arg, int i, int j, class_5341[] args, LootFunction[] args2) {
        super(i, j, args, args2);
        this.id = arg;
    }

    @Override
    public class_5338 method_29318() {
        return LootEntries.LOOT_TABLE;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> consumer, LootContext arg) {
        LootTable lv = arg.getSupplier(this.id);
        lv.generateUnprocessedLoot(arg, consumer);
    }

    @Override
    public void validate(LootTableReporter arg) {
        if (arg.hasTable(this.id)) {
            arg.report("Table " + this.id + " is recursively called");
            return;
        }
        super.validate(arg);
        LootTable lv = arg.getTable(this.id);
        if (lv == null) {
            arg.report("Unknown loot table called " + this.id);
        } else {
            lv.validate(arg.withTable("->{" + this.id + "}", this.id));
        }
    }

    public static LeafEntry.Builder<?> builder(Identifier arg) {
        return LootTableEntry.builder((int i, int j, class_5341[] args, LootFunction[] args2) -> new LootTableEntry(arg, i, j, args, args2));
    }

    public static class Serializer
    extends LeafEntry.Serializer<LootTableEntry> {
        @Override
        public void method_422(JsonObject jsonObject, LootTableEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.method_422(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", arg.id.toString());
        }

        @Override
        protected LootTableEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_5341[] args, LootFunction[] args2) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new LootTableEntry(lv, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_5341[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

