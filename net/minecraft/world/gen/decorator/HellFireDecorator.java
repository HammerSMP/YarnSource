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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class HellFireDecorator
extends SimpleDecorator<CountDecoratorConfig> {
    public HellFireDecorator(Codec<CountDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountDecoratorConfig arg, BlockPos arg2) {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < random.nextInt(random.nextInt(arg.count) + 1) + 1; ++i) {
            int j = random.nextInt(16) + arg2.getX();
            int k = random.nextInt(16) + arg2.getZ();
            int l = random.nextInt(120) + 4;
            list.add(new BlockPos(j, l, k));
        }
        return list.stream();
    }
}

