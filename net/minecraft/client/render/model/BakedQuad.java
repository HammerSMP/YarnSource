/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class BakedQuad {
    protected final int[] vertexData;
    protected final int colorIndex;
    protected final Direction face;
    protected final Sprite sprite;
    private final boolean shade;

    public BakedQuad(int[] is, int i, Direction arg, Sprite arg2, boolean bl) {
        this.vertexData = is;
        this.colorIndex = i;
        this.face = arg;
        this.sprite = arg2;
        this.shade = bl;
    }

    public int[] getVertexData() {
        return this.vertexData;
    }

    public boolean hasColor() {
        return this.colorIndex != -1;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public Direction getFace() {
        return this.face;
    }

    public boolean hasShade() {
        return this.shade;
    }
}

