/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionType;

public interface RegistryTracker {
    public <E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> var1);

    @Environment(value=EnvType.CLIENT)
    public Registry<DimensionType> getDimensionTypeRegistry();

    public static Modifiable create() {
        return DimensionType.addRegistryDefaults(new Modifiable());
    }

    public static final class Modifiable
    implements RegistryTracker {
        public static final Codec<Modifiable> CODEC = SimpleRegistry.method_29098(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental(), DimensionType.CODEC).xmap(Modifiable::new, arg -> arg.registry).fieldOf("dimension").codec();
        private final SimpleRegistry<DimensionType> registry;

        public Modifiable() {
            this(new SimpleRegistry<DimensionType>(Registry.DIMENSION_TYPE_KEY, Lifecycle.experimental()));
        }

        private Modifiable(SimpleRegistry<DimensionType> arg) {
            this.registry = arg;
        }

        public void addDimensionType(RegistryKey<DimensionType> arg, DimensionType arg2) {
            this.registry.add(arg, arg2);
        }

        @Override
        public <E> Optional<MutableRegistry<E>> get(RegistryKey<Registry<E>> arg) {
            if (Objects.equals(arg, Registry.DIMENSION_TYPE_KEY)) {
                return Optional.of(this.registry);
            }
            return Optional.empty();
        }

        @Override
        public Registry<DimensionType> getDimensionTypeRegistry() {
            return this.registry;
        }
    }
}
