/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.recipe;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class SpecialRecipeSerializer<T extends Recipe<?>>
implements RecipeSerializer<T> {
    private final Function<Identifier, T> id;

    public SpecialRecipeSerializer(Function<Identifier, T> function) {
        this.id = function;
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        return (T)((Recipe)this.id.apply(id));
    }

    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        return (T)((Recipe)this.id.apply(id));
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {
    }
}

