/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import java.util.List;
import net.minecraft.class_5413;
import net.minecraft.class_5414;
import net.minecraft.class_5415;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class FluidTags {
    protected static final GlobalTagAccessor<Fluid> ACCESSOR = class_5413.method_30201(new Identifier("fluid"), class_5415::method_30220);
    public static final Tag.Identified<Fluid> WATER = FluidTags.register("water");
    public static final Tag.Identified<Fluid> LAVA = FluidTags.register("lava");

    private static Tag.Identified<Fluid> register(String string) {
        return ACCESSOR.get(string);
    }

    public static class_5414<Fluid> getContainer() {
        return ACCESSOR.getContainer();
    }

    public static List<? extends Tag<Fluid>> method_29897() {
        return ACCESSOR.method_29902();
    }
}

