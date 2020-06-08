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

    protected LootPoolEntry(LootCondition[] args) {
        this.conditions = args;
        this.conditionPredicate = LootConditionTypes.joinAnd(args);
    }

    public void validate(LootTableReporter arg) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(arg.makeChild(".condition[" + i + "]"));
        }
    }

    protected final boolean test(LootContext arg) {
        return this.conditionPredicate.test(arg);
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
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }

        @Override
        public /* synthetic */ void toJson(JsonObject jsonObject, Object object, JsonSerializationContext jsonSerializationContext) {
            this.toJson(jsonObject, (T)((LootPoolEntry)object), jsonSerializationContext);
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

        public AlternativeEntry.Builder alternatively(Builder<?> arg) {
            return new AlternativeEntry.Builder(this, arg);
        }

        public abstract LootPoolEntry build();

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object conditionally(LootCondition.Builder arg) {
            return this.conditionally(arg);
        }
    }
}

