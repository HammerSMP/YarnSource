/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5421;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class class_5427
implements Packet<ServerPlayPacketListener> {
    private class_5421 field_25798;
    private boolean field_25799;
    private boolean field_25800;

    public class_5427() {
    }

    @Environment(value=EnvType.CLIENT)
    public class_5427(class_5421 arg, boolean bl, boolean bl2) {
        this.field_25798 = arg;
        this.field_25799 = bl;
        this.field_25800 = bl2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.field_25798 = arg.readEnumConstant(class_5421.class);
        this.field_25799 = arg.readBoolean();
        this.field_25800 = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.field_25798);
        arg.writeBoolean(this.field_25799);
        arg.writeBoolean(this.field_25800);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.method_30303(this);
    }

    public class_5421 method_30305() {
        return this.field_25798;
    }

    public boolean method_30306() {
        return this.field_25799;
    }

    public boolean method_30307() {
        return this.field_25800;
    }
}

