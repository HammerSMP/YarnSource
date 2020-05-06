/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.AffineTransformations;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;

@Environment(value=EnvType.CLIENT)
public class BakedQuadFactory {
    private static final float MIN_SCALE = 1.0f / (float)Math.cos(0.3926991f) - 1.0f;
    private static final float MAX_SCALE = 1.0f / (float)Math.cos(0.7853981852531433) - 1.0f;

    public BakedQuad bake(Vector3f arg, Vector3f arg2, ModelElementFace arg3, Sprite arg4, Direction arg5, ModelBakeSettings arg6, @Nullable ModelRotation arg7, boolean bl, Identifier arg8) {
        ModelElementTexture lv = arg3.textureData;
        if (arg6.isShaded()) {
            lv = BakedQuadFactory.uvLock(arg3.textureData, arg5, arg6.getRotation(), arg8);
        }
        float[] fs = new float[lv.uvs.length];
        System.arraycopy(lv.uvs, 0, fs, 0, fs.length);
        float f = arg4.getAnimationFrameDelta();
        float g = (lv.uvs[0] + lv.uvs[0] + lv.uvs[2] + lv.uvs[2]) / 4.0f;
        float h = (lv.uvs[1] + lv.uvs[1] + lv.uvs[3] + lv.uvs[3]) / 4.0f;
        lv.uvs[0] = MathHelper.lerp(f, lv.uvs[0], g);
        lv.uvs[2] = MathHelper.lerp(f, lv.uvs[2], g);
        lv.uvs[1] = MathHelper.lerp(f, lv.uvs[1], h);
        lv.uvs[3] = MathHelper.lerp(f, lv.uvs[3], h);
        int[] is = this.packVertexData(lv, arg4, arg5, this.getPositionMatrix(arg, arg2), arg6.getRotation(), arg7, bl);
        Direction lv2 = BakedQuadFactory.decodeDirection(is);
        System.arraycopy(fs, 0, lv.uvs, 0, fs.length);
        if (arg7 == null) {
            this.encodeDirection(is, lv2);
        }
        return new BakedQuad(is, arg3.tintIndex, lv2, arg4, bl);
    }

