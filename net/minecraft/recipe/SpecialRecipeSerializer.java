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
    public T read(Identifier arg, JsonObject jsonObject) {
        return (T)((Recipe)this.id.apply(arg));
    }

    @Override
    public T read(Identifier arg, PacketByteBuf arg2) {
        return (T)((Recipe)this.id.apply(arg));
    }

    @Override
    public void write(PacketByteBuf arg, T arg2) {
    }
}

