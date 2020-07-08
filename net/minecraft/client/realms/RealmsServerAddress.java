/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public class RealmsServerAddress {
    private final String host;
    private final int port;

    protected RealmsServerAddress(String string, int i) {
        this.host = string;
        this.port = i;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static RealmsServerAddress parseString(String string) {
        ServerAddress lv = ServerAddress.parse(string);
        return new RealmsServerAddress(lv.getAddress(), lv.getPort());
    }
}

