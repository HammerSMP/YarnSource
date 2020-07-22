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
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

public class SetCountLootFunction
extends ConditionalLootFunction {
    private final LootTableRange countRange;

    private SetCountLootFunction(LootCondition[] conditions, LootTableRange countRange) {
        super(conditions);
        this.countRange = countRange;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_COUNT;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        stack.setCount(this.countRange.next(context.getRandom()));
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(LootTableRange countRange) {
        return SetCountLootFunction.builder((LootCondition[] conditions) -> new SetCountLootFunction((LootCondition[])conditions, countRange));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetCountLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetCountLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("count", LootTableRanges.toJson(arg.countRange, jsonSerializationContext));
        }

        @Override
        public SetCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            LootTableRange lv = LootTableRanges.fromJson(jsonObject.get("count"), jsonDeserializationContext);
            return new SetCountLootFunction(args, lv);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

