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
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    private final LootPoolEntry[] entries;
    private final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    private final LootTableRange rolls;
    private final UniformLootTableRange bonusRolls;

    private LootPool(LootPoolEntry[] entries, LootCondition[] conditions, LootFunction[] functions, LootTableRange rolls, UniformLootTableRange bonusRolls) {
        this.entries = entries;
        this.conditions = conditions;
        this.predicate = LootConditionTypes.joinAnd(conditions);
        this.functions = functions;
        this.javaFunctions = LootFunctionTypes.join(functions);
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
    }

    private void supplyOnce(Consumer<ItemStack> lootConsumer, LootContext context) {
        Random random = context.getRandom();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        for (LootPoolEntry lv : this.entries) {
            lv.expand(context, choice -> {
                int i = choice.getWeight(context.getLuck());
                if (i > 0) {
                    list.add(choice);
                    mutableInt.add(i);
                }
            });
        }
        int i = list.size();
        if (mutableInt.intValue() == 0 || i == 0) {
            return;
        }
        if (i == 1) {
            ((LootChoice)list.get(0)).generateLoot(lootConsumer, context);
            return;
        }
        int j = random.nextInt(mutableInt.intValue());
        for (LootChoice lv2 : list) {
            if ((j -= lv2.getWeight(context.getLuck())) >= 0) continue;
            lv2.generateLoot(lootConsumer, context);
            return;
        }
    }

    public void addGeneratedLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        if (!this.predicate.test(context)) {
            return;
        }
        Consumer<ItemStack> consumer2 = LootFunction.apply(this.javaFunctions, lootConsumer, context);
        Random random = context.getRandom();
        int i = this.rolls.next(random) + MathHelper.floor(this.bonusRolls.nextFloat(random) * context.getLuck());
        for (int j = 0; j < i; ++j) {
            this.supplyOnce(consumer2, context);
        }
    }

    public void validate(LootTableReporter arg) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(arg.makeChild(".condition[" + i + "]"));
        }
        for (int j = 0; j < this.functions.length; ++j) {
            this.functions[j].validate(arg.makeChild(".functions[" + j + "]"));
        }
        for (int k = 0; k < this.entries.length; ++k) {
            this.entries[k].validate(arg.makeChild(".entries[" + k + "]"));
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
            LootPoolEntry[] lvs = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootPoolEntry[].class);
            LootCondition[] lvs2 = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            LootFunction[] lvs3 = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            LootTableRange lv = LootTableRanges.fromJson(jsonObject.get("rolls"), jsonDeserializationContext);
            UniformLootTableRange lv2 = JsonHelper.deserialize(jsonObject, "bonus_rolls", new UniformLootTableRange(0.0f, 0.0f), jsonDeserializationContext, UniformLootTableRange.class);
            return new LootPool(lvs, lvs2, lvs3, lv, lv2);
        }

        public JsonElement serialize(LootPool arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rolls", LootTableRanges.toJson(arg.rolls, jsonSerializationContext));
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)arg.entries));
            if (arg.bonusRolls.getMinValue() != 0.0f && arg.bonusRolls.getMaxValue() != 0.0f) {
                jsonObject.add("bonus_rolls", jsonSerializationContext.serialize((Object)arg.bonusRolls));
            }
            if (!ArrayUtils.isEmpty((Object[])arg.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)arg.conditions));
            }
            if (!ArrayUtils.isEmpty((Object[])arg.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)arg.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((LootPool)entry, unused, context);
        }

        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder>,
    LootConditionConsumingBuilder<Builder> {
        private final List<LootPoolEntry> entries = Lists.newArrayList();
        private final List<LootCondition> conditions = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootTableRange rolls = new UniformLootTableRange(1.0f);
        private UniformLootTableRange bonusRollsRange = new UniformLootTableRange(0.0f, 0.0f);

        public Builder rolls(LootTableRange rolls) {
            this.rolls = rolls;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public Builder with(LootPoolEntry.Builder<?> entry) {
            this.entries.add(entry.build());
            return this;
        }

        @Override
        public Builder conditionally(LootCondition.Builder arg) {
            this.conditions.add(arg.build());
            return this;
        }

        @Override
        public Builder apply(LootFunction.Builder arg) {
            this.functions.add(arg.build());
            return this;
        }

        public LootPool build() {
            if (this.rolls == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool(this.entries.toArray(new LootPoolEntry[0]), this.conditions.toArray(new LootCondition[0]), this.functions.toArray(new LootFunction[0]), this.rolls, this.bonusRollsRange);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object apply(LootFunction.Builder function) {
            return this.apply(function);
        }

        @Override
        public /* synthetic */ Object conditionally(LootCondition.Builder condition) {
            return this.conditionally(condition);
        }
    }
}

