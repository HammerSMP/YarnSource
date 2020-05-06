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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("nether_travel");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        LocationPredicate lv = LocationPredicate.fromJson(jsonObject.get("entered"));
        LocationPredicate lv2 = LocationPredicate.fromJson(jsonObject.get("exited"));
        DistancePredicate lv3 = DistancePredicate.fromJson(jsonObject.get("distance"));
        return new Conditions(arg, lv, lv2, lv3);
    }

    public void trigger(ServerPlayerEntity arg, Vec3d arg2) {
        this.test(arg, arg3 -> arg3.matches(arg.getServerWorld(), arg2, arg.getX(), arg.getY(), arg.getZ()));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate enteredPos;
        private final LocationPredicate exitedPos;
        private final DistancePredicate distance;

        public Conditions(EntityPredicate.Extended arg, LocationPredicate arg2, LocationPredicate arg3, DistancePredicate arg4) {
            super(ID, arg);
            this.enteredPos = arg2;
            this.exitedPos = arg3;
            this.distance = arg4;
        }

        public static Conditions distance(DistancePredicate arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, LocationPredicate.ANY, LocationPredicate.ANY, arg);
        }

        public boolean matches(ServerWorld arg, Vec3d arg2, double d, double e, double f) {
            if (!this.enteredPos.test(arg, arg2.x, arg2.y, arg2.z)) {
                return false;
            }
            if (!this.exitedPos.test(arg, d, e, f)) {
                return false;
            }
            return this.distance.test(arg2.x, arg2.y, arg2.z, d, e, f);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("entered", this.enteredPos.toJson());
            jsonObject.add("exited", this.exitedPos.toJson());
            jsonObject.add("distance", this.distance.toJson());
            return jsonObject;
        }
    }
}

