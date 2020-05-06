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

    public void trigger(ServerPlayerEntity arg) {
        this.test(arg, arg2 -> arg2.matches(arg));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityEffectPredicate effects;

        public Conditions(EntityPredicate.Extended arg, EntityEffectPredicate arg2) {
            super(ID, arg);
            this.effects = arg2;
        }

        public static Conditions create(EntityEffectPredicate arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg);
        }

        public boolean matches(ServerPlayerEntity arg) {
            return this.effects.test(arg);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("effects", this.effects.toJson());
            return jsonObject;
        }
    }
}

