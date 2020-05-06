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

@Environment(value=EnvType.CLIENT)
public class RenderTickCounter {
    public float tickDelta;
    public float lastFrameDuration;
    private long prevTimeMillis;
    private final float tickTime;

    public RenderTickCounter(float f, long l) {
        this.tickTime = 1000.0f / f;
        this.prevTimeMillis = l;
    }

    public int beginRenderTick(long l) {
        this.lastFrameDuration = (float)(l - this.prevTimeMillis) / this.tickTime;
        this.prevTimeMillis = l;
        this.tickDelta += this.lastFrameDuration;
        int i = (int)this.tickDelta;
        this.tickDelta -= (float)i;
        return i;
    }
}

