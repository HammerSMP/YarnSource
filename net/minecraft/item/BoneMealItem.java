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
    public ActionResult useOnBlock(ItemUsageContext arg) {
        World lv = arg.getWorld();
        BlockPos lv2 = arg.getBlockPos();
        BlockPos lv3 = lv2.offset(arg.getSide());
        if (BoneMealItem.useOnFertilizable(arg.getStack(), lv, lv2)) {
            if (!lv.isClient) {
                lv.syncWorldEvent(2005, lv2, 0);
            }
            return ActionResult.method_29236(lv.isClient);
        }
        BlockState lv4 = lv.getBlockState(lv2);
        boolean bl = lv4.isSideSolidFullSquare(lv, lv2, arg.getSide());
        if (bl && BoneMealItem.useOnGround(arg.getStack(), lv, lv3, arg.getSide())) {
            if (!lv.isClient) {
                lv.syncWorldEvent(2005, lv3, 0);
            }
            return ActionResult.method_29236(lv.isClient);
        }
        return ActionResult.PASS;
    }

    public static boolean useOnFertilizable(ItemStack arg, World arg2, BlockPos arg3) {
        Fertilizable lv2;
        BlockState lv = arg2.getBlockState(arg3);
        if (lv.getBlock() instanceof Fertilizable && (lv2 = (Fertilizable)((Object)lv.getBlock())).isFertilizable(arg2, arg3, lv, arg2.isClient)) {
            if (arg2 instanceof ServerWorld) {
                if (lv2.canGrow(arg2, arg2.random, arg3, lv)) {
                    lv2.grow((ServerWorld)arg2, arg2.random, arg3, lv);
                }
                arg.decrement(1);
            }
            return true;
        }
        return false;
    }

    public static boolean useOnGround(ItemStack arg, World arg2, BlockPos arg3, @Nullable Direction arg4) {
        if (!arg2.getBlockState(arg3).isOf(Blocks.WATER) || arg2.getFluidState(arg3).getLevel() != 8) {
            return false;
        }
        if (!(arg2 instanceof ServerWorld)) {
            return true;
        }
        block0: for (int i = 0; i < 128; ++i) {
            BlockPos lv = arg3;
            Biome lv2 = arg2.getBiome(lv);
            BlockState lv3 = Blocks.SEAGRASS.getDefaultState();
            for (int j = 0; j < i / 16; ++j) {
                lv = lv.add(RANDOM.nextInt(3) - 1, (RANDOM.nextInt(3) - 1) * RANDOM.nextInt(3) / 2, RANDOM.nextInt(3) - 1);
                lv2 = arg2.getBiome(lv);
                if (arg2.getBlockState(lv).isFullCube(arg2, lv)) continue block0;
            }
            if (lv2 == Biomes.WARM_OCEAN || lv2 == Biomes.DEEP_WARM_OCEAN) {
                if (i == 0 && arg4 != null && arg4.getAxis().isHorizontal()) {
                    lv3 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandom(arg2.random)).getDefaultState().with(DeadCoralWallFanBlock.FACING, arg4);
                } else if (RANDOM.nextInt(4) == 0) {
                    lv3 = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandom(RANDOM)).getDefaultState();
                }
            }
            if (lv3.getBlock().isIn(BlockTags.WALL_CORALS)) {
                for (int k = 0; !lv3.canPlaceAt(arg2, lv) && k < 4; ++k) {
                    lv3 = (BlockState)lv3.with(DeadCoralWallFanBlock.FACING, Direction.Type.HORIZONTAL.random(RANDOM));
                }
            }
            if (!lv3.canPlaceAt(arg2, lv)) continue;
            BlockState lv4 = arg2.getBlockState(lv);
            if (lv4.isOf(Blocks.WATER) && arg2.getFluidState(lv).getLevel() == 8) {
                arg2.setBlockState(lv, lv3, 3);
                continue;
            }
            if (!lv4.isOf(Blocks.SEAGRASS) || RANDOM.nextInt(10) != 0) continue;
            ((Fertilizable)((Object)Blocks.SEAGRASS)).grow((ServerWorld)arg2, RANDOM, lv, lv4);
        }
        arg.decrement(1);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static void createParticles(WorldAccess arg, BlockPos arg2, int i) {
        double g;
        BlockState lv;
        if (i == 0) {
            i = 15;
        }
        if ((lv = arg.getBlockState(arg2)).isAir()) {
            return;
        }
        double d = 0.5;
        if (!lv.getFluidState().isEmpty()) {
            i *= 3;
            double e = 1.0;
            d = 3.0;
        } else if (lv.isOpaqueFullCube(arg, arg2)) {
            arg2 = arg2.up();
            i *= 3;
            d = 3.0;
            double f = 1.0;
        } else {
            g = lv.getOutlineShape(arg, arg2).getMax(Direction.Axis.Y);
        }
        arg.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, 0.0, 0.0, 0.0);
        for (int j = 0; j < i; ++j) {
            double p;
            double o;
            double h = RANDOM.nextGaussian() * 0.02;
            double k = RANDOM.nextGaussian() * 0.02;
            double l = RANDOM.nextGaussian() * 0.02;
            double m = 0.5 - d;
            double n = (double)arg2.getX() + m + RANDOM.nextDouble() * d * 2.0;
            if (arg.getBlockState(new BlockPos(n, o = (double)arg2.getY() + RANDOM.nextDouble() * g, p = (double)arg2.getZ() + m + RANDOM.nextDouble() * d * 2.0).down()).isAir()) continue;
            arg.addParticle(ParticleTypes.HAPPY_VILLAGER, n, o, p, h, k, l);
        }
    }
}

