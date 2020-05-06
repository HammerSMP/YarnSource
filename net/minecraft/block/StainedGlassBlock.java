/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;

public class StainedGlassBlock
extends AbstractGlassBlock
implements Stainable {
    private final DyeColor color;

    public StainedGlassBlock(DyeColor arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.color = arg;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }
}

