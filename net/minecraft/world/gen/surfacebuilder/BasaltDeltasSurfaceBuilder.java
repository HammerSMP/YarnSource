/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.AbstractNetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class BasaltDeltasSurfaceBuilder
extends AbstractNetherSurfaceBuilder {
    private static final BlockState BASALT = Blocks.BASALT.getDefaultState();
    private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.getDefaultState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final ImmutableList<BlockState> field_23918 = ImmutableList.of((Object)BASALT, (Object)BLACKSTONE);
    private static final ImmutableList<BlockState> field_23919 = ImmutableList.of((Object)BASALT);

    public BasaltDeltasSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }

    @Override
    protected ImmutableList<BlockState> method_27129() {
        return field_23918;
    }

    @Override
    protected ImmutableList<BlockState> method_27133() {
        return field_23919;
    }

    @Override
    protected BlockState method_27135() {
        return GRAVEL;
    }
}

