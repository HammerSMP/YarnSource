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

    public void test(ServerPlayerEntity player, ItemStack stack, Entity entity) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(player, entity);
        this.test(player, arg3 -> arg3.test(stack, lv));
    }

    @Override
    protected /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final ItemPredicate item;
        private final EntityPredicate.Extended entity;

        public Conditions(EntityPredicate.Extended player, ItemPredicate item, EntityPredicate.Extended entity) {
            super(ID, player);
            this.item = item;
            this.entity = entity;
        }

        public static Conditions create(EntityPredicate.Extended player, ItemPredicate.Builder itemBuilder, EntityPredicate.Extended entity) {
            return new Conditions(player, itemBuilder.build(), entity);
        }

        public boolean test(ItemStack stack, LootContext context) {
            if (!this.item.test(stack)) {
                return false;
            }
            return this.entity.test(context);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("entity", this.entity.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