    public static ModelElementTexture uvLock(ModelElementTexture arg, Direction arg2, AffineTransformation arg3, Identifier arg4) {
        float u;
        float t;
        float q;
        float p;
        Matrix4f lv = AffineTransformations.uvLock(arg3, arg2, () -> "Unable to resolve UVLock for model: " + arg4).getMatrix();
        float f = arg.getU(arg.getDirectionIndex(0));
        float g = arg.getV(arg.getDirectionIndex(0));
        Vector4f lv2 = new Vector4f(f / 16.0f, g / 16.0f, 0.0f, 1.0f);
        lv2.transform(lv);
        float h = 16.0f * lv2.getX();
        float i = 16.0f * lv2.getY();
        float j = arg.getU(arg.getDirectionIndex(2));
        float k = arg.getV(arg.getDirectionIndex(2));
        Vector4f lv3 = new Vector4f(j / 16.0f, k / 16.0f, 0.0f, 1.0f);
        lv3.transform(lv);
        float l = 16.0f * lv3.getX();
        float m = 16.0f * lv3.getY();
        if (Math.signum(j - f) == Math.signum(l - h)) {
            float n = h;
            float o = l;
        } else {
            p = l;
            q = h;
        }
        if (Math.signum(k - g) == Math.signum(m - i)) {
            float r = i;
            float s = m;
        } else {
            t = m;
            u = i;
        }
        float v = (float)Math.toRadians(arg.rotation);
        Vector3f lv4 = new Vector3f(MathHelper.cos(v), MathHelper.sin(v), 0.0f);
        Matrix3f lv5 = new Matrix3f(lv);
        lv4.transform(lv5);
        int w = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2(lv4.getY(), lv4.getX())) / 90.0)) * 90, 360);
        return new ModelElementTexture(new float[]{p, t, q, u}, w);
    }

    private int[] packVertexData(ModelElementTexture arg, Sprite arg2, Direction arg3, float[] fs, AffineTransformation arg4, @Nullable ModelRotation arg5, boolean bl) {
        int[] is = new int[32];
        for (int i = 0; i < 4; ++i) {
            this.packVertexData(is, i, arg3, arg, fs, arg2, arg4, arg5, bl);
        }
        return is;
    }

    private float[] getPositionMatrix(Vector3f arg, Vector3f arg2) {
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = arg.getX() / 16.0f;
        fs[CubeFace.DirectionIds.DOWN] = arg.getY() / 16.0f;
        fs[CubeFace.DirectionIds.NORTH] = arg.getZ() / 16.0f;
        fs[CubeFace.DirectionIds.EAST] = arg2.getX() / 16.0f;
        fs[CubeFace.DirectionIds.UP] = arg2.getY() / 16.0f;
        fs[CubeFace.DirectionIds.SOUTH] = arg2.getZ() / 16.0f;
        return fs;
    }

    private void packVertexData(int[] is, int i, Direction arg, ModelElementTexture arg2, float[] fs, Sprite arg3, AffineTransformation arg4, @Nullable ModelRotation arg5, boolean bl) {
        CubeFace.Corner lv = CubeFace.getFace(arg).getCorner(i);
        Vector3f lv2 = new Vector3f(fs[lv.xSide], fs[lv.ySide], fs[lv.zSide]);
        this.rotateVertex(lv2, arg5);
        this.transformVertex(lv2, arg4);
        this.packVertexData(is, i, lv2, arg3, arg2);
    }

    private void packVertexData(int[] is, int i, Vector3f arg, Sprite arg2, ModelElementTexture arg3) {
        int j = i * 8;
        is[j] = Float.floatToRawIntBits(arg.getX());
        is[j + 1] = Float.floatToRawIntBits(arg.getY());
        is[j + 2] = Float.floatToRawIntBits(arg.getZ());
        is[j + 3] = -1;
        is[j + 4] = Float.floatToRawIntBits(arg2.getFrameU(arg3.getU(i)));
        is[j + 4 + 1] = Float.floatToRawIntBits(arg2.getFrameV(arg3.getV(i)));
    }

    /*
     * WARNING - void declaration
     */
    private void rotateVertex(Vector3f arg, @Nullable ModelRotation arg2) {
        void lv8;
        void lv7;
        if (arg2 == null) {
            return;
        }
        switch (arg2.axis) {
            case X: {
                Vector3f lv = new Vector3f(1.0f, 0.0f, 0.0f);
                Vector3f lv2 = new Vector3f(0.0f, 1.0f, 1.0f);
                break;
            }
            case Y: {
                Vector3f lv3 = new Vector3f(0.0f, 1.0f, 0.0f);
                Vector3f lv4 = new Vector3f(1.0f, 0.0f, 1.0f);
                break;
            }
            case Z: {
                Vector3f lv5 = new Vector3f(0.0f, 0.0f, 1.0f);
                Vector3f lv6 = new Vector3f(1.0f, 1.0f, 0.0f);
                break;
            }
            default: {
                throw new IllegalArgumentException("There are only 3 axes");
            }
        }
        Quaternion lv9 = new Quaternion((Vector3f)lv7, arg2.angle, true);
        if (arg2.rescale) {
            if (Math.abs(arg2.angle) == 22.5f) {
                lv8.scale(MIN_SCALE);
            } else {
                lv8.scale(MAX_SCALE);
            }
            lv8.add(1.0f, 1.0f, 1.0f);
        } else {
            lv8.set(1.0f, 1.0f, 1.0f);
        }
        this.transformVertex(arg, arg2.origin.copy(), new Matrix4f(lv9), (Vector3f)lv8);
    }

    public void transformVertex(Vector3f arg, AffineTransformation arg2) {
        if (arg2 == AffineTransformation.identity()) {
            return;
        }
        this.transformVertex(arg, new Vector3f(0.5f, 0.5f, 0.5f), arg2.getMatrix(), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    private void transformVertex(Vector3f arg, Vector3f arg2, Matrix4f arg3, Vector3f arg4) {
        Vector4f lv = new Vector4f(arg.getX() - arg2.getX(), arg.getY() - arg2.getY(), arg.getZ() - arg2.getZ(), 1.0f);
        lv.transform(arg3);
        lv.multiplyComponentwise(arg4);
        arg.set(lv.getX() + arg2.getX(), lv.getY() + arg2.getY(), lv.getZ() + arg2.getZ());
    }

    public static Direction decodeDirection(int[] is) {
        Vector3f lv = new Vector3f(Float.intBitsToFloat(is[0]), Float.intBitsToFloat(is[1]), Float.intBitsToFloat(is[2]));
        Vector3f lv2 = new Vector3f(Float.intBitsToFloat(is[8]), Float.intBitsToFloat(is[9]), Float.intBitsToFloat(is[10]));
        Vector3f lv3 = new Vector3f(Float.intBitsToFloat(is[16]), Float.intBitsToFloat(is[17]), Float.intBitsToFloat(is[18]));
        Vector3f lv4 = lv.copy();
        lv4.subtract(lv2);
        Vector3f lv5 = lv3.copy();
        lv5.subtract(lv2);
        Vector3f lv6 = lv5.copy();
        lv6.cross(lv4);
        lv6.normalize();
        Direction lv7 = null;
        float f = 0.0f;
        for (Direction lv8 : Direction.values()) {
            Vec3i lv9 = lv8.getVector();
            Vector3f lv10 = new Vector3f(lv9.getX(), lv9.getY(), lv9.getZ());
            float g = lv6.dot(lv10);
            if (!(g >= 0.0f) || !(g > f)) continue;
            f = g;
            lv7 = lv8;
        }
        if (lv7 == null) {
            return Direction.UP;
        }
        return lv7;
    }

    private void encodeDirection(int[] is, Direction arg) {
        int[] js = new int[is.length];
        System.arraycopy(is, 0, js, 0, is.length);
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = 999.0f;
        fs[CubeFace.DirectionIds.DOWN] = 999.0f;
        fs[CubeFace.DirectionIds.NORTH] = 999.0f;
        fs[CubeFace.DirectionIds.EAST] = -999.0f;
        fs[CubeFace.DirectionIds.UP] = -999.0f;
        fs[CubeFace.DirectionIds.SOUTH] = -999.0f;
        for (int i = 0; i < 4; ++i) {
            int j = 8 * i;
            float f = Float.intBitsToFloat(js[j]);
            float g = Float.intBitsToFloat(js[j + 1]);
            float h = Float.intBitsToFloat(js[j + 2]);
            if (f < fs[CubeFace.DirectionIds.WEST]) {
                fs[CubeFace.DirectionIds.WEST] = f;
            }
            if (g < fs[CubeFace.DirectionIds.DOWN]) {
                fs[CubeFace.DirectionIds.DOWN] = g;
            }
            if (h < fs[CubeFace.DirectionIds.NORTH]) {
                fs[CubeFace.DirectionIds.NORTH] = h;
            }
            if (f > fs[CubeFace.DirectionIds.EAST]) {
                fs[CubeFace.DirectionIds.EAST] = f;
            }
            if (g > fs[CubeFace.DirectionIds.UP]) {
                fs[CubeFace.DirectionIds.UP] = g;
            }
            if (!(h > fs[CubeFace.DirectionIds.SOUTH])) continue;
            fs[CubeFace.DirectionIds.SOUTH] = h;
        }
        CubeFace lv = CubeFace.getFace(arg);
        for (int k = 0; k < 4; ++k) {
            int l = 8 * k;
            CubeFace.Corner lv2 = lv.getCorner(k);
            float m = fs[lv2.xSide];
            float n = fs[lv2.ySide];
            float o = fs[lv2.zSide];
            is[l] = Float.floatToRawIntBits(m);
            is[l + 1] = Float.floatToRawIntBits(n);
            is[l + 2] = Float.floatToRawIntBits(o);
            for (int p = 0; p < 4; ++p) {
                int q = 8 * p;
                float r = Float.intBitsToFloat(js[q]);
                float s = Float.intBitsToFloat(js[q + 1]);
                float t = Float.intBitsToFloat(js[q + 2]);
                if (!MathHelper.approximatelyEquals(m, r) || !MathHelper.approximatelyEquals(n, s) || !MathHelper.approximatelyEquals(o, t)) continue;
                is[l + 4] = js[q + 4];
                is[l + 4 + 1] = js[q + 4 + 1];
            }
        }
    }
}

