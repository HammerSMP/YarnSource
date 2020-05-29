/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.placer;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class ColumnPlacer
extends BlockPlacer {
    public static final Codec<ColumnPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("min_size").forGetter(arg -> arg.minSize), (App)Codec.INT.fieldOf("extra_size").forGetter(arg -> arg.extraSize)).apply((Applicative)instance, ColumnPlacer::new));
    private final int minSize;
    private final int extraSize;

    public ColumnPlacer(int i, int j) {
        this.minSize = i;
        this.extraSize = j;
    }

    @Override
    protected BlockPlacerType<?> method_28673() {
        return BlockPlacerType.COLUMN_PLACER;
    }

    @Override
    public void method_23403(WorldAccess arg, BlockPos arg2, BlockState arg3, Random random) {
        BlockPos.Mutable lv = arg2.mutableCopy();
        int i = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);
        for (int j = 0; j < i; ++j) {
            arg.setBlockState(lv, arg3, 2);
            lv.move(Direction.UP);
        }
    }
}

