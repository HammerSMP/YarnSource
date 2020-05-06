/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.listener;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

public interface ClientQueryPacketListener
extends PacketListener {
    public void onResponse(QueryResponseS2CPacket var1);

    public void onPong(QueryPongS2CPacket var1);
}

