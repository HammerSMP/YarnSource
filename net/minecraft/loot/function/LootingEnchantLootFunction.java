/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;

public class LootingEnchantLootFunction
extends ConditionalLootFunction {
    private final UniformLootTableRange countRange;
    private final int limit;

    private LootingEnchantLootFunction(LootCondition[] conditions, UniformLootTableRange countRange, int limit) {
        super(conditions);
        this.countRange = countRange;
        this.limit = limit;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.LOOTING_ENCHANT;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    private boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Entity lv = context.get(LootContextParameters.KILLER_ENTITY);
        if (lv instanceof LivingEntity) {
            int i = EnchantmentHelper.getLooting((LivingEntity)lv);
            if (i == 0) {
                return stack;
            }
            float f = (float)i * this.countRange.nextFloat(context.getRandom());
            stack.increment(Math.round(f));
            if (this.hasLimit() && stack.getCount() > this.limit) {
                stack.setCount(this.limit);
            }
        }
        return stack;
    }

    public static Builder builder(UniformLootTableRange countRange) {
        return new Builder(countRange);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<LootingEnchantLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, LootingEnchantLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("count", jsonSerializationContext.serialize((Object)arg.countRange));
            if (arg.hasLimit()) {
                jsonObject.add("limit", jsonSerializationContext.serialize((Object)arg.limit));
            }
        }

        @Override
        public LootingEnchantLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            int i = JsonHelper.getInt(jsonObject, "limit", 0);
            return new LootingEnchantLootFunction(args, JsonHelper.deserialize(jsonObject, "count", jsonDeserializationContext, UniformLootTableRange.class), i);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final UniformLootTableRange countRange;
        private int limit = 0;

        public Builder(UniformLootTableRange countRange) {
            this.countRange = countRange;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public LootFunction build() {
            return new LootingEnchantLootFunction(this.getConditions(), this.countRange, this.limit);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

