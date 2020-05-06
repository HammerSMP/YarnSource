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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("used_ender_eye");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        NumberRange.FloatRange lv = NumberRange.FloatRange.fromJson(jsonObject.get("distance"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity arg2, BlockPos arg22) {
        double d = arg2.getX() - (double)arg22.getX();
        double e = arg2.getZ() - (double)arg22.getZ();
        double f = d * d + e * e;
        this.test(arg2, arg -> arg.matches(f));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final NumberRange.FloatRange distance;

        public Conditions(EntityPredicate.Extended arg, NumberRange.FloatRange arg2) {
            super(ID, arg);
            this.distance = arg2;
        }

        public boolean matches(double d) {
            return this.distance.testSqrt(d);
        }
    }
}

