/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntry
implements EntryCombiner {
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> conditionPredicate;

    protected LootPoolEntry(LootCondition[] conditions) {
        this.conditions = conditions;
        this.conditionPredicate = LootConditionTypes.joinAnd(conditions);
    }

    public void validate(LootTableReporter reporter) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(reporter.makeChild(".condition[" + i + "]"));
        }
    }

    protected final boolean test(LootContext context) {
        return this.conditionPredicate.test(context);
    }

    public abstract LootPoolEntryType getType();

    public static abstract class Serializer<T extends LootPoolEntry>
    implements JsonSerializer<T> {
        @Override
        public final void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty((Object[])((LootPoolEntry)arg).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)((LootPoolEntry)arg).conditions));
            }
            this.addEntryFields(jsonObject, arg, jsonSerializationContext);
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public abstract void addEntryFields(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);

        @Override
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }

        @Override
        public /* synthetic */ void toJson(JsonObject json, Object object, JsonSerializationContext context) {
            this.toJson(json, (T)((LootPoolEntry)object), context);
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootConditionConsumingBuilder<T> {
        private final List<LootCondition> conditions = Lists.newArrayList();

        protected abstract T getThisBuilder();

        @Override
        public T conditionally(LootCondition.Builder arg) {
            this.conditions.add(arg.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected LootCondition[] getConditions() {
            return this.conditions.toArray(new LootCondition[0]);
        }

        public AlternativeEntry.Builder alternatively(Builder<?> builder) {
            return new AlternativeEntry.Builder(this, builder);
        }

        public abstract LootPoolEntry build();

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object conditionally(LootCondition.Builder condition) {
            return this.conditionally(condition);
        }
    }
}

