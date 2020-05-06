/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractBannerBlock
extends BlockWithEntity {
    private final DyeColor color;

    protected AbstractBannerBlock(DyeColor arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.color = arg;
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new BannerBlockEntity(this.color);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv;
        if (arg5.hasCustomName() && (lv = arg.getBlockEntity(arg2)) instanceof BannerBlockEntity) {
            ((BannerBlockEntity)lv).setCustomName(arg5.getName());
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof BannerBlockEntity) {
            return ((BannerBlockEntity)lv).getPickStack(arg3);
        }
        return super.getPickStack(arg, arg2, arg3);
    }

    public DyeColor getColor() {
        return this.color;
    }
}

