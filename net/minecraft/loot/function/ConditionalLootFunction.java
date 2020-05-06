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
package net.minecraft.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction
implements LootFunction {
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;

    protected ConditionalLootFunction(LootCondition[] args) {
        this.conditions = args;
        this.predicate = LootConditions.joinAnd(args);
    }

    @Override
    public final ItemStack apply(ItemStack arg, LootContext arg2) {
        return this.predicate.test(arg2) ? this.process(arg, arg2) : arg;
    }

    protected abstract ItemStack process(ItemStack var1, LootContext var2);

    @Override
    public void check(LootTableReporter arg) {
        LootFunction.super.check(arg);
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(arg.makeChild(".conditions[" + i + "]"));
        }
    }

    protected static Builder<?> builder(Function<LootCondition[], LootFunction> function) {
        return new Joiner(function);
    }

    @Override
    public /* synthetic */ Object apply(Object object, Object object2) {
        return this.apply((ItemStack)object, (LootContext)object2);
    }

    public static abstract class Factory<T extends ConditionalLootFunction>
    extends LootFunction.Factory<T> {
        public Factory(Identifier arg, Class<T> arg2) {
            super(arg, arg2);
        }

        @Override
        public void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty((Object[])((ConditionalLootFunction)arg).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)((ConditionalLootFunction)arg).conditions));
            }
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);

        @Override
        public /* synthetic */ LootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }

    static final class Joiner
    extends Builder<Joiner> {
        private final Function<LootCondition[], LootFunction> joiner;

        public Joiner(Function<LootCondition[], LootFunction> function) {
            this.joiner = function;
        }

        @Override
        protected Joiner getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return this.joiner.apply(this.getConditions());
        }

        @Override
        protected /* synthetic */ Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootFunction.Builder,
    LootConditionConsumingBuilder<T> {
        private final List<LootCondition> conditionList = Lists.newArrayList();

        @Override
        public T withCondition(LootCondition.Builder arg) {
            this.conditionList.add(arg.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected abstract T getThisBuilder();

        protected LootCondition[] getConditions() {
            return this.conditionList.toArray(new LootCondition[0]);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object withCondition(LootCondition.Builder arg) {
            return this.withCondition(arg);
        }
    }
}

