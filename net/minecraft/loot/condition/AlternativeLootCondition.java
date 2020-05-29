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
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonHelper;

public class AlternativeLootCondition
implements class_5341 {
    private final class_5341[] terms;
    private final Predicate<LootContext> predicate;

    private AlternativeLootCondition(class_5341[] args) {
        this.terms = args;
        this.predicate = LootConditions.joinOr(args);
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.ALTERNATIVE;
    }

    @Override
    public final boolean test(LootContext arg) {
        return this.predicate.test(arg);
    }

    @Override
    public void validate(LootTableReporter arg) {
        class_5341.super.validate(arg);
        for (int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(arg.makeChild(".term[" + i + "]"));
        }
    }

    public static Builder builder(class_5341.Builder ... args) {
        return new Builder(args);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<AlternativeLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, AlternativeLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("terms", jsonSerializationContext.serialize((Object)arg.terms));
        }

        @Override
        public AlternativeLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            class_5341[] lvs = JsonHelper.deserialize(jsonObject, "terms", jsonDeserializationContext, class_5341[].class);
            return new AlternativeLootCondition(lvs);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements class_5341.Builder {
        private final List<class_5341> terms = Lists.newArrayList();

        public Builder(class_5341.Builder ... args) {
            for (class_5341.Builder lv : args) {
                this.terms.add(lv.build());
            }
        }

        @Override
        public Builder or(class_5341.Builder arg) {
            this.terms.add(arg.build());
            return this;
        }

        @Override
        public class_5341 build() {
            return new AlternativeLootCondition(this.terms.toArray(new class_5341[0]));
        }
    }
}

