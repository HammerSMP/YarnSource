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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsConnect;
import net.minecraft.client.realms.RealmsServerAddress;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectTask
extends LongRunningTask {
    private final RealmsConnect realmsConnect;
    private final net.minecraft.client.realms.dto.RealmsServerAddress a;

    public RealmsConnectTask(Screen lastScreen, net.minecraft.client.realms.dto.RealmsServerAddress address) {
        this.a = address;
        this.realmsConnect = new RealmsConnect(lastScreen);
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.connect.connecting", new Object[0]));
        RealmsServerAddress lv = RealmsServerAddress.parseString(this.a.address);
        this.realmsConnect.connect(lv.getHost(), lv.getPort());
    }

    @Override
    public void abortTask() {
        this.realmsConnect.abort();
        MinecraftClient.getInstance().getResourcePackDownloader().clear();
    }

    @Override
    public void tick() {
        this.realmsConnect.tick();
    }
}

