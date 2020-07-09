/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5472
extends ClientConnection {
    private static final Logger field_26342 = LogManager.getLogger();
    private static final Text field_26343 = new TranslatableText("disconnect.exceeded_packet_rate");
    private final int field_26344;

    public class_5472(int i) {
        super(NetworkSide.SERVERBOUND);
        this.field_26344 = i;
    }

    @Override
    protected void method_30615() {
        super.method_30615();
        float f = this.getAveragePacketsReceived();
        if (f > (float)this.field_26344) {
            field_26342.warn("Player exceeded rate-limit (sent {} packets per second)", (Object)Float.valueOf(f));
            this.send(new DisconnectS2CPacket(field_26343), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> this.disconnect(field_26343)));
            this.disableAutoRead();
        }
    }
}

