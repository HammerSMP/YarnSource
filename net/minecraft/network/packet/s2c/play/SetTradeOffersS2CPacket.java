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
import net.minecraft.village.TraderOfferList;

public class SetTradeOffersS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int syncId;
    private TraderOfferList recipes;
    private int levelProgress;
    private int experience;
    private boolean leveled;
    private boolean refreshable;

    public SetTradeOffersS2CPacket() {
    }

    public SetTradeOffersS2CPacket(int i, TraderOfferList arg, int j, int k, boolean bl, boolean bl2) {
        this.syncId = i;
        this.recipes = arg;
        this.levelProgress = j;
        this.experience = k;
        this.leveled = bl;
        this.refreshable = bl2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.syncId = arg.readVarInt();
        this.recipes = TraderOfferList.fromPacket(arg);
        this.levelProgress = arg.readVarInt();
        this.experience = arg.readVarInt();
        this.leveled = arg.readBoolean();
        this.refreshable = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.syncId);
        this.recipes.toPacket(arg);
        arg.writeVarInt(this.levelProgress);
        arg.writeVarInt(this.experience);
        arg.writeBoolean(this.leveled);
        arg.writeBoolean(this.refreshable);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSetTradeOffers(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Environment(value=EnvType.CLIENT)
    public TraderOfferList getOffers() {
        return this.recipes;
    }

    @Environment(value=EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }

    @Environment(value=EnvType.CLIENT)
    public int getExperience() {
        return this.experience;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isLeveled() {
        return this.leveled;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isRefreshable() {
        return this.refreshable;
    }
}

