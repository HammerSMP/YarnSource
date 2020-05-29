/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.class_5339;
import net.minecraft.class_5341;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTableRanges;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.JsonHelper;

public class EnchantWithLevelsLootFunction
extends ConditionalLootFunction {
    private final LootTableRange range;
    private final boolean treasureEnchantmentsAllowed;

    private EnchantWithLevelsLootFunction(class_5341[] args, LootTableRange arg, boolean bl) {
        super(args);
        this.range = arg;
        this.treasureEnchantmentsAllowed = bl;
    }

    @Override
    public class_5339 method_29321() {
        return LootFunctions.ENCHANT_WITH_LEVELS;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Random random = arg2.getRandom();
        return EnchantmentHelper.enchant(random, arg, this.range.next(random), this.treasureEnchantmentsAllowed);
    }

    public static Builder builder(LootTableRange arg) {
        return new Builder(arg);
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<EnchantWithLevelsLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, EnchantWithLevelsLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.add("levels", LootTableRanges.toJson(arg.range, jsonSerializationContext));
            jsonObject.addProperty("treasure", Boolean.valueOf(arg.treasureEnchantmentsAllowed));
        }

        @Override
        public EnchantWithLevelsLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            LootTableRange lv = LootTableRanges.fromJson(jsonObject.get("levels"), jsonDeserializationContext);
            boolean bl = JsonHelper.getBoolean(jsonObject, "treasure", false);
            return new EnchantWithLevelsLootFunction(args, lv, bl);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private final LootTableRange range;
        private boolean treasureEnchantmentsAllowed;

        public Builder(LootTableRange arg) {
            this.range = arg;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder allowTreasureEnchantments() {
            this.treasureEnchantmentsAllowed = true;
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantWithLevelsLootFunction(this.getConditions(), this.range, this.treasureEnchantmentsAllowed);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

