/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
public abstract class FixedColorVertexConsumer
implements VertexConsumer {
    protected boolean colorFixed = false;
    protected int fixedRed = 255;
    protected int fixedGreen = 255;
    protected int fixedBlue = 255;
    protected int fixedAlpha = 255;

    public void fixedColor(int i, int j, int k, int l) {
        this.fixedRed = i;
        this.fixedGreen = j;
        this.fixedBlue = k;
        this.fixedAlpha = l;
        this.colorFixed = true;
    }
}
