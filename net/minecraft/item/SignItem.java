/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignItem
extends WallStandingBlockItem {
    public SignItem(Item.Settings arg, Block arg2, Block arg3) {
        super(arg2, arg3, arg);
    }

    @Override
    protected boolean postPlacement(BlockPos arg, World arg2, @Nullable PlayerEntity arg3, ItemStack arg4, BlockState arg5) {
        boolean bl = super.postPlacement(arg, arg2, arg3, arg4, arg5);
        if (!arg2.isClient && !bl && arg3 != null) {
            arg3.openEditSignScreen((SignBlockEntity)arg2.getBlockEntity(arg));
        }
        return bl;
    }
}

