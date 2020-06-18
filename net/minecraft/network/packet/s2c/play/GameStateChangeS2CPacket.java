/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class GameStateChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    public static final Reason NO_RESPAWN_BLOCK = new Reason(0);
    public static final Reason RAIN_STARTED = new Reason(1);
    public static final Reason RAIN_STOPPED = new Reason(2);
    public static final Reason GAME_MODE_CHANGED = new Reason(3);
    public static final Reason GAME_WON = new Reason(4);
    public static final Reason DEMO_MESSAGE_SHOWN = new Reason(5);
    public static final Reason PROJECTILE_HIT_PLAYER = new Reason(6);
    public static final Reason RAIN_GRADIENT_CHANGED = new Reason(7);
    public static final Reason THUNDER_GRADIENT_CHANGED = new Reason(8);
    public static final Reason PUFFERFISH_STING = new Reason(9);
    public static final Reason ELDER_GUARDIAN_EFFECT = new Reason(10);
    public static final Reason IMMEDIATE_RESPAWN = new Reason(11);
    private Reason reason;
    private float value;

    public GameStateChangeS2CPacket() {
    }

    public GameStateChangeS2CPacket(Reason arg, float f) {
        this.reason = arg;
        this.value = f;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.reason = (Reason)Reason.REASONS.get((int)arg.readUnsignedByte());
        this.value = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.reason.id);
        arg.writeFloat(this.value);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGameStateChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Reason getReason() {
        return this.reason;
    }

    @Environment(value=EnvType.CLIENT)
    public float getValue() {
        return this.value;
    }

    public static class Reason {
        private static final Int2ObjectMap<Reason> REASONS = new Int2ObjectOpenHashMap();
        private final int id;

        public Reason(int i) {
            this.id = i;
            REASONS.put(i, (Object)this);
        }
    }
}

