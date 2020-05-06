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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("recipe_unlocked");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
        return new Conditions(arg, lv);
    }

    public void trigger(ServerPlayerEntity arg, Recipe<?> arg22) {
        this.test(arg, arg2 -> arg2.matches(arg22));
    }

    public static Conditions create(Identifier arg) {
        return new Conditions(EntityPredicate.Extended.EMPTY, arg);
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Identifier recipe;

        public Conditions(EntityPredicate.Extended arg, Identifier arg2) {
            super(ID, arg);
            this.recipe = arg2;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            jsonObject.addProperty("recipe", this.recipe.toString());
            return jsonObject;
        }

        public boolean matches(Recipe<?> arg) {
            return this.recipe.equals(arg.getId());
        }
    }
}

