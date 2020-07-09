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
import net.minecraft.class_5468;
import net.minecraft.class_5469;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

public class BastionUnitsData {
    public static void init() {
    }

    static {
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/center_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_1", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/center_pieces/center_2", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/pathways"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/pathways/pathway_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/pathways/pathway_wall_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/walls/wall_bases"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/walls/wall_base", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/walls/connected_wall", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/stages/stage_0"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_1", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_2", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_0_3", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/stages/stage_1"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_1", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_2", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_1_3", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/stages/rot/stage_1"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/rot/stage_1_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/stages/stage_2"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_2_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_2_1", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/stages/stage_3"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_1", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_2", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/stages/stage_3_3", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/fillers/stage_0"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/fillers/stage_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/edges"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/edges/edge_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/wall_units"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/wall_units/unit_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/edge_wall_units"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/wall_units/edge_0_large", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/ramparts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_0", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_1", class_5469.HOUSING), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_2", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/large_ramparts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/ramparts/ramparts_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/units/rampart_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/rampart_plates/plate_0", class_5469.HOUSING), (Object)1)), StructurePool.Projection.RIGID));
    }
}

