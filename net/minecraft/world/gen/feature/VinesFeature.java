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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class VinesFeature
extends Feature<DefaultFeatureConfig> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public VinesFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        BlockPos.Mutable lv = arg4.mutableCopy();
        block0: for (int i = arg4.getY(); i < 256; ++i) {
            lv.set(arg4);
            lv.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            lv.setY(i);
            if (!arg.isAir(lv)) continue;
            for (Direction lv2 : DIRECTIONS) {
                if (lv2 == Direction.DOWN || !VineBlock.shouldConnectTo(arg, lv, lv2)) continue;
                arg.setBlockState(lv, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(lv2), true), 2);
                continue block0;
            }
        }
        return true;
    }
}

