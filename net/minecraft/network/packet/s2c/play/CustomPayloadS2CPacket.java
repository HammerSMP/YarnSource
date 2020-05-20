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
import net.minecraft.util.Identifier;

public class CustomPayloadS2CPacket
implements Packet<ClientPlayPacketListener> {
    public static final Identifier BRAND = new Identifier("brand");
    public static final Identifier DEBUG_PATH = new Identifier("debug/path");
    public static final Identifier DEBUG_NEIGHBORS_UPDATE = new Identifier("debug/neighbors_update");
    public static final Identifier DEBUG_CAVES = new Identifier("debug/caves");
    public static final Identifier DEBUG_STRUCTURES = new Identifier("debug/structures");
    public static final Identifier DEBUG_WORLDGEN_ATTEMPT = new Identifier("debug/worldgen_attempt");
    public static final Identifier DEBUG_POI_TICKET_COUNT = new Identifier("debug/poi_ticket_count");
    public static final Identifier DEBUG_POI_ADDED = new Identifier("debug/poi_added");
    public static final Identifier DEBUG_POI_REMOVED = new Identifier("debug/poi_removed");
    public static final Identifier DEBUG_VILLAGE_SECTIONS = new Identifier("debug/village_sections");
    public static final Identifier DEBUG_GOAL_SELECTOR = new Identifier("debug/goal_selector");
    public static final Identifier DEBUG_BRAIN = new Identifier("debug/brain");
    public static final Identifier DEBUG_BEE = new Identifier("debug/bee");
    public static final Identifier DEBUG_HIVE = new Identifier("debug/hive");
    public static final Identifier DEBUG_GAME_TEST_ADD_MARKER = new Identifier("debug/game_test_add_marker");
    public static final Identifier DEBUG_GAME_TEST_CLEAR = new Identifier("debug/game_test_clear");
    public static final Identifier DEBUG_RAIDS = new Identifier("debug/raids");
    private Identifier channel;
    private PacketByteBuf data;

    public CustomPayloadS2CPacket() {
    }

    public CustomPayloadS2CPacket(Identifier arg, PacketByteBuf arg2) {
        this.channel = arg;
        this.data = arg2;
        if (arg2.writerIndex() > 0x100000) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.channel = arg.readIdentifier();
        int i = arg.readableBytes();
        if (i < 0 || i > 0x100000) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new PacketByteBuf(arg.readBytes(i));
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeIdentifier(this.channel);
        arg.writeBytes(this.data.copy());
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCustomPayload(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getChannel() {
        return this.channel;
    }

    @Environment(value=EnvType.CLIENT)
    public PacketByteBuf getData() {
        return new PacketByteBuf(this.data.copy());
    }
}
