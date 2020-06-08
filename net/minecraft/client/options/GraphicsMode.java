/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum GraphicsMode {
    FAST(0, "options.graphics.fast"),
    FANCY(1, "options.graphics.fancy"),
    FABULOUS(2, "options.graphics.fabulous");

    private static final GraphicsMode[] VALUES;
    private final int id;
    private final String translationKey;

    private GraphicsMode(int j, String string2) {
        this.id = j;
        this.translationKey = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public GraphicsMode next() {
        return GraphicsMode.byId(this.getId() + 1);
    }

    public GraphicsMode previous() {
        return GraphicsMode.byId(this.getId() - 1);
    }

    public String toString() {
        switch (this) {
            case FAST: {
                return "fast";
            }
            case FANCY: {
                return "fancy";
            }
            case FABULOUS: {
                return "fabulous";
            }
        }
        throw new IllegalArgumentException();
    }

    public static GraphicsMode byId(int i) {
        return VALUES[MathHelper.floorMod(i, VALUES.length)];
    }

    static {
        VALUES = (GraphicsMode[])Arrays.stream(GraphicsMode.values()).sorted(Comparator.comparingInt(GraphicsMode::getId)).toArray(GraphicsMode[]::new);
    }
}

