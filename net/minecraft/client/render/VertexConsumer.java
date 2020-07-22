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

    default public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertex(x, y, z);
        this.color(red, green, blue, alpha);
        this.texture(u, v);
        this.overlay(overlay);
        this.light(light);
        this.normal(normalX, normalY, normalZ);
        this.next();
    }

    default public VertexConsumer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    default public VertexConsumer light(int uv) {
        return this.light(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlay(int uv) {
        return this.overlay(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
        this.quad(matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData) {
        int[] js = quad.getVertexData();
        Vec3i lv = quad.getFace().getVector();
        Vector3f lv2 = new Vector3f(lv.getX(), lv.getY(), lv.getZ());
        Matrix4f lv3 = matrixEntry.getModel();
        lv2.transform(matrixEntry.getNormal());
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
                if (useQuadColorData) {
                    float p = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    float q = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    float r = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    float s = p * brightnesses[l] * red;
                    float t = q * brightnesses[l] * green;
                    float u = r * brightnesses[l] * blue;
                } else {
                    v = brightnesses[l] * red;
                    w = brightnesses[l] * green;
                    x = brightnesses[l] * blue;
                }
                int y = lights[l];
                float z = byteBuffer.getFloat(16);
                float aa = byteBuffer.getFloat(20);
                Vector4f lv4 = new Vector4f(m, n, o, 1.0f);
                lv4.transform(lv3);
                this.vertex(lv4.getX(), lv4.getY(), lv4.getZ(), v, w, x, 1.0f, z, aa, overlay, y, lv2.getX(), lv2.getY(), lv2.getZ());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Vector4f lv = new Vector4f(x, y, z, 1.0f);
        lv.transform(matrix);
        return this.vertex(lv.getX(), lv.getY(), lv.getZ());
    }

    default public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        Vector3f lv = new Vector3f(x, y, z);
        lv.transform(matrix);
        return this.normal(lv.getX(), lv.getY(), lv.getZ());
    }
}

