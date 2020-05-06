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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class GlyphRenderer {
    private final RenderLayer field_21692;
    private final RenderLayer field_21693;
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;
    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;

    public GlyphRenderer(RenderLayer arg, RenderLayer arg2, float f, float g, float h, float i, float j, float k, float l, float m) {
        this.field_21692 = arg;
        this.field_21693 = arg2;
        this.uMin = f;
        this.uMax = g;
        this.vMin = h;
        this.vMax = i;
        this.xMin = j;
        this.xMax = k;
        this.yMin = l;
        this.yMax = m;
    }

    public void draw(boolean bl, float f, float g, Matrix4f arg, VertexConsumer arg2, float h, float i, float j, float k, int l) {
        int m = 3;
        float n = f + this.xMin;
        float o = f + this.xMax;
        float p = this.yMin - 3.0f;
        float q = this.yMax - 3.0f;
        float r = g + p;
        float s = g + q;
        float t = bl ? 1.0f - 0.25f * p : 0.0f;
        float u = bl ? 1.0f - 0.25f * q : 0.0f;
        arg2.vertex(arg, n + t, r, 0.0f).color(h, i, j, k).texture(this.uMin, this.vMin).light(l).next();
        arg2.vertex(arg, n + u, s, 0.0f).color(h, i, j, k).texture(this.uMin, this.vMax).light(l).next();
        arg2.vertex(arg, o + u, s, 0.0f).color(h, i, j, k).texture(this.uMax, this.vMax).light(l).next();
        arg2.vertex(arg, o + t, r, 0.0f).color(h, i, j, k).texture(this.uMax, this.vMin).light(l).next();
    }

    public void drawRectangle(Rectangle arg, Matrix4f arg2, VertexConsumer arg3, int i) {
        arg3.vertex(arg2, arg.xMin, arg.yMin, arg.zIndex).color(arg.red, arg.green, arg.blue, arg.alpha).texture(this.uMin, this.vMin).light(i).next();
        arg3.vertex(arg2, arg.xMax, arg.yMin, arg.zIndex).color(arg.red, arg.green, arg.blue, arg.alpha).texture(this.uMin, this.vMax).light(i).next();
        arg3.vertex(arg2, arg.xMax, arg.yMax, arg.zIndex).color(arg.red, arg.green, arg.blue, arg.alpha).texture(this.uMax, this.vMax).light(i).next();
        arg3.vertex(arg2, arg.xMin, arg.yMax, arg.zIndex).color(arg.red, arg.green, arg.blue, arg.alpha).texture(this.uMax, this.vMin).light(i).next();
    }

    public RenderLayer method_24045(boolean bl) {
        return bl ? this.field_21693 : this.field_21692;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Rectangle {
        protected final float xMin;
        protected final float yMin;
        protected final float xMax;
        protected final float yMax;
        protected final float zIndex;
        protected final float red;
        protected final float green;
        protected final float blue;
        protected final float alpha;

        public Rectangle(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
            this.xMin = f;
            this.yMin = g;
            this.xMax = h;
            this.yMax = i;
            this.zIndex = j;
            this.red = k;
            this.green = l;
            this.blue = m;
            this.alpha = n;
        }
    }
}

