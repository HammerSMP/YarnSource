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

    private CookingRecipeJsonFactory(ItemConvertible arg, Ingredient arg2, float f, int i, CookingRecipeSerializer<?> arg3) {
        this.result = arg.asItem();
        this.ingredient = arg2;
        this.experience = f;
        this.cookingTime = i;
        this.serializer = arg3;
    }

    public static CookingRecipeJsonFactory create(Ingredient arg, ItemConvertible arg2, float f, int i, CookingRecipeSerializer<?> arg3) {
        return new CookingRecipeJsonFactory(arg2, arg, f, i, arg3);
    }

    public static CookingRecipeJsonFactory createBlasting(Ingredient arg, ItemConvertible arg2, float f, int i) {
        return CookingRecipeJsonFactory.create(arg, arg2, f, i, RecipeSerializer.BLASTING);
    }

    public static CookingRecipeJsonFactory createSmelting(Ingredient arg, ItemConvertible arg2, float f, int i) {
        return CookingRecipeJsonFactory.create(arg, arg2, f, i, RecipeSerializer.SMELTING);
    }

    public CookingRecipeJsonFactory criterion(String string, CriterionConditions arg) {
        this.builder.criterion(string, arg);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer) {
        this.offerTo(consumer, Registry.ITEM.getId(this.result));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
        Identifier lv2 = new Identifier(string);
        Identifier lv = Registry.ITEM.getId(this.result);
        if (lv2.equals(lv)) {
            throw new IllegalStateException("Recipe " + lv2 + " should remove its 'save' argument");
        }
        this.offerTo(consumer, lv2);
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.validate(arg);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriterionMerger.OR);
        consumer.accept(new CookingRecipeJsonProvider(arg, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.builder, new Identifier(arg.getNamespace(), "recipes/" + this.result.getGroup().getName() + "/" + arg.getPath()), this.serializer));
    }

    private void validate(Identifier arg) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
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

        public CookingRecipeJsonProvider(Identifier arg, String string, Ingredient arg2, Item arg3, float f, int i, Advancement.Task arg4, Identifier arg5, RecipeSerializer<? extends AbstractCookingRecipe> arg6) {
            this.recipeId = arg;
            this.group = string;
            this.ingredient = arg2;
            this.result = arg3;
            this.experience = f;
            this.cookingTime = i;
            this.builder = arg4;
            this.advancementId = arg5;
            this.serializer = arg6;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.ingredient.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getId(this.result).toString());
            jsonObject.addProperty("experience", (Number)Float.valueOf(this.experience));
            jsonObject.addProperty("cookingtime", (Number)this.cookingTime);
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

