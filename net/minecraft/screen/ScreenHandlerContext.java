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
        public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
            return Optional.empty();
        }
    };

    public static ScreenHandlerContext create(final World world, final BlockPos pos) {
        return new ScreenHandlerContext(){

            @Override
            public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
                return Optional.of(function.apply(world, pos));
            }
        };
    }

    public <T> Optional<T> run(BiFunction<World, BlockPos, T> var1);

    default public <T> T run(BiFunction<World, BlockPos, T> function, T defaultValue) {
        return this.run(function).orElse(defaultValue);
    }

    default public void run(BiConsumer<World, BlockPos> function) {
        this.run((World arg, BlockPos arg2) -> {
            function.accept((World)arg, (BlockPos)arg2);
            return Optional.empty();
        });
    }
}

