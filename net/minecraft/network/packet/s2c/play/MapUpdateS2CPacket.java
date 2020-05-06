/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class MapUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private byte scale;
    private boolean showIcons;
    private boolean locked;
    private MapIcon[] icons;
    private int startX;
    private int startZ;
    private int width;
    private int height;
    private byte[] colors;

    public MapUpdateS2CPacket() {
    }

    public MapUpdateS2CPacket(int i, byte b, boolean bl, boolean bl2, Collection<MapIcon> collection, byte[] bs, int j, int k, int l, int m) {
        this.id = i;
        this.scale = b;
        this.showIcons = bl;
        this.locked = bl2;
        this.icons = collection.toArray(new MapIcon[collection.size()]);
        this.startX = j;
        this.startZ = k;
        this.width = l;
        this.height = m;
        this.colors = new byte[l * m];
        for (int n = 0; n < l; ++n) {
            for (int o = 0; o < m; ++o) {
                this.colors[n + o * l] = bs[j + n + (k + o) * 128];
            }
        }
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.id = arg.readVarInt();
        this.scale = arg.readByte();
        this.showIcons = arg.readBoolean();
        this.locked = arg.readBoolean();
        this.icons = new MapIcon[arg.readVarInt()];
        for (int i = 0; i < this.icons.length; ++i) {
            MapIcon.Type lv = arg.readEnumConstant(MapIcon.Type.class);
            this.icons[i] = new MapIcon(lv, arg.readByte(), arg.readByte(), (byte)(arg.readByte() & 0xF), arg.readBoolean() ? arg.readText() : null);
        }
        this.width = arg.readUnsignedByte();
        if (this.width > 0) {
            this.height = arg.readUnsignedByte();
            this.startX = arg.readUnsignedByte();
            this.startZ = arg.readUnsignedByte();
            this.colors = arg.readByteArray();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        arg.writeByte(this.scale);
        arg.writeBoolean(this.showIcons);
        arg.writeBoolean(this.locked);
        arg.writeVarInt(this.icons.length);
        for (MapIcon lv : this.icons) {
            arg.writeEnumConstant(lv.getType());
            arg.writeByte(lv.getX());
            arg.writeByte(lv.getZ());
            arg.writeByte(lv.getRotation() & 0xF);
            if (lv.getText() != null) {
                arg.writeBoolean(true);
                arg.writeText(lv.getText());
                continue;
            }
            arg.writeBoolean(false);
        }
        arg.writeByte(this.width);
        if (this.width > 0) {
            arg.writeByte(this.height);
            arg.writeByte(this.startX);
            arg.writeByte(this.startZ);
            arg.writeByteArray(this.colors);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onMapUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void apply(MapState arg) {
        arg.scale = this.scale;
        arg.showIcons = this.showIcons;
        arg.locked = this.locked;
        arg.icons.clear();
        for (int i = 0; i < this.icons.length; ++i) {
            MapIcon lv = this.icons[i];
            arg.icons.put("icon-" + i, lv);
        }
        for (int j = 0; j < this.width; ++j) {
            for (int k = 0; k < this.height; ++k) {
                arg.colors[this.startX + j + (this.startZ + k) * 128] = this.colors[j + k * this.width];
            }
        }
    }
}

