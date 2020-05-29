/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.tag;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class FluidTags {
    private static final GlobalTagAccessor<Fluid> ACCESSOR = new GlobalTagAccessor();
    public static final Tag.Identified<Fluid> WATER = FluidTags.register("water");
    public static final Tag.Identified<Fluid> LAVA = FluidTags.register("lava");

    private static Tag.Identified<Fluid> register(String string) {
        return ACCESSOR.get(string);
    }

    public static void setContainer(TagContainer<Fluid> arg) {
        ACCESSOR.setContainer(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public static void markReady() {
        ACCESSOR.markReady();
    }

    public static TagContainer<Fluid> getContainer() {
        return ACCESSOR.getContainer();
    }

    public static Set<Identifier> method_29216(TagContainer<Fluid> arg) {
        return ACCESSOR.method_29224(arg);
    }
}

