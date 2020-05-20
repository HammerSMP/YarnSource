/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class TextRenderer {
    private static final Vector3f FORWARD_SHIFT = new Vector3f(0.0f, 0.0f, 0.03f);
    public final int fontHeight = 9;
    public final Random random = new Random();
    private final Function<Identifier, FontStorage> fontStorageAccessor;
    private boolean rightToLeft;
    private final TextHandler handler;

    public TextRenderer(Function<Identifier, FontStorage> function) {
        this.fontStorageAccessor = function;
        this.handler = new TextHandler((i, arg) -> this.getFontStorage(arg.getFont()).getGlyph(i).getAdvance(arg.isBold()));
    }

    private FontStorage getFontStorage(Identifier arg) {
        return this.fontStorageAccessor.apply(arg);
    }

    public int drawWithShadow(MatrixStack arg, String string, float f, float g, int i) {
        return this.draw(string, f, g, i, arg.peek().getModel(), true, this.rightToLeft);
    }

    public int draw(MatrixStack arg, String string, float f, float g, int i) {
        RenderSystem.enableAlphaTest();
        return this.draw(string, f, g, i, arg.peek().getModel(), false, this.rightToLeft);
    }

    public int drawWithShadow(MatrixStack arg, Text arg2, float f, float g, int i) {
        RenderSystem.enableAlphaTest();
        return this.draw(arg2, f, g, i, arg.peek().getModel(), true);
    }

    public int draw(MatrixStack arg, Text arg2, float f, float g, int i) {
        RenderSystem.enableAlphaTest();
        return this.draw(arg2, f, g, i, arg.peek().getModel(), false);
    }

    public String mirror(String string) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return string;
        }
    }

    private int draw(String string, float f, float g, int i, Matrix4f arg, boolean bl, boolean bl2) {
        if (string == null) {
            return 0;
        }
        VertexConsumerProvider.Immediate lv = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int j = this.draw(string, f, g, i, bl, arg, lv, false, 0, 0xF000F0, bl2);
        lv.draw();
        return j;
    }

    private int draw(Text arg, float f, float g, int i, Matrix4f arg2, boolean bl) {
        VertexConsumerProvider.Immediate lv = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int j = this.draw(arg, f, g, i, bl, arg2, (VertexConsumerProvider)lv, false, 0, 0xF000F0);
        lv.draw();
        return j;
    }

    public int draw(String string, float f, float g, int i, boolean bl, Matrix4f arg, VertexConsumerProvider arg2, boolean bl2, int j, int k) {
        return this.draw(string, f, g, i, bl, arg, arg2, bl2, j, k, this.rightToLeft);
    }

    public int draw(String string, float f, float g, int i, boolean bl, Matrix4f arg, VertexConsumerProvider arg2, boolean bl2, int j, int k, boolean bl3) {
        return this.drawInternal(string, f, g, i, bl, arg, arg2, bl2, j, k, bl3);
    }

    public int draw(Text arg, float f, float g, int i, boolean bl, Matrix4f arg2, VertexConsumerProvider arg3, boolean bl2, int j, int k) {
        return this.drawInternal(arg, f, g, i, bl, arg2, arg3, bl2, j, k);
    }

    private static int tweakTransparency(int i) {
        if ((i & 0xFC000000) == 0) {
            return i | 0xFF000000;
        }
        return i;
    }

    private int drawInternal(String string, float f, float g, int i, boolean bl, Matrix4f arg, VertexConsumerProvider arg2, boolean bl2, int j, int k, boolean bl3) {
        if (bl3) {
            string = this.mirror(string);
        }
        i = TextRenderer.tweakTransparency(i);
        if (bl) {
            this.drawLayer(string, f, g, i, true, arg, arg2, bl2, j, k);
        }
        Matrix4f lv = arg.copy();
        lv.addToLastColumn(FORWARD_SHIFT);
        f = this.drawLayer(string, f, g, i, false, lv, arg2, bl2, j, k);
        return (int)f + (bl ? 1 : 0);
    }

    private int drawInternal(Text arg, float f, float g, int i, boolean bl, Matrix4f arg2, VertexConsumerProvider arg3, boolean bl2, int j, int k) {
        i = TextRenderer.tweakTransparency(i);
        if (bl) {
            this.drawLayer(arg, f, g, i, true, arg2, arg3, bl2, j, k);
        }
        Matrix4f lv = arg2.copy();
        lv.addToLastColumn(FORWARD_SHIFT);
        f = this.drawLayer(arg, f, g, i, false, lv, arg3, bl2, j, k);
        return (int)f + (bl ? 1 : 0);
    }

    private float drawLayer(String string, float f, float g, int i, boolean bl, Matrix4f arg, VertexConsumerProvider arg2, boolean bl2, int j, int k) {
        ShadowDrawer lv = new ShadowDrawer(arg2, f, g, i, bl, arg, bl2, k);
        TextVisitFactory.visitFormatted(string, Style.EMPTY, (TextVisitFactory.CharacterVisitor)lv);
        return lv.drawLayer(j, f);
    }

    private float drawLayer(Text arg, float f, float g, int i, boolean bl, Matrix4f arg2, VertexConsumerProvider arg3, boolean bl2, int j, int k) {
        ShadowDrawer lv = new ShadowDrawer(arg3, f, g, i, bl, arg2, bl2, k);
        TextVisitFactory.visitFormatted(arg, Style.EMPTY, (TextVisitFactory.CharacterVisitor)lv);
        return lv.drawLayer(j, f);
    }

    private void drawGlyph(GlyphRenderer arg, boolean bl, boolean bl2, float f, float g, float h, Matrix4f arg2, VertexConsumer arg3, float i, float j, float k, float l, int m) {
        arg.draw(bl2, g, h, arg2, arg3, i, j, k, l, m);
        if (bl) {
            arg.draw(bl2, g + f, h, arg2, arg3, i, j, k, l, m);
        }
    }

    public int getWidth(String string) {
        return MathHelper.ceil(this.handler.getWidth(string));
    }

    public int getWidth(Text arg) {
        return MathHelper.ceil(this.handler.getWidth(arg));
    }

    public String trimToWidth(String string, int i, boolean bl) {
        return bl ? this.handler.trimToWidthBackwards(string, i, Style.EMPTY) : this.handler.trimToWidth(string, i, Style.EMPTY);
    }

    public String trimToWidth(String string, int i) {
        return this.handler.trimToWidth(string, i, Style.EMPTY);
    }

    public MutableText trimToWidth(Text arg, int i) {
        return this.handler.trimToWidth(arg, i, Style.EMPTY);
    }

    public void drawTrimmed(Text arg, int i, int j, int k, int l) {
        Matrix4f lv = AffineTransformation.identity().getMatrix();
        for (Text lv2 : this.wrapLines(arg, k)) {
            this.draw(lv2, i, j, l, lv, false);
            j += 9;
        }
    }

    public int getStringBoundedHeight(String string, int i) {
        return 9 * this.handler.wrapLines(string, i, Style.EMPTY).size();
    }

    public void setRightToLeft(boolean bl) {
        this.rightToLeft = bl;
    }

    public List<Text> wrapLines(Text arg, int i) {
        return this.handler.wrapLines(arg, i, Style.EMPTY);
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public TextHandler getTextHandler() {
        return this.handler;
    }

    @Environment(value=EnvType.CLIENT)
    class ShadowDrawer
    implements TextVisitFactory.CharacterVisitor {
        final VertexConsumerProvider vertexConsumers;
        private final boolean shadow;
        private final float brightnessMultiplier;
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final Matrix4f matrix;
        private final boolean seeThrough;
        private final int light;
        private float x;
        private float y;
        @Nullable
        private List<GlyphRenderer.Rectangle> rectangles;

        private void addRectangle(GlyphRenderer.Rectangle arg) {
            if (this.rectangles == null) {
                this.rectangles = Lists.newArrayList();
            }
            this.rectangles.add(arg);
        }

        public ShadowDrawer(VertexConsumerProvider arg2, float f, float g, int i, boolean bl, Matrix4f arg3, boolean bl2, int j) {
            this.vertexConsumers = arg2;
            this.x = f;
            this.y = g;
            this.shadow = bl;
            this.brightnessMultiplier = bl ? 0.25f : 1.0f;
            this.red = (float)(i >> 16 & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.green = (float)(i >> 8 & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.blue = (float)(i & 0xFF) / 255.0f * this.brightnessMultiplier;
            this.alpha = (float)(i >> 24 & 0xFF) / 255.0f;
            this.matrix = arg3;
            this.seeThrough = bl2;
            this.light = j;
        }

        @Override
        public boolean onChar(int i, Style arg, int j) {
            float s;
            float o;
            float n;
            float m;
            FontStorage lv = TextRenderer.this.getFontStorage(arg.getFont());
            Glyph lv2 = lv.getGlyph(j);
            GlyphRenderer lv3 = arg.isObfuscated() && j != 32 ? lv.getObfuscatedGlyphRenderer(lv2) : lv.getGlyphRenderer(j);
            boolean bl = arg.isBold();
            float f = this.alpha;
            TextColor lv4 = arg.getColor();
            if (lv4 != null) {
                int k = lv4.getRgb();
                float g = (float)(k >> 16 & 0xFF) / 255.0f * this.brightnessMultiplier;
                float h = (float)(k >> 8 & 0xFF) / 255.0f * this.brightnessMultiplier;
                float l = (float)(k & 0xFF) / 255.0f * this.brightnessMultiplier;
            } else {
                m = this.red;
                n = this.green;
                o = this.blue;
            }
            if (!(lv3 instanceof EmptyGlyphRenderer)) {
                float p = bl ? lv2.getBoldOffset() : 0.0f;
                float q = this.shadow ? lv2.getShadowOffset() : 0.0f;
                VertexConsumer lv5 = this.vertexConsumers.getBuffer(lv3.method_24045(this.seeThrough));
                TextRenderer.this.drawGlyph(lv3, bl, arg.isItalic(), p, this.x + q, this.y + q, this.matrix, lv5, m, n, o, f, this.light);
            }
            float r = lv2.getAdvance(bl);
            float f2 = s = this.shadow ? 1.0f : 0.0f;
            if (arg.isStrikethrough()) {
                this.addRectangle(new GlyphRenderer.Rectangle(this.x + s - 1.0f, this.y + s + 4.5f, this.x + s + r, this.y + s + 4.5f - 1.0f, 0.01f, m, n, o, f));
            }
            if (arg.isUnderlined()) {
                this.addRectangle(new GlyphRenderer.Rectangle(this.x + s - 1.0f, this.y + s + 9.0f, this.x + s + r, this.y + s + 9.0f - 1.0f, 0.01f, m, n, o, f));
            }
            this.x += r;
            return true;
        }

        public float drawLayer(int i, float f) {
            if (i != 0) {
                float g = (float)(i >> 24 & 0xFF) / 255.0f;
                float h = (float)(i >> 16 & 0xFF) / 255.0f;
                float j = (float)(i >> 8 & 0xFF) / 255.0f;
                float k = (float)(i & 0xFF) / 255.0f;
                this.addRectangle(new GlyphRenderer.Rectangle(f - 1.0f, this.y + 9.0f, this.x + 1.0f, this.y - 1.0f, 0.01f, h, j, k, g));
            }
            if (this.rectangles != null) {
                GlyphRenderer lv = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleRenderer();
                VertexConsumer lv2 = this.vertexConsumers.getBuffer(lv.method_24045(this.seeThrough));
                for (GlyphRenderer.Rectangle lv3 : this.rectangles) {
                    lv.drawRectangle(lv3, this.matrix, lv2, this.light);
                }
            }
            return this.x;
        }
    }
}

