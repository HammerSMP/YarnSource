/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    private final LootEntry[] entries;
    private final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    private final LootTableRange rollsRange;
    private final UniformLootTableRange bonusRollsRange;

    private LootPool(LootEntry[] args, LootCondition[] args2, LootFunction[] args3, LootTableRange arg, UniformLootTableRange arg2) {
        this.entries = args;
        this.conditions = args2;
        this.predicate = LootConditions.joinAnd(args2);
        this.functions = args3;
        this.javaFunctions = LootFunctions.join(args3);
        this.rollsRange = arg;
        this.bonusRollsRange = arg2;
    }

    private void supplyOnce(Consumer<ItemStack> consumer, LootContext arg) {
        Random random = arg.getRandom();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        for (LootEntry lv : this.entries) {
            lv.expand(arg, arg2 -> {
                int i = arg2.getWeight(arg.getLuck());
                if (i > 0) {
                    list.add(arg2);
                    mutableInt.add(i);
                }
            });
        }
        int i = list.size();
        if (mutableInt.intValue() == 0 || i == 0) {
            return;
        }
        if (i == 1) {
            ((LootChoice)list.get(0)).drop(consumer, arg);
            return;
        }
        int j = random.nextInt(mutableInt.intValue());
        for (LootChoice lv2 : list) {
            if ((j -= lv2.getWeight(arg.getLuck())) >= 0) continue;
            lv2.drop(consumer, arg);
            return;
        }
    }

    public void drop(Consumer<ItemStack> consumer, LootContext arg) {
        if (!this.predicate.test(arg)) {
            return;
        }
        Consumer<ItemStack> consumer2 = LootFunction.apply(this.javaFunctions, consumer, arg);
        Random random = arg.getRandom();
        int i = this.rollsRange.next(random) + MathHelper.floor(this.bonusRollsRange.nextFloat(random) * arg.getLuck());
        for (int j = 0; j < i; ++j) {
            this.supplyOnce(consumer2, arg);
        }
    }

    public void check(LootTableReporter arg) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(arg.makeChild(".condition[" + i + "]"));
        }
        for (int j = 0; j < this.functions.length; ++j) {
            this.functions[j].check(arg.makeChild(".functions[" + j + "]"));
        }
        for (int k = 0; k < this.entries.length; ++k) {
            this.entries[k].check(arg.makeChild(".entries[" + k + "]"));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer
    implements JsonDeserializer<LootPool>,
    JsonSerializer<LootPool> {
        public LootPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
            LootEntry[] lvs = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootEntry[].class);
            LootCondition[] lvs2 = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            LootFunction[] lvs3 = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            LootTableRange lv = LootTableRanges.fromJson(jsonObject.get("rolls"), jsonDeserializationContext);
            UniformLootTableRange lv2 = JsonHelper.deserialize(jsonObject, "bonus_rolls", new UniformLootTableRange(0.0f, 0.0f), jsonDeserializationContext, UniformLootTableRange.class);
            return new LootPool(lvs, lvs2, lvs3, lv, lv2);
        }

        public JsonElement serialize(LootPool arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rolls", LootTableRanges.toJson(arg.rollsRange, jsonSerializationContext));
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)arg.entries));
            if (arg.bonusRollsRange.getMinValue() != 0.0f && arg.bonusRollsRange.getMaxValue() != 0.0f) {
                jsonObject.add("bonus_rolls", jsonSerializationContext.serialize((Object)arg.bonusRollsRange));
            }
            if (!ArrayUtils.isEmpty((Object[])arg.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)arg.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])arg.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)arg.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootPool)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder>,
    LootConditionConsumingBuilder<Builder> {
        private final List<LootEntry> entries = Lists.newArrayList();
        private final List<LootCondition> conditions = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootTableRange rollsRange = new UniformLootTableRange(1.0f);
        private UniformLootTableRange bonusRollsRange = new UniformLootTableRange(0.0f, 0.0f);

        public Builder withRolls(LootTableRange arg) {
            this.rollsRange = arg;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public Builder withEntry(LootEntry.Builder<?> arg) {
            this.entries.add(arg.build());
            return this;
        }

        @Override
        public Builder withCondition(LootCondition.Builder arg) {
            this.conditions.add(arg.build());
            return this;
        }

        @Override
        public Builder withFunction(LootFunction.Builder arg) {
            this.functions.add(arg.build());
            return this;
        }

        public LootPool build() {
            if (this.rollsRange == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool(this.entries.toArray(new LootEntry[0]), this.conditions.toArray(new LootCondition[0]), this.functions.toArray(new LootFunction[0]), this.rollsRange, this.bonusRollsRange);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object withFunction(LootFunction.Builder arg) {
            return this.withFunction(arg);
        }

        @Override
        public /* synthetic */ Object withCondition(LootCondition.Builder arg) {
            return this.withCondition(arg);
        }
    }
}

