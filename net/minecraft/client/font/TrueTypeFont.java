/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    private final ByteBuffer field_21839;
    private final STBTTFontinfo info;
    private final float oversample;
    private final IntSet excludedCharacters = new IntArraySet();
    private final float shiftX;
    private final float shiftY;
    private final float scaleFactor;
    private final float ascent;

    public TrueTypeFont(ByteBuffer byteBuffer, STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i, String string) {
        this.field_21839 = byteBuffer;
        this.info = sTBTTFontinfo;
        this.oversample = g;
        string.codePoints().forEach(((IntSet)this.excludedCharacters)::add);
        this.shiftX = h * g;
        this.shiftY = i * g;
        this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight((STBTTFontinfo)sTBTTFontinfo, (float)(f * g));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics((STBTTFontinfo)sTBTTFontinfo, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3);
            this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
        }
    }

    @Override
    @Nullable
    public TtfGlyph getGlyph(int i) {
        if (this.excludedCharacters.contains(i)) {
            return null;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            IntBuffer intBuffer4 = memoryStack.mallocInt(1);
            int j = STBTruetype.stbtt_FindGlyphIndex((STBTTFontinfo)this.info, (int)i);
            if (j == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel((STBTTFontinfo)this.info, (int)j, (float)this.scaleFactor, (float)this.scaleFactor, (float)this.shiftX, (float)this.shiftY, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (IntBuffer)intBuffer4);
            int k = intBuffer3.get(0) - intBuffer.get(0);
            int l = intBuffer4.get(0) - intBuffer2.get(0);
            if (k == 0 || l == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            IntBuffer intBuffer5 = memoryStack.mallocInt(1);
            IntBuffer intBuffer6 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics((STBTTFontinfo)this.info, (int)j, (IntBuffer)intBuffer5, (IntBuffer)intBuffer6);
            TtfGlyph ttfGlyph = new TtfGlyph(intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), (float)intBuffer5.get(0) * this.scaleFactor, (float)intBuffer6.get(0) * this.scaleFactor, j);
            return ttfGlyph;
        }
    }

    @Override
    public void close() {
        this.info.free();
        MemoryUtil.memFree((Buffer)this.field_21839);
    }

    @Override
    public IntSet method_27442() {
        return (IntSet)IntStream.range(0, 65535).filter(i -> !this.excludedCharacters.contains(i)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    @Override
    @Nullable
    public /* synthetic */ RenderableGlyph getGlyph(int i) {
        return this.getGlyph(i);
    }

    @Environment(value=EnvType.CLIENT)
    class TtfGlyph
    implements RenderableGlyph {
        private final int width;
        private final int height;
        private final float bearingX;
        private final float ascent;
        private final float advance;
        private final int glyphIndex;

        private TtfGlyph(int i, int j, int k, int l, float f, float g, int m) {
            this.width = j - i;
            this.height = k - l;
            this.advance = f / TrueTypeFont.this.oversample;
            this.bearingX = (g + (float)i + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
            this.ascent = (TrueTypeFont.this.ascent - (float)k + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
            this.glyphIndex = m;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public float getOversample() {
            return TrueTypeFont.this.oversample;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public float getBearingX() {
            return this.bearingX;
        }

        @Override
        public float getAscent() {
            return this.ascent;
        }

        @Override
        public void upload(int i, int j) {
            NativeImage lv = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
            lv.makeGlyphBitmapSubpixel(TrueTypeFont.this.info, this.glyphIndex, this.width, this.height, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.shiftX, TrueTypeFont.this.shiftY, 0, 0);
            lv.upload(0, i, j, 0, 0, this.width, this.height, false, true);
        }

        @Override
        public boolean hasColor() {
            return false;
        }
    }
}

