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
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("construct_beacon");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("level"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity arg, BeaconBlockEntity arg22) {
        this.test(arg, arg2 -> arg2.matches(arg22));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange level;

        public Conditions(EntityPredicate.Extended arg, NumberRange.IntRange arg2) {
            super(ID, arg);
            this.level = arg2;
        }

        public static Conditions level(NumberRange.IntRange arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg);
        }

        public boolean matches(BeaconBlockEntity arg) {
            return this.level.test(arg.getLevel());
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("level", this.level.toJson());
            return jsonObject;
        }
    }
}

