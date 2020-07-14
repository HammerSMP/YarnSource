/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CookingRecipeJsonFactory {
    private final Item result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Task builder = Advancement.Task.create();
    private String group;
    private final CookingRecipeSerializer<?> serializer;

    private CookingRecipeJsonFactory(ItemConvertible result, Ingredient ingredient, float experience, int cookingTime, CookingRecipeSerializer<?> serializer) {
        this.result = result.asItem();
        this.ingredient = ingredient;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
    }

    public static CookingRecipeJsonFactory create(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime, CookingRecipeSerializer<?> serializer) {
        return new CookingRecipeJsonFactory(result, ingredient, experience, cookingTime, serializer);
    }

    public static CookingRecipeJsonFactory createBlasting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
        return CookingRecipeJsonFactory.create(ingredient, result, experience, cookingTime, RecipeSerializer.BLASTING);
    }

    public static CookingRecipeJsonFactory createSmelting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
        return CookingRecipeJsonFactory.create(ingredient, result, experience, cookingTime, RecipeSerializer.SMELTING);
    }

    public CookingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
        this.builder.criterion(criterionName, conditions);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, Registry.ITEM.getId(this.result));
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
        Identifier lv2 = new Identifier(recipeIdStr);
        Identifier lv = Registry.ITEM.getId(this.result);
        if (lv2.equals(lv)) {
            throw new IllegalStateException("Recipe " + lv2 + " should remove its 'save' argument");
        }
        this.offerTo(exporter, lv2);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new CookingRecipeJsonProvider(recipeId, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.builder, new Identifier(recipeId.getNamespace(), "recipes/" + this.result.getGroup().getName() + "/" + recipeId.getPath()), this.serializer));
    }

    private void validate(Identifier recipeId) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }

    public static class CookingRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public CookingRecipeJsonProvider(Identifier recipeId, String group, Ingredient ingredient, Item result, float experience, int cookingTime, Advancement.Task builder, Identifier advancementId, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
            this.recipeId = recipeId;
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.builder = builder;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("result", Registry.ITEM.getId(this.result).toString());
            json.addProperty("experience", (Number)Float.valueOf(this.experience));
            json.addProperty("cookingtime", (Number)this.cookingTime);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.builder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}

