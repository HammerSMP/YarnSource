/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.class_5444;
import net.minecraft.class_5445;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class class_5448<DC extends DecoratorConfig>
extends class_5445<DC> {
    public class_5448(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type method_30463(DC arg) {
        return Heightmap.Type.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, DC arg2, BlockPos arg3) {
        int i = arg3.getX();
        int j = arg3.getZ();
        int k = arg.method_30460(this.method_30463(arg2), i, j);
        if (k == 0) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(new BlockPos(i, random.nextInt(k * 2), j));
    }
}

