/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ApplyBonusLootFunction
extends ConditionalLootFunction {
    private static final Map<Identifier, FormulaFactory> FACTORIES = Maps.newHashMap();
    private final Enchantment enchantment;
    private final Formula formula;

    private ApplyBonusLootFunction(LootCondition[] conditions, Enchantment enchantment, Formula formula) {
        super(conditions);
        this.enchantment = enchantment;
        this.formula = formula;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ItemStack lv = context.get(LootContextParameters.TOOL);
        if (lv != null) {
            int i = EnchantmentHelper.getLevel(this.enchantment, lv);
            int j = this.formula.getValue(context.getRandom(), stack.getCount(), i);
            stack.setCount(j);
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> binomialWithBonusCount(Enchantment enchantment, float probability, int extra) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new BinomialWithBonusCount(extra, probability)));
    }

    public static ConditionalLootFunction.Builder<?> oreDrops(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new OreDrops()));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new UniformBonusCount(1)));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment enchantment, int bonusMultiplier) {
        return ApplyBonusLootFunction.builder(conditions -> new ApplyBonusLootFunction((LootCondition[])conditions, enchantment, new UniformBonusCount(bonusMultiplier)));
    }

    static {
        FACTORIES.put(BinomialWithBonusCount.ID, BinomialWithBonusCount::fromJson);
        FACTORIES.put(OreDrops.ID, OreDrops::fromJson);
        FACTORIES.put(UniformBonusCount.ID, UniformBonusCount::fromJson);
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<ApplyBonusLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, ApplyBonusLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(arg.enchantment).toString());
            jsonObject.addProperty("formula", arg.formula.getId().toString());
            JsonObject jsonObject2 = new JsonObject();
            arg.formula.toJson(jsonObject2, jsonSerializationContext);
            if (jsonObject2.size() > 0) {
                jsonObject.add("parameters", (JsonElement)jsonObject2);
            }
        }

        @Override
        public ApplyBonusLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            Formula lv6;
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
            Enchantment lv2 = Registry.ENCHANTMENT.getOrEmpty(lv).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + lv));
            Identifier lv3 = new Identifier(JsonHelper.getString(jsonObject, "formula"));
            FormulaFactory lv4 = (FormulaFactory)FACTORIES.get(lv3);
            if (lv4 == null) {
                throw new JsonParseException("Invalid formula id: " + lv3);
            }
            if (jsonObject.has("parameters")) {
                Formula lv5 = lv4.deserialize(JsonHelper.getObject(jsonObject, "parameters"), jsonDeserializationContext);
            } else {
                lv6 = lv4.deserialize(new JsonObject(), jsonDeserializationContext);
            }
            return new ApplyBonusLootFunction(args, lv2, lv6);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    static final class OreDrops
    implements Formula {
        public static final Identifier ID = new Identifier("ore_drops");

        private OreDrops() {
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            if (enchantmentLevel > 0) {
                int k = random.nextInt(enchantmentLevel + 2) - 1;
                if (k < 0) {
                    k = 0;
                }
                return initialCount * (k + 1);
            }
            return initialCount;
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            return new OreDrops();
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static final class UniformBonusCount
    implements Formula {
        public static final Identifier ID = new Identifier("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int bonusMultiplier) {
            this.bonusMultiplier = bonusMultiplier;
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
            json.addProperty("bonusMultiplier", (Number)this.bonusMultiplier);
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            int i = JsonHelper.getInt(json, "bonusMultiplier");
            return new UniformBonusCount(i);
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static final class BinomialWithBonusCount
    implements Formula {
        public static final Identifier ID = new Identifier("binomial_with_bonus_count");
        private final int extra;
        private final float probability;

        public BinomialWithBonusCount(int extra, float probability) {
            this.extra = extra;
            this.probability = probability;
        }

        @Override
        public int getValue(Random random, int initialCount, int enchantmentLevel) {
            for (int k = 0; k < enchantmentLevel + this.extra; ++k) {
                if (!(random.nextFloat() < this.probability)) continue;
                ++initialCount;
            }
            return initialCount;
        }

        @Override
        public void toJson(JsonObject json, JsonSerializationContext context) {
            json.addProperty("extra", (Number)this.extra);
            json.addProperty("probability", (Number)Float.valueOf(this.probability));
        }

        public static Formula fromJson(JsonObject json, JsonDeserializationContext context) {
            int i = JsonHelper.getInt(json, "extra");
            float f = JsonHelper.getFloat(json, "probability");
            return new BinomialWithBonusCount(i, f);
        }

        @Override
        public Identifier getId() {
            return ID;
        }
    }

    static interface FormulaFactory {
        public Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
    }

    static interface Formula {
        public int getValue(Random var1, int var2, int var3);

        public void toJson(JsonObject var1, JsonSerializationContext var2);

        public Identifier getId();
    }
}

