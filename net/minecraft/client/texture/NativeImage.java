/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.stb.STBIWriteCallback
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.stb.STBImageResize
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.Untracker;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class NativeImage
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean isStbImage;
    private long pointer;
    private final long sizeBytes;

    public NativeImage(int i, int j, boolean bl) {
        this(Format.RGBA, i, j, bl);
    }

    public NativeImage(Format arg, int i, int j, boolean bl) {
        this.format = arg;
        this.width = i;
        this.height = j;
        this.sizeBytes = (long)i * (long)j * (long)arg.getChannelCount();
        this.isStbImage = false;
        this.pointer = bl ? MemoryUtil.nmemCalloc((long)1L, (long)this.sizeBytes) : MemoryUtil.nmemAlloc((long)this.sizeBytes);
    }

    private NativeImage(Format arg, int i, int j, boolean bl, long l) {
        this.format = arg;
        this.width = i;
        this.height = j;
        this.isStbImage = bl;
        this.pointer = l;
        this.sizeBytes = i * j * arg.getChannelCount();
    }

    public String toString() {
        return "NativeImage[" + (Object)((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }

    public static NativeImage read(InputStream inputStream) throws IOException {
        return NativeImage.read(Format.RGBA, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format arg, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.method_24962(inputStream);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(arg, byteBuffer);
            return nativeImage;
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
            IOUtils.closeQuietly((InputStream)inputStream);
        }
    }

    public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
        return NativeImage.read(Format.RGBA, byteBuffer);
    }

    public static NativeImage read(@Nullable Format arg, ByteBuffer byteBuffer) throws IOException {
        if (arg != null && !arg.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to read format " + (Object)((Object)arg));
        }
        if (MemoryUtil.memAddress((ByteBuffer)byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)(arg == null ? 0 : arg.channelCount));
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage(arg == null ? Format.getFormat(intBuffer3.get(0)) : arg, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress((ByteBuffer)byteBuffer2));
            return nativeImage;
        }
    }

    private static void setTextureClamp(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (bl) {
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
        } else {
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
        }
    }

    private static void setTextureFilter(boolean bl, boolean bl2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (bl) {
            GlStateManager.texParameter(3553, 10241, bl2 ? 9987 : 9729);
            GlStateManager.texParameter(3553, 10240, 9729);
        } else {
            GlStateManager.texParameter(3553, 10241, bl2 ? 9986 : 9728);
            GlStateManager.texParameter(3553, 10240, 9728);
        }
    }

    private void checkAllocated() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            if (this.isStbImage) {
                STBImage.nstbi_image_free((long)this.pointer);
            } else {
                MemoryUtil.nmemFree((long)this.pointer);
            }
        }
        this.pointer = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format getFormat() {
        return this.format;
    }

    public int getPixelRgba(int i, int j) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        this.checkAllocated();
        long l = (i + j * this.width) * 4;
        return MemoryUtil.memGetInt((long)(this.pointer + l));
    }

    public void setPixelRgba(int i, int j, int k) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        this.checkAllocated();
        long l = (i + j * this.width) * 4;
        MemoryUtil.memPutInt((long)(this.pointer + l), (int)k);
    }

    public byte getPixelOpacity(int i, int j) {
        if (!this.format.hasOpacityChannel()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        int k = (i + j * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8;
        return MemoryUtil.memGetByte((long)(this.pointer + (long)k));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] is = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                int p;
                int k = this.getPixelRgba(j, i);
                int l = NativeImage.method_24030(k);
                int m = NativeImage.method_24035(k);
                int n = NativeImage.method_24034(k);
                int o = NativeImage.method_24033(k);
                is[j + i * this.getWidth()] = p = l << 24 | o << 16 | n << 8 | m;
            }
        }
        return is;
    }

    public void upload(int i, int j, int k, boolean bl) {
        this.upload(i, j, k, 0, 0, this.width, this.height, false, bl);
    }

    public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2) {
        this.upload(i, j, k, l, m, n, o, false, false, bl, bl2);
    }

    public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.uploadInternal(i, j, k, l, m, n, o, bl, bl2, bl3, bl4));
        } else {
            this.uploadInternal(i, j, k, l, m, n, o, bl, bl2, bl3, bl4);
        }
    }

    private void uploadInternal(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.checkAllocated();
        NativeImage.setTextureFilter(bl, bl3);
        NativeImage.setTextureClamp(bl2);
        if (n == this.getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        } else {
            GlStateManager.pixelStore(3314, this.getWidth());
        }
        GlStateManager.pixelStore(3316, l);
        GlStateManager.pixelStore(3315, m);
        this.format.setUnpackAlignment();
        GlStateManager.texSubImage2D(3553, i, j, k, n, o, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (bl4) {
            this.close();
        }
    }

    public void loadFromTextureImage(int i, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager.getTexImage(3553, i, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (bl && this.format.hasAlphaChannel()) {
            for (int j = 0; j < this.getHeight(); ++j) {
                for (int k = 0; k < this.getWidth(); ++k) {
                    this.setPixelRgba(k, j, this.getPixelRgba(k, j) | 255 << this.format.getAlphaChannelOffset());
                }
            }
        }
    }

    public void writeFile(File file) throws IOException {
        this.writeFile(file.toPath());
    }

    public void makeGlyphBitmapSubpixel(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
        if (m < 0 || m + j > this.getWidth() || n < 0 || n + k > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.getWidth(), this.getHeight()));
        }
        if (this.format.getChannelCount() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel((long)sTBTTFontinfo.address(), (long)(this.pointer + (long)m + (long)(n * this.getWidth())), (int)j, (int)k, (int)this.getWidth(), (float)f, (float)g, (float)h, (float)l, (int)i);
    }

    public void writeFile(Path path) throws IOException {
        if (!this.format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to write format " + (Object)((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0]);){
            if (!this.method_24032(writableByteChannel)) {
                throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
        }
    }

    /*
     * Exception decompiling
     */
    public byte[] getBytes() throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean method_24032(WritableByteChannel writableByteChannel) throws IOException {
        WriteCallback lv = new WriteCallback(writableByteChannel);
        try {
            int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.getChannelCount());
            if (i < this.getHeight()) {
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)i);
            }
            if (STBImageWrite.nstbi_write_png_to_func((long)lv.address(), (long)0L, (int)this.getWidth(), (int)i, (int)this.format.getChannelCount(), (long)this.pointer, (int)0) == 0) {
                boolean bl = false;
                return bl;
            }
            lv.throwStoredException();
            boolean bl = true;
            return bl;
        }
        finally {
            lv.free();
        }
    }

    public void copyFrom(NativeImage arg) {
        if (arg.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int i = this.format.getChannelCount();
        this.checkAllocated();
        arg.checkAllocated();
        if (this.width == arg.width) {
            MemoryUtil.memCopy((long)arg.pointer, (long)this.pointer, (long)Math.min(this.sizeBytes, arg.sizeBytes));
        } else {
            int j = Math.min(this.getWidth(), arg.getWidth());
            int k = Math.min(this.getHeight(), arg.getHeight());
            for (int l = 0; l < k; ++l) {
                int m = l * arg.getWidth() * i;
                int n = l * this.getWidth() * i;
                MemoryUtil.memCopy((long)(arg.pointer + (long)m), (long)(this.pointer + (long)n), (long)j);
            }
        }
    }

    public void fillRect(int i, int j, int k, int l, int m) {
        for (int n = j; n < j + l; ++n) {
            for (int o = i; o < i + k; ++o) {
                this.setPixelRgba(o, n, m);
            }
        }
    }

    public void copyRect(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
        for (int o = 0; o < n; ++o) {
            for (int p = 0; p < m; ++p) {
                int q = bl ? m - 1 - p : p;
                int r = bl2 ? n - 1 - o : o;
                int s = this.getPixelRgba(i + p, j + o);
                this.setPixelRgba(i + k + q, j + l + r, s);
            }
        }
    }

    public void mirrorVertically() {
        this.checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = this.format.getChannelCount();
            int j = this.getWidth() * i;
            long l = memoryStack.nmalloc(j);
            for (int k = 0; k < this.getHeight() / 2; ++k) {
                int m = k * this.getWidth() * i;
                int n = (this.getHeight() - 1 - k) * this.getWidth() * i;
                MemoryUtil.memCopy((long)(this.pointer + (long)m), (long)l, (long)j);
                MemoryUtil.memCopy((long)(this.pointer + (long)n), (long)(this.pointer + (long)m), (long)j);
                MemoryUtil.memCopy((long)l, (long)(this.pointer + (long)n), (long)j);
            }
        }
    }

    public void resizeSubRectTo(int i, int j, int k, int l, NativeImage arg) {
        this.checkAllocated();
        if (arg.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int m = this.format.getChannelCount();
        STBImageResize.nstbir_resize_uint8((long)(this.pointer + (long)((i + j * this.getWidth()) * m)), (int)k, (int)l, (int)(this.getWidth() * m), (long)arg.pointer, (int)arg.getWidth(), (int)arg.getHeight(), (int)0, (int)m);
    }

    public void untrack() {
        Untracker.untrack(this.pointer);
    }

    public static NativeImage read(String string) throws IOException {
        byte[] bs = Base64.getDecoder().decode(string.replaceAll("\n", "").getBytes(Charsets.UTF_8));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(bs.length);
            byteBuffer.put(bs);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(byteBuffer);
            return nativeImage;
        }
    }

    public static int method_24030(int i) {
        return i >> 24 & 0xFF;
    }

    public static int method_24033(int i) {
        return i >> 0 & 0xFF;
    }

    public static int method_24034(int i) {
        return i >> 8 & 0xFF;
    }

    public static int method_24035(int i) {
        return i >> 16 & 0xFF;
    }

    public static int method_24031(int i, int j, int k, int l) {
        return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | (l & 0xFF) << 0;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        private final int channelCount;
        private final int pixelDataFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceChannelOffset;
        private final int alphaChannelOffset;
        private final boolean writeable;

        private Format(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
            this.channelCount = j;
            this.pixelDataFormat = k;
            this.hasRed = bl;
            this.hasGreen = bl2;
            this.hasBlue = bl3;
            this.hasLuminance = bl4;
            this.hasAlpha = bl5;
            this.redOffset = l;
            this.greenOffset = m;
            this.blueOffset = n;
            this.luminanceChannelOffset = o;
            this.alphaChannelOffset = p;
            this.writeable = bl6;
        }

        public int getChannelCount() {
            return this.channelCount;
        }

        public void setPackAlignment() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GlStateManager.pixelStore(3333, this.getChannelCount());
        }

        public void setUnpackAlignment() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            GlStateManager.pixelStore(3317, this.getChannelCount());
        }

        public int getPixelDataFormat() {
            return this.pixelDataFormat;
        }

        public boolean hasAlphaChannel() {
            return this.hasAlpha;
        }

        public int getAlphaChannelOffset() {
            return this.alphaChannelOffset;
        }

        public boolean hasOpacityChannel() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int getOpacityOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
        }

        public boolean isWriteable() {
            return this.writeable;
        }

        private static Format getFormat(int i) {
            switch (i) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GLFormat {
        RGBA(6408),
        RGB(6407),
        LUMINANCE_ALPHA(6410),
        LUMINANCE(6409),
        INTENSITY(32841);

        private final int glConstant;

        private GLFormat(int j) {
            this.glConstant = j;
        }

        int getGlConstant() {
            return this.glConstant;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel channel;
        @Nullable
        private IOException exception;

        private WriteCallback(WritableByteChannel writableByteChannel) {
            this.channel = writableByteChannel;
        }

        public void invoke(long l, long m, int i) {
            ByteBuffer byteBuffer = WriteCallback.getData((long)m, (int)i);
            try {
                this.channel.write(byteBuffer);
            }
            catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwStoredException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

