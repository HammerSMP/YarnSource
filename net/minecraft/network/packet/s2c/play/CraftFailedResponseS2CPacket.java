/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftFailedResponseS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private Identifier recipeId;

    public CraftFailedResponseS2CPacket() {
    }

    public CraftFailedResponseS2CPacket(int i, Recipe<?> arg) {
        this.syncId = i;
        this.recipeId = arg.getId();
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getRecipeId() {
        return this.recipeId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readByte();
        this.recipeId = arg.readIdentifier();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeIdentifier(this.recipeId);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCraftFailedResponse(this);
    }
}

