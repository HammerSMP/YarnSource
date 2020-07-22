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

    public MapUpdateS2CPacket(int id, byte scale, boolean showIcons, boolean locked, Collection<MapIcon> icons, byte[] mapColors, int startX, int startZ, int width, int height) {
        this.id = id;
        this.scale = scale;
        this.showIcons = showIcons;
        this.locked = locked;
        this.icons = icons.toArray(new MapIcon[icons.size()]);
        this.startX = startX;
        this.startZ = startZ;
        this.width = width;
        this.height = height;
        this.colors = new byte[width * height];
        for (int n = 0; n < width; ++n) {
            for (int o = 0; o < height; ++o) {
                this.colors[n + o * width] = mapColors[startX + n + (startZ + o) * 128];
            }
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.scale = buf.readByte();
        this.showIcons = buf.readBoolean();
        this.locked = buf.readBoolean();
        this.icons = new MapIcon[buf.readVarInt()];
        for (int i = 0; i < this.icons.length; ++i) {
            MapIcon.Type lv = buf.readEnumConstant(MapIcon.Type.class);
            this.icons[i] = new MapIcon(lv, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 0xF), buf.readBoolean() ? buf.readText() : null);
        }
        this.width = buf.readUnsignedByte();
        if (this.width > 0) {
            this.height = buf.readUnsignedByte();
            this.startX = buf.readUnsignedByte();
            this.startZ = buf.readUnsignedByte();
            this.colors = buf.readByteArray();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeByte(this.scale);
        buf.writeBoolean(this.showIcons);
        buf.writeBoolean(this.locked);
        buf.writeVarInt(this.icons.length);
        for (MapIcon lv : this.icons) {
            buf.writeEnumConstant(lv.getType());
            buf.writeByte(lv.getX());
            buf.writeByte(lv.getZ());
            buf.writeByte(lv.getRotation() & 0xF);
            if (lv.getText() != null) {
                buf.writeBoolean(true);
                buf.writeText(lv.getText());
                continue;
            }
            buf.writeBoolean(false);
        }
        buf.writeByte(this.width);
        if (this.width > 0) {
            buf.writeByte(this.height);
            buf.writeByte(this.startX);
            buf.writeByte(this.startZ);
            buf.writeByteArray(this.colors);
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

