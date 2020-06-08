/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonElement;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SmithingRecipeJsonFactory {
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;
    private final Advancement.Task builder = Advancement.Task.create();
    private final RecipeSerializer<?> serializer;

    public SmithingRecipeJsonFactory(RecipeSerializer<?> arg, Ingredient arg2, Ingredient arg3, Item arg4) {
        this.serializer = arg;
        this.base = arg2;
        this.addition = arg3;
        this.result = arg4;
    }

    public static SmithingRecipeJsonFactory create(Ingredient arg, Ingredient arg2, Item arg3) {
        return new SmithingRecipeJsonFactory(RecipeSerializer.SMITHING, arg, arg2, arg3);
    }

    public SmithingRecipeJsonFactory criterion(String string, CriterionConditions arg) {
        this.builder.criterion(string, arg);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
        this.offerTo(consumer, new Identifier(string));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.validate(arg);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriterionMerger.OR);
        consumer.accept(new SmithingRecipeJsonProvider(arg, this.serializer, this.base, this.addition, this.result, this.builder, new Identifier(arg.getNamespace(), "recipes/" + this.result.getGroup().getName() + "/" + arg.getPath())));
    }

    private void validate(Identifier arg) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
        }
    }

    public static class SmithingRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item result;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        private final RecipeSerializer<?> serializer;

        public SmithingRecipeJsonProvider(Identifier arg, RecipeSerializer<?> arg2, Ingredient arg3, Ingredient arg4, Item arg5, Advancement.Task arg6, Identifier arg7) {
            this.recipeId = arg;
            this.serializer = arg2;
            this.base = arg3;
            this.addition = arg4;
            this.result = arg5;
            this.builder = arg6;
            this.advancementId = arg7;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.add("base", this.base.toJson());
            jsonObject.add("addition", this.addition.toJson());
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getId(this.result).toString());
            jsonObject.add("result", (JsonElement)jsonObject2);
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
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

