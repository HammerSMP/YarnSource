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
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeJsonFactory {
    private final Item output;
    private final Ingredient input;
    private final int count;
    private final Advancement.Task builder = Advancement.Task.create();
    private String group;
    private final RecipeSerializer<?> serializer;

    public SingleItemRecipeJsonFactory(RecipeSerializer<?> arg, Ingredient arg2, ItemConvertible arg3, int i) {
        this.serializer = arg;
        this.output = arg3.asItem();
        this.input = arg2;
        this.count = i;
    }

    public static SingleItemRecipeJsonFactory create(Ingredient arg, ItemConvertible arg2) {
        return new SingleItemRecipeJsonFactory(RecipeSerializer.STONECUTTING, arg, arg2, 1);
    }

    public static SingleItemRecipeJsonFactory create(Ingredient arg, ItemConvertible arg2, int i) {
        return new SingleItemRecipeJsonFactory(RecipeSerializer.STONECUTTING, arg, arg2, i);
    }

    public SingleItemRecipeJsonFactory create(String string, CriterionConditions arg) {
        this.builder.criterion(string, arg);
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
        Identifier lv = Registry.ITEM.getId(this.output);
        if (new Identifier(string).equals(lv)) {
            throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
        }
        this.offerTo(consumer, new Identifier(string));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.validate(arg);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriteriaMerger.OR);
        consumer.accept(new SingleItemRecipeJsonProvider(arg, this.serializer, this.group == null ? "" : this.group, this.input, this.output, this.count, this.builder, new Identifier(arg.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + arg.getPath())));
    }

    private void validate(Identifier arg) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
        }
    }

    public static class SingleItemRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final String group;
        private final Ingredient input;
        private final Item output;
        private final int count;
        private final Advancement.Task builder;
        private final Identifier advancementId;
        private final RecipeSerializer<?> serializer;

        public SingleItemRecipeJsonProvider(Identifier arg, RecipeSerializer<?> arg2, String string, Ingredient arg3, Item arg4, int i, Advancement.Task arg5, Identifier arg6) {
            this.recipeId = arg;
            this.serializer = arg2;
            this.group = string;
            this.input = arg3;
            this.output = arg4;
            this.count = i;
            this.builder = arg5;
            this.advancementId = arg6;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            jsonObject.add("ingredient", this.input.toJson());
            jsonObject.addProperty("result", Registry.ITEM.getId(this.output).toString());
            jsonObject.addProperty("count", (Number)this.count);
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

