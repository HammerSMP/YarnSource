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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion
extends AbstractCriterion<Conditions> {
    private final Identifier id;

    public OnKilledCriterion(Identifier arg) {
        this.id = arg;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return new Conditions(this.id, arg, EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2), DamageSourcePredicate.fromJson(jsonObject.get("killing_blow")));
    }

    public void trigger(ServerPlayerEntity arg, Entity arg2, DamageSource arg3) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        this.test(arg, arg4 -> arg4.test(arg, lv, arg3));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final EntityPredicate.Extended entity;
        private final DamageSourcePredicate killingBlow;

        public Conditions(Identifier arg, EntityPredicate.Extended arg2, EntityPredicate.Extended arg3, DamageSourcePredicate arg4) {
            super(arg, arg2);
            this.entity = arg3;
            this.killingBlow = arg4;
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder arg) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(arg.build()), DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity() {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
        }

        public static Conditions createPlayerKilledEntity(EntityPredicate.Builder arg, DamageSourcePredicate.Builder arg2) {
            return new Conditions(Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(arg.build()), arg2.build());
        }

        public static Conditions createEntityKilledPlayer() {
            return new Conditions(Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY);
        }

        public boolean test(ServerPlayerEntity arg, LootContext arg2, DamageSource arg3) {
            if (!this.killingBlow.test(arg, arg3)) {
                return false;
            }
            return this.entity.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("entity", this.entity.toJson(arg));
            jsonObject.add("killing_blow", this.killingBlow.toJson());
            return jsonObject;
        }
    }
}

