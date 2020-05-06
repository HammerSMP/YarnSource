/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public enum WhiteRectangleGlyph implements RenderableGlyph
{
    INSTANCE;

    private static final NativeImage IMAGE;

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    @Override
    public float getAdvance() {
        return 6.0f;
    }

    @Override
    public float getOversample() {
        return 1.0f;
    }

    @Override
    public void upload(int i, int j) {
        IMAGE.upload(0, i, j, false);
    }

    @Override
    public boolean hasColor() {
        return true;
    }

    static {
        IMAGE = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), arg -> {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 5; ++j) {
                    boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                    arg.setPixelRgba(j, i, -1);
                }
            }
            arg.untrack();
        });
    }
}

