/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class OpenScreenS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private int screenHandlerId;
    private Text name;

    public OpenScreenS2CPacket() {
    }

    public OpenScreenS2CPacket(int i, ScreenHandlerType<?> arg, Text arg2) {
        this.syncId = i;
        this.screenHandlerId = Registry.SCREEN_HANDLER.getRawId(arg);
        this.name = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readVarInt();
        this.screenHandlerId = arg.readVarInt();
        this.name = arg.readText();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.syncId);
        arg.writeVarInt(this.screenHandlerId);
        arg.writeText(this.name);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onOpenScreen(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ScreenHandlerType<?> getScreenHandlerType() {
        return (ScreenHandlerType)Registry.SCREEN_HANDLER.get(this.screenHandlerId);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return this.name;
    }
}

