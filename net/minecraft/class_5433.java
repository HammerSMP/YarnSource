/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;

public class class_5433
extends DiskFeature {
    public class_5433(Codec<DiskFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, ChunkGenerator arg2, Random random, BlockPos arg3, DiskFeatureConfig arg4) {
        if (!arg.getFluidState(arg3).isIn(FluidTags.WATER)) {
            return false;
        }
        return super.generate(arg, arg2, random, arg3, arg4);
    }
}

