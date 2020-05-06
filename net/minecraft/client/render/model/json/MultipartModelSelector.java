/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface MultipartModelSelector {
    public static final MultipartModelSelector TRUE = arg2 -> arg -> true;
    public static final MultipartModelSelector FALSE = arg2 -> arg -> false;

    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> var1);
}

