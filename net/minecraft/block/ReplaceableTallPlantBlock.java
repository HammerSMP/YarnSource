/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;

public class ReplaceableTallPlantBlock
extends TallPlantBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;

    public ReplaceableTallPlantBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        boolean bl = super.canReplace(arg, arg2);
        if (bl && arg2.getStack().getItem() == this.asItem()) {
            return false;
        }
        return bl;
    }
}

