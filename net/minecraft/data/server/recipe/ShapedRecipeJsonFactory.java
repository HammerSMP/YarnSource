/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeJsonFactory {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Item output;
    private final int outputCount;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> inputs = Maps.newLinkedHashMap();
    private final Advancement.Task builder = Advancement.Task.create();
    private String group;

    public ShapedRecipeJsonFactory(ItemConvertible arg, int i) {
        this.output = arg.asItem();
        this.outputCount = i;
    }

    public static ShapedRecipeJsonFactory create(ItemConvertible arg) {
        return ShapedRecipeJsonFactory.create(arg, 1);
    }

    public static ShapedRecipeJsonFactory create(ItemConvertible arg, int i) {
        return new ShapedRecipeJsonFactory(arg, i);
    }

    public ShapedRecipeJsonFactory input(Character character, Tag<Item> arg) {
        return this.input(character, Ingredient.fromTag(arg));
    }

    public ShapedRecipeJsonFactory input(Character character, ItemConvertible arg) {
        return this.input(character, Ingredient.ofItems(arg));
    }

    public ShapedRecipeJsonFactory input(Character character, Ingredient arg) {
        if (this.inputs.containsKey(character)) {
            throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
        }
        if (character.charValue() == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.inputs.put(character, arg);
        return this;
    }

    public ShapedRecipeJsonFactory pattern(String string) {
        if (!this.pattern.isEmpty() && string.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.pattern.add(string);
        return this;
    }

    public ShapedRecipeJsonFactory criterion(String string, CriterionConditions arg) {
        this.builder.criterion(string, arg);
        return this;
    }

    public ShapedRecipeJsonFactory group(String string) {
        this.group = string;
        return this;
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer) {
        this.offerTo(consumer, Registry.ITEM.getId(this.output));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
        Identifier lv = Registry.ITEM.getId(this.output);
        if (new Identifier(string).equals(lv)) {
            throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
        }
        this.offerTo(consumer, new Identifier(string));
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.validate(arg);
        this.builder.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriterionMerger.OR);
        consumer.accept(new ShapedRecipeJsonProvider(arg, this.output, this.outputCount, this.group == null ? "" : this.group, this.pattern, this.inputs, this.builder, new Identifier(arg.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + arg.getPath())));
    }

    private void validate(Identifier arg) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + arg + "!");
        }
        HashSet set = Sets.newHashSet(this.inputs.keySet());
        set.remove(Character.valueOf(' '));
        for (String string : this.pattern) {
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (!this.inputs.containsKey(Character.valueOf(c)) && c != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + arg + " uses undefined symbol '" + c + "'");
                }
                set.remove(Character.valueOf(c));
            }
        }
        if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + arg);
        }
        if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + arg + " only takes in a single item - should it be a shapeless recipe instead?");
        }
        if (this.builder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
        }
    }

    class ShapedRecipeJsonProvider
    implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final Item output;
        private final int resultCount;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> inputs;
        private final Advancement.Task builder;
        private final Identifier advancementId;

        public ShapedRecipeJsonProvider(Identifier arg2, Item arg3, int i, String string, List<String> list, Map<Character, Ingredient> map, Advancement.Task arg4, Identifier arg5) {
            this.recipeId = arg2;
            this.output = arg3;
            this.resultCount = i;
            this.group = string;
            this.pattern = list;
            this.inputs = map;
            this.builder = arg4;
            this.advancementId = arg5;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            if (!this.group.isEmpty()) {
                jsonObject.addProperty("group", this.group);
            }
            JsonArray jsonArray = new JsonArray();
            for (String string : this.pattern) {
                jsonArray.add(string);
            }
            jsonObject.add("pattern", (JsonElement)jsonArray);
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : this.inputs.entrySet()) {
                jsonObject2.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            jsonObject.add("key", (JsonElement)jsonObject2);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("item", Registry.ITEM.getId(this.output).toString());
            if (this.resultCount > 1) {
                jsonObject3.addProperty("count", (Number)this.resultCount);
            }
            jsonObject.add("result", (JsonElement)jsonObject3);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPED;
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

