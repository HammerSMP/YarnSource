/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LocationArrivalCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public LocationArrivalCriterion(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "location", jsonObject);
        LocationPredicate lv = LocationPredicate.fromJson((JsonElement)jsonObject2);
        return new Conditions(this.id, arg, lv);
    }

    public void trigger(ServerPlayerEntity player) {
        this.test(player, arg2 -> arg2.matches(player.getServerWorld(), player.getX(), player.getY(), player.getZ()));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final LocationPredicate location;

        public Conditions(Identifier id, EntityPredicate.Extended player, LocationPredicate location) {
            super(id, player);
            this.location = location;
        }

        public static Conditions create(LocationPredicate location) {
            return new Conditions(Criteria.LOCATION.id, EntityPredicate.Extended.EMPTY, location);
        }

        public static Conditions createSleptInBed() {
            return new Conditions(Criteria.SLEPT_IN_BED.id, EntityPredicate.Extended.EMPTY, LocationPredicate.ANY);
        }

        public static Conditions createHeroOfTheVillage() {
            return new Conditions(Criteria.HERO_OF_THE_VILLAGE.id, EntityPredicate.Extended.EMPTY, LocationPredicate.ANY);
        }

        public boolean matches(ServerWorld world, double x, double y, double z) {
            return this.location.test(world, x, y, z);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("location", this.location.toJson());
            return jsonObject;
        }
    }
}

