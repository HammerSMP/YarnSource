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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerInteractedWithEntityCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("player_interacted_with_entity");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("item"));
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2);
        return new Conditions(arg, lv, lv2);
    }

    public void test(ServerPlayerEntity arg, ItemStack arg2, Entity arg32) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg32);
        this.test(arg, arg3 -> arg3.test(arg2, lv));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final EntityPredicate.Extended entity;

        public Conditions(EntityPredicate.Extended arg, ItemPredicate arg2, EntityPredicate.Extended arg3) {
            super(ID, arg);
            this.item = arg2;
            this.entity = arg3;
        }

        public static Conditions create(EntityPredicate.Extended arg, ItemPredicate.Builder arg2, EntityPredicate.Extended arg3) {
            return new Conditions(arg, arg2.build(), arg3);
        }

        public boolean test(ItemStack arg, LootContext arg2) {
            if (!this.item.test(arg)) {
                return false;
            }
            return this.entity.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("entity", this.entity.toJson(arg));
            return jsonObject;
        }
    }
}
