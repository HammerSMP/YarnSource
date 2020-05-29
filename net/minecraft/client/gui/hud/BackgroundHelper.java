/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BackgroundHelper {

    public static class ColorMixer {
        @Environment(value=EnvType.CLIENT)
        public static int getAlpha(int i) {
            return i >>> 24;
        }

        public static int getRed(int i) {
            return i >> 16 & 0xFF;
        }

        public static int getGreen(int i) {
            return i >> 8 & 0xFF;
        }

        public static int getBlue(int i) {
            return i & 0xFF;
        }

        @Environment(value=EnvType.CLIENT)
        public static int getArgb(int i, int j, int k, int l) {
            return i << 24 | j << 16 | k << 8 | l;
        }

        @Environment(value=EnvType.CLIENT)
        public static int mixColor(int i, int j) {
            return ColorMixer.getArgb(ColorMixer.getAlpha(i) * ColorMixer.getAlpha(j) / 255, ColorMixer.getRed(i) * ColorMixer.getRed(j) / 255, ColorMixer.getGreen(i) * ColorMixer.getGreen(j) / 255, ColorMixer.getBlue(i) * ColorMixer.getBlue(j) / 255);
        }
    }
}

