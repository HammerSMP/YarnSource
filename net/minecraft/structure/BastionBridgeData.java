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

public class BastionBridgeData {
    public static void init() {
    }

    static {
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/starting_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance", ProcessorLists.ENTRANCE_REPLACEMENT), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_face", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/bridge_pieces"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/bridge_pieces/bridge", ProcessorLists.BRIDGE), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/legs"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_0", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/legs/leg_1", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/walls"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/walls/wall_base_1", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/ramparts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/ramparts/rampart_1", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/rampart_plates"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/rampart_plates/plate_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
        TemplatePools.register(new StructurePool(new Identifier("bastion/bridge/connectors"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_top", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/connectors/back_bridge_bottom", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));
    }
}

