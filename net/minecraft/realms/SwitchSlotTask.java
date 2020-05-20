/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class SwitchSlotTask
extends LongRunningTask {
    private final long worldId;
    private final int slot;
    private final Runnable callback;

    public SwitchSlotTask(long l, int i, Runnable runnable) {
        this.worldId = l;
        this.slot = i;
        this.callback = runnable;
    }

    @Override
    public void run() {
        RealmsClient lv = RealmsClient.createRealmsClient();
        String string = I18n.translate("mco.minigame.world.slot.screen.title", new Object[0]);
        this.setTitle(string);
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (!lv.switchSlot(this.worldId, this.slot)) continue;
                this.callback.run();
                break;
            }
            catch (RetryCallException lv2) {
                if (this.aborted()) {
                    return;
                }
                SwitchSlotTask.pause(lv2.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't switch world!");
                this.error(exception.toString());
            }
        }
    }
}

