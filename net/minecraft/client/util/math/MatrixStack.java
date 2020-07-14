/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class MatrixStack {
    private final Deque<Entry> stack = Util.make(Queues.newArrayDeque(), arrayDeque -> {
        Matrix4f lv = new Matrix4f();
        lv.loadIdentity();
        Matrix3f lv2 = new Matrix3f();
        lv2.loadIdentity();
        arrayDeque.add(new Entry(lv, lv2));
    });

    public void translate(double x, double y, double z) {
        Entry lv = this.stack.getLast();
        lv.modelMatrix.multiply(Matrix4f.translate((float)x, (float)y, (float)z));
    }

    public void scale(float x, float y, float z) {
        Entry lv = this.stack.getLast();
        lv.modelMatrix.multiply(Matrix4f.scale(x, y, z));
        if (x == y && y == z) {
            if (x > 0.0f) {
                return;
            }
            lv.normalMatrix.multiply(-1.0f);
        }
        float i = 1.0f / x;
        float j = 1.0f / y;
        float k = 1.0f / z;
        float l = MathHelper.fastInverseCbrt(i * j * k);
        lv.normalMatrix.multiply(Matrix3f.scale(l * i, l * j, l * k));
    }

    public void multiply(Quaternion quaternion) {
        Entry lv = this.stack.getLast();
        lv.modelMatrix.multiply(quaternion);
        lv.normalMatrix.multiply(quaternion);
    }

    public void push() {
        Entry lv = this.stack.getLast();
        this.stack.addLast(new Entry(lv.modelMatrix.copy(), lv.normalMatrix.copy()));
    }

    public void pop() {
        this.stack.removeLast();
    }

    public Entry peek() {
        return this.stack.getLast();
    }

    public boolean isEmpty() {
        return this.stack.size() == 1;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Entry {
        private final Matrix4f modelMatrix;
        private final Matrix3f normalMatrix;

        private Entry(Matrix4f arg, Matrix3f arg2) {
            this.modelMatrix = arg;
            this.normalMatrix = arg2;
        }

        public Matrix4f getModel() {
            return this.modelMatrix;
        }

        public Matrix3f getNormal() {
            return this.normalMatrix;
        }
    }
}

