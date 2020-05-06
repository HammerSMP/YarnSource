/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class DoubleBlockProperties {
    public static <S extends BlockEntity> PropertySource<S> toPropertySource(BlockEntityType<S> arg, Function<BlockState, Type> function, Function<BlockState, Direction> function2, DirectionProperty arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BiPredicate<IWorld, BlockPos> biPredicate) {
        Type lv5;
        boolean bl2;
        S lv = arg.get(arg4, arg5);
        if (lv == null) {
            return PropertyRetriever::getFallback;
        }
        if (biPredicate.test(arg4, arg5)) {
            return PropertyRetriever::getFallback;
        }
        Type lv2 = function.apply(arg3);
        boolean bl = lv2 == Type.SINGLE;
        boolean bl3 = bl2 = lv2 == Type.FIRST;
        if (bl) {
            return new PropertySource.Single<S>(lv);
        }
        BlockPos lv3 = arg5.offset(function2.apply(arg3));
        BlockState lv4 = arg4.getBlockState(lv3);
        if (lv4.isOf(arg3.getBlock()) && (lv5 = function.apply(lv4)) != Type.SINGLE && lv2 != lv5 && lv4.get(arg2) == arg3.get(arg2)) {
            if (biPredicate.test(arg4, lv3)) {
                return PropertyRetriever::getFallback;
            }
            S lv6 = arg.get(arg4, lv3);
            if (lv6 != null) {
                S lv7 = bl2 ? lv : lv6;
                S lv8 = bl2 ? lv6 : lv;
                return new PropertySource.Pair<S>(lv7, lv8);
            }
        }
        return new PropertySource.Single<S>(lv);
    }

    public static interface PropertySource<S> {
        public <T> T apply(PropertyRetriever<? super S, T> var1);

        public static final class Single<S>
        implements PropertySource<S> {
            private final S single;

            public Single(S object) {
                this.single = object;
            }

            @Override
            public <T> T apply(PropertyRetriever<? super S, T> arg) {
                return arg.getFrom(this.single);
            }
        }

        public static final class Pair<S>
        implements PropertySource<S> {
            private final S first;
            private final S second;

            public Pair(S object, S object2) {
                this.first = object;
                this.second = object2;
            }

            @Override
            public <T> T apply(PropertyRetriever<? super S, T> arg) {
                return arg.getFromBoth(this.first, this.second);
            }
        }
    }

    public static interface PropertyRetriever<S, T> {
        public T getFromBoth(S var1, S var2);

        public T getFrom(S var1);

        public T getFallback();
    }

    public static enum Type {
        SINGLE,
        FIRST,
        SECOND;

    }
}

