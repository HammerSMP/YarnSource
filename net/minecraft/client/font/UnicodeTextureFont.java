/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class UnicodeTextureFont
implements Font {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String template;
    private final Map<Identifier, NativeImage> images = Maps.newHashMap();

    public UnicodeTextureFont(ResourceManager arg, byte[] bs, String string) {
        this.resourceManager = arg;
        this.sizes = bs;
        this.template = string;
        for (int i = 0; i < 256; ++i) {
            int j = i * 256;
            Identifier lv = this.getImageId(j);
            try (Resource lv2 = this.resourceManager.getResource(lv);
                 NativeImage lv3 = NativeImage.read(NativeImage.Format.RGBA, lv2.getInputStream());){
                if (lv3.getWidth() == 256 && lv3.getHeight() == 256) {
                    for (int k = 0; k < 256; ++k) {
                        byte b = bs[j + k];
                        if (b == 0 || UnicodeTextureFont.getStart(b) <= UnicodeTextureFont.getEnd(b)) continue;
                        bs[j + k] = 0;
                    }
                    continue;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            Arrays.fill(bs, j, j + 256, (byte)0);
        }
    }

    @Override
    public void close() {
        this.images.values().forEach(NativeImage::close);
    }

    private Identifier getImageId(int i) {
        Identifier lv = new Identifier(String.format(this.template, String.format("%02x", i / 256)));
        return new Identifier(lv.getNamespace(), "textures/" + lv.getPath());
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(int i) {
        NativeImage lv;
        if (i < 0 || i > 65535) {
            return null;
        }
        byte b = this.sizes[i];
        if (b != 0 && (lv = this.images.computeIfAbsent(this.getImageId(i), this::getGlyphImage)) != null) {
            int j = UnicodeTextureFont.getStart(b);
            return new UnicodeTextureGlyph(i % 16 * 16 + j, (i & 0xFF) / 16 * 16, UnicodeTextureFont.getEnd(b) - j, 16, lv);
        }
        return null;
    }

    @Override
    public IntSet method_27442() {
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (int i = 0; i < 65535; ++i) {
            if (this.sizes[i] == 0) continue;
            intSet.add(i);
        }
        return intSet;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private NativeImage getGlyphImage(Identifier arg) {
        try (Resource lv = this.resourceManager.getResource(arg);){
            NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, lv.getInputStream());
            return nativeImage;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't load texture {}", (Object)arg, (Object)iOException);
            return null;
        }
    }

    private static int getStart(byte b) {
        return b >> 4 & 0xF;
    }

    private static int getEnd(byte b) {
        return (b & 0xF) + 1;
    }

    @Environment(value=EnvType.CLIENT)
    static class UnicodeTextureGlyph
    implements RenderableGlyph {
        private final int width;
        private final int height;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final NativeImage image;

        private UnicodeTextureGlyph(int i, int j, int k, int l, NativeImage arg) {
            this.width = k;
            this.height = l;
            this.unpackSkipPixels = i;
            this.unpackSkipRows = j;
            this.image = arg;
        }

        @Override
        public float getOversample() {
            return 2.0f;
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
        public float getAdvance() {
            return this.width / 2 + 1;
        }

        @Override
        public void upload(int i, int j) {
            this.image.upload(0, i, j, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        private final Identifier sizes;
        private final String template;

        public Loader(Identifier arg, String string) {
            this.sizes = arg;
            this.template = string;
        }

        public static FontLoader fromJson(JsonObject jsonObject) {
            return new Loader(new Identifier(JsonHelper.getString(jsonObject, "sizes")), JsonHelper.getString(jsonObject, "template"));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager arg) {
            try (Resource lv = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);){
                byte[] bs = new byte[65536];
                lv.getInputStream().read(bs);
                UnicodeTextureFont unicodeTextureFont = new UnicodeTextureFont(arg, bs, this.template);
                return unicodeTextureFont;
            }
            catch (IOException iOException) {
                LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
                return null;
            }
        }
    }
}

