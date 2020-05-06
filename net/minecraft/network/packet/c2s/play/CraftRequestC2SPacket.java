/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftRequestC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int syncId;
    private Identifier recipe;
    private boolean craftAll;

    public CraftRequestC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public CraftRequestC2SPacket(int i, Recipe<?> arg, boolean bl) {
        this.syncId = i;
        this.recipe = arg.getId();
        this.craftAll = bl;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readByte();
        this.recipe = arg.readIdentifier();
        this.craftAll = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.syncId);
        arg.writeIdentifier(this.recipe);
        arg.writeBoolean(this.craftAll);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onCraftRequest(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    public Identifier getRecipe() {
        return this.recipe;
    }

    public boolean shouldCraftAll() {
        return this.craftAll;
    }
}

