/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.tree;

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
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.tree.TreeDecorator;
import net.minecraft.world.gen.tree.TreeDecoratorType;

public class CocoaBeansTreeDecorator
extends TreeDecorator {
    public static final Codec<CocoaBeansTreeDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(CocoaBeansTreeDecorator::new, arg -> Float.valueOf(arg.probability)).codec();
    private final float probability;

    public CocoaBeansTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void generate(ServerWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.probability) {
            return;
        }
        int i = logPositions.get(0).getY();
        logPositions.stream().filter(arg -> arg.getY() - i <= 2).forEach(arg3 -> {
            for (Direction lv : Direction.Type.HORIZONTAL) {
                Direction lv2;
                BlockPos lv3;
                if (!(random.nextFloat() <= 0.25f) || !Feature.isAir(world, lv3 = arg3.add((lv2 = lv.getOpposite()).getOffsetX(), 0, lv2.getOffsetZ()))) continue;
                BlockState lv4 = (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, lv);
                this.setBlockStateAndEncompassPosition(world, lv3, lv4, set, box);
            }
        });
    }
}

