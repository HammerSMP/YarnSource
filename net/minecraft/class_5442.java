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
import net.minecraft.class_5443;
import net.minecraft.class_5444;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;

public class class_5442
extends Decorator<class_5443> {
    public class_5442(Codec<class_5443> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(class_5444 arg, Random random, class_5443 arg2, BlockPos arg32) {
        return arg2.method_30455().method_30444(arg, random, arg32).flatMap(arg3 -> arg2.method_30457().method_30444(arg, random, (BlockPos)arg3));
    }
}

