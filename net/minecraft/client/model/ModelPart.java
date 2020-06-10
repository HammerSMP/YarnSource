/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class ModelPart {
    private float textureWidth = 64.0f;
    private float textureHeight = 32.0f;
    private int textureOffsetU;
    private int textureOffsetV;
    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float pitch;
    public float yaw;
    public float roll;
    public boolean mirror;
    public boolean visible = true;
    private final ObjectList<Cuboid> cuboids = new ObjectArrayList();
    private final ObjectList<ModelPart> children = new ObjectArrayList();

    public ModelPart(Model arg) {
        arg.accept(this);
        this.setTextureSize(arg.textureWidth, arg.textureHeight);
    }

    public ModelPart(Model arg, int i, int j) {
        this(arg.textureWidth, arg.textureHeight, i, j);
        arg.accept(this);
    }

    public ModelPart(int i, int j, int k, int l) {
        this.setTextureSize(i, j);
        this.setTextureOffset(k, l);
    }

    private ModelPart() {
    }

    public ModelPart method_29991() {
        ModelPart lv = new ModelPart();
        lv.copyPositionAndRotation(this);
        return lv;
    }

    public void copyPositionAndRotation(ModelPart arg) {
        this.pitch = arg.pitch;
        this.yaw = arg.yaw;
        this.roll = arg.roll;
        this.pivotX = arg.pivotX;
        this.pivotY = arg.pivotY;
        this.pivotZ = arg.pivotZ;
    }

    public void addChild(ModelPart arg) {
        this.children.add((Object)arg);
    }

    public ModelPart setTextureOffset(int i, int j) {
        this.textureOffsetU = i;
        this.textureOffsetV = j;
        return this;
    }

    public ModelPart addCuboid(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
        this.setTextureOffset(m, n);
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, 0.0f, 0.0f, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, 0.0f, 0.0f, bl, false);
        return this;
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, m, n, this.mirror, false);
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, bl, false);
    }

    private void addCuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, boolean bl2) {
        this.cuboids.add((Object)new Cuboid(i, j, f, g, h, k, l, m, n, o, p, bl, this.textureWidth, this.textureHeight));
    }

    public void setPivot(float f, float g, float h) {
        this.pivotX = f;
        this.pivotY = g;
        this.pivotZ = h;
    }

    public void render(MatrixStack arg, VertexConsumer arg2, int i, int j) {
        this.render(arg, arg2, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(MatrixStack arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        arg.push();
        this.rotate(arg);
        this.renderCuboids(arg.peek(), arg2, i, j, f, g, h, k);
        for (ModelPart lv : this.children) {
            lv.render(arg, arg2, i, j, f, g, h, k);
        }
        arg.pop();
    }

    public void rotate(MatrixStack arg) {
        arg.translate(this.pivotX / 16.0f, this.pivotY / 16.0f, this.pivotZ / 16.0f);
        if (this.roll != 0.0f) {
            arg.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.roll));
        }
        if (this.yaw != 0.0f) {
            arg.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
        }
        if (this.pitch != 0.0f) {
            arg.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(this.pitch));
        }
    }

    private void renderCuboids(MatrixStack.Entry arg, VertexConsumer arg2, int i, int j, float f, float g, float h, float k) {
        Matrix4f lv = arg.getModel();
        Matrix3f lv2 = arg.getNormal();
        for (Cuboid lv3 : this.cuboids) {
            for (Quad lv4 : lv3.sides) {
                Vector3f lv5 = lv4.direction.copy();
                lv5.transform(lv2);
                float l = lv5.getX();
                float m = lv5.getY();
                float n = lv5.getZ();
                for (int o = 0; o < 4; ++o) {
                    Vertex lv6 = lv4.vertices[o];
                    float p = lv6.pos.getX() / 16.0f;
                    float q = lv6.pos.getY() / 16.0f;
                    float r = lv6.pos.getZ() / 16.0f;
                    Vector4f lv7 = new Vector4f(p, q, r, 1.0f);
                    lv7.transform(lv);
                    arg2.vertex(lv7.getX(), lv7.getY(), lv7.getZ(), f, g, h, k, lv6.u, lv6.v, j, i, l, m, n);
                }
            }
        }
    }

    public ModelPart setTextureSize(int i, int j) {
        this.textureWidth = i;
        this.textureHeight = j;
        return this;
    }

    public Cuboid getRandomCuboid(Random random) {
        return (Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(Vector3f arg, float f, float g) {
            this.pos = arg;
            this.u = f;
            this.v = g;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public final Vertex[] vertices;
        public final Vector3f direction;

        public Quad(Vertex[] args, float f, float g, float h, float i, float j, float k, boolean bl, Direction arg) {
            this.vertices = args;
            float l = 0.0f / j;
            float m = 0.0f / k;
            args[0] = args[0].remap(h / j - l, g / k + m);
            args[1] = args[1].remap(f / j + l, g / k + m);
            args[2] = args[2].remap(f / j + l, i / k - m);
            args[3] = args[3].remap(h / j - l, i / k - m);
            if (bl) {
                int n = args.length;
                for (int o = 0; o < n / 2; ++o) {
                    Vertex lv = args[o];
                    args[o] = args[n - 1 - o];
                    args[n - 1 - o] = lv;
                }
            }
            this.direction = arg.getUnitVector();
            if (bl) {
                this.direction.multiplyComponentwise(-1.0f, 1.0f, 1.0f);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cuboid {
        private final Quad[] sides;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r) {
            this.minX = f;
            this.minY = g;
            this.minZ = h;
            this.maxX = f + k;
            this.maxY = g + l;
            this.maxZ = h + m;
            this.sides = new Quad[6];
            float s = f + k;
            float t = g + l;
            float u = h + m;
            f -= n;
            g -= o;
            h -= p;
            s += n;
            t += o;
            u += p;
            if (bl) {
                float v = s;
                s = f;
                f = v;
            }
            Vertex lv = new Vertex(f, g, h, 0.0f, 0.0f);
            Vertex lv2 = new Vertex(s, g, h, 0.0f, 8.0f);
            Vertex lv3 = new Vertex(s, t, h, 8.0f, 8.0f);
            Vertex lv4 = new Vertex(f, t, h, 8.0f, 0.0f);
            Vertex lv5 = new Vertex(f, g, u, 0.0f, 0.0f);
            Vertex lv6 = new Vertex(s, g, u, 0.0f, 8.0f);
            Vertex lv7 = new Vertex(s, t, u, 8.0f, 8.0f);
            Vertex lv8 = new Vertex(f, t, u, 8.0f, 0.0f);
            float w = i;
            float x = (float)i + m;
            float y = (float)i + m + k;
            float z = (float)i + m + k + k;
            float aa = (float)i + m + k + m;
            float ab = (float)i + m + k + m + k;
            float ac = j;
            float ad = (float)j + m;
            float ae = (float)j + m + l;
            this.sides[2] = new Quad(new Vertex[]{lv6, lv5, lv, lv2}, x, ac, y, ad, q, r, bl, Direction.DOWN);
            this.sides[3] = new Quad(new Vertex[]{lv3, lv4, lv8, lv7}, y, ad, z, ac, q, r, bl, Direction.UP);
            this.sides[1] = new Quad(new Vertex[]{lv, lv5, lv8, lv4}, w, ad, x, ae, q, r, bl, Direction.WEST);
            this.sides[4] = new Quad(new Vertex[]{lv2, lv, lv4, lv3}, x, ad, y, ae, q, r, bl, Direction.NORTH);
            this.sides[0] = new Quad(new Vertex[]{lv6, lv2, lv3, lv7}, y, ad, aa, ae, q, r, bl, Direction.EAST);
            this.sides[5] = new Quad(new Vertex[]{lv5, lv6, lv7, lv8}, aa, ad, ab, ae, q, r, bl, Direction.SOUTH);
        }
    }
}

