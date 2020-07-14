/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.Errable;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LongRunningTask
implements Errable,
Runnable {
    public static final Logger LOGGER = LogManager.getLogger();
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

    protected static void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("", (Throwable)interruptedException);
        }
    }

    public static void setScreen(Screen screen) {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.execute(() -> lv.openScreen(screen));
    }

    public void setScreen(RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen) {
        this.longRunningMcoTaskScreen = longRunningMcoTaskScreen;
    }

    @Override
    public void error(Text arg) {
        this.longRunningMcoTaskScreen.error(arg);
    }

    public void setTitle(String title) {
        this.longRunningMcoTaskScreen.setTitle(title);
    }

    public boolean aborted() {
        return this.longRunningMcoTaskScreen.aborted();
    }

    public void tick() {
    }

    public void init() {
    }

    public void abortTask() {
    }
}

