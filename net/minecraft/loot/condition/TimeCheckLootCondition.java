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
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class TimeCheckLootCondition
implements LootCondition {
    @Nullable
    private final Long period;
    private final UniformLootTableRange value;

    private TimeCheckLootCondition(@Nullable Long period, UniformLootTableRange value) {
        this.period = period;
        this.value = value;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.TIME_CHECK;
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
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<TimeCheckLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, TimeCheckLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("period", (Number)arg.period);
            jsonObject.add("value", jsonSerializationContext.serialize((Object)arg.value));
        }

        @Override
        public TimeCheckLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Long long_ = jsonObject.has("period") ? Long.valueOf(JsonHelper.getLong(jsonObject, "period")) : null;
            UniformLootTableRange lv = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, UniformLootTableRange.class);
            return new TimeCheckLootCondition(long_, lv);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

