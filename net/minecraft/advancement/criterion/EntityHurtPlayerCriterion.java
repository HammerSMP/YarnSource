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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("entity_hurt_player");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        DamagePredicate lv = DamagePredicate.fromJson(jsonObject.get("damage"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
        this.test(player, arg3 -> arg3.matches(player, source, dealt, taken, blocked));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DamagePredicate damage;

        public Conditions(EntityPredicate.Extended player, DamagePredicate damage) {
            super(ID, player);
            this.damage = damage;
        }

        public static Conditions create(DamagePredicate.Builder damageBuilder) {
            return new Conditions(EntityPredicate.Extended.EMPTY, damageBuilder.build());
        }

        public boolean matches(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
            return this.damage.test(player, source, dealt, taken, blocked);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("damage", this.damage.toJson());
            return jsonObject;
        }
    }
}

