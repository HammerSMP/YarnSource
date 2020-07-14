/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;

public class TemplatePools {
    public static final StructurePool EMPTY = TemplatePools.register(new StructurePool(new Identifier("empty"), new Identifier("empty"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructurePool.Projection.RIGID));
    public static final StructurePool INVALID = TemplatePools.register(new StructurePool(new Identifier("invalid"), new Identifier("invalid"), (List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructurePool.Projection.RIGID));

    public static StructurePool register(StructurePool templatePool) {
        return BuiltinRegistries.add(BuiltinRegistries.TEMPLATE_POOL, templatePool.getId(), templatePool);
    }

    public static void method_30599() {
        BastionRemnantGenerator.init();
        PillagerOutpostGenerator.init();
        VillageGenerator.init();
    }

    static {
        TemplatePools.method_30599();
    }
}

