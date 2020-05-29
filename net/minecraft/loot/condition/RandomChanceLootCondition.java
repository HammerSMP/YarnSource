/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;

public class RandomChanceLootCondition
implements class_5341 {
    private final float chance;

    private RandomChanceLootCondition(float f) {
        this.chance = f;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.RANDOM_CHANCE;
    }

    @Override
    public boolean test(LootContext arg) {
        return arg.getRandom().nextFloat() < this.chance;
    }

    public static class_5341.Builder builder(float f) {
        return () -> new RandomChanceLootCondition(f);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<RandomChanceLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, RandomChanceLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(arg.chance));
        }

        @Override
        public RandomChanceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

