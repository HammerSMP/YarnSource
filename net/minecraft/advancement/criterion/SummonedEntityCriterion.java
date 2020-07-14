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
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("summoned_entity");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityPredicate.Extended lv = EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2);
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity player, Entity entity) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(player, entity);
        this.test(player, arg2 -> arg2.matches(lv));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended entity;

        public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended entity) {
            super(ID, player);
            this.entity = entity;
        }

        public static Conditions create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
            return new Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(summonedEntityPredicateBuilder.build()));
        }

        public boolean matches(LootContext summonedEntityContext) {
            return this.entity.test(summonedEntityContext);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("entity", this.entity.toJson(predicateSerializer));
            return jsonObject;
        }
    }
}

