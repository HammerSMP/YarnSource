/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public class class_5452
extends Decorator<SeaPickleFeatureConfig> {
    public class_5452(Codec<SeaPickleFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, SeaPickleFeatureConfig arg2, BlockPos arg3) {
        boolean bl;
        ArrayList list = Lists.newArrayList();
        int i = 0;
        do {
            bl = false;
            for (int j = 0; j < arg2.method_30396().method_30321(random); ++j) {
                int l;
                int m;
                int k = random.nextInt(16) + arg3.getX();
                int n = class_5452.method_30473(arg, k, m = arg.method_30460(Heightmap.Type.MOTION_BLOCKING, k, l = random.nextInt(16) + arg3.getZ()), l, i);
                if (n == Integer.MAX_VALUE) continue;
                list.add(new BlockPos(k, n, l));
                bl = true;
            }
            ++i;
        } while (bl);
        return list.stream();
    }

    private static int method_30473(class_5444 arg, int i, int j, int k, int l) {
        BlockPos.Mutable lv = new BlockPos.Mutable(i, j, k);
        int m = 0;
        BlockState lv2 = arg.method_30461(lv);
        for (int n = j; n >= 1; --n) {
            lv.setY(n - 1);
            BlockState lv3 = arg.method_30461(lv);
            if (!class_5452.method_30472(lv3) && class_5452.method_30472(lv2) && !lv3.isOf(Blocks.BEDROCK)) {
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

