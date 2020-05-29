/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.class_5335;
import net.minecraft.class_5341;
import net.minecraft.class_5342;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonHelper;

public class RandomChanceWithLootingLootCondition
implements class_5341 {
    private final float chance;
    private final float lootingMultiplier;

    private RandomChanceWithLootingLootCondition(float f, float g) {
        this.chance = f;
        this.lootingMultiplier = g;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.RANDOM_CHANCE_WITH_LOOTING;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    @Override
    public boolean test(LootContext arg) {
        Entity lv = arg.get(LootContextParameters.KILLER_ENTITY);
        int i = 0;
        if (lv instanceof LivingEntity) {
            i = EnchantmentHelper.getLooting((LivingEntity)lv);
        }
        return arg.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
    }

    public static class_5341.Builder builder(float f, float g) {
        return () -> new RandomChanceWithLootingLootCondition(f, g);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<RandomChanceWithLootingLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, RandomChanceWithLootingLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", (Number)Float.valueOf(arg.chance));
            jsonObject.addProperty("looting_multiplier", (Number)Float.valueOf(arg.lootingMultiplier));
        }

        @Override
        public RandomChanceWithLootingLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceWithLootingLootCondition(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "looting_multiplier"));
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

