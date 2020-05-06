/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    public TrunkVineTreeDecorator() {
        super(TreeDecoratorType.TRUNK_VINE);
    }

    public <T> TrunkVineTreeDecorator(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void generate(IWorld arg, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg2) {
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

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString())))).getValue();
    }
}

