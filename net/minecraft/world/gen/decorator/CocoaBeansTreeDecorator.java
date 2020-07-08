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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    public static final Codec<CocoaBeansTreeDecorator> CODEC = Codec.FLOAT.fieldOf("probability").xmap(CocoaBeansTreeDecorator::new, arg -> Float.valueOf(arg.field_21318)).codec();
    private final float field_21318;

    public CocoaBeansTreeDecorator(float f) {
        this.field_21318 = f;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void generate(ServerWorldAccess arg2, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BlockBox arg22) {
        if (random.nextFloat() >= this.field_21318) {
            return;
        }
        int i = list.get(0).getY();
        list.stream().filter(arg -> arg.getY() - i <= 2).forEach(arg3 -> {
            for (Direction lv : Direction.Type.HORIZONTAL) {
                Direction lv2;
                BlockPos lv3;
                if (!(random.nextFloat() <= 0.25f) || !Feature.isAir(arg2, lv3 = arg3.add((lv2 = lv.getOpposite()).getOffsetX(), 0, lv2.getOffsetZ()))) continue;
                BlockState lv4 = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, lv);
                this.setBlockStateAndEncompassPosition(arg2, lv3, lv4, set, arg22);
            }
        });
    }
}

