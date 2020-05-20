/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SetLootTableLootFunction
extends ConditionalLootFunction {
    private final Identifier id;
    private final long seed;

    private SetLootTableLootFunction(LootCondition[] args, Identifier arg, long l) {
        super(args);
        this.id = arg;
        this.seed = l;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        if (arg.isEmpty()) {
            return arg;
        }
        CompoundTag lv = new CompoundTag();
        lv.putString("LootTable", this.id.toString());
        if (this.seed != 0L) {
            lv.putLong("LootTableSeed", this.seed);
        }
        arg.getOrCreateTag().put("BlockEntityTag", lv);
        return arg;
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

    public static class Factory
    extends ConditionalLootFunction.Factory<SetLootTableLootFunction> {
        protected Factory() {
            super(new Identifier("set_loot_table"), SetLootTableLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetLootTableLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", arg.id.toString());
            if (arg.seed != 0L) {
                jsonObject.addProperty("seed", (Number)arg.seed);
            }
        }

        @Override
        public SetLootTableLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            long l = JsonHelper.getLong(jsonObject, "seed", 0L);
            return new SetLootTableLootFunction(args, lv, l);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

