/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.GsonBuilder
 */
package net.minecraft.loot;

import com.google.gson.GsonBuilder;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public class LootGsons {
    public static GsonBuilder getConditionGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter(UniformLootTableRange.class, (Object)new UniformLootTableRange.Serializer()).registerTypeAdapter(BinomialLootTableRange.class, (Object)new BinomialLootTableRange.Serializer()).registerTypeAdapter(ConstantLootTableRange.class, (Object)new ConstantLootTableRange.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, (Object)new LootContext.EntityTarget.Serializer());
    }

    public static GsonBuilder getFunctionGsonBuilder() {
        return LootGsons.getConditionGsonBuilder().registerTypeAdapter(BoundedIntUnaryOperator.class, (Object)new BoundedIntUnaryOperator.Serializer()).registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer());
    }

    public static GsonBuilder getTableGsonBuilder() {
        return LootGsons.getFunctionGsonBuilder().registerTypeAdapter(LootPool.class, (Object)new LootPool.Serializer()).registerTypeAdapter(LootTable.class, (Object)new LootTable.Serializer());
    }
}

