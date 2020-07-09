/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringIdentifiable;

public class GenerationStep {

    public static enum Carver implements StringIdentifiable
    {
        AIR("air"),
        LIQUID("liquid");

        public static final Codec<Carver> field_24770;
        private static final Map<String, Carver> BY_NAME;
        private final String name;

        private Carver(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Carver method_28546(String string) {
            return BY_NAME.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24770 = StringIdentifiable.createCodec(Carver::values, Carver::method_28546);
            BY_NAME = Arrays.stream(Carver.values()).collect(Collectors.toMap(Carver::getName, arg -> arg));
        }
    }

    public static enum Feature {
        RAW_GENERATION,
        LAKES,
        LOCAL_MODIFICATIONS,
        UNDERGROUND_STRUCTURES,
        SURFACE_STRUCTURES,
        STRONGHOLDS,
        UNDERGROUND_ORES,
        UNDERGROUND_DECORATION,
        VEGETAL_DECORATION,
        TOP_LAYER_MODIFICATION;

    }
}

