/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.gen.decorator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.gen.decorator.TreeDecoratorType;

public abstract class TreeDecorator
implements DynamicSerializable {
    protected final TreeDecoratorType<?> type;

    protected TreeDecorator(TreeDecoratorType<?> arg) {
        this.type = arg;
    }

    public abstract void generate(IWorld var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BlockBox var6);

    protected void placeVine(ModifiableWorld arg, BlockPos arg2, BooleanProperty arg3, Set<BlockPos> set, BlockBox arg4) {
        this.setBlockStateAndEncompassPosition(arg, arg2, (BlockState)Blocks.VINE.getDefaultState().with(arg3, true), set, arg4);
    }

    protected void setBlockStateAndEncompassPosition(ModifiableWorld arg, BlockPos arg2, BlockState arg3, Set<BlockPos> set, BlockBox arg4) {
        arg.setBlockState(arg2, arg3, 19);
        set.add(arg2);
        arg4.encompass(new BlockBox(arg2, arg2));
    }
}

