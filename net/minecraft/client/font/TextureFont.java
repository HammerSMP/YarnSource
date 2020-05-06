/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public class TextureFont
implements Font {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NativeImage image;
    private final Int2ObjectMap<TextureFontGlyph> glyphs;

    private TextureFont(NativeImage arg, Int2ObjectMap<TextureFontGlyph> int2ObjectMap) {
        this.image = arg;
        this.glyphs = int2ObjectMap;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(int i) {
        return (RenderableGlyph)this.glyphs.get(i);
    }

    @Override
    public IntSet method_27442() {
        return IntSets.unmodifiable((IntSet)this.glyphs.keySet());
    }

    @Environment(value=EnvType.CLIENT)
    static final class TextureFontGlyph
    implements RenderableGlyph {
        private final float scaleFactor;
        private final NativeImage image;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;

        private TextureFontGlyph(float f, NativeImage arg, int i, int j, int k, int l, int m, int n) {
            this.scaleFactor = f;
            this.image = arg;
            this.x = i;
            this.y = j;
            this.width = k;
            this.height = l;
            this.advance = m;
            this.ascent = n;
        }

        @Override
        public float getOversample() {
            return 1.0f / this.scaleFactor;
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
            return this.advance;
        }

        @Override
        public float getAscent() {
            return RenderableGlyph.super.getAscent() + 7.0f - (float)this.ascent;
        }

        @Override
        public void upload(int i, int j) {
            this.image.upload(0, i, j, this.x, this.y, this.width, this.height, false, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        private final Identifier filename;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Loader(Identifier arg, int i, int j, List<int[]> list) {
            this.filename = new Identifier(arg.getNamespace(), "textures/" + arg.getPath());
            this.chars = list;
            this.height = i;
            this.ascent = j;
        }

        public static Loader fromJson(JsonObject jsonObject) {
            int i = JsonHelper.getInt(jsonObject, "height", 8);
            int j = JsonHelper.getInt(jsonObject, "ascent");
            if (j > i) {
                throw new JsonParseException("Ascent " + j + " higher than height " + i);
            }
            ArrayList list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "chars");
            for (int k = 0; k < jsonArray.size(); ++k) {
                int l;
                String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
                int[] is = string.codePoints().toArray();
                if (k > 0 && is.length != (l = ((int[])list.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + is.length + ", expected: " + l + "), pad with space or \\u0000");
                }
                list.add(is);
            }
            if (list.isEmpty() || ((int[])list.get(0)).length == 0) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Loader(new Identifier(JsonHelper.getString(jsonObject, "file")), i, j, list);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager arg) {
            try (Resource lv = arg.getResource(this.filename);){
                NativeImage lv2 = NativeImage.read(NativeImage.Format.RGBA, lv.getInputStream());
                int i = lv2.getWidth();
                int j = lv2.getHeight();
                int k = i / this.chars.get(0).length;
                int l = j / this.chars.size();
                float f = (float)this.height / (float)l;
                Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
                int m = 0;
                do {
                    int n;
                    int[] arrn;
                    int n2;
                    if (m < this.chars.size()) {
                        n2 = 0;
                        arrn = this.chars.get(m);
                        n = arrn.length;
                    } else {
                        TextureFont textureFont = new TextureFont(lv2, (Int2ObjectMap)int2ObjectMap);
                        return textureFont;
                    }
                    for (int i2 = 0; i2 < n; ++i2) {
                        int q;
                        TextureFontGlyph lv3;
                        int o = arrn[i2];
                        int p = n2++;
                        if (o == 0 || o == 32 || (lv3 = (TextureFontGlyph)int2ObjectMap.put(o, (Object)new TextureFontGlyph(f, lv2, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(lv2, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
                        LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)this.filename);
                    }
                    ++m;
                } while (true);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException.getMessage());
            }
        }

        private int findCharacterStartX(NativeImage arg, int i, int j, int k, int l) {
            int m;
            for (m = i - 1; m >= 0; --m) {
                int n = k * i + m;
                for (int o = 0; o < j; ++o) {
                    int p = l * j + o;
                    if (arg.getPixelOpacity(n, p) == 0) continue;
                    return m + 1;
                }
            }
            return m + 1;
        }
    }
}

