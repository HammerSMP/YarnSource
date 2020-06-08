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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineTreeDecorator> field_24964 = Codec.unit(() -> field_24965);
    public static final TrunkVineTreeDecorator field_24965 = new TrunkVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void generate(WorldAccess arg, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg2) {
        list.forEach(arg3 -> {
            BlockPos lv4;
            BlockPos lv3;
            BlockPos lv2;
            BlockPos lv;
            if (random.nextInt(3) > 0 && Feature.method_27370(arg, lv = arg3.west())) {
                this.placeVine(arg, lv, VineBlock.EAST, set, arg2);
            }
            if (random.nextInt(3) > 0 && Feature.method_27370(arg, lv2 = arg3.east())) {
                this.placeVine(arg, lv2, VineBlock.WEST, set, arg2);
            }
            if (random.nextInt(3) > 0 && Feature.method_27370(arg, lv3 = arg3.north())) {
                this.placeVine(arg, lv3, VineBlock.SOUTH, set, arg2);
            }
            if (random.nextInt(3) > 0 && Feature.method_27370(arg, lv4 = arg3.south())) {
                this.placeVine(arg, lv4, VineBlock.NORTH, set, arg2);
            }
        });
    }
}

