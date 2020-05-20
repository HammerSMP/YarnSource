/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class ResettingWorldTask
extends LongRunningTask {
    private final String seed;
    private final WorldTemplate worldTemplate;
    private final int levelType;
    private final boolean generateStructures;
    private final long serverId;
    private String title = I18n.translate("mco.reset.world.resetting.screen.title", new Object[0]);
    private final Runnable callback;

    public ResettingWorldTask(@Nullable String string, @Nullable WorldTemplate arg, int i, boolean bl, long l, @Nullable String string2, Runnable runnable) {
        this.seed = string;
        this.worldTemplate = arg;
        this.levelType = i;
        this.generateStructures = bl;
        this.serverId = l;
        if (string2 != null) {
            this.title = string2;
        }
        this.callback = runnable;
    }

    @Override
    public void run() {
        RealmsClient lv = RealmsClient.createRealmsClient();
        this.setTitle(this.title);
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                if (this.worldTemplate != null) {
                    lv.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
                } else {
                    lv.resetWorldWithSeed(this.serverId, this.seed, this.levelType, this.generateStructures);
                }
                if (this.aborted()) {
                    return;
                }
                this.callback.run();
                return;
            }
            catch (RetryCallException lv2) {
                if (this.aborted()) {
                    return;
                }
                ResettingWorldTask.pause(lv2.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't reset world");
                this.error(exception.toString());
                return;
            }
        }
    }
}

