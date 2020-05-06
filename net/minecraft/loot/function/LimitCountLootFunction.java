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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LimitCountLootFunction
extends ConditionalLootFunction {
    private final BoundedIntUnaryOperator limit;

    private LimitCountLootFunction(LootCondition[] args, BoundedIntUnaryOperator arg) {
        super(args);
        this.limit = arg;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        int i = this.limit.applyAsInt(arg.getCount());
        arg.setCount(i);
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator arg) {
        return LimitCountLootFunction.builder((LootCondition[] args) -> new LimitCountLootFunction((LootCondition[])args, arg));
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<LimitCountLootFunction> {
        protected Factory() {
            super(new Identifier("limit_count"), LimitCountLootFunction.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, LimitCountLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize((Object)arg.limit));
        }

        @Override
        public LimitCountLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            BoundedIntUnaryOperator lv = JsonHelper.deserialize(jsonObject, "limit", jsonDeserializationContext, BoundedIntUnaryOperator.class);
            return new LimitCountLootFunction(args, lv);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

