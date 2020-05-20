/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class LanServerInfo {
    private final String motd;
    private final String addressPort;
    private long lastTimeMillis;

    public LanServerInfo(String string, String string2) {
        this.motd = string;
        this.addressPort = string2;
        this.lastTimeMillis = Util.getMeasuringTimeMs();
    }

    public String getMotd() {
        return this.motd;
    }

    public String getAddressPort() {
        return this.addressPort;
    }

    public void updateLastTime() {
        this.lastTimeMillis = Util.getMeasuringTimeMs();
    }
}
