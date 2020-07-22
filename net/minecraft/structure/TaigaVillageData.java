/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.TemplatePools;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class TaigaVillageData {
    public static final StructurePool field_26341 = TemplatePools.register(new StructurePool(new Identifier("village/taiga/town_centers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/taiga/town_centers/taiga_meeting_point_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)49), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/town_centers/taiga_meeting_point_2", ProcessorLists.MOSSIFY_10_PERCENT), (Object)49), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/town_centers/taiga_meeting_point_1", ProcessorLists.ZOMBIE_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/town_centers/taiga_meeting_point_2", ProcessorLists.ZOMBIE_TAIGA), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
    }

    static {
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/streets"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/corner_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/corner_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/corner_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_04", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_05", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/straight_06", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_04", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_05", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/streets/crossroad_06", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/streets/turn_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/zombie/streets"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/corner_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/corner_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/corner_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_04", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_05", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)7), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/straight_06", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_04", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_05", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/crossroad_06", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/streets/turn_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)3)}), StructurePool.Projection.TERRAIN_MATCHING));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/houses"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_house_2", ProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_house_3", ProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_house_4", ProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_house_5", ProcessorLists.MOSSIFY_10_PERCENT), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_medium_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_medium_house_2", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_medium_house_3", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_medium_house_4", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_butcher_shop_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_tool_smith_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_fletcher_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_shepherds_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_armorer_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_armorer_2", ProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_fisher_cottage_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)3), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_tannery_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_cartographer_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_library_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_masons_house_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_weaponsmith_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_weaponsmith_2", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_large_farm_1", ProcessorLists.FARM_TAIGA), (Object)6), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_large_farm_2", ProcessorLists.FARM_TAIGA), (Object)6), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_farm_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)1), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_animal_pen_1", ProcessorLists.MOSSIFY_10_PERCENT), (Object)2), Pair.of(StructurePoolElement.method_30438(), (Object)6)}), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/zombie/houses"), new Identifier("village/taiga/terminators"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_small_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_small_house_2", ProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_small_house_3", ProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_small_house_4", ProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_small_house_5", ProcessorLists.ZOMBIE_TAIGA), (Object)4), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_medium_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_medium_house_2", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_medium_house_3", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_medium_house_4", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_butcher_shop_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_tool_smith_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object)Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_fletcher_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_shepherds_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_armorer_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)1), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_fisher_cottage_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_tannery_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_cartographer_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_library_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_masons_house_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_weaponsmith_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_weaponsmith_2", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_large_farm_1", ProcessorLists.ZOMBIE_TAIGA), (Object)6), Pair.of(StructurePoolElement.method_30426("village/taiga/zombie/houses/taiga_large_farm_2", ProcessorLists.ZOMBIE_TAIGA), (Object)6), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_small_farm_1", ProcessorLists.ZOMBIE_TAIGA), (Object)1), Pair.of(StructurePoolElement.method_30426("village/taiga/houses/taiga_animal_pen_1", ProcessorLists.ZOMBIE_TAIGA), (Object)2), Pair.of(StructurePoolElement.method_30438(), (Object)6)}), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/terminators"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_01", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_02", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_03", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1), (Object)Pair.of(StructurePoolElement.method_30426("village/plains/terminators/terminator_04", ProcessorLists.STREET_SNOWY_OR_TAIGA), (Object)1)), StructurePool.Projection.TERRAIN_MATCHING));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_lamp_post_1"), (Object)10), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_1"), (Object)4), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_3"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_4"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_5"), (Object)2), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_6"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.SPRUCE), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PINE), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_PUMPKIN), (Object)2), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PATCH_TAIGA_GRASS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PATCH_BERRY_BUSH), (Object)1), (Object[])new Pair[]{Pair.of(StructurePoolElement.method_30438(), (Object)4)}), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/zombie/decor"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_1"), (Object)4), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_2"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_3"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/taiga_decoration_4"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.SPRUCE), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PINE), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_PUMPKIN), (Object)2), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PATCH_TAIGA_GRASS), (Object)4), (Object)Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PATCH_BERRY_BUSH), (Object)1), (Object)Pair.of(StructurePoolElement.method_30438(), (Object)4)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/taiga/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/villagers/baby"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("village/taiga/zombie/villagers"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30425("village/taiga/zombie/villagers/nitwit"), (Object)1), (Object)Pair.of(StructurePoolElement.method_30425("village/taiga/zombie/villagers/unemployed"), (Object)10)), StructurePool.Projection.RIGID));
    }
}

