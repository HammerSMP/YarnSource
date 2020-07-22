/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Enchantment> enchantments;

    private EnchantRandomlyLootFunction(LootCondition[] conditions, Collection<Enchantment> enchantments) {
        super(conditions);
        this.enchantments = ImmutableList.copyOf(enchantments);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Enchantment lv2;
        Random random = context.getRandom();
        if (this.enchantments.isEmpty()) {
            boolean bl = stack.getItem() == Items.BOOK;
            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isAvailableForRandomSelection).filter(arg2 -> bl || arg2.isAcceptableItem(stack)).collect(Collectors.toList());
            if (list.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)stack);
                return stack;
            }
            Enchantment lv = (Enchantment)list.get(random.nextInt(list.size()));
        } else {
            lv2 = this.enchantments.get(random.nextInt(this.enchantments.size()));
        }
        return EnchantRandomlyLootFunction.method_26266(stack, lv2, random);
    }

    private static ItemStack method_26266(ItemStack arg, Enchantment arg2, Random random) {
        int i = MathHelper.nextInt(random, arg2.getMinLevel(), arg2.getMaxLevel());
        if (arg.getItem() == Items.BOOK) {
            arg = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(arg, new EnchantmentLevelEntry(arg2, i));
        } else {
            arg.addEnchantment(arg2, i);
        }
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return EnchantRandomlyLootFunction.builder(conditions -> new EnchantRandomlyLootFunction((LootCondition[])conditions, (Collection<Enchantment>)ImmutableList.of()));
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<EnchantRandomlyLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, EnchantRandomlyLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            if (!arg.enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment lv : arg.enchantments) {
                    Identifier lv2 = Registry.ENCHANTMENT.getId(lv);
                    if (lv2 == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + lv);
                    }
                    jsonArray.add((JsonElement)new JsonPrimitive(lv2.toString()));
                }
                jsonObject.add("enchantments", (JsonElement)jsonArray);
            }
        }

        @Override
        public EnchantRandomlyLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            ArrayList list = Lists.newArrayList();
            if (jsonObject.has("enchantments")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "enchantments");
                for (JsonElement jsonElement : jsonArray) {
                    String string = JsonHelper.asString(jsonElement, "enchantment");
                    Enchantment lv = Registry.ENCHANTMENT.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    list.add(lv);
                }
            }
            return new EnchantRandomlyLootFunction(args, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final Set<Enchantment> enchantments = Sets.newHashSet();

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder add(Enchantment enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), this.enchantments);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

