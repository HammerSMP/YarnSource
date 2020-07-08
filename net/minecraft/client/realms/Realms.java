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

    public static void narrateNow(String string) {
        NarratorManager lv = NarratorManager.INSTANCE;
        lv.clear();
        lv.onChatMessage(MessageType.SYSTEM, new LiteralText(Realms.fixNarrationNewlines(string)), Util.NIL_UUID);
    }

    private static String fixNarrationNewlines(String string) {
        return string.replace("\\n", System.lineSeparator());
    }

    public static void narrateNow(String ... strings) {
        Realms.narrateNow(Arrays.asList(strings));
    }

    public static void narrateNow(Iterable<String> iterable) {
        Realms.narrateNow(Realms.joinNarrations(iterable));
    }

    public static String joinNarrations(Iterable<String> iterable) {
        return String.join((CharSequence)System.lineSeparator(), iterable);
    }

    public static void narrateRepeatedly(String string) {
        REPEATED_NARRATOR.narrate(Realms.fixNarrationNewlines(string));
    }
}

