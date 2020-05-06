/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

public interface BlockRenderView
extends BlockView {
    @Environment(value=EnvType.CLIENT)
    public float getBrightness(Direction var1, boolean var2);

    public LightingProvider getLightingProvider();

    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockPos var1, ColorResolver var2);

    default public int getLightLevel(LightType arg, BlockPos arg2) {
        return this.getLightingProvider().get(arg).getLightLevel(arg2);
    }

    default public int getBaseLightLevel(BlockPos arg, int i) {
        return this.getLightingProvider().getLight(arg, i);
    }

    default public boolean isSkyVisible(BlockPos arg) {
        return this.getLightLevel(LightType.SKY, arg) >= this.getMaxLightLevel();
    }
}

