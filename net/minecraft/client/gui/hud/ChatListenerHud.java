/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatListenerHud
implements ClientChatListener {
    private final MinecraftClient client;

    public ChatListenerHud(MinecraftClient arg) {
        this.client = arg;
    }

    @Override
    public void onChatMessage(MessageType arg, Text arg2, UUID uUID) {
        if (this.client.method_29042(uUID)) {
            return;
        }
        if (arg != MessageType.CHAT) {
            this.client.inGameHud.getChatHud().addMessage(arg2);
        } else {
            this.client.inGameHud.getChatHud().method_27147(arg2);
        }
    }
}

