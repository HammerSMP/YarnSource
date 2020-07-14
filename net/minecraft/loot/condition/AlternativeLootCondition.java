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
import net.minecraft.util.JsonSerializer;

public class AlternativeLootCondition
implements LootCondition {
    private final LootCondition[] terms;
    private final Predicate<LootContext> predicate;

    private AlternativeLootCondition(LootCondition[] terms) {
        this.terms = terms;
        this.predicate = LootConditionTypes.joinOr(terms);
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.ALTERNATIVE;
    }

    @Override
    public final boolean test(LootContext arg) {
        return this.predicate.test(arg);
    }

    @Override
    public void validate(LootTableReporter reporter) {
        LootCondition.super.validate(reporter);
        for (int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
        }
    }

    public static Builder builder(LootCondition.Builder ... terms) {
        return new Builder(terms);
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<AlternativeLootCondition> {
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
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    public static class Builder
    implements LootCondition.Builder {
        private final List<LootCondition> terms = Lists.newArrayList();

        public Builder(LootCondition.Builder ... terms) {
            for (LootCondition.Builder lv : terms) {
                this.terms.add(lv.build());
            }
        }

        @Override
        public Builder or(LootCondition.Builder condition) {
            this.terms.add(condition.build());
            return this;
        }

        @Override
        public LootCondition build() {
            return new AlternativeLootCondition(this.terms.toArray(new LootCondition[0]));
        }
    }
}

