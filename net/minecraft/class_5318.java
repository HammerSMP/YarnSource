/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5321;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionType;

public interface class_5318 {
    public Registry<DimensionType> method_29116();

    @Environment(value=EnvType.CLIENT)
    public static class_5319 method_29117() {
        return DimensionType.method_28523(new class_5319());
    }

    public static final class class_5319
    implements class_5318 {
        public static final Codec<class_5319> field_25119 = SimpleRegistry.method_29098(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.field_24756).xmap(class_5319::new, arg -> arg.field_25120).fieldOf("dimension").codec();
        private final SimpleRegistry<DimensionType> field_25120;

        public class_5319() {
            this(new SimpleRegistry<DimensionType>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
        }

        private class_5319(SimpleRegistry<DimensionType> arg) {
            this.field_25120 = arg;
        }

        public void method_29119(class_5321<DimensionType> arg, DimensionType arg2) {
            this.field_25120.add(arg, arg2);
        }

        @Override
        public Registry<DimensionType> method_29116() {
            return this.field_25120;
        }
    }
}

