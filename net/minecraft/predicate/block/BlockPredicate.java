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

    public BlockPredicate(Block arg) {
        this.block = arg;
    }

    public static BlockPredicate make(Block arg) {
        return new BlockPredicate(arg);
    }

    @Override
    public boolean test(@Nullable BlockState arg) {
        return arg != null && arg.isOf(this.block);
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((BlockState)object);
    }
}

