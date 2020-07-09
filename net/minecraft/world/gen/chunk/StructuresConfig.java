/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructuresConfig {
    public static final Codec<StructuresConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StrongholdConfig.CODEC.optionalFieldOf("stronghold").forGetter(arg -> Optional.ofNullable(arg.stronghold)), (App)Codec.simpleMap(Registry.STRUCTURE_FEATURE, StructureConfig.CODEC, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter(arg -> arg.structures)).apply((Applicative)instance, StructuresConfig::new));
    public static final ImmutableMap<StructureFeature<?>, StructureConfig> DEFAULT_STRUCTURES = ImmutableMap.builder().put(StructureFeature.VILLAGE, (Object)new StructureConfig(32, 8, 10387312)).put(StructureFeature.DESERT_PYRAMID, (Object)new StructureConfig(32, 8, 14357617)).put(StructureFeature.IGLOO, (Object)new StructureConfig(32, 8, 14357618)).put(StructureFeature.JUNGLE_PYRAMID, (Object)new StructureConfig(32, 8, 14357619)).put((Object)StructureFeature.SWAMP_HUT, (Object)new StructureConfig(32, 8, 14357620)).put(StructureFeature.PILLAGER_OUTPOST, (Object)new StructureConfig(32, 8, 165745296)).put(StructureFeature.STRONGHOLD, (Object)new StructureConfig(1, 0, 0)).put(StructureFeature.MONUMENT, (Object)new StructureConfig(32, 5, 10387313)).put(StructureFeature.END_CITY, (Object)new StructureConfig(20, 11, 10387313)).put(StructureFeature.MANSION, (Object)new StructureConfig(80, 20, 10387319)).put(StructureFeature.BURIED_TREASURE, (Object)new StructureConfig(1, 0, 0)).put(StructureFeature.MINESHAFT, (Object)new StructureConfig(1, 0, 0)).put(StructureFeature.RUINED_PORTAL, (Object)new StructureConfig(40, 15, 34222645)).put(StructureFeature.SHIPWRECK, (Object)new StructureConfig(24, 4, 165745295)).put(StructureFeature.OCEAN_RUIN, (Object)new StructureConfig(20, 8, 14357621)).put(StructureFeature.BASTION_REMNANT, (Object)new StructureConfig(27, 4, 30084232)).put(StructureFeature.FORTRESS, (Object)new StructureConfig(27, 4, 30084232)).put(StructureFeature.NETHER_FOSSIL, (Object)new StructureConfig(2, 1, 14357921)).build();
    public static final StrongholdConfig DEFAULT_STRONGHOLD;
    private final Map<StructureFeature<?>, StructureConfig> structures;
    @Nullable
    private final StrongholdConfig stronghold;

    public StructuresConfig(Optional<StrongholdConfig> optional, Map<StructureFeature<?>, StructureConfig> map) {
        this.stronghold = optional.orElse(null);
        this.structures = map;
    }

    public StructuresConfig(boolean bl) {
        this.structures = Maps.newHashMap(DEFAULT_STRUCTURES);
        this.stronghold = bl ? DEFAULT_STRONGHOLD : null;
    }

    public Map<StructureFeature<?>, StructureConfig> getStructures() {
        return this.structures;
    }

    public StructureConfig getForType(StructureFeature<?> arg) {
        return this.structures.getOrDefault(arg, new StructureConfig(1, 0, 0));
    }

    @Nullable
    public StrongholdConfig getStronghold() {
        return this.stronghold;
    }

    static {
        for (StructureFeature structureFeature : Registry.STRUCTURE_FEATURE) {
            if (DEFAULT_STRUCTURES.containsKey((Object)structureFeature)) continue;
            throw new IllegalStateException("Structure feature without default settings: " + Registry.STRUCTURE_FEATURE.getId(structureFeature));
        }
        DEFAULT_STRONGHOLD = new StrongholdConfig(32, 3, 128);
    }
}

