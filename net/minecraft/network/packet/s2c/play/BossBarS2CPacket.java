/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class BossBarS2CPacket
implements Packet<ClientPlayPacketListener> {
    private UUID uuid;
    private Type type;
    private Text name;
    private float percent;
    private BossBar.Color color;
    private BossBar.Style overlay;
    private boolean darkenSky;
    private boolean dragonMusic;
    private boolean thickenFog;

    public BossBarS2CPacket() {
    }

    public BossBarS2CPacket(Type arg, BossBar arg2) {
        this.type = arg;
        this.uuid = arg2.getUuid();
        this.name = arg2.getName();
        this.percent = arg2.getPercent();
        this.color = arg2.getColor();
        this.overlay = arg2.getOverlay();
        this.darkenSky = arg2.getDarkenSky();
        this.dragonMusic = arg2.hasDragonMusic();
        this.thickenFog = arg2.getThickenFog();
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.uuid = arg.readUuid();
        this.type = arg.readEnumConstant(Type.class);
        switch (this.type) {
            case ADD: {
                this.name = arg.readText();
                this.percent = arg.readFloat();
                this.color = arg.readEnumConstant(BossBar.Color.class);
                this.overlay = arg.readEnumConstant(BossBar.Style.class);
                this.setFlagBitfield(arg.readUnsignedByte());
                break;
            }
            case REMOVE: {
                break;
            }
            case UPDATE_PCT: {
                this.percent = arg.readFloat();
                break;
            }
            case UPDATE_NAME: {
                this.name = arg.readText();
                break;
            }
            case UPDATE_STYLE: {
                this.color = arg.readEnumConstant(BossBar.Color.class);
                this.overlay = arg.readEnumConstant(BossBar.Style.class);
                break;
            }
            case UPDATE_PROPERTIES: {
                this.setFlagBitfield(arg.readUnsignedByte());
            }
        }
    }

    private void setFlagBitfield(int i) {
        this.darkenSky = (i & 1) > 0;
        this.dragonMusic = (i & 2) > 0;
        this.thickenFog = (i & 4) > 0;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeUuid(this.uuid);
        arg.writeEnumConstant(this.type);
        switch (this.type) {
            case ADD: {
                arg.writeText(this.name);
                arg.writeFloat(this.percent);
                arg.writeEnumConstant(this.color);
                arg.writeEnumConstant(this.overlay);
                arg.writeByte(this.getFlagBitfield());
                break;
            }
            case REMOVE: {
                break;
            }
            case UPDATE_PCT: {
                arg.writeFloat(this.percent);
                break;
            }
            case UPDATE_NAME: {
                arg.writeText(this.name);
                break;
            }
            case UPDATE_STYLE: {
                arg.writeEnumConstant(this.color);
                arg.writeEnumConstant(this.overlay);
                break;
            }
            case UPDATE_PROPERTIES: {
                arg.writeByte(this.getFlagBitfield());
            }
        }
    }

    private int getFlagBitfield() {
        int i = 0;
        if (this.darkenSky) {
            i |= 1;
        }
        if (this.dragonMusic) {
            i |= 2;
        }
        if (this.thickenFog) {
            i |= 4;
        }
        return i;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onBossBar(this);
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public Type getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPercent() {
        return this.percent;
    }

    @Environment(value=EnvType.CLIENT)
    public BossBar.Color getColor() {
        return this.color;
    }

    @Environment(value=EnvType.CLIENT)
    public BossBar.Style getOverlay() {
        return this.overlay;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldThickenFog() {
        return this.thickenFog;
    }

    public static enum Type {
        ADD,
        REMOVE,
        UPDATE_PCT,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;

    }
}

