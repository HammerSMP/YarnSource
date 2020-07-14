/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class VillagerResourceMetadata {
    public static final VillagerResourceMetadataReader READER = new VillagerResourceMetadataReader();
    private final HatType hatType;

    public VillagerResourceMetadata(HatType arg) {
        this.hatType = arg;
    }

    public HatType getHatType() {
        return this.hatType;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum HatType {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        private static final Map<String, HatType> byName;
        private final String name;

        private HatType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static HatType from(String name) {
            return byName.getOrDefault(name, NONE);
        }

        static {
            byName = Arrays.stream(HatType.values()).collect(Collectors.toMap(HatType::getName, arg -> arg));
        }
    }
}

