/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import java.util.IllegalFormatException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;

@Environment(value=EnvType.CLIENT)
public class I18n {
    private static volatile Language field_25290 = Language.getInstance();

    static void method_29391(Language arg) {
        field_25290 = arg;
    }

    public static String translate(String string, Object ... objects) {
        String string2 = field_25290.get(string);
        try {
            return String.format(string2, objects);
        }
        catch (IllegalFormatException illegalFormatException) {
            return "Format error: " + string2;
        }
    }

    public static boolean hasTranslation(String string) {
        return field_25290.hasTranslation(string);
    }
}

