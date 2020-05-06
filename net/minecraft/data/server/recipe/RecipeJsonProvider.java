/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface RecipeJsonProvider {
    public void serialize(JsonObject var1);

    default public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", Registry.RECIPE_SERIALIZER.getId(this.getSerializer()).toString());
        this.serialize(jsonObject);
        return jsonObject;
    }

    public Identifier getRecipeId();

    public RecipeSerializer<?> getSerializer();

    @Nullable
    public JsonObject toAdvancementJson();

    @Nullable
    public Identifier getAdvancementId();
}

