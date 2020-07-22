/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class SkinProcessor {
    private int[] pixels;
    private int width;
    private int height;

    @Nullable
    public BufferedImage process(BufferedImage image) {
        boolean bl;
        if (image == null) {
            return null;
        }
        this.width = 64;
        this.height = 64;
        BufferedImage bufferedImage2 = new BufferedImage(this.width, this.height, 2);
        Graphics graphics = bufferedImage2.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        boolean bl2 = bl = image.getHeight() == 32;
        if (bl) {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);
            graphics.drawImage(bufferedImage2, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(bufferedImage2, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(bufferedImage2, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(bufferedImage2, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(bufferedImage2, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(bufferedImage2, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(bufferedImage2, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(bufferedImage2, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(bufferedImage2, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(bufferedImage2, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(bufferedImage2, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(bufferedImage2, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();
        this.pixels = ((DataBufferInt)bufferedImage2.getRaster().getDataBuffer()).getData();
        this.setNoAlpha(0, 0, 32, 16);
        if (bl) {
            this.doNotchTransparencyHack(32, 0, 64, 32);
        }
        this.setNoAlpha(0, 16, 64, 32);
        this.setNoAlpha(16, 48, 48, 64);
        return bufferedImage2;
    }

    private void doNotchTransparencyHack(int x0, int y0, int x1, int y1) {
        for (int m = x0; m < x1; ++m) {
            for (int n = y0; n < y1; ++n) {
                int o = this.pixels[m + n * this.width];
                if ((o >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int p = x0; p < x1; ++p) {
            for (int q = y0; q < y1; ++q) {
                int n = p + q * this.width;
                this.pixels[n] = this.pixels[n] & 0xFFFFFF;
            }
        }
    }

    private void setNoAlpha(int x0, int y0, int x1, int y1) {
        for (int m = x0; m < x1; ++m) {
            for (int n = y0; n < y1; ++n) {
                int n2 = m + n * this.width;
                this.pixels[n2] = this.pixels[n2] | 0xFF000000;
            }
        }
    }
}

