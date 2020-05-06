/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class OpenServerTask
extends LongRunningTask {
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final RealmsMainScreen mainScreen;

    public OpenServerTask(RealmsServer arg, Screen arg2, RealmsMainScreen arg3, boolean bl) {
        this.serverData = arg;
        this.returnScreen = arg2;
        this.join = bl;
        this.mainScreen = arg3;
    }

    @Override
    public void run() {
        this.setTitle(I18n.translate("mco.configure.world.opening", new Object[0]));
        RealmsClient lv = RealmsClient.createRealmsClient();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = lv.open(this.serverData.id);
                if (!bl) continue;
                if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                    ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                }
                this.serverData.state = RealmsServer.State.OPEN;
                if (this.join) {
                    this.mainScreen.play(this.serverData, this.returnScreen);
                    break;
                }
                OpenServerTask.setScreen(this.returnScreen);
                break;
            }
            catch (RetryCallException lv2) {
                if (this.aborted()) {
                    return;
                }
                OpenServerTask.pause(lv2.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to open server", (Throwable)exception);
                this.method_27453("Failed to open the server");
            }
        }
    }
}

