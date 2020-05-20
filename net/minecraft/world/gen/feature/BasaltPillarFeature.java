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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BasaltPillarFeature
extends Feature<DefaultFeatureConfig> {
    public BasaltPillarFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg4, DefaultFeatureConfig arg5) {
        if (!arg.isAir(arg4) || arg.isAir(arg4.up())) {
            return false;
        }
        BlockPos.Mutable lv = arg4.mutableCopy();
        BlockPos.Mutable lv2 = arg4.mutableCopy();
        boolean bl = true;
        boolean bl2 = true;
        boolean bl3 = true;
        boolean bl4 = true;
        while (arg.isAir(lv)) {
            if (World.isHeightInvalid(lv)) {
                return true;
            }
            arg.setBlockState(lv, Blocks.BASALT.getDefaultState(), 2);
            bl = bl && this.stopOrPlaceBasalt(arg, random, lv2.set(lv, Direction.NORTH));
            bl2 = bl2 && this.stopOrPlaceBasalt(arg, random, lv2.set(lv, Direction.SOUTH));
            bl3 = bl3 && this.stopOrPlaceBasalt(arg, random, lv2.set(lv, Direction.WEST));
            bl4 = bl4 && this.stopOrPlaceBasalt(arg, random, lv2.set(lv, Direction.EAST));
            lv.move(Direction.DOWN);
        }
        lv.move(Direction.UP);
        this.tryPlaceBasalt(arg, random, lv2.set(lv, Direction.NORTH));
        this.tryPlaceBasalt(arg, random, lv2.set(lv, Direction.SOUTH));
        this.tryPlaceBasalt(arg, random, lv2.set(lv, Direction.WEST));
        this.tryPlaceBasalt(arg, random, lv2.set(lv, Direction.EAST));
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int i = -3; i < 4; ++i) {
            for (int j = -3; j < 4; ++j) {
                int k = MathHelper.abs(i) * MathHelper.abs(j);
                if (random.nextInt(10) >= 10 - k) continue;
                lv3.set(lv.add(i, 0, j));
                int l = 3;
                while (arg.isAir(lv2.set(lv3, Direction.DOWN))) {
                    lv3.move(Direction.DOWN);
                    if (--l > 0) continue;
                }
                if (arg.isAir(lv2.set(lv3, Direction.DOWN))) continue;
                arg.setBlockState(lv3, Blocks.BASALT.getDefaultState(), 2);
            }
        }
        return true;
    }

    private void tryPlaceBasalt(WorldAccess arg, Random random, BlockPos arg2) {
        if (random.nextBoolean()) {
            arg.setBlockState(arg2, Blocks.BASALT.getDefaultState(), 2);
        }
    }

    private boolean stopOrPlaceBasalt(WorldAccess arg, Random random, BlockPos arg2) {
        if (random.nextInt(10) != 0) {
            arg.setBlockState(arg2, Blocks.BASALT.getDefaultState(), 2);
            return true;
        }
        return false;
    }
}

