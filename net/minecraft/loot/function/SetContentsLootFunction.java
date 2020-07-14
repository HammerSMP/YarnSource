/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SetContentsLootFunction
extends ConditionalLootFunction {
    private final List<LootPoolEntry> entries;

    private SetContentsLootFunction(LootCondition[] conditions, List<LootPoolEntry> entries) {
        super(conditions);
        this.entries = ImmutableList.copyOf(entries);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_CONTENTS;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        if (stack.isEmpty()) {
            return stack;
        }
        DefaultedList<ItemStack> lv = DefaultedList.of();
        this.entries.forEach(entry -> entry.expand(context, choice -> choice.generateLoot(LootTable.processStacks(lv::add), context)));
        CompoundTag lv2 = new CompoundTag();
        Inventories.toTag(lv2, lv);
        CompoundTag lv3 = stack.getOrCreateTag();
        lv3.put("BlockEntityTag", lv2.copyFrom(lv3.getCompound("BlockEntityTag")));
        return stack;
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).validate(reporter.makeChild(".entry[" + i + "]"));
        }
    }

    public static Builer builder() {
        return new Builer();
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetContentsLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetContentsLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)arg.entries));
        }

        @Override
        public SetContentsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            LootPoolEntry[] lvs = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootPoolEntry[].class);
            return new SetContentsLootFunction(args, Arrays.asList(lvs));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static class Builer
    extends ConditionalLootFunction.Builder<Builer> {
        private final List<LootPoolEntry> entries = Lists.newArrayList();

        @Override
        protected Builer getThisBuilder() {
            return this;
        }

        public Builer withEntry(LootPoolEntry.Builder<?> entryBuilder) {
            this.entries.add(entryBuilder.build());
            return this;
        }

        @Override
        public LootFunction build() {
            return new SetContentsLootFunction(this.getConditions(), this.entries);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

