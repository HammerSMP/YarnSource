/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class RecipeBookDataC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Identifier recipeId;

    public RecipeBookDataC2SPacket() {
    }

    public RecipeBookDataC2SPacket(Recipe<?> recipe) {
        this.recipeId = recipe.getId();
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.recipeId = buf.readIdentifier();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.recipeId);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRecipeBookData(this);
    }

    public Identifier getRecipeId() {
        return this.recipeId;
    }
}

