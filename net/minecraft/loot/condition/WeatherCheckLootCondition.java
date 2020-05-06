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
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class WeatherCheckLootCondition
implements LootCondition {
    @Nullable
    private final Boolean raining;
    @Nullable
    private final Boolean thundering;

    private WeatherCheckLootCondition(@Nullable Boolean arg, @Nullable Boolean arg2) {
        this.raining = arg;
        this.thundering = arg2;
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
    extends LootCondition.Factory<WeatherCheckLootCondition> {
        public Factory() {
            super(new Identifier("weather_check"), WeatherCheckLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, WeatherCheckLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("raining", arg.raining);
            jsonObject.addProperty("thundering", arg.thundering);
        }

        @Override
        public WeatherCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Boolean lv = jsonObject.has("raining") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "raining")) : null;
            Boolean lv2 = jsonObject.has("thundering") ? Boolean.valueOf(JsonHelper.getBoolean(jsonObject, "thundering")) : null;
            return new WeatherCheckLootCondition(lv, lv2);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

