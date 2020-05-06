/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class ModelRotation {
    public final Vector3f origin;
    public final Direction.Axis axis;
    public final float angle;
    public final boolean rescale;

    public ModelRotation(Vector3f arg, Direction.Axis arg2, float f, boolean bl) {
        this.origin = arg;
        this.axis = arg2;
        this.angle = f;
        this.rescale = bl;
    }
}

