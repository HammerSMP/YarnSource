/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.fluid;

import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.registry.Registry;

public class Fluids {
    public static final Fluid EMPTY = Fluids.register("empty", new EmptyFluid());
    public static final FlowableFluid FLOWING_WATER = Fluids.register("flowing_water", new WaterFluid.Flowing());
    public static final FlowableFluid WATER = Fluids.register("water", new WaterFluid.Still());
    public static final FlowableFluid FLOWING_LAVA = Fluids.register("flowing_lava", new LavaFluid.Flowing());
    public static final FlowableFluid LAVA = Fluids.register("lava", new LavaFluid.Still());

    private static <T extends Fluid> T register(String string, T arg) {
        return (T)Registry.register(Registry.FLUID, string, arg);
    }

    static {
        for (Fluid lv : Registry.FLUID) {
            for (FluidState lv2 : lv.getStateManager().getStates()) {
                Fluid.STATE_IDS.add(lv2);
            }
        }
    }
}

