/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.RealmsScreen;

@Environment(value=EnvType.CLIENT)
public class RealmsBridge
extends RealmsScreen {
    private Screen previousScreen;

    public void switchToRealms(Screen arg) {
        this.previousScreen = arg;
        MinecraftClient.getInstance().openScreen(new RealmsMainScreen(this));
    }

    @Nullable
    public RealmsScreen getNotificationScreen(Screen arg) {
        this.previousScreen = arg;
        return new RealmsNotificationsScreen();
    }

    @Override
    public void init() {
        MinecraftClient.getInstance().openScreen(this.previousScreen);
    }
}

