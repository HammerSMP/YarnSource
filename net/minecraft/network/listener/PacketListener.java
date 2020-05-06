/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.network.listener;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

public interface PacketListener {
    public void onDisconnected(Text var1);

    public ClientConnection getConnection();
}

