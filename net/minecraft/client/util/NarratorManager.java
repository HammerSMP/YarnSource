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
    public void onChatMessage(MessageType arg, Text arg2) {
        NarratorOption lv = NarratorManager.getNarratorOption();
        if (lv == NarratorOption.OFF || !this.narrator.active()) {
            return;
        }
        if (lv == NarratorOption.ALL || lv == NarratorOption.CHAT && arg == MessageType.CHAT || lv == NarratorOption.SYSTEM && arg == MessageType.SYSTEM) {
            Text lv3;
            if (arg2 instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)arg2).getKey())) {
                TranslatableText lv2 = new TranslatableText("chat.type.text.narrate", ((TranslatableText)arg2).getArgs());
            } else {
                lv3 = arg2;
            }
            this.narrate(arg.interruptsNarration(), lv3.getString());
        }
    }

    public void narrate(String string) {
        NarratorOption lv = NarratorManager.getNarratorOption();
        if (this.narrator.active() && lv != NarratorOption.OFF && lv != NarratorOption.CHAT && !string.isEmpty()) {
            this.narrator.clear();
            this.narrate(true, string);
        }
    }

    private static NarratorOption getNarratorOption() {
        return MinecraftClient.getInstance().options.narrator;
    }

    private void narrate(boolean bl, String string) {
        if (SharedConstants.isDevelopment) {
            LOGGER.debug("Narrating: {}", (Object)string);
        }
        this.narrator.say(string, bl);
    }

    public void addToast(NarratorOption arg) {
        this.clear();
        this.narrator.say(new TranslatableText("options.narrator").append(" : ").append(arg.getTranslationKey()).getString(), true);
        ToastManager lv = MinecraftClient.getInstance().getToastManager();
        if (this.narrator.active()) {
            if (arg == NarratorOption.OFF) {
                SystemToast.show(lv, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled"), null);
            } else {
                SystemToast.show(lv, SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.enabled"), arg.getTranslationKey());
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

