/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;

public class class_5463 {
    public static final ConfiguredCarver<ProbabilityConfig> CAVE = class_5463.method_30588("cave", Carver.CAVE.method_28614(new ProbabilityConfig(0.14285715f)));
    public static final ConfiguredCarver<ProbabilityConfig> CANYON = class_5463.method_30588("canyon", Carver.CANYON.method_28614(new ProbabilityConfig(0.02f)));
    public static final ConfiguredCarver<ProbabilityConfig> OCEAN_CAVE = class_5463.method_30588("ocean_cave", Carver.CAVE.method_28614(new ProbabilityConfig(0.06666667f)));
    public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CANYON = class_5463.method_30588("underwater_canyon", Carver.UNDERWATER_CANYON.method_28614(new ProbabilityConfig(0.02f)));
    public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CAVE = class_5463.method_30588("underwater_cave", Carver.UNDERWATER_CAVE.method_28614(new ProbabilityConfig(0.06666667f)));
    public static final ConfiguredCarver<ProbabilityConfig> NETHER_CAVE = class_5463.method_30588("nether_cave", Carver.NETHER_CAVE.method_28614(new ProbabilityConfig(0.2f)));

    private static <WC extends CarverConfig> ConfiguredCarver<WC> method_30588(String string, ConfiguredCarver<WC> arg) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, string, arg);
    }
}

