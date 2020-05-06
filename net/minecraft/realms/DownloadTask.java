/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class DownloadTask
extends LongRunningTask {
    private final long worldId;
    private final int slot;
    private final Screen lastScreen;
    private final String downloadName;

    public DownloadTask(long l, int i, String string, Screen arg) {
        this.worldId = l;
        this.slot = i;
        this.lastScreen = arg;
        this.downloadName = string;
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.download.preparing", new Object[0]));
        RealmsClient lv = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                WorldDownload lv2 = lv.download(this.worldId, this.slot);
                DownloadTask.pause(1);
                if (this.aborted()) {
                    return;
                }
                DownloadTask.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, lv2, this.downloadName, bl -> {}));
                return;
            }
            catch (RetryCallException lv3) {
                if (this.aborted()) {
                    return;
                }
                DownloadTask.pause(lv3.delaySeconds);
                continue;
            }
            catch (RealmsServiceException lv4) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data");
                DownloadTask.setScreen(new RealmsGenericErrorScreen(lv4, this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data", (Throwable)exception);
                this.method_27453(exception.getLocalizedMessage());
                return;
            }
        }
    }
}

