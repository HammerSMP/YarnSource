/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankGlyph;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.WhiteRectangleGlyph;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
    private static final Glyph SPACE = () -> 4.0f;
    private static final Random RANDOM = new Random();
    private final TextureManager textureManager;
    private final Identifier id;
    private GlyphRenderer blankGlyphRenderer;
    private GlyphRenderer whiteRectangleGlyphRenderer;
    private final List<Font> fonts = Lists.newArrayList();
    private final Int2ObjectMap<GlyphRenderer> glyphRendererCache = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<Glyph> glyphCache = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap();
    private final List<GlyphAtlasTexture> glyphAtlases = Lists.newArrayList();

    public FontStorage(TextureManager arg, Identifier arg2) {
        this.textureManager = arg;
        this.id = arg2;
    }

    public void setFonts(List<Font> list) {
        this.method_24290();
        this.closeGlyphAtlases();
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
        this.whiteRectangleGlyphRenderer = this.getGlyphRenderer(WhiteRectangleGlyph.INSTANCE);
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (Font lv : list) {
            intSet.addAll((IntCollection)lv.method_27442());
        }
        HashSet set = Sets.newHashSet();
        intSet.forEach(i2 -> {
            for (Font lv : list) {
                Glyph lv2 = i2 == 32 ? SPACE : lv.getGlyph(i2);
                if (lv2 == null) continue;
                set.add(lv);
                if (lv2 == BlankGlyph.INSTANCE) break;
                ((IntList)this.charactersByWidth.computeIfAbsent(MathHelper.ceil(lv2.getAdvance(false)), i -> new IntArrayList())).add(i2);
                break;
            }
        });
        list.stream().filter(set::contains).forEach(this.fonts::add);
    }

    @Override
    public void close() {
        this.method_24290();
        this.closeGlyphAtlases();
    }

    private void method_24290() {
        for (Font lv : this.fonts) {
            lv.close();
        }
        this.fonts.clear();
    }

    private void closeGlyphAtlases() {
        for (GlyphAtlasTexture lv : this.glyphAtlases) {
            lv.close();
        }
        this.glyphAtlases.clear();
    }

    public Glyph getGlyph(int i2) {
        return (Glyph)this.glyphCache.computeIfAbsent(i2, i -> i == 32 ? SPACE : this.getRenderableGlyph(i));
    }

    private RenderableGlyph getRenderableGlyph(int i) {
        for (Font lv : this.fonts) {
            RenderableGlyph lv2 = lv.getGlyph(i);
            if (lv2 == null) continue;
            return lv2;
        }
        return BlankGlyph.INSTANCE;
    }

    public GlyphRenderer getGlyphRenderer(int i2) {
        return (GlyphRenderer)this.glyphRendererCache.computeIfAbsent(i2, i -> i == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph(i)));
    }

    private GlyphRenderer getGlyphRenderer(RenderableGlyph arg) {
        for (GlyphAtlasTexture lv : this.glyphAtlases) {
            GlyphRenderer lv2 = lv.getGlyphRenderer(arg);
            if (lv2 == null) continue;
            return lv2;
        }
        GlyphAtlasTexture lv3 = new GlyphAtlasTexture(new Identifier(this.id.getNamespace(), this.id.getPath() + "/" + this.glyphAtlases.size()), arg.hasColor());
        this.glyphAtlases.add(lv3);
        this.textureManager.registerTexture(lv3.getId(), lv3);
        GlyphRenderer lv4 = lv3.getGlyphRenderer(arg);
        return lv4 == null ? this.blankGlyphRenderer : lv4;
    }

    public GlyphRenderer getObfuscatedGlyphRenderer(Glyph arg) {
        IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(arg.getAdvance(false)));
        if (intList != null && !intList.isEmpty()) {
            return this.getGlyphRenderer(intList.getInt(RANDOM.nextInt(intList.size())));
        }
        return this.blankGlyphRenderer;
    }

    public GlyphRenderer getRectangleRenderer() {
        return this.whiteRectangleGlyphRenderer;
    }
}

