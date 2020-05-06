/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.util.Identifier;

public class ImpossibleCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("impossible");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker arg, Criterion.ConditionsContainer<Conditions> arg2) {
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker arg, Criterion.ConditionsContainer<Conditions> arg2) {
    }

    @Override
    public void endTracking(PlayerAdvancementTracker arg) {
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        return new Conditions();
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        return this.conditionsFromJson(jsonObject, arg);
    }

    public static class Conditions
    implements CriterionConditions {
        @Override
        public Identifier getId() {
            return ID;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            return new JsonObject();
        }
    }
}

