/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.math;

import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix3f;

public enum AxisTransformation {
    P123(0, 1, 2),
    P213(1, 0, 2),
    P132(0, 2, 1),
    P231(1, 2, 0),
    P312(2, 0, 1),
    P321(2, 1, 0);

    private final int[] mappings;
    private final Matrix3f matrix;
    private static final AxisTransformation[][] COMBINATIONS;

    private AxisTransformation(int j, int k, int l) {
        this.mappings = new int[]{j, k, l};
        this.matrix = new Matrix3f();
        this.matrix.set(0, this.map(0), 1.0f);
        this.matrix.set(1, this.map(1), 1.0f);
        this.matrix.set(2, this.map(2), 1.0f);
    }

    public AxisTransformation prepend(AxisTransformation arg) {
        return COMBINATIONS[this.ordinal()][arg.ordinal()];
    }

    public int map(int i) {
        return this.mappings[i];
    }

    public Matrix3f getMatrix() {
        return this.matrix;
    }

    static {
        COMBINATIONS = Util.make(new AxisTransformation[AxisTransformation.values().length][AxisTransformation.values().length], args -> {
            for (AxisTransformation lv : AxisTransformation.values()) {
                for (AxisTransformation lv2 : AxisTransformation.values()) {
                    AxisTransformation lv3;
                    int[] is = new int[3];
                    for (int i = 0; i < 3; ++i) {
                        is[i] = lv.mappings[lv2.mappings[i]];
                    }
                    args[lv.ordinal()][lv2.ordinal()] = lv3 = Arrays.stream(AxisTransformation.values()).filter(arg -> Arrays.equals(arg.mappings, is)).findFirst().get();
                }
            }
        });
    }
}

