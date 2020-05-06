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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("levitation");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        DistancePredicate lv = DistancePredicate.fromJson(jsonObject.get("distance"));
        NumberRange.IntRange lv2 = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, Vec3d arg2, int i) {
        this.test(arg, arg3 -> arg3.matches(arg, arg2, i));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DistancePredicate distance;
        private final NumberRange.IntRange duration;

        public Conditions(EntityPredicate.Extended arg, DistancePredicate arg2, NumberRange.IntRange arg3) {
            super(ID, arg);
            this.distance = arg2;
            this.duration = arg3;
        }

        public static Conditions create(DistancePredicate arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, NumberRange.IntRange.ANY);
        }

        public boolean matches(ServerPlayerEntity arg, Vec3d arg2, int i) {
            if (!this.distance.test(arg2.x, arg2.y, arg2.z, arg.getX(), arg.getY(), arg.getZ())) {
                return false;
            }
            return this.duration.test(i);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("distance", this.distance.toJson());
            jsonObject.add("duration", this.duration.toJson());
            return jsonObject;
        }
    }
}

