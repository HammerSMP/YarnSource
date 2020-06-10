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
    public static final class_5402 field_25645 = new class_5402(0);
    public static final class_5402 field_25646 = new class_5402(1);
    public static final class_5402 field_25647 = new class_5402(2);
    public static final class_5402 field_25648 = new class_5402(3);
    public static final class_5402 field_25649 = new class_5402(4);
    public static final class_5402 field_25650 = new class_5402(5);
    public static final class_5402 field_25651 = new class_5402(6);
    public static final class_5402 field_25652 = new class_5402(7);
    public static final class_5402 field_25653 = new class_5402(8);
    public static final class_5402 field_25654 = new class_5402(9);
    public static final class_5402 field_25655 = new class_5402(10);
    public static final class_5402 field_25656 = new class_5402(11);
    private class_5402 reason;
    private float value;

    public GameStateChangeS2CPacket() {
    }

    public GameStateChangeS2CPacket(class_5402 arg, float f) {
        this.reason = arg;
        this.value = f;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.reason = (class_5402)class_5402.field_25657.get((int)arg.readUnsignedByte());
        this.value = arg.readFloat();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeByte(this.reason.field_25658);
        arg.writeFloat(this.value);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onGameStateChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public class_5402 getReason() {
        return this.reason;
    }

    @Environment(value=EnvType.CLIENT)
    public float getValue() {
        return this.value;
    }

    public static class class_5402 {
        private static final Int2ObjectMap<class_5402> field_25657 = new Int2ObjectOpenHashMap();
        private final int field_25658;

        public class_5402(int i) {
            this.field_25658 = i;
            field_25657.put(i, (Object)this);
        }
    }
}

