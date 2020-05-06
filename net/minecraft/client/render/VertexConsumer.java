/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    public static final Logger LOGGER = LogManager.getLogger();

    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer texture(float var1, float var2);

    public VertexConsumer overlay(int var1, int var2);

    public VertexConsumer light(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void next();

    default public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        this.vertex(f, g, h);
        this.color(i, j, k, l);
        this.texture(m, n);
        this.overlay(o);
        this.light(p);
        this.normal(q, r, s);
        this.next();
    }

    default public VertexConsumer color(float f, float g, float h, float i) {
        return this.color((int)(f * 255.0f), (int)(g * 255.0f), (int)(h * 255.0f), (int)(i * 255.0f));
    }

    default public VertexConsumer light(int i) {
        return this.light(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlay(int i) {
        return this.overlay(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry arg, BakedQuad arg2, float f, float g, float h, int i, int j) {
        this.quad(arg, arg2, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, f, g, h, new int[]{i, i, i, i}, j, false);
    }

    default public void quad(MatrixStack.Entry arg, BakedQuad arg2, float[] fs, float f, float g, float h, int[] is, int i, boolean bl) {
        int[] js = arg2.getVertexData();
        Vec3i lv = arg2.getFace().getVector();
        Vector3f lv2 = new Vector3f(lv.getX(), lv.getY(), lv.getZ());
        Matrix4f lv3 = arg.getModel();
        lv2.transform(arg.getNormal());
        int j = 8;
        int k = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int l = 0; l < k; ++l) {
                float x;
                float w;
                float v;
                intBuffer.clear();
                intBuffer.put(js, l * 8, 8);
                float m = byteBuffer.getFloat(0);
                float n = byteBuffer.getFloat(4);
                float o = byteBuffer.getFloat(8);
                if (bl) {
                    float p = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    float q = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    float r = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    float s = p * fs[l] * f;
                    float t = q * fs[l] * g;
                    float u = r * fs[l] * h;
                } else {
                    v = fs[l] * f;
                    w = fs[l] * g;
                    x = fs[l] * h;
                }
                int y = is[l];
                float z = byteBuffer.getFloat(16);
                float aa = byteBuffer.getFloat(20);
                Vector4f lv4 = new Vector4f(m, n, o, 1.0f);
                lv4.transform(lv3);
                this.vertex(lv4.getX(), lv4.getY(), lv4.getZ(), v, w, x, 1.0f, z, aa, i, y, lv2.getX(), lv2.getY(), lv2.getZ());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f arg, float f, float g, float h) {
        Vector4f lv = new Vector4f(f, g, h, 1.0f);
        lv.transform(arg);
        return this.vertex(lv.getX(), lv.getY(), lv.getZ());
    }

    default public VertexConsumer normal(Matrix3f arg, float f, float g, float h) {
        Vector3f lv = new Vector3f(f, g, h);
        lv.transform(arg);
        return this.normal(lv.getX(), lv.getY(), lv.getZ());
    }
}

