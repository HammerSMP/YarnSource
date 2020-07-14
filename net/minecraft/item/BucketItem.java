/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
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
import net.minecraft.item.ItemUsage;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BucketItem
extends Item {
    private final Fluid fluid;

    public BucketItem(Fluid fluid, Item.Settings settings) {
        super(settings);
        this.fluid = fluid;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        BlockHitResult lv2 = BucketItem.rayTrace(world, user, this.fluid == Fluids.EMPTY ? RayTraceContext.FluidHandling.SOURCE_ONLY : RayTraceContext.FluidHandling.NONE);
        if (((HitResult)lv2).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        if (((HitResult)lv2).getType() == HitResult.Type.BLOCK) {
            BlockPos lv11;
            BlockHitResult lv3 = lv2;
            BlockPos lv4 = lv3.getBlockPos();
            Direction lv5 = lv3.getSide();
            BlockPos lv6 = lv4.offset(lv5);
            if (!world.canPlayerModifyAt(user, lv4) || !user.canPlaceOn(lv6, lv5, lv)) {
                return TypedActionResult.fail(lv);
            }
            if (this.fluid == Fluids.EMPTY) {
                Fluid lv8;
                BlockState lv7 = world.getBlockState(lv4);
                if (lv7.getBlock() instanceof FluidDrainable && (lv8 = ((FluidDrainable)((Object)lv7.getBlock())).tryDrainFluid(world, lv4, lv7)) != Fluids.EMPTY) {
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    user.playSound(lv8.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0f, 1.0f);
                    ItemStack lv9 = ItemUsage.method_30012(lv, user, new ItemStack(lv8.getBucketItem()));
                    if (!world.isClient) {
                        Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, new ItemStack(lv8.getBucketItem()));
                    }
                    return TypedActionResult.method_29237(lv9, world.isClient());
                }
                return TypedActionResult.fail(lv);
            }
            BlockState lv10 = world.getBlockState(lv4);
            BlockPos blockPos = lv11 = lv10.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? lv4 : lv6;
            if (this.placeFluid(user, world, lv11, lv3)) {
                this.onEmptied(world, lv, lv11);
                if (user instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, lv11, lv);
                }
                user.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.method_29237(this.getEmptiedStack(lv, user), world.isClient());
            }
            return TypedActionResult.fail(lv);
        }
        return TypedActionResult.pass(lv);
    }

    protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        if (!player.abilities.creativeMode) {
            return new ItemStack(Items.BUCKET);
        }
        return stack;
    }

    public void onEmptied(World world, ItemStack stack, BlockPos pos) {
    }

    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult arg4) {
        boolean bl2;
        if (!(this.fluid instanceof FlowableFluid)) {
            return false;
        }
        BlockState lv = world.getBlockState(pos);
        Block lv2 = lv.getBlock();
        Material lv3 = lv.getMaterial();
        boolean bl = lv.canBucketPlace(this.fluid);
        boolean bl3 = bl2 = lv.isAir() || bl || lv2 instanceof FluidFillable && ((FluidFillable)((Object)lv2)).canFillWithFluid(world, pos, lv, this.fluid);
        if (!bl2) {
            return arg4 != null && this.placeFluid(player, world, arg4.getBlockPos().offset(arg4.getSide()), null);
        }
        if (world.getDimension().isUltrawarm() && this.fluid.isIn(FluidTags.WATER)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if (lv2 instanceof FluidFillable && this.fluid == Fluids.WATER) {
            ((FluidFillable)((Object)lv2)).tryFillWithFluid(world, pos, lv, ((FlowableFluid)this.fluid).getStill(false));
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        if (!world.isClient && bl && !lv3.isLiquid()) {
            world.breakBlock(pos, true);
        }
        if (world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11) || lv.getFluidState().isStill()) {
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        return false;
    }

    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        SoundEvent lv = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, lv, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}

