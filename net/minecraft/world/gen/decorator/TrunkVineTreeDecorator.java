/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineTreeDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void generate(ServerWorldAccess arg, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        logPositions.forEach(arg3 -> {
            BlockPos lv4;
            BlockPos lv3;
            BlockPos lv2;
            BlockPos lv;
            if (random.nextInt(3) > 0 && Feature.isAir(arg, lv = arg3.west())) {
                this.placeVine(arg, lv, VineBlock.EAST, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(arg, lv2 = arg3.east())) {
                this.placeVine(arg, lv2, VineBlock.WEST, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(arg, lv3 = arg3.north())) {
                this.placeVine(arg, lv3, VineBlock.SOUTH, set, box);
            }
            if (random.nextInt(3) > 0 && Feature.isAir(arg, lv4 = arg3.south())) {
                this.placeVine(arg, lv4, VineBlock.NORTH, set, box);
            }
        });
    }
}

