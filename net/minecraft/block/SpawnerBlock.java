/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SpawnerBlock
extends BlockWithEntity {
    protected SpawnerBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new MobSpawnerBlockEntity();
    }

    @Override
    public void onStacksDropped(BlockState arg, ServerWorld arg2, BlockPos arg3, ItemStack arg4) {
        super.onStacksDropped(arg, arg2, arg3, arg4);
        int i = 15 + arg2.random.nextInt(15) + arg2.random.nextInt(15);
        this.dropExperience(arg2, arg3, i);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }
}

