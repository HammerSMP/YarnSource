/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.time.Duration;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RepeatedNarrator;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class Realms {
    private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

    public static void narrateNow(String message) {
        NarratorManager lv = NarratorManager.INSTANCE;
        lv.clear();
        lv.onChatMessage(MessageType.SYSTEM, new LiteralText(Realms.fixNarrationNewlines(message)), Util.NIL_UUID);
    }

    private static String fixNarrationNewlines(String lines) {
        return lines.replace("\\n", System.lineSeparator());
    }

    public static void narrateNow(String ... lines) {
        Realms.narrateNow(Arrays.asList(lines));
    }

    public static void narrateNow(Iterable<String> lines) {
        Realms.narrateNow(Realms.joinNarrations(lines));
    }

    public static String joinNarrations(Iterable<String> lines) {
        return String.join((CharSequence)System.lineSeparator(), lines);
    }

    public static void narrateRepeatedly(String lines) {
        REPEATED_NARRATOR.narrate(Realms.fixNarrationNewlines(lines));
    }
}

