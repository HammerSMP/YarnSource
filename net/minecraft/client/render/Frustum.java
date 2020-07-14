/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class Frustum {
    private final Vector4f[] homogeneousCoordinates = new Vector4f[6];
    private double x;
    private double y;
    private double z;

    public Frustum(Matrix4f arg, Matrix4f arg2) {
        this.init(arg, arg2);
    }

    public void setPosition(double cameraX, double cameraY, double cameraZ) {
        this.x = cameraX;
        this.y = cameraY;
        this.z = cameraZ;
    }

    private void init(Matrix4f arg, Matrix4f arg2) {
        Matrix4f lv = arg2.copy();
        lv.multiply(arg);
        lv.transpose();
        this.transform(lv, -1, 0, 0, 0);
        this.transform(lv, 1, 0, 0, 1);
        this.transform(lv, 0, -1, 0, 2);
        this.transform(lv, 0, 1, 0, 3);
        this.transform(lv, 0, 0, -1, 4);
        this.transform(lv, 0, 0, 1, 5);
    }

    private void transform(Matrix4f function, int x, int y, int z, int index) {
        Vector4f lv = new Vector4f(x, y, z, 1.0f);
        lv.transform(function);
        lv.normalize();
        this.homogeneousCoordinates[index] = lv;
    }

    public boolean isVisible(Box box) {
        return this.isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    private boolean isVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float j = (float)(minX - this.x);
        float k = (float)(minY - this.y);
        float l = (float)(minZ - this.z);
        float m = (float)(maxX - this.x);
        float n = (float)(maxY - this.y);
        float o = (float)(maxZ - this.z);
        return this.isAnyCornerVisible(j, k, l, m, n, o);
    }

    private boolean isAnyCornerVisible(float x1, float y1, float z1, float x2, float y2, float z2) {
        for (int l = 0; l < 6; ++l) {
            Vector4f lv = this.homogeneousCoordinates[l];
            if (lv.dotProduct(new Vector4f(x1, y1, z1, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x2, y1, z1, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x1, y2, z1, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x2, y2, z1, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x1, y1, z2, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x2, y1, z2, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x1, y2, z2, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(x2, y2, z2, 1.0f)) > 0.0f) continue;
            return false;
        }
        return true;
    }
}

