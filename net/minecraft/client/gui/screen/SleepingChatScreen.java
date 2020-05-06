/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SleepingChatScreen
extends ChatScreen {
    public SleepingChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("multiplayer.stopSleeping"), arg -> this.stopSleeping()));
    }

    @Override
    public void onClose() {
        this.stopSleeping();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.stopSleeping();
        } else if (i == 257 || i == 335) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.client.player.sendChatMessage(string);
            }
            this.chatField.setText("");
            this.client.inGameHud.getChatHud().resetScroll();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void stopSleeping() {
        ClientPlayNetworkHandler lv = this.client.player.networkHandler;
        lv.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
    }
}

