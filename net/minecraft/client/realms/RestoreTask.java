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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class RestoreTask
extends LongRunningTask {
    private final Backup backup;
    private final long worldId;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup arg, long l, RealmsConfigureWorldScreen arg2) {
        this.backup = arg;
        this.worldId = l;
        this.lastScreen = arg2;
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.backup.restoring", new Object[0]));
        RealmsClient lv = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                lv.restoreWorld(this.worldId, this.backup.backupId);
                RestoreTask.pause(1);
                if (this.aborted()) {
                    return;
                }
                RestoreTask.setScreen(this.lastScreen.getNewScreen());
                return;
            }
            catch (RetryCallException lv2) {
                if (this.aborted()) {
                    return;
                }
                RestoreTask.pause(lv2.delaySeconds);
                continue;
            }
            catch (RealmsServiceException lv3) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)lv3);
                RestoreTask.setScreen(new RealmsGenericErrorScreen(lv3, (Screen)this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't restore backup", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                return;
            }
        }
    }
}

