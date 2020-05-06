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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public abstract class FlowerFeature<U extends FeatureConfig>
extends Feature<U> {
    public FlowerFeature(Function<Dynamic<?>, ? extends U> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, Random random, BlockPos arg4, U arg5) {
        BlockState lv = this.getFlowerState(random, arg4, arg5);
        int i = 0;
        for (int j = 0; j < this.getFlowerAmount(arg5); ++j) {
            BlockPos lv2 = this.getPos(random, arg4, arg5);
            if (!arg.isAir(lv2) || lv2.getY() >= 255 || !lv.canPlaceAt(arg, lv2) || !this.isPosValid(arg, lv2, arg5)) continue;
            arg.setBlockState(lv2, lv, 2);
            ++i;
        }
        return i > 0;
    }

    public abstract boolean isPosValid(IWorld var1, BlockPos var2, U var3);

    public abstract int getFlowerAmount(U var1);

    public abstract BlockPos getPos(Random var1, BlockPos var2, U var3);

    public abstract BlockState getFlowerState(Random var1, BlockPos var2, U var3);
}

