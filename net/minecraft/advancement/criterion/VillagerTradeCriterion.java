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

    public void handle(ServerPlayerEntity arg, AbstractTraderEntity arg2, ItemStack arg32) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        this.test(arg, arg3 -> arg3.matches(lv, arg32));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended villager;
        private final ItemPredicate item;

        public Conditions(EntityPredicate.Extended arg, EntityPredicate.Extended arg2, ItemPredicate arg3) {
            super(ID, arg);
            this.villager = arg2;
            this.item = arg3;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, ItemPredicate.ANY);
        }

        public boolean matches(LootContext arg, ItemStack arg2) {
            if (!this.villager.test(arg)) {
                return false;
            }
            return this.item.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("villager", this.villager.toJson(arg));
            return jsonObject;
        }
    }
}

