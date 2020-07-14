/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BoneMealItem
extends Item {
    public BoneMealItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World lv = context.getWorld();
        BlockPos lv2 = context.getBlockPos();
        BlockPos lv3 = lv2.offset(context.getSide());
        if (BoneMealItem.useOnFertilizable(context.getStack(), lv, lv2)) {
            if (!lv.isClient) {
                lv.syncWorldEvent(2005, lv2, 0);
            }
            return ActionResult.success(lv.isClient);
        }
        BlockState lv4 = lv.getBlockState(lv2);
        boolean bl = lv4.isSideSolidFullSquare(lv, lv2, context.getSide());
        if (bl && BoneMealItem.useOnGround(context.getStack(), lv, lv3, context.getSide())) {
            if (!lv.isClient) {
                lv.syncWorldEvent(2005, lv3, 0);
            }
            return ActionResult.success(lv.isClient);
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack stack, World world, BlockPos pos) {
        Fertilizable lv2;
        BlockState lv = world.getBlockState(pos);
        if (lv.getBlock() instanceof Fertilizable && (lv2 = (Fertilizable)((Object)lv.getBlock())).isFertilizable(world, pos, lv, world.isClient)) {
            if (world instanceof ServerWorld) {
                if (lv2.canGrow(world, world.random, pos, lv)) {
                    lv2.grow((ServerWorld)world, world.random, pos, lv);
                }
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack stack, World world, BlockPos blockPos, @Nullable Direction facing) {
        if (!world.getBlockState(blockPos).isOf(Blocks.WATER) || world.getFluidState(blockPos).getLevel() != 8) {
            return false;
        }
        if (!(world instanceof ServerWorld)) {
            return true;
        }
        block0: for (int i = 0; i < 128; ++i) {
            BlockPos lv = blockPos;
            Biome lv2 = world.getBiome(lv);
            BlockState lv3 = Blocks.SEAGRASS.getDefaultState();
            for (int j = 0; j < i / 16; ++j) {
                lv = lv.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
                lv2 = world.getBiome(lv);
                if (world.getBlockState(lv).isFullCube(world, lv)) continue block0;
            }
            if (lv2 == Biomes.WARM_OCEAN || lv2 == Biomes.DEEP_WARM_OCEAN) {
                if (i == 0 && facing != null && facing.getAxis().isHorizontal()) {
                    lv3 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandom(world.random)).getDefaultState().with(DeadCoralWallFanBlock.FACING, facing);
                } else if (RANDOM.nextInt(4) == 0) {
                    lv3 = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM)).getDefaultState();
                }
            }
            if (lv3.getBlock().isIn(BlockTags.WALL_CORALS)) {
                for (int k = 0; !lv3.canPlaceAt(world, lv) && k < 4; ++k) {
                    lv3 = (BlockState)lv3.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(RANDOM));
                }
            }
            if (!lv3.canPlaceAt(world, lv)) continue;
            BlockState lv4 = world.getBlockState(lv);
            if (lv4.isOf(Blocks.WATER) && world.getFluidState(lv).getLevel() == 8) {
                world.setBlockState(lv, lv3, 3);
                continue;
            }
            if (!lv4.isOf(Blocks.SEAGRASS) || RANDOM.nextInt(10) != 0) continue;
            ((Fertilizable)((Object)Blocks.SEAGRASS)).grow((ServerWorld)world, RANDOM, lv, lv4);
        }
        stack.decrement(1);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static void createParticles(WorldAccess world, BlockPos pos, int count) {
        double g;
        BlockState lv;
        if (count == 0) {
            count = 15;
        }
        if ((lv = world.getBlockState(pos)).isAir()) {
            return;
        }
        double d = 0.5;
        if (lv.isOf(Blocks.WATER)) {
            count *= 3;
            double e = 1.0;
            d = 3.0;
        } else if (lv.isOpaqueFullCube(world, pos)) {
            pos = pos.up();
            count *= 3;
            d = 3.0;
            double f = 1.0;
        } else {
            g = lv.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
        }
        world.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        for (int j = 0; j < count; ++j) {
            double p;
            double o;
            double h = RANDOM.nextGaussian() * 0.02;
            double k = RANDOM.nextGaussian() * 0.02;
            double l = RANDOM.nextGaussian() * 0.02;
            double m = 0.5 - d;
            double n = (double)pos.getX() + m + RANDOM.nextDouble() * d * 2.0;
            if (world.getBlockState(new BlockPos(n, o = (double)pos.getY() + RANDOM.nextDouble() * g, p = (double)pos.getZ() + m + RANDOM.nextDouble() * d * 2.0).down()).isAir()) continue;
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, n, o, p, h, k, l);
        }
    }
}

