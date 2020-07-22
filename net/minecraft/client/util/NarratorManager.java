/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.text2speech.Narrator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NarratorManager
implements ClientChatListener {
    public static final Text EMPTY = LiteralText.EMPTY;
    private static final Logger LOGGER = LogManager.getLogger();
    public static final NarratorManager INSTANCE = new NarratorManager();
    private final Narrator narrator = Narrator.getNarrator();

    @Override
    public void onChatMessage(MessageType messageType, Text message, UUID senderUuid) {
        if (MinecraftClient.getInstance().shouldBlockMessages(senderUuid)) {
            return;
        }
        NarratorOption lv = NarratorManager.getNarratorOption();
        if (lv == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        if (lv == NarratorOption.ALL || lv == NarratorOption.CHAT && messageType == MessageType.CHAT || lv == NarratorOption.SYSTEM && messageType == MessageType.SYSTEM) {
            Text lv3;
            if (message instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)message).getKey())) {
                TranslatableText lv2 = new TranslatableText("chat.type.text.narrate", ((TranslatableText)message).getArgs());
            } else {
                lv3 = message;
            }
            this.narrate(messageType.interruptsNarration(), lv3.getString());
        }
    }

    public void narrate(String text) {
        NarratorOption lv = NarratorManager.getNarratorOption();
        if (this.narrator.active() && lv != NarratorOption.OFF && lv != NarratorOption.CHAT && !text.isEmpty()) {
            this.narrator.clear();
            this.narrate(true, text);
        }
    }

    private static NarratorOption getNarratorOption() {
        return MinecraftClient.getInstance().options.narrator;
    }

    private void narrate(boolean interrupt, String message) {
        if (SharedConstants.isDevelopment) {
            LOGGER.debug("Narrating: {}", (Object)message.replaceAll("\n", "\\\\n"));
        }
        this.narrator.say(message, interrupt);
    }

    public void addToast(NarratorOption option) {
        this.clear();
        this.narrator.say(new TranslatableText("options.narrator").append(" : ").append(option.getTranslationKey()).getString(), true);
        ToastManager lv = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (option == NarratorOption.OFF) {
                SystemToast.show(lv, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled"), null);
            } else {
                SystemToast.show(lv, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.enabled"), option.getTranslationKey());
            }
        } else {
            SystemToast.show(lv, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled"), new TranslatableText("options.narrator.notavailable"));
        }
    }

    public boolean isActive() {
        return this.narrator.active();
    }

    public void clear() {
        if (NarratorManager.getNarratorOption() == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        this.narrator.clear();
    }

    public void destroy() {
        this.narrator.destroy();
    }
}

