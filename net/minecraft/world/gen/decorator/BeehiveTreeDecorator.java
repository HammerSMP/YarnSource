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
import java.util.stream.Collectors;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;

public class BeehiveTreeDecorator
extends TreeDecorator {
    public static final Codec<BeehiveTreeDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(BeehiveTreeDecorator::new, arg -> Float.valueOf(arg.chance)).codec();
    private final float chance;

    public BeehiveTreeDecorator(float f) {
        this.chance = f;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.BEEHIVE;
    }

    @Override
    public void generate(ServerWorldAccess arg2, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        if (random.nextFloat() >= this.chance) {
            return;
        }
        Direction lv = BeehiveBlock.getRandomGenerationDirection(random);
        int i = !leavesPositions.isEmpty() ? Math.max(leavesPositions.get(0).getY() - 1, logPositions.get(0).getY()) : Math.min(logPositions.get(0).getY() + 1 + random.nextInt(3), logPositions.get(logPositions.size() - 1).getY());
        List list3 = logPositions.stream().filter(arg -> arg.getY() == i).collect(Collectors.toList());
        if (list3.isEmpty()) {
            return;
        }
        BlockPos lv2 = (BlockPos)list3.get(random.nextInt(list3.size()));
        BlockPos lv3 = lv2.offset(lv);
        if (!Feature.isAir(arg2, lv3) || !Feature.isAir(arg2, lv3.offset(Direction.SOUTH))) {
            return;
        }
        BlockState lv4 = (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, Direction.SOUTH);
        this.setBlockStateAndEncompassPosition(arg2, lv3, lv4, set, box);
        BlockEntity lv5 = arg2.getBlockEntity(lv3);
        if (lv5 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv6 = (BeehiveBlockEntity)lv5;
            int j = 2 + random.nextInt(2);
            for (int k = 0; k < j; ++k) {
                BeeEntity lv7 = new BeeEntity((EntityType<? extends BeeEntity>)EntityType.BEE, arg2.getWorld());
                lv6.tryEnterHive(lv7, false, random.nextInt(599));
            }
        }
    }
}

