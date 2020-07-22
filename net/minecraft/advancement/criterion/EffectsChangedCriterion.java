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
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("effects_changed");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        EntityEffectPredicate lv = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity player) {
        this.test(player, arg2 -> arg2.matches(player));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityEffectPredicate effects;

        public Conditions(EntityPredicate.Extended player, EntityEffectPredicate effects) {
            super(ID, player);
            this.effects = effects;
        }

        public static Conditions create(EntityEffectPredicate effects) {
            return new Conditions(EntityPredicate.Extended.EMPTY, effects);
        }

        public boolean matches(ServerPlayerEntity player) {
            return this.effects.test(player);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("effects", this.effects.toJson());
            return jsonObject;
        }
    }
}

