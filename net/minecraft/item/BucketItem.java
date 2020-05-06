/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BucketItem
extends Item {
    private final Fluid fluid;

    public BucketItem(Fluid arg, Item.Settings arg2) {
        super(arg2);
        this.fluid = arg;
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        HitResult lv2 = BucketItem.rayTrace(arg, arg2, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.SOURCE_ONLY : RayTraceContext.FluidHandling.NONE);
        if (lv2.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        if (lv2.getType() == HitResult.Type.BLOCK) {
            BlockPos lv11;
            BlockHitResult lv3 = (BlockHitResult)lv2;
            BlockPos lv4 = lv3.getBlockPos();
            Direction lv5 = lv3.getSide();
            BlockPos lv6 = lv4.offset(lv5);
            if (!arg.canPlayerModifyAt(arg2, lv4) || !arg2.canPlaceOn(lv6, lv5, lv)) {
                return TypedActionResult.fail(lv);
            }
            if (this.fluid == Fluids.EMPTY) {
                Fluid lv8;
                BlockState lv7 = arg.getBlockState(lv4);
                if (lv7.getBlock() instanceof FluidDrainable && (lv8 = ((FluidDrainable)((Object)lv7.getBlock())).tryDrainFluid(arg, lv4, lv7)) != Fluids.EMPTY) {
                    arg2.incrementStat(Stats.USED.getOrCreateStat(this));
                    arg2.playSound(lv8.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0f, 1.0f);
                    ItemStack lv9 = this.getFilledStack(lv, arg2, lv8.getBucketItem());
                    if (!arg.isClient) {
                        Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)arg2, new ItemStack(lv8.getBucketItem()));
                    }
                    return TypedActionResult.success(lv9);
                }
                return TypedActionResult.fail(lv);
            }
            BlockState lv10 = arg.getBlockState(lv4);
            BlockPos blockPos = lv11 = lv10.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? lv4 : lv6;
            if (this.placeFluid(arg2, arg, lv11, lv3)) {
                this.onEmptied(arg, lv, lv11);
                if (arg2 instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)arg2, lv11, lv);
                }
                arg2.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(this.getEmptiedStack(lv, arg2));
            }
            return TypedActionResult.fail(lv);
        }
        return TypedActionResult.pass(lv);
    }

    protected ItemStack getEmptiedStack(ItemStack arg, PlayerEntity arg2) {
        if (!arg2.abilities.creativeMode) {
            return new ItemStack(Items.BUCKET);
        }
        return arg;
    }

    public void onEmptied(World arg, ItemStack arg2, BlockPos arg3) {
    }

    private ItemStack getFilledStack(ItemStack arg, PlayerEntity arg2, Item arg3) {
        if (arg2.abilities.creativeMode) {
            return arg;
        }
        arg.decrement(1);
        if (arg.isEmpty()) {
            return new ItemStack(arg3);
        }
        if (!arg2.inventory.insertStack(new ItemStack(arg3))) {
            arg2.dropItem(new ItemStack(arg3), false);
        }
        return arg;
    }

    public boolean placeFluid(@Nullable PlayerEntity arg, World arg2, BlockPos arg3, @Nullable BlockHitResult arg4) {
        if (!(this.fluid instanceof FlowableFluid)) {
            return false;
        }
        BlockState lv = arg2.getBlockState(arg3);
        Material lv2 = lv.getMaterial();
        boolean bl = lv.canBucketPlace(this.fluid);
        if (lv.isAir() || bl || lv.getBlock() instanceof FluidFillable && ((FluidFillable)((Object)lv.getBlock())).canFillWithFluid(arg2, arg3, lv, this.fluid)) {
            if (arg2.dimension.doesWaterVaporize() && this.fluid.isIn(FluidTags.WATER)) {
                int i = arg3.getX();
                int j = arg3.getY();
                int k = arg3.getZ();
                arg2.playSound(arg, arg3, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (arg2.random.nextFloat() - arg2.random.nextFloat()) * 0.8f);
                for (int l = 0; l < 8; ++l) {
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
                }
            } else if (lv.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER) {
                if (((FluidFillable)((Object)lv.getBlock())).tryFillWithFluid(arg2, arg3, lv, ((FlowableFluid)this.fluid).getStill(false))) {
                    this.playEmptyingSound(arg, arg2, arg3);
                }
            } else {
                if (!arg2.isClient && bl && !lv2.isLiquid()) {
                    arg2.breakBlock(arg3, true);
                }
                this.playEmptyingSound(arg, arg2, arg3);
                return arg2.setBlockState(arg3, this.fluid.getDefaultState().getBlockState(), 11);
            }
            return true;
        }
        if (arg4 == null) {
            return false;
        }
        return this.placeFluid(arg, arg2, arg4.getBlockPos().offset(arg4.getSide()), null);
    }

    protected void playEmptyingSound(@Nullable PlayerEntity arg, IWorld arg2, BlockPos arg3) {
        SoundEvent lv = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        arg2.playSound(arg, arg3, lv, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}

