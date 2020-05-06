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
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;

public class BookUpdateC2SPacket
implements Packet<ServerPlayPacketListener> {
    private ItemStack book;
    private boolean signed;
    private Hand hand;

    public BookUpdateC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public BookUpdateC2SPacket(ItemStack arg, boolean bl, Hand arg2) {
        this.book = arg.copy();
        this.signed = bl;
        this.hand = arg2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.book = arg.readItemStack();
        this.signed = arg.readBoolean();
        this.hand = arg.readEnumConstant(Hand.class);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeItemStack(this.book);
        arg.writeBoolean(this.signed);
        arg.writeEnumConstant(this.hand);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onBookUpdate(this);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean wasSigned() {
        return this.signed;
    }

    public Hand getHand() {
        return this.hand;
    }
}

