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

    private InvertedLootCondition(LootCondition term) {
        this.term = term;
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
    public void validate(LootTableReporter reporter) {
        LootCondition.super.validate(reporter);
        this.term.validate(reporter);
    }

    public static LootCondition.Builder builder(LootCondition.Builder term) {
        InvertedLootCondition lv = new InvertedLootCondition(term.build());
        return () -> lv;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
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
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

