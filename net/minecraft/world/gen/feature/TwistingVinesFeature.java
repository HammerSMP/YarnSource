/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class TwistingVinesFeature
extends Feature<DefaultFeatureConfig> {
    public TwistingVinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        return TwistingVinesFeature.method_26265(arg, random, arg4, 8, 4, 8);
    }

    public static boolean method_26265(IWorld arg, Random random, BlockPos arg2, int i, int j, int k) {
        if (TwistingVinesFeature.isNotSuitable(arg, arg2)) {
            return false;
        }
        TwistingVinesFeature.generateVinesInArea(arg, random, arg2, i, j, k);
        return true;
    }

    private static void generateVinesInArea(IWorld arg, Random random, BlockPos arg2, int i, int j, int k) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = 0; l < i * i; ++l) {
            lv.set(arg2).move(MathHelper.nextInt(random, -i, i), MathHelper.nextInt(random, -j, j), MathHelper.nextInt(random, -i, i));
            if (!TwistingVinesFeature.method_27220(arg, lv) || TwistingVinesFeature.isNotSuitable(arg, lv)) continue;
            int m = MathHelper.nextInt(random, 1, k);
            if (random.nextInt(6) == 0) {
                m *= 2;
            }
            if (random.nextInt(5) == 0) {
                m = 1;
            }
            int n = 17;
            int o = 25;
            TwistingVinesFeature.generateVineColumn(arg, random, lv, m, 17, 25);
        }
    }

    private static boolean method_27220(IWorld arg, BlockPos.Mutable arg2) {
        do {
            arg2.move(0, -1, 0);
            if (!World.isHeightInvalid(arg2)) continue;
            return false;
        } while (arg.getBlockState(arg2).isAir());
        arg2.move(0, 1, 0);
        return true;
    }

    public static void generateVineColumn(IWorld arg, Random random, BlockPos.Mutable arg2, int i, int j, int k) {
        for (int l = 1; l <= i; ++l) {
            if (arg.isAir(arg2)) {
                if (l == i || !arg.isAir(arg2.up())) {
                    arg.setBlockState(arg2, (BlockState)Blocks.TWISTING_VINES.getDefaultState().with(AbstractPlantStemBlock.AGE, MathHelper.nextInt(random, j, k)), 2);
                    break;
                }
                arg.setBlockState(arg2, Blocks.TWISTING_VINES_PLANT.getDefaultState(), 2);
            }
            arg2.move(Direction.UP);
        }
    }

    private static boolean isNotSuitable(IWorld arg, BlockPos arg2) {
        if (!arg.isAir(arg2)) {
            return true;
        }
        BlockState lv = arg.getBlockState(arg2.down());
        return !lv.isOf(Blocks.NETHERRACK) && !lv.isOf(Blocks.WARPED_NYLIUM) && !lv.isOf(Blocks.WARPED_WART_BLOCK);
    }
}

