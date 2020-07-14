/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BlockPredicate
implements Predicate<BlockState> {
    private final Block block;

    public BlockPredicate(Block block) {
        this.block = block;
    }

    public static BlockPredicate make(Block block) {
        return new BlockPredicate(block);
    }

    @Override
    public boolean test(@Nullable BlockState arg) {
        return arg != null && arg.isOf(this.block);
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object context) {
        return this.test((BlockState)context);
    }
}

