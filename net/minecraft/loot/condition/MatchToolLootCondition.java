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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;

public class MatchToolLootCondition
implements class_5341 {
    private final ItemPredicate predicate;

    public MatchToolLootCondition(ItemPredicate arg) {
        this.predicate = arg;
    }

    @Override
    public class_5342 method_29325() {
        return LootConditions.MATCH_TOOL;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext arg) {
        ItemStack lv = arg.get(LootContextParameters.TOOL);
        return lv != null && this.predicate.test(lv);
    }

    public static class_5341.Builder builder(ItemPredicate.Builder arg) {
        return () -> new MatchToolLootCondition(arg.build());
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    implements class_5335<MatchToolLootCondition> {
        @Override
        public void toJson(JsonObject jsonObject, MatchToolLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", arg.predicate.toJson());
        }

        @Override
        public MatchToolLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("predicate"));
            return new MatchToolLootCondition(lv);
        }

        @Override
        public /* synthetic */ Object fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

