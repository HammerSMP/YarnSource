/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseMarking {
    NONE(0),
    WHITE(1),
    WHITE_FIELD(2),
    WHITE_DOTS(3),
    BLACK_DOTS(4);

    private static final HorseMarking[] VALUES;
    private final int index;

    private HorseMarking(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static HorseMarking byIndex(int index) {
        return VALUES[index % VALUES.length];
    }

    static {
        VALUES = (HorseMarking[])Arrays.stream(HorseMarking.values()).sorted(Comparator.comparingInt(HorseMarking::getIndex)).toArray(HorseMarking[]::new);
    }
}

