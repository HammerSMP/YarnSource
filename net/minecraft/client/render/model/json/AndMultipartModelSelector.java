/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;

@Environment(value=EnvType.CLIENT)
public class AndMultipartModelSelector
implements MultipartModelSelector {
    private final Iterable<? extends MultipartModelSelector> selectors;

    public AndMultipartModelSelector(Iterable<? extends MultipartModelSelector> iterable) {
        this.selectors = iterable;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> arg3) {
        List list = Streams.stream(this.selectors).map(arg2 -> arg2.getPredicate(arg3)).collect(Collectors.toList());
        return arg -> list.stream().allMatch(predicate -> predicate.test(arg));
    }
}

