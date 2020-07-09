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
import net.minecraft.structure.BastionBridgeData;
import net.minecraft.structure.BastionData;
import net.minecraft.structure.BastionTreasureData;
import net.minecraft.structure.BastionUnitsData;
import net.minecraft.structure.HoglinStableData;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

public class BastionRemnantGenerator {
    public static final StructurePool field_25941 = class_5468.method_30600(new StructurePool(new Identifier("bastion/starts"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.method_30435("bastion/units/air_base", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/hoglin_stable/air_base", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/treasure/big_air_full", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.method_30435("bastion/bridge/starting_pieces/entrance_base", class_5469.BASTION_GENERIC_DEGRADATION), (Object)1)), StructurePool.Projection.RIGID));

    public static void init() {
        BastionUnitsData.init();
        HoglinStableData.init();
        BastionTreasureData.init();
        BastionBridgeData.init();
        BastionData.init();
    }
}

