/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;

public class AdvancementTabC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Action action;
    private Identifier tabToOpen;

    public AdvancementTabC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public AdvancementTabC2SPacket(Action action, @Nullable Identifier tab) {
        this.action = action;
        this.tabToOpen = tab;
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket open(Advancement advancement) {
        return new AdvancementTabC2SPacket(Action.OPENED_TAB, advancement.getId());
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.CLOSED_SCREEN, null);
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.action = buf.readEnumConstant(Action.class);
        if (this.action == Action.OPENED_TAB) {
            this.tabToOpen = buf.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.OPENED_TAB) {
            buf.writeIdentifier(this.tabToOpen);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onAdvancementTab(this);
    }

    public Action getAction() {
        return this.action;
    }

    public Identifier getTabToOpen() {
        return this.tabToOpen;
    }

    public static enum Action {
        OPENED_TAB,
        CLOSED_SCREEN;

    }
}

