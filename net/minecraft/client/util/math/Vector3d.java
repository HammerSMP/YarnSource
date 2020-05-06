/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Vector3d {
    public double x;
    public double y;
    public double z;

    public Vector3d(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
    }
}

