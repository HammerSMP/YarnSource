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
    public AdvancementTabC2SPacket(Action arg, @Nullable Identifier arg2) {
        this.action = arg;
        this.tabToOpen = arg2;
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket open(Advancement arg) {
        return new AdvancementTabC2SPacket(Action.OPENED_TAB, arg.getId());
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.CLOSED_SCREEN, null);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        if (this.action == Action.OPENED_TAB) {
            this.tabToOpen = arg.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.action);
        if (this.action == Action.OPENED_TAB) {
            arg.writeIdentifier(this.tabToOpen);
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

