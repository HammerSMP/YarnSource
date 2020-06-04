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

    private ApplyBonusLootFunction(LootCondition[] args, Enchantment arg, Formula arg2) {
        super(args);
        this.enchantment = arg;
        this.formula = arg2;
    }

    @Override
    public LootFunctionType method_29321() {
        return LootFunctionTypes.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        ItemStack lv = arg2.get(LootContextParameters.TOOL);
        if (lv != null) {
            int i = EnchantmentHelper.getLevel(this.enchantment, lv);
            int j = this.formula.getValue(arg2.getRandom(), arg.getCount(), i);
            arg.setCount(j);
        }
        return arg;
    }

    public static ConditionalLootFunction.Builder<?> binomialWithBonusCount(Enchantment arg, float f, int i) {
        return ApplyBonusLootFunction.builder(args -> new ApplyBonusLootFunction((LootCondition[])args, arg, new BinomialWithBonusCount(i, f)));
    }

    public static ConditionalLootFunction.Builder<?> oreDrops(Enchantment arg) {
        return ApplyBonusLootFunction.builder(args -> new ApplyBonusLootFunction((LootCondition[])args, arg, new OreDrops()));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment arg) {
        return ApplyBonusLootFunction.builder(args -> new ApplyBonusLootFunction((LootCondition[])args, arg, new UniformBonusCount(1)));
    }

    public static ConditionalLootFunction.Builder<?> uniformBonusCount(Enchantment arg, int i) {
        return ApplyBonusLootFunction.builder(args -> new ApplyBonusLootFunction((LootCondition[])args, arg, new UniformBonusCount(i)));
    }

    static {
        FACTORIES.put(BinomialWithBonusCount.ID, BinomialWithBonusCount::fromJson);
        FACTORIES.put(OreDrops.ID, OreDrops::fromJson);
        FACTORIES.put(UniformBonusCount.ID, UniformBonusCount::fromJson);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<ApplyBonusLootFunction> {
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
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    static final class OreDrops
    implements Formula {
        public static final Identifier ID = new Identifier("ore_drops");

        private OreDrops() {
        }

        @Override
        public int getValue(Random random, int i, int j) {
            if (j > 0) {
                int k = random.nextInt(j + 2) - 1;
                if (k < 0) {
                    k = 0;
                }
                return i * (k + 1);
            }
            return i;
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
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

        public UniformBonusCount(int i) {
            this.bonusMultiplier = i;
        }

        @Override
        public int getValue(Random random, int i, int j) {
            return i + random.nextInt(this.bonusMultiplier * j + 1);
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("bonusMultiplier", (Number)this.bonusMultiplier);
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int i = JsonHelper.getInt(jsonObject, "bonusMultiplier");
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

        public BinomialWithBonusCount(int i, float f) {
            this.extra = i;
            this.probability = f;
        }

        @Override
        public int getValue(Random random, int i, int j) {
            for (int k = 0; k < j + this.extra; ++k) {
                if (!(random.nextFloat() < this.probability)) continue;
                ++i;
            }
            return i;
        }

        @Override
        public void toJson(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("extra", (Number)this.extra);
            jsonObject.addProperty("probability", (Number)Float.valueOf(this.probability));
        }

        public static Formula fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int i = JsonHelper.getInt(jsonObject, "extra");
            float f = JsonHelper.getFloat(jsonObject, "probability");
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

