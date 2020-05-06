/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class WorldCreationTask
extends LongRunningTask {
    private final String name;
    private final String motd;
    private final long worldId;
    private final Screen lastScreen;

    public WorldCreationTask(long l, String string, String string2, Screen arg) {
        this.worldId = l;
        this.name = string;
        this.motd = string2;
        this.lastScreen = arg;
    }

    @Override
    public void run() {
        String string = I18n.translate("mco.create.world.wait", new Object[0]);
        this.setTitle(string);
        RealmsClient lv = RealmsClient.createRealmsClient();
        try {
            lv.initializeWorld(this.worldId, this.name, this.motd);
            WorldCreationTask.setScreen(this.lastScreen);
        }
        catch (RealmsServiceException lv2) {
            LOGGER.error("Couldn't create world");
            this.method_27453(lv2.toString());
        }
        catch (Exception exception) {
            LOGGER.error("Could not create world");
            this.method_27453(exception.getLocalizedMessage());
        }
    }
}

