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
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.util.JsonHelper;

public class InvertedLootCondition
implements class_5341 {
    private final class_5341 term;

    private InvertedLootCondition(class_5341 arg) {
        this.term = arg;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.INVERTED;
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
        class_5341.super.validate(arg);
        this.term.validate(arg);
    }

    public static class_5341.Builder builder(class_5341.Builder arg) {
        InvertedLootCondition lv = new InvertedLootCondition(arg.build());
        return () -> lv;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<InvertedLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, InvertedLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("term", jsonSerializationContext.serialize((Object)arg.term));
        }

        @Override
        public InvertedLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            class_5341 lv = JsonHelper.deserialize(jsonObject, "term", jsonDeserializationContext, class_5341.class);
            return new InvertedLootCondition(lv);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

