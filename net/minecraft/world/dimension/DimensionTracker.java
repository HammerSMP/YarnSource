/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionType;

public interface DimensionTracker {
    @Environment(value=EnvType.CLIENT)
    public Registry<DimensionType> getRegistry();

    @Environment(value=EnvType.CLIENT)
    public static Modifiable create() {
        return DimensionType.addDefaults(new Modifiable());
    }

    public static final class Modifiable
    implements DimensionTracker {
        public static final Codec<Modifiable> CODEC = SimpleRegistry.method_29098(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.field_24756).xmap(Modifiable::new, arg -> arg.registry).fieldOf("dimension").codec();
        private final SimpleRegistry<DimensionType> registry;

        public Modifiable() {
            this(new SimpleRegistry<DimensionType>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
        }

        private Modifiable(SimpleRegistry<DimensionType> arg) {
            this.registry = arg;
        }

        public void add(RegistryKey<DimensionType> arg, DimensionType arg2) {
            this.registry.add(arg, arg2);
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Registry<DimensionType> getRegistry() {
            return this.registry;
        }
    }
}

