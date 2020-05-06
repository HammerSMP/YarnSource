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
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InvertedLootCondition
implements LootCondition {
    private final LootCondition term;

    private InvertedLootCondition(LootCondition arg) {
        this.term = arg;
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
    public void check(LootTableReporter arg) {
        LootCondition.super.check(arg);
        this.term.check(arg);
    }

    public static LootCondition.Builder builder(LootCondition.Builder arg) {
        InvertedLootCondition lv = new InvertedLootCondition(arg.build());
        return () -> lv;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<InvertedLootCondition> {
        public Factory() {
            super(new Identifier("inverted"), InvertedLootCondition.class);
        }

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
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

