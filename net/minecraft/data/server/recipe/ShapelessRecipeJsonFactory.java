/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
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
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeJsonFactory {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item output;
    private final int outputCount;
    private final List<Ingredient> inputs = Lists.newArrayList();
    private final Advancement.Task builder = Advancement.Task.create();
    private String group;

    public ShapelessRecipeJsonFactory(ItemConvertible arg, int i) {
        this.output = arg.asItem();
        this.outputCount = i;
    }

    public static ShapelessRecipeJsonFactory create(ItemConvertible arg) {
        return new ShapelessRecipeJsonFactory(arg, 1);
    }

    public static ShapelessRecipeJsonFactory create(ItemConvertible arg, int i) {
        return new ShapelessRecipeJsonFactory(arg, i);
    }

    public ShapelessRecipeJsonFactory input(Tag<Item> arg) {
        return this.input(Ingredient.fromTag(arg));
    }

    public ShapelessRecipeJsonFactory input(ItemConvertible arg) {
        return this.input(arg, 1);
    }

    public ShapelessRecipeJsonFactory input(ItemConvertible arg, int i) {
        for (int j = 0; j < i; ++j) {
            this.input(Ingredient.ofItems(arg));
        }
        return this;
    }

    public ShapelessRecipeJsonFactory input(Ingredient arg) {
        return this.input(arg, 1);
    }

    public ShapelessRecipeJsonFactory input(Ingredient arg, int i) {
        for (int j = 0; j < i; ++j) {
            this.inputs.add(arg);
        }
        return this;
    }

    public ShapelessRecipeJsonFactory criterion(String string, CriterionConditions arg) {
        this.builder.criterion(string, arg);
        return this;
    }

    public ShapelessRecipeJsonFactory group(String string) {
        this.group = string;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer) {
        this.offerTo(consumer, Registry.ITEM.getId(this.output));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
        Identifier lv = Registry.ITEM.getId(this.output);
        if (new Identifier(string).equals(lv)) {
            throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
        }
        this.offerTo(consumer, new Identifier(string));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.validate(arg);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriteriaMerger.OR);
        consumer.accept(new ShapelessRecipeJsonProvider(arg, this.output, this.outputCount, this.group == null ? "" : this.group, this.inputs, this.builder, new Identifier(arg.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + arg.getPath())));
    }

    private void validate(Identifier arg) {
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
        }
    }

    public static class ShapelessRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Item output;
        private final int count;
        private final String group;
        private final List<Ingredient> inputs;
        private final Advancement.Task builder;
        private final Identifier advancementId;

        public ShapelessRecipeJsonProvider(Identifier arg, Item arg2, int i, String string, List<Ingredient> list, Advancement.Task arg3, Identifier arg4) {
            this.recipeId = arg;
            this.output = arg2;
            this.count = i;
            this.group = string;
            this.inputs = list;
            this.builder = arg3;
            this.advancementId = arg4;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (Ingredient lv : this.inputs) {
                jsonArray.add(lv.toJson());
            }
            jsonObject.add("ingredients", (JsonElement)jsonArray);
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getId(this.output).toString());
            if (this.count > 1) {
                jsonObject2.addProperty("count", (Number)this.count);
            }
            jsonObject.add("result", (JsonElement)jsonObject2);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS;
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

