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
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class TimeCheckLootCondition
implements LootCondition {
    @Nullable
    private final Long period;
    private final UniformLootTableRange value;

    private TimeCheckLootCondition(@Nullable Long arg, UniformLootTableRange arg2) {
        this.period = arg;
        this.value = arg2;
    }

    @Override
    public boolean test(LootContext arg) {
        ServerWorld lv = arg.getWorld();
        long l = lv.getTimeOfDay();
        if (this.period != null) {
            l %= this.period.longValue();
        }
        return this.value.contains((int)l);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<TimeCheckLootCondition> {
        public Factory() {
            super(new Identifier("time_check"), TimeCheckLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, TimeCheckLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("period", (Number)arg.period);
            jsonObject.add("value", jsonSerializationContext.serialize((Object)arg.value));
        }

        @Override
        public TimeCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Long lv = jsonObject.has("period") ? Long.valueOf(JsonHelper.getLong(jsonObject, "period")) : null;
            UniformLootTableRange lv2 = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, UniformLootTableRange.class);
            return new TimeCheckLootCondition(lv, lv2);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

