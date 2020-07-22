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

    public BakedQuad bake(Vector3f from, Vector3f to, ModelElementFace face, Sprite texture, Direction side, ModelBakeSettings settings, @Nullable ModelRotation rotation, boolean shade, Identifier modelId) {
        ModelElementTexture lv = face.textureData;
        if (settings.isShaded()) {
            lv = BakedQuadFactory.uvLock(face.textureData, side, settings.getRotation(), modelId);
        }
        float[] fs = new float[lv.uvs.length];
        System.arraycopy(lv.uvs, 0, fs, 0, fs.length);
        float f = texture.getAnimationFrameDelta();
        float g = (lv.uvs[0] + lv.uvs[0] + lv.uvs[2] + lv.uvs[2]) / 4.0f;
        float h = (lv.uvs[1] + lv.uvs[1] + lv.uvs[3] + lv.uvs[3]) / 4.0f;
        lv.uvs[0] = MathHelper.lerp(f, lv.uvs[0], g);
        lv.uvs[2] = MathHelper.lerp(f, lv.uvs[2], g);
        lv.uvs[1] = MathHelper.lerp(f, lv.uvs[1], h);
        lv.uvs[3] = MathHelper.lerp(f, lv.uvs[3], h);
        int[] is = this.packVertexData(lv, texture, side, this.getPositionMatrix(from, to), settings.getRotation(), rotation, shade);
        Direction lv2 = BakedQuadFactory.decodeDirection(is);
        System.arraycopy(fs, 0, lv.uvs, 0, fs.length);
        if (rotation == null) {
            this.encodeDirection(is, lv2);
        }
        return new BakedQuad(is, face.tintIndex, lv2, texture, shade);
    }

    public static ModelElementTexture uvLock(ModelElementTexture texture, Direction orientation, AffineTransformation rotation, Identifier modelId) {
        float u;
        float t;
        float q;
        float p;
        Matrix4f lv = AffineTransformations.uvLock(rotation, orientation, () -> "Unable to resolve UVLock for model: " + modelId).getMatrix();
        float f = texture.getU(texture.getDirectionIndex(0));
        float g = texture.getV(texture.getDirectionIndex(0));
        Vector4f lv2 = new Vector4f(f / 16.0f, g / 16.0f, 0.0f, 1.0f);
        lv2.transform(lv);
        float h = 16.0f * lv2.getX();
        float i = 16.0f * lv2.getY();
        float j = texture.getU(texture.getDirectionIndex(2));
        float k = texture.getV(texture.getDirectionIndex(2));
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
        float v = (float)Math.toRadians(texture.rotation);
        Vector3f lv4 = new Vector3f(MathHelper.cos(v), MathHelper.sin(v), 0.0f);
        Matrix3f lv5 = new Matrix3f(lv);
        lv4.transform(lv5);
        int w = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2(lv4.getY(), lv4.getX())) / 90.0)) * 90, 360);
        return new ModelElementTexture(new float[]{p, t, q, u}, w);
    }

    private int[] packVertexData(ModelElementTexture texture, Sprite sprite, Direction direction, float[] positionMatrix, AffineTransformation orientation, @Nullable ModelRotation rotation, boolean shaded) {
        int[] is = new int[32];
        for (int i = 0; i < 4; ++i) {
            this.packVertexData(is, i, direction, texture, positionMatrix, sprite, orientation, rotation, shaded);
        }
        return is;
    }

    private float[] getPositionMatrix(Vector3f from, Vector3f to) {
        float[] fs = new float[Direction.values().length];
        fs[CubeFace.DirectionIds.WEST] = from.getX() / 16.0f;
        fs[CubeFace.DirectionIds.DOWN] = from.getY() / 16.0f;
        fs[CubeFace.DirectionIds.NORTH] = from.getZ() / 16.0f;
        fs[CubeFace.DirectionIds.EAST] = to.getX() / 16.0f;
        fs[CubeFace.DirectionIds.UP] = to.getY() / 16.0f;
        fs[CubeFace.DirectionIds.SOUTH] = to.getZ() / 16.0f;
        return fs;
    }

    private void packVertexData(int[] vertices, int cornerIndex, Direction direction, ModelElementTexture texture, float[] positionMatrix, Sprite sprite, AffineTransformation orientation, @Nullable ModelRotation rotation, boolean shaded) {
        CubeFace.Corner lv = CubeFace.getFace(direction).getCorner(cornerIndex);
        Vector3f lv2 = new Vector3f(positionMatrix[lv.xSide], positionMatrix[lv.ySide], positionMatrix[lv.zSide]);
        this.rotateVertex(lv2, rotation);
        this.transformVertex(lv2, orientation);
        this.packVertexData(vertices, cornerIndex, lv2, sprite, texture);
    }

    private void packVertexData(int[] vertices, int cornerIndex, Vector3f position, Sprite arg2, ModelElementTexture arg3) {
        int j = cornerIndex * 8;
        vertices[j] = Float.floatToRawIntBits(position.getX());
        vertices[j + 1] = Float.floatToRawIntBits(position.getY());
        vertices[j + 2] = Float.floatToRawIntBits(position.getZ());
        vertices[j + 3] = -1;
        vertices[j + 4] = Float.floatToRawIntBits(arg2.getFrameU(arg3.getU(cornerIndex)));
        vertices[j + 4 + 1] = Float.floatToRawIntBits(arg2.getFrameV(arg3.getV(cornerIndex)));
    }

    /*
     * WARNING - void declaration
     */
    private void rotateVertex(Vector3f vector, @Nullable ModelRotation rotation) {
        void lv8;
        void lv7;
        if (rotation == null) {
            return;
        }
        switch (rotation.axis) {
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
        Quaternion lv9 = new Quaternion((Vector3f)lv7, rotation.angle, true);
        if (rotation.rescale) {
            if (Math.abs(rotation.angle) == 22.5f) {
                lv8.scale(MIN_SCALE);
            } else {
                lv8.scale(MAX_SCALE);
            }
            lv8.add(1.0f, 1.0f, 1.0f);
        } else {
            lv8.set(1.0f, 1.0f, 1.0f);
        }
        this.transformVertex(vector, rotation.origin.copy(), new Matrix4f(lv9), (Vector3f)lv8);
    }

    public void transformVertex(Vector3f vertex, AffineTransformation transformation) {
        if (transformation == AffineTransformation.identity()) {
            return;
        }
        this.transformVertex(vertex, new Vector3f(0.5f, 0.5f, 0.5f), transformation.getMatrix(), new Vector3f(1.0f, 1.0f, 1.0f));
    }

    private void transformVertex(Vector3f vertex, Vector3f origin, Matrix4f transformationMatrix, Vector3f scale) {
        Vector4f lv = new Vector4f(vertex.getX() - origin.getX(), vertex.getY() - origin.getY(), vertex.getZ() - origin.getZ(), 1.0f);
        lv.transform(transformationMatrix);
        lv.multiplyComponentwise(scale);
        vertex.set(lv.getX() + origin.getX(), lv.getY() + origin.getY(), lv.getZ() + origin.getZ());
    }

    public static Direction decodeDirection(int[] rotationMatrix) {
        Vector3f lv = new Vector3f(Float.intBitsToFloat(rotationMatrix[0]), Float.intBitsToFloat(rotationMatrix[1]), Float.intBitsToFloat(rotationMatrix[2]));
        Vector3f lv2 = new Vector3f(Float.intBitsToFloat(rotationMatrix[8]), Float.intBitsToFloat(rotationMatrix[9]), Float.intBitsToFloat(rotationMatrix[10]));
        Vector3f lv3 = new Vector3f(Float.intBitsToFloat(rotationMatrix[16]), Float.intBitsToFloat(rotationMatrix[17]), Float.intBitsToFloat(rotationMatrix[18]));
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

    private void encodeDirection(int[] rotationMatrix, Direction direction) {
        int[] js = new int[rotationMatrix.length];
        System.arraycopy(rotationMatrix, 0, js, 0, rotationMatrix.length);
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
        CubeFace lv = CubeFace.getFace(direction);
        for (int k = 0; k < 4; ++k) {
            int l = 8 * k;
            CubeFace.Corner lv2 = lv.getCorner(k);
            float m = fs[lv2.xSide];
            float n = fs[lv2.ySide];
            float o = fs[lv2.zSide];
            rotationMatrix[l] = Float.floatToRawIntBits(m);
            rotationMatrix[l + 1] = Float.floatToRawIntBits(n);
            rotationMatrix[l + 2] = Float.floatToRawIntBits(o);
            for (int p = 0; p < 4; ++p) {
                int q = 8 * p;
                float r = Float.intBitsToFloat(js[q]);
                float s = Float.intBitsToFloat(js[q + 1]);
                float t = Float.intBitsToFloat(js[q + 2]);
                if (!MathHelper.approximatelyEquals(m, r) || !MathHelper.approximatelyEquals(n, s) || !MathHelper.approximatelyEquals(o, t)) continue;
                rotationMatrix[l + 4] = js[q + 4];
                rotationMatrix[l + 4 + 1] = js[q + 4 + 1];
            }
        }
    }
}

