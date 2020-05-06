/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TrunkPlacerType<P extends TrunkPlacer> {
    public static final TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = TrunkPlacerType.register("straight_trunk_placer", StraightTrunkPlacer::new);
    public static final TrunkPlacerType<ForkingTrunkPlacer> FORKING_TRUNK_PLACER = TrunkPlacerType.register("forking_trunk_placer", ForkingTrunkPlacer::new);
    public static final TrunkPlacerType<GiantTrunkPlacer> GIANT_TRUNK_PLACER = TrunkPlacerType.register("giant_trunk_placer", GiantTrunkPlacer::new);
    public static final TrunkPlacerType<MegaJungleTrunkPlacer> MEGA_JUNGLE_TRUNK_PLACER = TrunkPlacerType.register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer::new);
    public static final TrunkPlacerType<DarkOakTrunkPlacer> DARK_OAK_TRUNK_PLACER = TrunkPlacerType.register("dark_oak_trunk_placer", DarkOakTrunkPlacer::new);
    public static final TrunkPlacerType<LargeOakTrunkPlacer> FANCY_TRUNK_PLACER = TrunkPlacerType.register("fancy_trunk_placer", LargeOakTrunkPlacer::new);
    private final Function<Dynamic<?>, P> deserializer;

    private static <P extends TrunkPlacer> TrunkPlacerType<P> register(String string, Function<Dynamic<?>, P> function) {
        return Registry.register(Registry.TRUNK_PLACER_TYPE, string, new TrunkPlacerType<P>(function));
    }

    private TrunkPlacerType(Function<Dynamic<?>, P> function) {
        this.deserializer = function;
    }

    public P deserialize(Dynamic<?> dynamic) {
        return (P)((TrunkPlacer)this.deserializer.apply(dynamic));
    }
}

