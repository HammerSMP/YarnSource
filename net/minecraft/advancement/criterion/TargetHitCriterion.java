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

    public void trigger(ServerPlayerEntity player, Entity projectile, Vec3d hitPos, int signalStrength) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(player, projectile);
        this.test(player, arg3 -> arg3.test(lv, hitPos, signalStrength));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.IntRange signalStrength;
        private final EntityPredicate.Extended projectile;

        public Conditions(EntityPredicate.Extended player, NumberRange.IntRange signalStrength, EntityPredicate.Extended projectile) {
            super(ID, player);
            this.signalStrength = signalStrength;
            this.projectile = projectile;
        }

        public static Conditions create(NumberRange.IntRange signalStrength, EntityPredicate.Extended arg2) {
            return new Conditions(EntityPredicate.Extended.EMPTY, signalStrength, arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("signal_strength", this.signalStrength.toJson());
            jsonObject.add("projectile", this.projectile.toJson(predicateSerializer));
            return jsonObject;
        }

        public boolean test(LootContext projectileContext, Vec3d hitPos, int signalStrength) {
            if (!this.signalStrength.test(signalStrength)) {
                return false;
            }
            return this.projectile.test(projectileContext);
        }
    }
}

