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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("target_hit");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("signal_strength"));
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "projectile", arg2);
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, Entity arg2, Vec3d arg32, int i) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        this.test(arg, arg3 -> arg3.test(lv, arg32, i));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange signalStrength;
        private final EntityPredicate.Extended projectile;

        public Conditions(EntityPredicate.Extended arg, NumberRange.IntRange arg2, EntityPredicate.Extended arg3) {
            super(ID, arg);
            this.signalStrength = arg2;
            this.projectile = arg3;
        }

        public static Conditions create(NumberRange.IntRange arg, EntityPredicate.Extended arg2) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg, arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("signal_strength", this.signalStrength.toJson());
            jsonObject.add("projectile", this.projectile.toJson(arg));
            return jsonObject;
        }

        public boolean test(LootContext arg, Vec3d arg2, int i) {
            if (!this.signalStrength.test(i)) {
                return false;
            }
            return this.projectile.test(arg);
        }
    }
}

