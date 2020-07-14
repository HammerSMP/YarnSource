/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("villager_trade");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended lv = EntityPredicate.Extended.getInJson(jsonObject, "villager", arg2);
        ItemPredicate lv2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(arg, lv, lv2);
    }

    public void handle(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(player, trader);
        this.test(player, arg3 -> arg3.matches(lv, stack));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended villager;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended villager, ItemPredicate item) {
            super(ID, player);
            this.villager = villager;
            this.item = item;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext traderContext, ItemStack stack) {
            if (!this.villager.test(traderContext)) {
                return false;
            }
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("villager", this.villager.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

