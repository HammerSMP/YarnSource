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

    public static NativeImage[] getMipmapLevelsImages(NativeImage arg, int i) {
        NativeImage[] lvs = new NativeImage[i + 1];
        lvs[0] = arg;
        if (i > 0) {
            boolean bl = false;
            block0: for (int j = 0; j < arg.getWidth(); ++j) {
                for (int k = 0; k < arg.getHeight(); ++k) {
                    if (arg.getPixelColor(j, k) >> 24 != 0) continue;
                    bl = true;
                    break block0;
                }
            }
            for (int l = 1; l <= i; ++l) {
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

    private static int blend(int i, int j, int k, int l, boolean bl) {
        if (bl) {
            float f = 0.0f;
            float g = 0.0f;
            float h = 0.0f;
            float m = 0.0f;
            if (i >> 24 != 0) {
                f += MipmapHelper.getColorFraction(i >> 24);
                g += MipmapHelper.getColorFraction(i >> 16);
                h += MipmapHelper.getColorFraction(i >> 8);
                m += MipmapHelper.getColorFraction(i >> 0);
            }
            if (j >> 24 != 0) {
                f += MipmapHelper.getColorFraction(j >> 24);
                g += MipmapHelper.getColorFraction(j >> 16);
                h += MipmapHelper.getColorFraction(j >> 8);
                m += MipmapHelper.getColorFraction(j >> 0);
            }
            if (k >> 24 != 0) {
                f += MipmapHelper.getColorFraction(k >> 24);
                g += MipmapHelper.getColorFraction(k >> 16);
                h += MipmapHelper.getColorFraction(k >> 8);
                m += MipmapHelper.getColorFraction(k >> 0);
            }
            if (l >> 24 != 0) {
                f += MipmapHelper.getColorFraction(l >> 24);
                g += MipmapHelper.getColorFraction(l >> 16);
                h += MipmapHelper.getColorFraction(l >> 8);
                m += MipmapHelper.getColorFraction(l >> 0);
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
        int r = MipmapHelper.getColorComponent(i, j, k, l, 24);
        int s = MipmapHelper.getColorComponent(i, j, k, l, 16);
        int t = MipmapHelper.getColorComponent(i, j, k, l, 8);
        int u = MipmapHelper.getColorComponent(i, j, k, l, 0);
        return r << 24 | s << 16 | t << 8 | u;
    }

    private static int getColorComponent(int i, int j, int k, int l, int m) {
        float f = MipmapHelper.getColorFraction(i >> m);
        float g = MipmapHelper.getColorFraction(j >> m);
        float h = MipmapHelper.getColorFraction(k >> m);
        float n = MipmapHelper.getColorFraction(l >> m);
        float o = (float)((double)((float)Math.pow((double)(f + g + h + n) * 0.25, 0.45454545454545453)));
        return (int)((double)o * 255.0);
    }

    private static float getColorFraction(int i) {
        return COLOR_FRACTIONS[i & 0xFF];
    }
}

