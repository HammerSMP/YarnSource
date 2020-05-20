/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WallShape implements StringIdentifiable
{
    NONE("none"),
    LOW("low"),
    TALL("tall");

    private final String name;

    private WallShape(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this.name;
    }
}
