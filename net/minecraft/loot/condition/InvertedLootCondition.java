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
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class InvertedLootCondition
implements LootCondition {
    private final LootCondition term;

    private InvertedLootCondition(LootCondition arg) {
        this.term = arg;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.INVERTED;
    }

    @Override
    public final boolean test(LootContext arg) {
        return !this.term.test(arg);
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return this.term.getRequiredParameters();
    }

    @Override
    public void validate(LootTableReporter arg) {
        LootCondition.super.validate(arg);
        this.term.validate(arg);
    }

    public static LootCondition.Builder builder(LootCondition.Builder arg) {
        InvertedLootCondition lv = new InvertedLootCondition(arg.build());
        return () -> lv;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Serializer
    implements JsonSerializer<InvertedLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, InvertedLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("term", jsonSerializationContext.serialize((Object)arg.term));
        }

        @Override
        public InvertedLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition lv = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, LootCondition.class);
            return new InvertedLootCondition(lv);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

