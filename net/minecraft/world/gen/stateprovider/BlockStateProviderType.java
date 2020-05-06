/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class BlockStateProviderType<P extends BlockStateProvider> {
    public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = BlockStateProviderType.register("simple_state_provider", SimpleBlockStateProvider::new);
    public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = BlockStateProviderType.register("weighted_state_provider", WeightedBlockStateProvider::new);
    public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = BlockStateProviderType.register("plain_flower_provider", PlainsFlowerBlockStateProvider::new);
    public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = BlockStateProviderType.register("forest_flower_provider", ForestFlowerBlockStateProvider::new);
    private final Function<Dynamic<?>, P> configDeserializer;

    private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String string, Function<Dynamic<?>, P> function) {
        return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, string, new BlockStateProviderType<P>(function));
    }

    private BlockStateProviderType(Function<Dynamic<?>, P> function) {
        this.configDeserializer = function;
    }

    public P deserialize(Dynamic<?> dynamic) {
        return (P)((BlockStateProvider)this.configDeserializer.apply(dynamic));
    }
}

