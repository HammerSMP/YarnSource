/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MetricsData {
    private final long[] samples = new long[240];
    private int startIndex;
    private int sampleCount;
    private int writeIndex;

    public void pushSample(long l) {
        this.samples[this.writeIndex] = l;
        ++this.writeIndex;
        if (this.writeIndex == 240) {
            this.writeIndex = 0;
        }
        if (this.sampleCount < 240) {
            this.startIndex = 0;
            ++this.sampleCount;
        } else {
            this.startIndex = this.wrapIndex(this.writeIndex + 1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int method_15248(long l, int i, int j) {
        double d = (double)l / (double)(1000000000L / (long)j);
        return (int)(d * (double)i);
    }

    @Environment(value=EnvType.CLIENT)
    public int getStartIndex() {
        return this.startIndex;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCurrentIndex() {
        return this.writeIndex;
    }

    public int wrapIndex(int i) {
        return i % 240;
    }

    @Environment(value=EnvType.CLIENT)
    public long[] getSamples() {
        return this.samples;
    }
}

