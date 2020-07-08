/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum SizeUnit {
    B,
    KB,
    MB,
    GB;


    public static SizeUnit getLargestUnit(long l) {
        if (l < 1024L) {
            return B;
        }
        try {
            int i = (int)(Math.log(l) / Math.log(1024.0));
            String string = String.valueOf("KMGTPE".charAt(i - 1));
            return SizeUnit.valueOf(string + "B");
        }
        catch (Exception exception) {
            return GB;
        }
    }

    public static double convertToUnit(long l, SizeUnit arg) {
        if (arg == B) {
            return l;
        }
        return (double)l / Math.pow(1024.0, arg.ordinal());
    }

    public static String getUserFriendlyString(long l) {
        int i = 1024;
        if (l < 1024L) {
            return l + " B";
        }
        int j = (int)(Math.log(l) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB", (double)l / Math.pow(1024.0, j), string);
    }

    public static String humanReadableSize(long l, SizeUnit arg) {
        return String.format("%." + (arg == GB ? "1" : "0") + "f %s", SizeUnit.convertToUnit(l, arg), arg.name());
    }
}

