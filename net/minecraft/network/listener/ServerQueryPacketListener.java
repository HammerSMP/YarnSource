/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;

public interface ServerQueryPacketListener
extends PacketListener {
    public void onPing(QueryPingC2SPacket var1);

    public void onRequest(QueryRequestC2SPacket var1);
}

