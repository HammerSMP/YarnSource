/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.google.gson.JsonElement;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_5377 {
    private final Ingredient field_25491;
    private final Ingredient field_25492;
    private final Item field_25493;
    private final Advancement.Task field_25494 = Advancement.Task.create();
    private final RecipeSerializer<?> field_25495;

    public class_5377(RecipeSerializer<?> arg, Ingredient arg2, Ingredient arg3, Item arg4) {
        this.field_25495 = arg;
        this.field_25491 = arg2;
        this.field_25492 = arg3;
        this.field_25493 = arg4;
    }

    public static class_5377 method_29729(Ingredient arg, Ingredient arg2, Item arg3) {
        return new class_5377(RecipeSerializer.SMITHING, arg, arg2, arg3);
    }

    public class_5377 method_29730(String string, CriterionConditions arg) {
        this.field_25494.criterion(string, arg);
        return this;
    }

    public void method_29731(Consumer<RecipeJsonProvider> consumer, String string) {
        this.method_29732(consumer, new Identifier(string));
    }

    public void method_29732(Consumer<RecipeJsonProvider> consumer, Identifier arg) {
        this.method_29733(arg);
        this.field_25494.parent(new Identifier("recipes/root")).criterion("has_the_recipe", RecipeUnlockedCriterion.create(arg)).rewards(AdvancementRewards.Builder.recipe(arg)).criteriaMerger(CriteriaMerger.OR);
        consumer.accept(new class_5378(arg, this.field_25495, this.field_25491, this.field_25492, this.field_25493, this.field_25494, new Identifier(arg.getNamespace(), "recipes/" + this.field_25493.getGroup().getName() + "/" + arg.getPath())));
    }

    private void method_29733(Identifier arg) {
        if (this.field_25494.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + arg);
        }
    }

    public static class class_5378
    implements RecipeJsonProvider {
        private final Identifier field_25496;
        private final Ingredient field_25497;
        private final Ingredient field_25498;
        private final Item field_25499;
        private final Advancement.Task field_25500;
        private final Identifier field_25501;
        private final RecipeSerializer<?> field_25502;

        public class_5378(Identifier arg, RecipeSerializer<?> arg2, Ingredient arg3, Ingredient arg4, Item arg5, Advancement.Task arg6, Identifier arg7) {
            this.field_25496 = arg;
            this.field_25502 = arg2;
            this.field_25497 = arg3;
            this.field_25498 = arg4;
            this.field_25499 = arg5;
            this.field_25500 = arg6;
            this.field_25501 = arg7;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.add("base", this.field_25497.toJson());
            jsonObject.add("addition", this.field_25498.toJson());
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("item", Registry.ITEM.getId(this.field_25499).toString());
            jsonObject.add("result", (JsonElement)jsonObject2);
        }

        @Override
        public Identifier getRecipeId() {
            return this.field_25496;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.field_25502;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.field_25500.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.field_25501;
        }
    }
}

