/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BrewedPotionCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("brewed_potion");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Potion lv = null;
        if (jsonObject.has("potion")) {
            Identifier lv2 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
            lv = (Potion)Registry.POTION.getOrEmpty(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + lv2 + "'"));
        }
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity arg, Potion arg22) {
        this.test(arg, arg2 -> arg2.matches(arg22));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Potion potion;

        public Conditions(EntityPredicate.Extended arg, @Nullable Potion arg2) {
            super(ID, arg);
            this.potion = arg2;
        }

        public static Conditions any() {
            return new Conditions(EntityPredicate.Extended.EMPTY, null);
        }

        public boolean matches(Potion arg) {
            return this.potion == null || this.potion == arg;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (this.potion != null) {
                jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
            }
            return jsonObject;
        }
    }
}

