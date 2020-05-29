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
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction
implements LootFunction {
    protected final class_5341[] conditions;
    private final Predicate<LootContext> predicate;

    protected ConditionalLootFunction(class_5341[] args) {
        this.conditions = args;
        this.predicate = LootConditions.joinAnd(args);
    }

    @Override
    public final ItemStack apply(ItemStack arg, LootContext arg2) {
        return this.predicate.test(arg2) ? this.process(arg, arg2) : arg;
    }

    protected abstract ItemStack process(ItemStack var1, LootContext var2);

    @Override
    public void validate(LootTableReporter arg) {
        LootFunction.super.validate(arg);
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(arg.makeChild(".conditions[" + i + "]"));
        }
    }

    protected static Builder<?> builder(Function<class_5341[], LootFunction> function) {
        return new Joiner(function);
    }

    @Override
    public /* synthetic */ Object apply(Object object, Object object2) {
        return this.apply((ItemStack)object, (LootContext)object2);
    }

    public static abstract class Factory<T extends ConditionalLootFunction>
    implements class_5335<T> {
        @Override
        public void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty((Object[])((ConditionalLootFunction)arg).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)((ConditionalLootFunction)arg).conditions));
            }
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            class_5341[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new class_5341[0], jsonDeserializationContext, class_5341[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, class_5341[] var3);

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }

    static final class Joiner
    extends Builder<Joiner> {
        private final Function<class_5341[], LootFunction> joiner;

        public Joiner(Function<class_5341[], LootFunction> function) {
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
        private final List<class_5341> conditionList = Lists.newArrayList();

        @Override
        public T conditionally(class_5341.Builder arg) {
            this.conditionList.add(arg.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected abstract T getThisBuilder();

        protected class_5341[] getConditions() {
            return this.conditionList.toArray(new class_5341[0]);
        }

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

