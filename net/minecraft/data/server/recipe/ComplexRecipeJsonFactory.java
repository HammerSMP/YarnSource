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
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonFactory {
    private final SpecialRecipeSerializer<?> serializer;

    public ComplexRecipeJsonFactory(SpecialRecipeSerializer<?> arg) {
        this.serializer = arg;
    }

    public static ComplexRecipeJsonFactory create(SpecialRecipeSerializer<?> arg) {
        return new ComplexRecipeJsonFactory(arg);
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, final String string) {
        consumer.accept(new RecipeJsonProvider(){

            @Override
            public void serialize(JsonObject jsonObject) {
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return ComplexRecipeJsonFactory.this.serializer;
            }

            @Override
            public Identifier getRecipeId() {
                return new Identifier(string);
            }

            @Override
            @Nullable
            public JsonObject toAdvancementJson() {
                return null;
            }

            @Override
            public Identifier getAdvancementId() {
                return new Identifier("");
            }
        });
    }
}

