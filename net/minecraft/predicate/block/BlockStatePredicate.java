/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class BlockStatePredicate
implements Predicate<BlockState> {
    public static final Predicate<BlockState> ANY = arg -> true;
    private final StateManager<Block, BlockState> manager;
    private final Map<Property<?>, Predicate<Object>> propertyTests = Maps.newHashMap();

    private BlockStatePredicate(StateManager<Block, BlockState> arg) {
        this.manager = arg;
    }

    public static BlockStatePredicate forBlock(Block arg) {
        return new BlockStatePredicate(arg.getStateManager());
    }

    @Override
    public boolean test(@Nullable BlockState arg) {
        if (arg == null || !arg.getBlock().equals(this.manager.getOwner())) {
            return false;
        }
        if (this.propertyTests.isEmpty()) {
            return true;
        }
        for (Map.Entry<Property<?>, Predicate<Object>> entry : this.propertyTests.entrySet()) {
            if (this.testProperty(arg, entry.getKey(), entry.getValue())) continue;
            return false;
        }
        return true;
    }

    protected <T extends Comparable<T>> boolean testProperty(BlockState arg, Property<T> arg2, Predicate<Object> predicate) {
        T comparable = arg.get(arg2);
        return predicate.test(comparable);
    }

    public <V extends Comparable<V>> BlockStatePredicate with(Property<V> arg, Predicate<Object> predicate) {
        if (!this.manager.getProperties().contains(arg)) {
            throw new IllegalArgumentException(this.manager + " cannot support property " + arg);
        }
        this.propertyTests.put(arg, predicate);
        return this;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((BlockState)object);
    }
}

