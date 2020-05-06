/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class TableBonusLootCondition
implements LootCondition {
    private final Enchantment enchantment;
    private final float[] chances;

    private TableBonusLootCondition(Enchantment arg, float[] fs) {
        this.enchantment = arg;
        this.chances = fs;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext arg) {
        ItemStack lv = arg.get(LootContextParameters.TOOL);
        int i = lv != null ? EnchantmentHelper.getLevel(this.enchantment, lv) : 0;
        float f = this.chances[Math.min(i, this.chances.length - 1)];
        return arg.getRandom().nextFloat() < f;
    }

    public static LootCondition.Builder builder(Enchantment arg, float ... fs) {
        return () -> new TableBonusLootCondition(arg, fs);
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<TableBonusLootCondition> {
        public Factory() {
            super(new Identifier("table_bonus"), TableBonusLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, TableBonusLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(arg.enchantment).toString());
            jsonObject.add("chances", jsonSerializationContext.serialize((Object)arg.chances));
        }

        @Override
        public TableBonusLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
            Enchantment lv2 = Registry.ENCHANTMENT.getOrEmpty(lv).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + lv));
            float[] fs = JsonHelper.deserialize(jsonObject, "chances", jsonDeserializationContext, float[].class);
            return new TableBonusLootCondition(lv2, fs);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

