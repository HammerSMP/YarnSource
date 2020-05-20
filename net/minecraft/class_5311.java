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
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.class_5313;
import net.minecraft.class_5314;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public class class_5311 {
    public static final Codec<class_5311> field_24821 = RecordCodecBuilder.create(instance -> instance.group((App)class_5313.field_24913.optionalFieldOf("stronghold").forGetter(arg -> Optional.ofNullable(arg.field_24825)), (App)Codec.simpleMap(Registry.STRUCTURE_FEATURE, class_5314.field_24917, Registry.STRUCTURE_FEATURE).fieldOf("structures").forGetter(arg -> arg.field_24824)).apply((Applicative)instance, class_5311::new));
    public static final ImmutableMap<StructureFeature<?>, class_5314> field_24822 = ImmutableMap.builder().put(StructureFeature.VILLAGE, (Object)new class_5314(32, 8, 10387312)).put(StructureFeature.DESERT_PYRAMID, (Object)new class_5314(32, 8, 14357617)).put(StructureFeature.IGLOO, (Object)new class_5314(32, 8, 14357618)).put(StructureFeature.JUNGLE_PYRAMID, (Object)new class_5314(32, 8, 14357619)).put((Object)StructureFeature.field_24851, (Object)new class_5314(32, 8, 14357620)).put(StructureFeature.PILLAGER_OUTPOST, (Object)new class_5314(32, 8, 165745296)).put(StructureFeature.STRONGHOLD, (Object)new class_5314(1, 0, 0)).put(StructureFeature.MONUMENT, (Object)new class_5314(32, 5, 10387313)).put(StructureFeature.END_CITY, (Object)new class_5314(20, 11, 10387313)).put(StructureFeature.MANSION, (Object)new class_5314(80, 20, 10387319)).put(StructureFeature.RUINED_PORTAL, (Object)new class_5314(40, 15, 34222645)).put(StructureFeature.SHIPWRECK, (Object)new class_5314(24, 4, 165745295)).put(StructureFeature.OCEAN_RUIN, (Object)new class_5314(20, 8, 14357621)).put(StructureFeature.BASTION_REMNANT, (Object)new class_5314(30, 4, 30084232)).put(StructureFeature.FORTRESS, (Object)new class_5314(30, 4, 30084232)).put(StructureFeature.NETHER_FOSSIL, (Object)new class_5314(2, 1, 14357921)).build();
    public static final class_5313 field_24823 = new class_5313(32, 3, 128);
    private final Map<StructureFeature<?>, class_5314> field_24824;
    @Nullable
    private final class_5313 field_24825;

    public class_5311(Optional<class_5313> optional, Map<StructureFeature<?>, class_5314> map) {
        this.field_24825 = optional.orElse(null);
        this.field_24824 = map;
    }

    public class_5311(boolean bl) {
        this.field_24824 = Maps.newHashMap(field_24822);
        this.field_24825 = bl ? field_24823 : null;
    }

    public Map<StructureFeature<?>, class_5314> method_28598() {
        return this.field_24824;
    }

    public class_5314 method_28600(StructureFeature<?> arg) {
        return this.field_24824.getOrDefault(arg, new class_5314(1, 0, 0));
    }

    @Nullable
    public class_5313 method_28602() {
        return this.field_24825;
    }
}

