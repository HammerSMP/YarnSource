/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class SimpleBlockPlacer
extends BlockPlacer {
    public static final Codec<SimpleBlockPlacer> field_24870 = Codec.unit(() -> field_24871);
    public static final SimpleBlockPlacer field_24871 = new SimpleBlockPlacer();

    @Override
    protected BlockPlacerType<?> method_28673() {
        return BlockPlacerType.SIMPLE_BLOCK_PLACER;
    }

    @Override
    public void method_23403(WorldAccess arg, BlockPos arg2, BlockState arg3, Random random) {
        arg.setBlockState(arg2, arg3, 2);
    }
}

