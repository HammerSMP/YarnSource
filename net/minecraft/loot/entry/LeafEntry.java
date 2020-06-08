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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LeafEntry
extends LootPoolEntry {
    protected final int weight;
    protected final int quality;
    protected final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
    private final LootChoice choice = new Choice(){

        @Override
        public void generateLoot(Consumer<ItemStack> consumer, LootContext arg) {
            LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, consumer, arg), arg);
        }
    };

    protected LeafEntry(int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(args);
        this.weight = i;
        this.quality = j;
        this.functions = args2;
        this.compiledFunctions = LootFunctionTypes.join(args2);
    }

    @Override
    public void validate(LootTableReporter arg) {
        super.validate(arg);
        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(arg.makeChild(".functions[" + i + "]"));
        }
    }

    protected abstract void generateLoot(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext arg, Consumer<LootChoice> consumer) {
        if (this.test(arg)) {
            consumer.accept(this.choice);
            return true;
        }
        return false;
    }

    public static Builder<?> builder(Factory arg) {
        return new BasicBuilder(arg);
    }

    public static abstract class Serializer<T extends LeafEntry>
    extends LootPoolEntry.Serializer<T> {
        @Override
        public void addEntryFields(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            if (((LeafEntry)arg).weight != 1) {
                jsonObject.addProperty("weight", (Number)((LeafEntry)arg).weight);
            }
            if (((LeafEntry)arg).quality != 0) {
                jsonObject.addProperty("quality", (Number)((LeafEntry)arg).quality);
            }
            if (!ArrayUtils.isEmpty((Object[])((LeafEntry)arg).functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)((LeafEntry)arg).functions));
            }
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            int i = JsonHelper.getInt(jsonObject, "weight", 1);
            int j = JsonHelper.getInt(jsonObject, "quality", 0);
            LootFunction[] lvs = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, lvs);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootCondition[] var5, LootFunction[] var6);

        @Override
        public /* synthetic */ LootPoolEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    static class BasicBuilder
    extends Builder<BasicBuilder> {
        private final Factory factory;

        public BasicBuilder(Factory arg) {
            this.factory = arg;
        }

        @Override
        protected BasicBuilder getThisBuilder() {
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    @FunctionalInterface
    public static interface Factory {
        public LeafEntry build(int var1, int var2, LootCondition[] var3, LootFunction[] var4);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntry.Builder<T>
    implements LootFunctionConsumingBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootFunction> functions = Lists.newArrayList();

        @Override
        public T apply(LootFunction.Builder arg) {
            this.functions.add(arg.build());
            return (T)((Builder)this.getThisBuilder());
        }

        protected LootFunction[] getFunctions() {
            return this.functions.toArray(new LootFunction[0]);
        }

        public T weight(int i) {
            this.weight = i;
            return (T)((Builder)this.getThisBuilder());
        }

        public T quality(int i) {
            this.quality = i;
            return (T)((Builder)this.getThisBuilder());
        }

        @Override
        public /* synthetic */ Object apply(LootFunction.Builder arg) {
            return this.apply(arg);
        }
    }

    public abstract class Choice
    implements LootChoice {
        protected Choice() {
        }

        @Override
        public int getWeight(float f) {
            return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * f), 0);
        }
    }
}

