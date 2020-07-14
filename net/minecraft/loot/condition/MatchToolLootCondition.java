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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.JsonSerializer;

public class MatchToolLootCondition
implements LootCondition {
    private final ItemPredicate predicate;

    public MatchToolLootCondition(ItemPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public LootConditionType getType() {
        return LootConditionTypes.MATCH_TOOL;
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

    public static LootCondition.Builder builder(ItemPredicate.Builder predicate) {
        return () -> new MatchToolLootCondition(predicate.build());
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((LootContext)context);
    }

    public static class Serializer
    implements JsonSerializer<MatchToolLootCondition> {
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
        public /* synthetic */ Object fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }
}

