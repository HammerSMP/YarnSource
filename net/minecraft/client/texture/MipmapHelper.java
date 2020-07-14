/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MipmapHelper {
    private static final float[] COLOR_FRACTIONS = Util.make(new float[256], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i) {
            fs[i] = (float)Math.pow((float)i / 255.0f, 2.2);
        }
    });

    public static NativeImage[] getMipmapLevelsImages(NativeImage image, int mipmap) {
        NativeImage[] lvs = new NativeImage[mipmap + 1];
        lvs[0] = image;
        if (mipmap > 0) {
            boolean bl = false;
            block0: for (int j = 0; j < image.getWidth(); ++j) {
                for (int k = 0; k < image.getHeight(); ++k) {
                    if (image.getPixelColor(j, k) >> 24 != 0) continue;
                    bl = true;
                    break block0;
                }
            }
            for (int l = 1; l <= mipmap; ++l) {
                NativeImage lv = lvs[l - 1];
                NativeImage lv2 = new NativeImage(lv.getWidth() >> 1, lv.getHeight() >> 1, false);
                int m = lv2.getWidth();
                int n = lv2.getHeight();
                for (int o = 0; o < m; ++o) {
                    for (int p = 0; p < n; ++p) {
                        lv2.setPixelColor(o, p, MipmapHelper.blend(lv.getPixelColor(o * 2 + 0, p * 2 + 0), lv.getPixelColor(o * 2 + 1, p * 2 + 0), lv.getPixelColor(o * 2 + 0, p * 2 + 1), lv.getPixelColor(o * 2 + 1, p * 2 + 1), bl));
                    }
                }
                lvs[l] = lv2;
            }
        }
        return lvs;
    }

    private static int blend(int one, int two, int three, int four, boolean checkAlpha) {
        if (checkAlpha) {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            float m = 0.0f;
            if (one >> 24 != 0) {
                f += MipmapHelper.getColorFraction(one >> 24);
                g += MipmapHelper.getColorFraction(one >> 16);
                h += MipmapHelper.getColorFraction(one >> 8);
                m += MipmapHelper.getColorFraction(one >> 0);
            }
            if (two >> 24 != 0) {
                f += MipmapHelper.getColorFraction(two >> 24);
                g += MipmapHelper.getColorFraction(two >> 16);
                h += MipmapHelper.getColorFraction(two >> 8);
                m += MipmapHelper.getColorFraction(two >> 0);
            }
            if (three >> 24 != 0) {
                f += MipmapHelper.getColorFraction(three >> 24);
                g += MipmapHelper.getColorFraction(three >> 16);
                h += MipmapHelper.getColorFraction(three >> 8);
                m += MipmapHelper.getColorFraction(three >> 0);
            }
            if (four >> 24 != 0) {
                f += MipmapHelper.getColorFraction(four >> 24);
                g += MipmapHelper.getColorFraction(four >> 16);
                h += MipmapHelper.getColorFraction(four >> 8);
                m += MipmapHelper.getColorFraction(four >> 0);
            }
            int n = (int)(Math.pow(f /= 4.0f, 0.45454545454545453) * 255.0);
            int o = (int)(Math.pow(g /= 4.0f, 0.45454545454545453) * 255.0);
            int p = (int)(Math.pow(h /= 4.0f, 0.45454545454545453) * 255.0);
            int q = (int)(Math.pow(m /= 4.0f, 0.45454545454545453) * 255.0);
            if (n < 96) {
                n = 0;
            }
            return n << 24 | o << 16 | p << 8 | q;
        }
        int r = MipmapHelper.getColorComponent(one, two, three, four, 24);
        int s = MipmapHelper.getColorComponent(one, two, three, four, 16);
        int t = MipmapHelper.getColorComponent(one, two, three, four, 8);
        int u = MipmapHelper.getColorComponent(one, two, three, four, 0);
        return r << 24 | s << 16 | t << 8 | u;
    }

    private static int getColorComponent(int one, int two, int three, int four, int bits) {
        float f = MipmapHelper.getColorFraction(one >> bits);
        float g = MipmapHelper.getColorFraction(two >> bits);
        float h = MipmapHelper.getColorFraction(three >> bits);
        float n = MipmapHelper.getColorFraction(four >> bits);
        float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
        return (int)((double)o * 255.0);
    }

    private static float getColorFraction(int value) {
        return COLOR_FRACTIONS[value & 0xFF];
    }
}

