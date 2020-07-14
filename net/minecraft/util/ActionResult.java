/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util;

public enum ActionResult {
    SUCCESS,
    CONSUME,
    PASS,
    FAIL;


    public boolean isAccepted() {
        return this == SUCCESS || this == CONSUME;
    }

    public boolean shouldSwingHand() {
        return this == SUCCESS;
    }

    public static ActionResult success(boolean swingHand) {
        return swingHand ? SUCCESS : CONSUME;
    }
}

