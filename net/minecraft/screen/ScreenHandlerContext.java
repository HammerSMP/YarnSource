/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.screen;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ScreenHandlerContext {
    public static final ScreenHandlerContext EMPTY = new ScreenHandlerContext(){

        @Override
        public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
            return Optional.empty();
        }
    };

    public static ScreenHandlerContext create(final World arg, final BlockPos arg2) {
        return new ScreenHandlerContext(){

            @Override
            public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
                return Optional.of(biFunction.apply(arg, arg2));
            }
        };
    }

    public <T> Optional<T> run(BiFunction<World, BlockPos, T> var1);

    default public <T> T run(BiFunction<World, BlockPos, T> biFunction, T object) {
        return this.run(biFunction).orElse(object);
    }

    default public void run(BiConsumer<World, BlockPos> biConsumer) {
        this.run((World arg, BlockPos arg2) -> {
            biConsumer.accept((World)arg, (BlockPos)arg2);
            return Optional.empty();
        });
    }
}

