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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("entity_hurt_player");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        DamagePredicate lv = DamagePredicate.fromJson(jsonObject.get("damage"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity arg, DamageSource arg2, float f, float g, boolean bl) {
        this.test(arg, arg3 -> arg3.matches(arg, arg2, f, g, bl));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final DamagePredicate damage;

        public Conditions(EntityPredicate.Extended arg, DamagePredicate arg2) {
            super(ID, arg);
            this.damage = arg2;
        }

        public static Conditions create(DamagePredicate.Builder arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, arg.build());
        }

        public boolean matches(ServerPlayerEntity arg, DamageSource arg2, float f, float g, boolean bl) {
            return this.damage.test(arg, arg2, f, g, bl);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.add("damage", this.damage.toJson());
            return jsonObject;
        }
    }
}

