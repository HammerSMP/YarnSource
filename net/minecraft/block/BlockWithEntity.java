/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockWithEntity
extends Block
implements BlockEntityProvider {
    protected BlockWithEntity(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState arg, World arg2, BlockPos arg3, int i, int j) {
        super.onSyncedBlockEvent(arg, arg2, arg3, i, j);
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv == null) {
            return false;
        }
        return lv.onSyncedBlockEvent(i, j);
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState arg, World arg2, BlockPos arg3) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        return lv instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)((Object)lv) : null;
    }
}

