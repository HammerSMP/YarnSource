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

public class BastionBridgeData {
    public static void init() {
    }

    static {
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/starting_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance", class_5469.ENTRANCE_REPLACEMENT), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_face", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/bridge_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/bridge_pieces/bridge", class_5469.BRIDGE), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/legs"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_0", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_1", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/walls"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_0", class_5469.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_1", class_5469.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/ramparts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_0", class_5469.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_1", class_5469.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/rampart_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/rampart_plates/plate_0", class_5469.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        class_5468.method_30600(new StructurePool(new Identifier("bastion/bridge/connectors"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_top", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_bottom", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
    }
}

