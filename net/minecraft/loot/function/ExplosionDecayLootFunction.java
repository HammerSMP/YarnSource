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
import net.minecraft.class_5339;
import net.minecraft.class_5341;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctions;

public class ExplosionDecayLootFunction
extends ConditionalLootFunction {
    private ExplosionDecayLootFunction(class_5341[] args) {
        super(args);
    }

    @Override
    public class_5339 method_29321() {
        return LootFunctions.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Float lv = arg2.get(LootContextParameters.EXPLOSION_RADIUS);
        if (lv != null) {
            Random random = arg2.getRandom();
            float f = 1.0f / lv.floatValue();
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

    public static class Factory
    extends ConditionalLootFunction.Factory<ExplosionDecayLootFunction> {
        @Override
        public ExplosionDecayLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            return new ExplosionDecayLootFunction(args);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

