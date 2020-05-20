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
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LootTable EMPTY = new LootTable(LootContextTypes.EMPTY, new LootPool[0], new LootFunction[0]);
    public static final LootContextType GENERIC = LootContextTypes.GENERIC;
    private final LootContextType type;
    private final LootPool[] pools;
    private final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunction;

    private LootTable(LootContextType arg, LootPool[] args, LootFunction[] args2) {
        this.type = arg;
        this.pools = args;
        this.functions = args2;
        this.combinedFunction = LootFunctions.join(args2);
    }

    public static Consumer<ItemStack> processStacks(Consumer<ItemStack> consumer) {
        return arg -> {
            if (arg.getCount() < arg.getMaxCount()) {
                consumer.accept((ItemStack)arg);
            } else {
                ItemStack lv;
                for (int i = arg.getCount(); i > 0; i -= lv.getCount()) {
                    lv = arg.copy();
                    lv.setCount(Math.min(arg.getMaxCount(), i));
                    consumer.accept(lv);
                }
            }
        };
    }

    public void generateUnprocessedLoot(LootContext arg, Consumer<ItemStack> consumer) {
        if (arg.markActive(this)) {
            Consumer<ItemStack> consumer2 = LootFunction.apply(this.combinedFunction, consumer, arg);
            for (LootPool lv : this.pools) {
                lv.addGeneratedLoot(consumer2, arg);
            }
            arg.markInactive(this);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
    }

    public void generateLoot(LootContext arg, Consumer<ItemStack> consumer) {
        this.generateUnprocessedLoot(arg, LootTable.processStacks(consumer));
    }

    public List<ItemStack> generateLoot(LootContext arg) {
        ArrayList list = Lists.newArrayList();
        this.generateLoot(arg, list::add);
        return list;
    }

    public LootContextType getType() {
        return this.type;
    }

    public void validate(LootTableReporter arg) {
        for (int i = 0; i < this.pools.length; ++i) {
            this.pools[i].validate(arg.makeChild(".pools[" + i + "]"));
        }
        for (int j = 0; j < this.functions.length; ++j) {
            this.functions[j].validate(arg.makeChild(".functions[" + j + "]"));
        }
    }

    public void supplyInventory(Inventory arg, LootContext arg2) {
        List<ItemStack> list = this.generateLoot(arg2);
        Random random = arg2.getRandom();
        List<Integer> list2 = this.getFreeSlots(arg, random);
        this.shuffle(list, list2.size(), random);
        for (ItemStack lv : list) {
            if (list2.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (lv.isEmpty()) {
                arg.setStack(list2.remove(list2.size() - 1), ItemStack.EMPTY);
                continue;
            }
            arg.setStack(list2.remove(list2.size() - 1), lv);
        }
    }

    private void shuffle(List<ItemStack> list, int i, Random random) {
        ArrayList list2 = Lists.newArrayList();
        Iterator<ItemStack> iterator = list.iterator();
        while (iterator.hasNext()) {
            ItemStack lv = iterator.next();
            if (lv.isEmpty()) {
                iterator.remove();
                continue;
            }
            if (lv.getCount() <= 1) continue;
            list2.add(lv);
            iterator.remove();
        }
        while (i - list.size() - list2.size() > 0 && !list2.isEmpty()) {
            ItemStack lv2 = (ItemStack)list2.remove(MathHelper.nextInt(random, 0, list2.size() - 1));
            int j = MathHelper.nextInt(random, 1, lv2.getCount() / 2);
            ItemStack lv3 = lv2.split(j);
            if (lv2.getCount() > 1 && random.nextBoolean()) {
                list2.add(lv2);
            } else {
                list.add(lv2);
            }
            if (lv3.getCount() > 1 && random.nextBoolean()) {
                list2.add(lv3);
                continue;
            }
            list.add(lv3);
        }
        list.addAll(list2);
        Collections.shuffle(list, random);
    }

    private List<Integer> getFreeSlots(Inventory arg, Random random) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg.size(); ++i) {
            if (!arg.getStack(i).isEmpty()) continue;
            list.add(i);
        }
        Collections.shuffle(list, random);
        return list;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Serializer
    implements JsonDeserializer<LootTable>,
    JsonSerializer<LootTable> {
        public LootTable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot table");
            LootPool[] lvs = JsonHelper.deserialize(jsonObject, "pools", new LootPool[0], jsonDeserializationContext, LootPool[].class);
            LootContextType lv = null;
            if (jsonObject.has("type")) {
                String string = JsonHelper.getString(jsonObject, "type");
                lv = LootContextTypes.get(new Identifier(string));
            }
            LootFunction[] lvs2 = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            return new LootTable(lv != null ? lv : LootContextTypes.GENERIC, lvs, lvs2);
        }

        public JsonElement serialize(LootTable arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (arg.type != GENERIC) {
                Identifier lv = LootContextTypes.getId(arg.type);
                if (lv != null) {
                    jsonObject.addProperty("type", lv.toString());
                } else {
                    LOGGER.warn("Failed to find id for param set " + arg.type);
                }
            }
            if (arg.pools.length > 0) {
                jsonObject.add("pools", jsonSerializationContext.serialize((Object)arg.pools));
            }
            if (!ArrayUtils.isEmpty((Object[])arg.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize((Object)arg.functions));
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootTable)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder> {
        private final List<LootPool> pools = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootContextType type = GENERIC;

        public Builder pool(LootPool.Builder arg) {
            this.pools.add(arg.build());
            return this;
        }

        public Builder type(LootContextType arg) {
            this.type = arg;
            return this;
        }

        @Override
        public Builder apply(LootFunction.Builder arg) {
            this.functions.add(arg.build());
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public LootTable build() {
            return new LootTable(this.type, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootFunction[0]));
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object apply(LootFunction.Builder arg) {
            return this.apply(arg);
        }
    }
}

