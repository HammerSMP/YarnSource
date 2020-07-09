/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_5458;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

public class class_5468 {
    public static final StructurePool field_26254 = class_5468.method_30600(new StructurePool(new Identifier("empty"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructurePool.Projection.RIGID));
    public static final StructurePool field_26255 = class_5468.method_30600(new StructurePool(new Identifier("invalid"), new Identifier("invalid"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructurePool.Projection.RIGID));

    public static StructurePool method_30600(StructurePool arg) {
        return class_5458.method_30562(class_5458.field_25932, arg.getId(), arg);
    }

    public static void method_30599() {
        BastionRemnantGenerator.init();
        PillagerOutpostGenerator.init();
        VillageGenerator.init();
    }

    static {
        class_5468.method_30599();
    }
}

