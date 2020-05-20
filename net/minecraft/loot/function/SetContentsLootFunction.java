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
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class SetContentsLootFunction
extends ConditionalLootFunction {
    private final List<LootEntry> entries;

    private SetContentsLootFunction(LootCondition[] args, List<LootEntry> list) {
        super(args);
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        if (arg.isEmpty()) {
            return arg;
        }
        DefaultedList<ItemStack> lv = DefaultedList.of();
        this.entries.forEach(arg32 -> arg32.expand(arg2, arg3 -> arg3.generateLoot(LootTable.processStacks(lv::add), arg2)));
        CompoundTag lv2 = new CompoundTag();
        Inventories.toTag(lv2, lv);
        CompoundTag lv3 = arg.getOrCreateTag();
        lv3.put("BlockEntityTag", lv2.copyFrom(lv3.getCompound("BlockEntityTag")));
        return arg;
    }

    @Override
    public void validate(LootTableReporter arg) {
        super.validate(arg);
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).validate(arg.makeChild(".entry[" + i + "]"));
        }
    }

    public static Builer builder() {
        return new Builer();
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetContentsLootFunction> {
        protected Factory() {
            super(new Identifier("set_contents"), SetContentsLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, SetContentsLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)arg.entries));
        }

        @Override
        public SetContentsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            LootEntry[] lvs = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
            return new SetContentsLootFunction(args, Arrays.asList(lvs));
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static class Builer
    extends ConditionalLootFunction.Builder<Builer> {
        private final List<LootEntry> entries = Lists.newArrayList();

        @Override
        protected Builer getThisBuilder() {
            return this;
        }

        public Builer withEntry(LootEntry.Builder<?> arg) {
            this.entries.add(arg.build());
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

