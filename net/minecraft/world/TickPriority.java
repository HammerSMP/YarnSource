/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

public enum TickPriority {
    EXTREMELY_HIGH(-3),
    VERY_HIGH(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    VERY_LOW(2),
    EXTREMELY_LOW(3);

    private final int index;

    private TickPriority(int j) {
        this.index = j;
    }

    public static TickPriority byIndex(int i) {
        for (TickPriority lv : TickPriority.values()) {
            if (lv.index != i) continue;
            return lv;
        }
        if (i < TickPriority.EXTREMELY_HIGH.index) {
            return EXTREMELY_HIGH;
        }
        return EXTREMELY_LOW;
    }

    public int getIndex() {
        return this.index;
    }
}

