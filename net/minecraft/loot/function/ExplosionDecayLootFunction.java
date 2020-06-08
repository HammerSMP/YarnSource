/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

public class ExplosionDecayLootFunction
extends ConditionalLootFunction {
    private ExplosionDecayLootFunction(LootCondition[] args) {
        super(args);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Float float_ = arg2.get(LootContextParameters.EXPLOSION_RADIUS);
        if (float_ != null) {
            Random random = arg2.getRandom();
            float f = 1.0f / float_.floatValue();
            int i = arg.getCount();
            int j = 0;
            for (int k = 0; k < i; ++k) {
                if (!(random.nextFloat() <= f)) continue;
                ++j;
            }
            arg.setCount(j);
        }
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return ExplosionDecayLootFunction.builder(ExplosionDecayLootFunction::new);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<ExplosionDecayLootFunction> {
        @Override
        public ExplosionDecayLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return new ExplosionDecayLootFunction(args);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

