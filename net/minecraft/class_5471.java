/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.block.Blocks;
import net.minecraft.class_5458;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class class_5471 {
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BADLANDS = class_5471.method_30610("badlands", SurfaceBuilder.BADLANDS.method_30478(SurfaceBuilder.BADLANDS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BASALT_DELTAS = class_5471.method_30610("basalt_deltas", SurfaceBuilder.BASALT_DELTAS.method_30478(SurfaceBuilder.BASALT_DELTA_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> CRIMSON_FOREST = class_5471.method_30610("crimson_forest", SurfaceBuilder.NETHER_FOREST.method_30478(SurfaceBuilder.CRIMSON_NYLIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> DESERT = class_5471.method_30610("desert", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> END = class_5471.method_30610("end", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.END_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS = class_5471.method_30610("eroded_badlands", SurfaceBuilder.ERODED_BADLANDS.method_30478(SurfaceBuilder.BADLANDS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN = class_5471.method_30610("frozen_ocean", SurfaceBuilder.FROZEN_OCEAN.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FULL_SAND = class_5471.method_30610("full_sand", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA = class_5471.method_30610("giant_tree_taiga", SurfaceBuilder.GIANT_TREE_TAIGA.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRASS = class_5471.method_30610("grass", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN = class_5471.method_30610("gravelly_mountain", SurfaceBuilder.GRAVELLY_MOUNTAIN.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ICE_SPIKES = class_5471.method_30610("ice_spikes", SurfaceBuilder.DEFAULT.method_30478(new TernarySurfaceConfig(Blocks.SNOW_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOUNTAIN = class_5471.method_30610("mountain", SurfaceBuilder.MOUNTAIN.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MYCELIUM = class_5471.method_30610("mycelium", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.MYCELIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NETHER = class_5471.method_30610("nether", SurfaceBuilder.NETHER.method_30478(SurfaceBuilder.NETHER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NOPE = class_5471.method_30610("nope", SurfaceBuilder.NOPE.method_30478(SurfaceBuilder.STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> OCEAN_SAND = class_5471.method_30610("ocean_sand", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA = class_5471.method_30610("shattered_savanna", SurfaceBuilder.SHATTERED_SAVANNA.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SOUL_SAND_VALLEY = class_5471.method_30610("soul_sand_valley", SurfaceBuilder.SOUL_SAND_VALLEY.method_30478(SurfaceBuilder.SOUL_SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> STONE = class_5471.method_30610("stone", SurfaceBuilder.DEFAULT.method_30478(SurfaceBuilder.STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SWAMP = class_5471.method_30610("swamp", SurfaceBuilder.SWAMP.method_30478(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WARPED_FOREST = class_5471.method_30610("warped_forest", SurfaceBuilder.NETHER_FOREST.method_30478(SurfaceBuilder.WARPED_NYLIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS = class_5471.method_30610("wooded_badlands", SurfaceBuilder.WOODED_BADLANDS.method_30478(SurfaceBuilder.BADLANDS_CONFIG));

    private static <SC extends SurfaceConfig> ConfiguredSurfaceBuilder<SC> method_30610(String string, ConfiguredSurfaceBuilder<SC> arg) {
        return class_5458.method_30561(class_5458.field_25927, string, arg);
    }
}

