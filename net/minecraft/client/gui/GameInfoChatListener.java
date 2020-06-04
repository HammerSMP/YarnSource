/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GameInfoChatListener
implements ClientChatListener {
    private final MinecraftClient client;

    public GameInfoChatListener(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void onChatMessage(MessageType arg, Text arg2, UUID uUID) {
        if (this.client.shouldBlockMessages(uUID)) {
            return;
        }
        this.client.inGameHud.setOverlayMessage(arg2, false);
    }
}

