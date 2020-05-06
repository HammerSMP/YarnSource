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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class WeepingVinesFeature
extends Feature<DefaultFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public WeepingVinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        if (!arg.isAir(arg4)) {
            return false;
        }
        BlockState lv = arg.getBlockState(arg4.up());
        if (!lv.isOf(Blocks.NETHERRACK) && !lv.isOf(Blocks.NETHER_WART_BLOCK)) {
            return false;
        }
        this.generateNetherWartBlocksInArea(arg, random, arg4);
        this.generateVinesInArea(arg, random, arg4);
        return true;
    }

    private void generateNetherWartBlocksInArea(IWorld arg, Random random, BlockPos arg2) {
        arg.setBlockState(arg2, Blocks.NETHER_WART_BLOCK.getDefaultState(), 2);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (int i = 0; i < 200; ++i) {
            lv.set(arg2, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (!arg.isAir(lv)) continue;
            int j = 0;
            for (Direction lv3 : DIRECTIONS) {
                BlockState lv4 = arg.getBlockState(lv2.set(lv, lv3));
                if (lv4.isOf(Blocks.NETHERRACK) || lv4.isOf(Blocks.NETHER_WART_BLOCK)) {
                    ++j;
                }
                if (j > 1) break;
            }
            if (j != true) continue;
            arg.setBlockState(lv, Blocks.NETHER_WART_BLOCK.getDefaultState(), 2);
        }
    }

    private void generateVinesInArea(IWorld arg, Random random, BlockPos arg2) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i < 100; ++i) {
            BlockState lv2;
            lv.set(arg2, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (!arg.isAir(lv) || !(lv2 = arg.getBlockState(lv.up())).isOf(Blocks.NETHERRACK) && !lv2.isOf(Blocks.NETHER_WART_BLOCK)) continue;
            int j = MathHelper.nextInt(random, 1, 8);
            if (random.nextInt(6) == 0) {
                j *= 2;
            }
            if (random.nextInt(5) == 0) {
                j = 1;
            }
            int k = 17;
            int l = 25;
            WeepingVinesFeature.generateVineColumn(arg, random, lv, j, 17, 25);
        }
    }

    public static void generateVineColumn(IWorld arg, Random random, BlockPos.Mutable arg2, int i, int j, int k) {
        for (int l = 0; l <= i; ++l) {
            if (arg.isAir(arg2)) {
                if (l == i || !arg.isAir((BlockPos)arg2.down())) {
                    arg.setBlockState(arg2, (BlockState)Blocks.WEEPING_VINES.getDefaultState().with(AbstractPlantStemBlock.AGE, MathHelper.nextInt(random, j, k)), 2);
                    break;
                }
                arg.setBlockState(arg2, Blocks.WEEPING_VINES_PLANT.getDefaultState(), 2);
            }
            arg2.move(Direction.DOWN);
        }
    }
}

