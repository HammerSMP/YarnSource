/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.TranslationStorage;

@Environment(value=EnvType.CLIENT)
public class I18n {
    private static TranslationStorage storage;

    static void setLanguage(TranslationStorage arg) {
        storage = arg;
    }

    public static String translate(String string, Object ... objects) {
        return storage.translate(string, objects);
    }

    public static boolean hasTranslation(String string) {
        return storage.containsKey(string);
    }
}

