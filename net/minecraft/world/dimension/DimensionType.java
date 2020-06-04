/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class DimensionType {
    public static final Codec<RegistryKey<DimensionType>> field_24751 = Identifier.field_25139.xmap(RegistryKey.createKeyFactory(Registry.DIMENSION_TYPE_KEY), RegistryKey::getValue);
    private static final Codec<DimensionType> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.LONG.optionalFieldOf("fixed_time").xmap(optional -> optional.map(OptionalLong::of).orElseGet(OptionalLong::empty), optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty()).forGetter(arg -> arg.fixedTime), (App)Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), (App)Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), (App)Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::isUltrawarm), (App)Codec.BOOL.fieldOf("natural").forGetter(DimensionType::isNatural), (App)Codec.BOOL.fieldOf("shrunk").forGetter(DimensionType::isShrunk), (App)Codec.FLOAT.fieldOf("ambient_light").forGetter(arg -> Float.valueOf(arg.ambientLight))).apply((Applicative)instance, DimensionType::new));
    public static final float[] field_24752 = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
    public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
    public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
    private static final LinkedHashSet<RegistryKey<World>> DIMENSION_TYPES = Sets.newLinkedHashSet((Iterable)ImmutableList.of(World.OVERWORLD, World.NETHER, World.END));
    private static final Map<RegistryKey<DimensionType>, DimensionType> field_24759 = ImmutableMap.of(OVERWORLD_REGISTRY_KEY, (Object)DimensionType.method_29294(), THE_NETHER_REGISTRY_KEY, (Object)DimensionType.method_29295(), THE_END_REGISTRY_KEY, (Object)DimensionType.method_29296());
    private static final Codec<DimensionType> field_24760 = field_24751.flatXmap(arg -> Optional.ofNullable(field_24759.get(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown builtin dimension: " + arg))), arg -> arg.field_24765.map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown builtin dimension: " + arg)))).stable();
    public static final Codec<DimensionType> field_24756 = Codec.either(field_24760, CODEC).flatXmap(either -> (DataResult)either.map(arg -> DataResult.success((Object)arg, (Lifecycle)Lifecycle.stable()), DataResult::success), arg -> arg.field_24765.isPresent() ? DataResult.success((Object)Either.left((Object)arg), (Lifecycle)Lifecycle.stable()) : DataResult.success((Object)Either.right((Object)arg)));
    private final String suffix;
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultrawarm;
    private final boolean natural;
    private final boolean shrunk;
    private final boolean hasEnderDragonFight;
    private final BiomeAccessType biomeAccessType;
    private final Optional<RegistryKey<DimensionType>> field_24765;
    private final float ambientLight;
    private final transient float[] field_24767;

    public static DimensionType method_29294() {
        return new DimensionType("", OptionalLong.empty(), true, false, false, true, false, false, HorizontalVoronoiBiomeAccessType.INSTANCE, Optional.of(OVERWORLD_REGISTRY_KEY), 0.0f);
    }

    private static DimensionType method_29295() {
        return new DimensionType("_nether", OptionalLong.of(18000L), false, true, true, false, true, false, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_NETHER_REGISTRY_KEY), 0.1f);
    }

    private static DimensionType method_29296() {
        return new DimensionType("_end", OptionalLong.of(6000L), false, false, false, false, false, true, VoronoiBiomeAccessType.INSTANCE, Optional.of(THE_END_REGISTRY_KEY), 0.0f);
    }

    protected DimensionType(OptionalLong optionalLong, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, float f) {
        this("", optionalLong, bl, bl2, bl3, bl4, bl5, false, VoronoiBiomeAccessType.INSTANCE, Optional.empty(), f);
    }

    protected DimensionType(String string, OptionalLong optionalLong, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, BiomeAccessType arg, Optional<RegistryKey<DimensionType>> optional, float f) {
        this.suffix = string;
        this.fixedTime = optionalLong;
        this.hasSkyLight = bl;
        this.hasCeiling = bl2;
        this.ultrawarm = bl3;
        this.natural = bl4;
        this.shrunk = bl5;
        this.hasEnderDragonFight = bl6;
        this.biomeAccessType = arg;
        this.field_24765 = optional;
        this.ambientLight = f;
        this.field_24767 = DimensionType.method_28515(f);
    }

    private static float[] method_28515(float f) {
        float[] fs = new float[16];
        for (int i = 0; i <= 15; ++i) {
            float g = (float)i / 15.0f;
            float h = g / (4.0f - 3.0f * g);
            fs[i] = MathHelper.lerp(f, h, 1.0f);
        }
        return fs;
    }

    @Deprecated
    public static DataResult<RegistryKey<World>> method_28521(Dynamic<?> dynamic) {
        DataResult dataResult = dynamic.asNumber();
        if (dataResult.result().equals(Optional.of(-1))) {
            return DataResult.success(World.NETHER);
        }
        if (dataResult.result().equals(Optional.of(0))) {
            return DataResult.success(World.OVERWORLD);
        }
        if (dataResult.result().equals(Optional.of(1))) {
            return DataResult.success(World.END);
        }
        return World.CODEC.parse(dynamic);
    }

    @Environment(value=EnvType.CLIENT)
    public static DimensionTracker.Modifiable addDefaults(DimensionTracker.Modifiable arg) {
        arg.add(OVERWORLD_REGISTRY_KEY, DimensionType.method_29294());
        arg.add(THE_NETHER_REGISTRY_KEY, DimensionType.method_29295());
        arg.add(THE_END_REGISTRY_KEY, DimensionType.method_29296());
        return arg;
    }

    private static ChunkGenerator method_28533(long l) {
        return new SurfaceChunkGenerator(new TheEndBiomeSource(l), l, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
    }

    private static ChunkGenerator method_28535(long l) {
        return new SurfaceChunkGenerator(MultiNoiseBiomeSource.class_5305.field_24723.method_28469(l), l, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
    }

    public static LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> method_28517(long l) {
        LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
        linkedHashMap.put(World.NETHER, Pair.of((Object)DimensionType.method_29295(), (Object)DimensionType.method_28535(l)));
        linkedHashMap.put(World.END, Pair.of((Object)DimensionType.method_29296(), (Object)DimensionType.method_28533(l)));
        return linkedHashMap;
    }

    public static boolean method_28518(long l, LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> linkedHashMap) {
        ArrayList list = Lists.newArrayList(linkedHashMap.entrySet());
        if (list.size() != 3) {
            return false;
        }
        Map.Entry entry = (Map.Entry)list.get(0);
        Map.Entry entry2 = (Map.Entry)list.get(1);
        Map.Entry entry3 = (Map.Entry)list.get(2);
        if (entry.getKey() != World.OVERWORLD || entry2.getKey() != World.NETHER || entry3.getKey() != World.END) {
            return false;
        }
        if (!(((DimensionType)((Pair)entry.getValue()).getFirst()).isOverworld() && ((DimensionType)((Pair)entry2.getValue()).getFirst()).isNether() && ((DimensionType)((Pair)entry3.getValue()).getFirst()).isEnd())) {
            return false;
        }
        if (!(((Pair)entry2.getValue()).getSecond() instanceof SurfaceChunkGenerator) || !(((Pair)entry3.getValue()).getSecond() instanceof SurfaceChunkGenerator)) {
            return false;
        }
        SurfaceChunkGenerator lv = (SurfaceChunkGenerator)((Pair)entry2.getValue()).getSecond();
        SurfaceChunkGenerator lv2 = (SurfaceChunkGenerator)((Pair)entry3.getValue()).getSecond();
        if (!lv.method_28548(l, ChunkGeneratorType.Preset.NETHER)) {
            return false;
        }
        if (!lv2.method_28548(l, ChunkGeneratorType.Preset.END)) {
            return false;
        }
        if (!(lv.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource lv3 = (MultiNoiseBiomeSource)lv.getBiomeSource();
        if (!lv3.method_28462(l)) {
            return false;
        }
        if (!(lv2.getBiomeSource() instanceof TheEndBiomeSource)) {
            return false;
        }
        TheEndBiomeSource lv4 = (TheEndBiomeSource)lv2.getBiomeSource();
        return lv4.method_28479(l);
    }

    public String getSuffix() {
        return this.suffix;
    }

    public static File getSaveDirectory(RegistryKey<World> arg, File file) {
        if (arg == World.OVERWORLD) {
            return file;
        }
        if (arg == World.END) {
            return new File(file, "DIM1");
        }
        if (arg == World.NETHER) {
            return new File(file, "DIM-1");
        }
        return new File(file, "dimensions/" + arg.getValue().getNamespace() + "/" + arg.getValue().getPath());
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean isUltrawarm() {
        return this.ultrawarm;
    }

    public boolean isNatural() {
        return this.natural;
    }

    public boolean isShrunk() {
        return this.shrunk;
    }

    public boolean hasEnderDragonFight() {
        return this.hasEnderDragonFight;
    }

    public BiomeAccessType getBiomeAccessType() {
        return this.biomeAccessType;
    }

    public float method_28528(long l) {
        double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(l) / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0f;
    }

    public int method_28531(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    public float method_28516(int i) {
        return this.field_24767[i];
    }

    public boolean isOverworld() {
        return this.field_24765.equals(Optional.of(OVERWORLD_REGISTRY_KEY));
    }

    public boolean isNether() {
        return this.field_24765.equals(Optional.of(THE_NETHER_REGISTRY_KEY));
    }

    public boolean isEnd() {
        return this.field_24765.equals(Optional.of(THE_END_REGISTRY_KEY));
    }

    public static LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> method_28524(Map<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> map) {
        LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
        for (RegistryKey registryKey : DIMENSION_TYPES) {
            Pair<DimensionType, ChunkGenerator> pair = map.get(registryKey);
            if (pair == null) continue;
            linkedHashMap.put(registryKey, pair);
        }
        for (Map.Entry entry : map.entrySet()) {
            if (DIMENSION_TYPES.contains(entry.getKey())) continue;
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }
}

