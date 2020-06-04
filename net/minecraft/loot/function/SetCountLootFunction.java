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

    private SetCountLootFunction(LootCondition[] args, LootTableRange arg) {
        super(args);
        this.countRange = arg;
    }

    @Override
    public LootFunctionType method_29321() {
        return LootFunctionTypes.SET_COUNT;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        arg.setCount(this.countRange.next(arg2.getRandom()));
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder(LootTableRange arg) {
        return SetCountLootFunction.builder((LootCondition[] args) -> new SetCountLootFunction((LootCondition[])args, arg));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<SetCountLootFunction> {
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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

