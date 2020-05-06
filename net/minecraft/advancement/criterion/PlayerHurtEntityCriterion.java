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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("player_hurt_entity");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        DamagePredicate lv = DamagePredicate.fromJson(jsonObject.get("damage"));
        EntityPredicate.Extended lv2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", arg2);
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, Entity arg2, DamageSource arg3, float f, float g, boolean bl) {
        LootContext lv = EntityPredicate.createAdvancementEntityLootContext(arg, arg2);
        this.test(arg, arg4 -> arg4.matches(arg, lv, arg3, f, g, bl));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DamagePredicate damage;
        private final EntityPredicate.Extended entity;

        public Conditions(EntityPredicate.Extended arg, DamagePredicate arg2, EntityPredicate.Extended arg3) {
            super(ID, arg);
            this.damage = arg2;
            this.entity = arg3;
        }

        public static Conditions create(DamagePredicate.Builder arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg.build(), EntityPredicate.Extended.EMPTY);
        }

        public boolean matches(ServerPlayerEntity arg, LootContext arg2, DamageSource arg3, float f, float g, boolean bl) {
            if (!this.damage.test(arg, arg3, f, g, bl)) {
                return false;
            }
            return this.entity.test(arg2);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("damage", this.damage.toJson());
            jsonObject.add("entity", this.entity.toJson(arg));
            return jsonObject;
        }
    }
}

