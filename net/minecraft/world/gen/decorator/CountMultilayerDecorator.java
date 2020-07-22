/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class CountMultilayerDecorator
extends Decorator<CountConfig> {
    public CountMultilayerDecorator(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext arg, Random random, CountConfig arg2, BlockPos arg3) {
        boolean bl;
        ArrayList list = Lists.newArrayList();
        int i = 0;
        do {
            bl = false;
            for (int j = 0; j < arg2.method_30396().getValue(random); ++j) {
                int l;
                int m;
                int k = random.nextInt(16) + arg3.getX();
                int n = CountMultilayerDecorator.method_30473(arg, k, m = arg.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l = random.nextInt(16) + arg3.getZ()), l, i);
                if (n == Integer.MAX_VALUE) continue;
                list.add(new BlockPos(k, n, l));
                bl = true;
            }
            ++i;
        } while (bl);
        return list.stream();
    }

    private static int method_30473(DecoratorContext arg, int i, int j, int k, int l) {
        BlockPos.Mutable lv = new BlockPos.Mutable(i, j, k);
        int m = 0;
        BlockState lv2 = arg.getBlockState(lv);
        for (int n = j; n >= 1; --n) {
            lv.setY(n - 1);
            BlockState lv3 = arg.getBlockState(lv);
            if (!CountMultilayerDecorator.method_30472(lv3) && CountMultilayerDecorator.method_30472(lv2) && !lv3.isOf(Blocks.BEDROCK)) {
                if (m == l) {
                    return lv.getY() + 1;
                }
                ++m;
            }
            lv2 = lv3;
        }
        return Integer.MAX_VALUE;
    }

    private static boolean method_30472(BlockState arg) {
        return arg.isAir() || arg.isOf(Blocks.WATER) || arg.isOf(Blocks.LAVA);
    }
}

