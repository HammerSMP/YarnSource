/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GlyphAtlasTexture
extends AbstractTexture
implements Closeable {
    private final Identifier id;
    private final RenderLayer field_21690;
    private final RenderLayer field_21691;
    private final boolean hasColor;
    private final Slot rootSlot;

    public GlyphAtlasTexture(Identifier arg, boolean bl) {
        this.id = arg;
        this.hasColor = bl;
        this.rootSlot = new Slot(0, 0, 256, 256);
        TextureUtil.allocate(bl ? NativeImage.GLFormat.ABGR : NativeImage.GLFormat.INTENSITY, this.getGlId(), 256, 256);
        this.field_21690 = RenderLayer.getText(arg);
        this.field_21691 = RenderLayer.getTextSeeThrough(arg);
    }

    @Override
    public void load(ResourceManager arg) {
    }

    @Override
    public void close() {
        this.clearGlId();
    }

    @Nullable
    public GlyphRenderer getGlyphRenderer(RenderableGlyph arg) {
        if (arg.hasColor() != this.hasColor) {
            return null;
        }
        Slot lv = this.rootSlot.findSlotFor(arg);
        if (lv != null) {
            this.bindTexture();
            arg.upload(lv.x, lv.y);
            float f = 256.0f;
            float g = 256.0f;
            float h = 0.01f;
            return new GlyphRenderer(this.field_21690, this.field_21691, ((float)lv.x + 0.01f) / 256.0f, ((float)lv.x - 0.01f + (float)arg.getWidth()) / 256.0f, ((float)lv.y + 0.01f) / 256.0f, ((float)lv.y - 0.01f + (float)arg.getHeight()) / 256.0f, arg.getXMin(), arg.getXMax(), arg.getYMin(), arg.getYMax());
        }
        return null;
    }

    public Identifier getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    static class Slot {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private Slot subSlot1;
        private Slot subSlot2;
        private boolean occupied;

        private Slot(int i, int j, int k, int l) {
            this.x = i;
            this.y = j;
            this.width = k;
            this.height = l;
        }

        @Nullable
        Slot findSlotFor(RenderableGlyph arg) {
            if (this.subSlot1 != null && this.subSlot2 != null) {
                Slot lv = this.subSlot1.findSlotFor(arg);
                if (lv == null) {
                    lv = this.subSlot2.findSlotFor(arg);
                }
                return lv;
            }
            if (this.occupied) {
                return null;
            }
            int i = arg.getWidth();
            int j = arg.getHeight();
            if (i > this.width || j > this.height) {
                return null;
            }
            if (i == this.width && j == this.height) {
                this.occupied = true;
                return this;
            }
            int k = this.width - i;
            int l = this.height - j;
            if (k > l) {
                this.subSlot1 = new Slot(this.x, this.y, i, this.height);
                this.subSlot2 = new Slot(this.x + i + 1, this.y, this.width - i - 1, this.height);
            } else {
                this.subSlot1 = new Slot(this.x, this.y, this.width, j);
                this.subSlot2 = new Slot(this.x, this.y + j + 1, this.width, this.height - j - 1);
            }
            return this.subSlot1.findSlotFor(arg);
        }
    }
}

