/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 */
package net.minecraft.data.client.model;

import com.google.gson.JsonPrimitive;
import net.minecraft.data.client.model.VariantSetting;
import net.minecraft.util.Identifier;

public class VariantSettings {
    public static final VariantSetting<Rotation> X = new VariantSetting<Rotation>("x", arg -> new JsonPrimitive((Number)((Rotation)arg).degrees));
    public static final VariantSetting<Rotation> Y = new VariantSetting<Rotation>("y", arg -> new JsonPrimitive((Number)((Rotation)arg).degrees));
    public static final VariantSetting<Identifier> MODEL = new VariantSetting<Identifier>("model", arg -> new JsonPrimitive(arg.toString()));
    public static final VariantSetting<Boolean> UVLOCK = new VariantSetting<Boolean>("uvlock", JsonPrimitive::new);
    public static final VariantSetting<Integer> WEIGHT = new VariantSetting<Integer>("weight", JsonPrimitive::new);

    public static enum Rotation {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        private final int degrees;

        private Rotation(int j) {
            this.degrees = j;
        }
    }
}

