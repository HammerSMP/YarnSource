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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator
extends TreeDecorator {
    public LeaveVineTreeDecorator() {
        super(TreeDecoratorType.LEAVE_VINE);
    }

    public <T> LeaveVineTreeDecorator(Dynamic<T> dynamic) {
        this();
    }

    @Override
    public void generate(IWorld arg, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg2) {
        list2.forEach(arg3 -> {
            BlockPos lv4;
            BlockPos lv3;
            BlockPos lv2;
            BlockPos lv;
            if (random.nextInt(4) == 0 && Feature.method_27370(arg, lv = arg3.west())) {
                this.method_23467(arg, lv, VineBlock.EAST, set, arg2);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(arg, lv2 = arg3.east())) {
                this.method_23467(arg, lv2, VineBlock.WEST, set, arg2);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(arg, lv3 = arg3.north())) {
                this.method_23467(arg, lv3, VineBlock.SOUTH, set, arg2);
            }
            if (random.nextInt(4) == 0 && Feature.method_27370(arg, lv4 = arg3.south())) {
                this.method_23467(arg, lv4, VineBlock.NORTH, set, arg2);
            }
        });
    }

    private void method_23467(ModifiableTestableWorld arg, BlockPos arg2, BooleanProperty arg3, Set<BlockPos> set, BlockBox arg4) {
        this.placeVine(arg, arg2, arg3, set, arg4);
        arg2 = arg2.down();
        for (int i = 4; Feature.method_27370(arg, arg2) && i > 0; --i) {
            this.placeVine(arg, arg2, arg3, set, arg4);
            arg2 = arg2.down();
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString())))).getValue();
    }
}

