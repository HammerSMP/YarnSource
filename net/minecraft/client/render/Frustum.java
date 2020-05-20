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

    public void setPosition(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
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

    private void transform(Matrix4f arg, int i, int j, int k, int l) {
        Vector4f lv = new Vector4f(i, j, k, 1.0f);
        lv.transform(arg);
        lv.normalize();
        this.homogeneousCoordinates[l] = lv;
    }

    public boolean isVisible(Box arg) {
        return this.isVisible(arg.minX, arg.minY, arg.minZ, arg.maxX, arg.maxY, arg.maxZ);
    }

    private boolean isVisible(double d, double e, double f, double g, double h, double i) {
        float j = (float)(d - this.x);
        float k = (float)(e - this.y);
        float l = (float)(f - this.z);
        float m = (float)(g - this.x);
        float n = (float)(h - this.y);
        float o = (float)(i - this.z);
        return this.isAnyCornerVisible(j, k, l, m, n, o);
    }

    private boolean isAnyCornerVisible(float f, float g, float h, float i, float j, float k) {
        for (int l = 0; l < 6; ++l) {
            Vector4f lv = this.homogeneousCoordinates[l];
            if (lv.dotProduct(new Vector4f(f, g, h, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(i, g, h, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(f, j, h, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(i, j, h, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(f, g, k, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(i, g, k, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(f, j, k, 1.0f)) > 0.0f) continue;
            if (lv.dotProduct(new Vector4f(i, j, k, 1.0f)) > 0.0f) continue;
            return false;
        }
        return true;
    }
}

