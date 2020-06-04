/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializable;

public class WeatherCheckLootCondition
implements LootCondition {
    @Nullable
    private final Boolean raining;
    @Nullable
    private final Boolean thundering;

    private WeatherCheckLootCondition(@Nullable Boolean boolean_, @Nullable Boolean boolean2) {
        this.raining = boolean_;
        this.thundering = boolean2;
    }

    @Override
    public LootConditionType method_29325() {
        return LootConditionTypes.WEATHER_CHECK;
    }

    @Override
    public boolean test(LootContext arg) {
        ServerWorld lv = arg.getWorld();
        if (this.raining != null && this.raining.booleanValue() != lv.isRaining()) {
            return false;
        }
        return this.thundering == null || this.thundering.booleanValue() == lv.isThundering();
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements JsonSerializable<WeatherCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, WeatherCheckLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("raining", arg.raining);
            jsonObject.addProperty("thundering", arg.thundering);
        }

        @Override
        public WeatherCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Boolean boolean_ = jsonObject.has("raining") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "raining")) : null;
            Boolean boolean2 = jsonObject.has("thundering") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "thundering")) : null;
            return new WeatherCheckLootCondition(boolean_, boolean2);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

