/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;

@Environment(value=EnvType.CLIENT)
public class BiomeColors {
    public static final ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
    public static final ColorResolver FOLIAGE_COLOR = (arg, d, e) -> arg.getFoliageColor();
    public static final ColorResolver WATER_COLOR = (arg, d, e) -> arg.getWaterColor();

    private static int getColor(BlockRenderView arg, BlockPos arg2, ColorResolver colorResolver) {
        return arg.getColor(arg2, colorResolver);
    }

    public static int getGrassColor(BlockRenderView arg, BlockPos arg2) {
        return BiomeColors.getColor(arg, arg2, GRASS_COLOR);
    }

    public static int getFoliageColor(BlockRenderView arg, BlockPos arg2) {
        return BiomeColors.getColor(arg, arg2, FOLIAGE_COLOR);
    }

    public static int getWaterColor(BlockRenderView arg, BlockPos arg2) {
        return BiomeColors.getColor(arg, arg2, WATER_COLOR);
    }
}

