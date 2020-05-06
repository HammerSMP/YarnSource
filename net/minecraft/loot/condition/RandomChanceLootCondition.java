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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RandomChanceLootCondition
implements LootCondition {
    private final float chance;

    private RandomChanceLootCondition(float f) {
        this.chance = f;
    }

    @Override
    public boolean test(LootContext arg) {
        return arg.getRandom().nextFloat() < this.chance;
    }

    public static LootCondition.Builder builder(float f) {
        return () -> new RandomChanceLootCondition(f);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<RandomChanceLootCondition> {
        protected Factory() {
            super(new Identifier("random_chance"), RandomChanceLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, RandomChanceLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(arg.chance));
        }

        @Override
        public RandomChanceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceLootCondition(JsonHelper.getFloat(jsonObject, "chance"));
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

