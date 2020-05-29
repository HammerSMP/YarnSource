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
import net.minecraft.class_5335;
import net.minecraft.class_5338;
import net.minecraft.class_5341;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootEntry
implements EntryCombiner {
    protected final class_5341[] conditions;
    private final Predicate<LootContext> conditionPredicate;

    protected LootEntry(class_5341[] args) {
        this.conditions = args;
        this.conditionPredicate = LootConditions.joinAnd(args);
    }

    public void validate(LootTableReporter arg) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(arg.makeChild(".condition[" + i + "]"));
        }
    }

    protected final boolean test(LootContext arg) {
        return this.conditionPredicate.test(arg);
    }

    public abstract class_5338 method_29318();

    public static abstract class class_5337<T extends LootEntry>
    implements class_5335<T> {
        @Override
        public final void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty((Object[])((LootEntry)arg).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)((LootEntry)arg).conditions));
            }
            this.method_422(jsonObject, arg, jsonSerializationContext);
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            class_5341[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new class_5341[0], jsonDeserializationContext, class_5341[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public abstract void method_422(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, class_5341[] var3);

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }

        @Override
        public /* synthetic */ void toJson(JsonObject jsonObject, Object object, JsonSerializationContext jsonSerializationContext) {
            this.toJson(jsonObject, (T)((LootEntry)object), jsonSerializationContext);
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootConditionConsumingBuilder<T> {
        private final List<class_5341> conditions = Lists.newArrayList();

        protected abstract T getThisBuilder();

        @Override
        public T conditionally(class_5341.Builder arg) {
            this.conditions.add(arg.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected class_5341[] getConditions() {
            return this.conditions.toArray(new class_5341[0]);
        }

        public AlternativeEntry.Builder alternatively(Builder<?> arg) {
            return new AlternativeEntry.Builder(this, arg);
        }

        public abstract LootEntry build();

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object conditionally(class_5341.Builder arg) {
            return this.conditionally(arg);
        }
    }
}

