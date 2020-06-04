/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializable;

public class AlternativeLootCondition
implements LootCondition {
    private final LootCondition[] terms;
    private final Predicate<LootContext> predicate;

    private AlternativeLootCondition(LootCondition[] args) {
        this.terms = args;
        this.predicate = LootConditionTypes.joinOr(args);
    }

    @Override
    public LootConditionType method_29325() {
        return LootConditionTypes.ALTERNATIVE;
    }

    @Override
    public final boolean test(LootContext arg) {
        return this.predicate.test(arg);
    }

    @Override
    public void validate(LootTableReporter arg) {
        LootCondition.super.validate(arg);
        for (int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(arg.makeChild(".term[" + i + "]"));
        }
    }

    public static Builder builder(LootCondition.Builder ... args) {
        return new Builder(args);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements JsonSerializable<AlternativeLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, AlternativeLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("terms", jsonSerializationContext.serialize((Object)arg.terms));
        }

        @Override
        public AlternativeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition[] lvs = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, LootCondition[].class);
            return new AlternativeLootCondition(lvs);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final List<LootCondition> terms = Lists.newArrayList();

        public Builder(LootCondition.Builder ... args) {
            for (LootCondition.Builder lv : args) {
                this.terms.add(lv.build());
            }
        }

        @Override
        public Builder or(LootCondition.Builder arg) {
            this.terms.add(arg.build());
            return this;
        }

        @Override
        public LootCondition build() {
            return new AlternativeLootCondition(this.terms.toArray(new LootCondition[0]));
        }
    }
}

