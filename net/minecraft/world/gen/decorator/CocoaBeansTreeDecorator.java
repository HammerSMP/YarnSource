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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    private final float field_21318;

    public CocoaBeansTreeDecorator(float f) {
        super(TreeDecoratorType.COCOA);
        this.field_21318 = f;
    }

    public <T> CocoaBeansTreeDecorator(Dynamic<T> dynamic) {
        this(dynamic.get("probability").asFloat(0.0f));
    }

    @Override
    public void generate(IWorld arg2, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg22) {
        if (random.nextFloat() >= this.field_21318) {
            return;
        }
        int i = list.get(0).getY();
        list.stream().filter(arg -> arg.getY() - i <= 2).forEach(arg3 -> {
            for (Direction lv : Direction.Type.HORIZONTAL) {
                Direction lv2;
                BlockPos lv3;
                if (!(random.nextFloat() <= 0.25f) || !Feature.method_27370(arg2, lv3 = arg3.add((lv2 = lv.getOpposite()).getOffsetX(), 0, lv2.getOffsetZ()))) continue;
                BlockState lv4 = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, lv);
                this.setBlockStateAndEncompassPosition(arg2, lv3, lv4, set, arg22);
            }
        });
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.TREE_DECORATOR_TYPE.getId(this.type).toString()), (Object)dynamicOps.createString("probability"), (Object)dynamicOps.createFloat(this.field_21318)))).getValue();
    }
}

