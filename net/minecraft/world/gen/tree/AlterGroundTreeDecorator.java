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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.tree.TreeDecorator;
import net.minecraft.world.gen.tree.TreeDecoratorType;

public class AlterGroundTreeDecorator
extends TreeDecorator {
    public static final Codec<AlterGroundTreeDecorator> CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundTreeDecorator::new, arg -> arg.provider).codec();
    private final BlockStateProvider provider;

    public AlterGroundTreeDecorator(BlockStateProvider provider) {
        this.provider = provider;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.ALTER_GROUND;
    }

    @Override
    public void generate(ServerWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box) {
        int i = logPositions.get(0).getY();
        logPositions.stream().filter(arg -> arg.getY() == i).forEach(arg2 -> {
            this.method_23462(world, random, arg2.west().north());
            this.method_23462(world, random, arg2.east(2).north());
            this.method_23462(world, random, arg2.west().south(2));
            this.method_23462(world, random, arg2.east(2).south(2));
            for (int i = 0; i < 5; ++i) {
                int j = random.nextInt(64);
                int k = j % 8;
                int l = j / 8;
                if (k != 0 && k != 7 && l != 0 && l != 7) continue;
                this.method_23462(world, random, arg2.add(-3 + k, 0, -3 + l));
            }
        });
    }

    private void method_23462(ModifiableTestableWorld arg, Random random, BlockPos arg2) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) == 2 && Math.abs(j) == 2) continue;
                this.method_23463(arg, random, arg2.add(i, 0, j));
            }
        }
    }

    private void method_23463(ModifiableTestableWorld arg, Random random, BlockPos arg2) {
        for (int i = 2; i >= -3; --i) {
            BlockPos lv = arg2.up(i);
            if (Feature.isSoil(arg, lv)) {
                arg.setBlockState(lv, this.provider.getBlockState(random, arg2), 19);
                break;
            }
            if (!Feature.isAir(arg, lv) && i < 0) break;
        }
    }
}

