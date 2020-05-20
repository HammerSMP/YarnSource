/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class FlatChunkGeneratorLayer {
    private final BlockState blockState;
    private final int thickness;
    private int startY;

    public FlatChunkGeneratorLayer(int i, Block arg) {
        this.thickness = i;
        this.blockState = arg.getDefaultState();
    }

    public int getThickness() {
        return this.thickness;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public int getStartY() {
        return this.startY;
    }

    public void setStartY(int i) {
        this.startY = i;
    }

    public String toString() {
        return (this.thickness != 1 ? this.thickness + "*" : "") + Registry.BLOCK.getId(this.blockState.getBlock());
    }
}
