/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.client.Errable;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LongRunningTask
implements Errable,
Runnable {
    public static final Logger LOGGER = LogManager.getLogger();
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

    protected static void pause(int i) {
        try {
            Thread.sleep(i * 1000);
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("", (Throwable)interruptedException);
        }
    }

    public static void setScreen(Screen arg) {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.execute(() -> lv.openScreen(arg));
    }

    public void setScreen(RealmsLongRunningMcoTaskScreen arg) {
        this.longRunningMcoTaskScreen = arg;
    }

    @Override
    public void error(Text arg) {
        this.longRunningMcoTaskScreen.error(arg);
    }

    public void setTitle(String string) {
        this.longRunningMcoTaskScreen.setTitle(string);
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

