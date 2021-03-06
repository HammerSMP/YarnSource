/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.class_5455;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class DimensionType {
    public static final MapCodec<DimensionType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.LONG.optionalFieldOf("fixed_time").xmap(optional -> optional.map(OptionalLong::of).orElseGet(OptionalLong::empty), optionalLong -> optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty()).forGetter(arg -> arg.fixedTime), (App)Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), (App)Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), (App)Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::isUltrawarm), (App)Codec.BOOL.fieldOf("natural").forGetter(DimensionType::isNatural), (App)Codec.BOOL.fieldOf("shrunk").forGetter(DimensionType::isShrunk), (App)Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::isPiglinSafe), (App)Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::isBedWorking), (App)Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::isRespawnAnchorWorking), (App)Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), (App)Codec.intRange((int)0, (int)256).fieldOf("logical_height").forGetter(DimensionType::getLogicalHeight), (App)Identifier.CODEC.fieldOf("infiniburn").forGetter(arg -> arg.infiniburn), (App)Codec.FLOAT.fieldOf("ambient_light").forGetter(arg -> Float.valueOf(arg.ambientLight))).apply((Applicative)instance, DimensionType::new));
    public static final float[] field_24752 = new float[]{1.0f, 0.75f, 0.5f, 0.25f, 0.0f, 0.25f, 0.5f, 0.75f};
    public static final RegistryKey<DimensionType> OVERWORLD_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld"));
    public static final RegistryKey<DimensionType> THE_NETHER_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_nether"));
    public static final RegistryKey<DimensionType> THE_END_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("the_end"));
    protected static final DimensionType OVERWORLD = new DimensionType(OptionalLong.empty(), true, false, false, true, false, false, false, true, false, true, 256, HorizontalVoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getId(), 0.0f);
    protected static final DimensionType THE_NETHER = new DimensionType(OptionalLong.of(18000L), false, true, true, false, true, false, true, false, true, false, 128, VoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_NETHER.getId(), 0.1f);
    protected static final DimensionType THE_END = new DimensionType(OptionalLong.of(6000L), false, false, false, false, false, true, false, false, false, true, 256, VoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_END.getId(), 0.0f);
    public static final RegistryKey<DimensionType> OVERWORLD_CAVES_REGISTRY_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("overworld_caves"));
    protected static final DimensionType OVERWORLD_CAVES = new DimensionType(OptionalLong.empty(), true, true, false, true, false, false, false, true, false, true, 256, HorizontalVoronoiBiomeAccessType.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getId(), 0.0f);
    public static final Codec<Supplier<DimensionType>> field_24756 = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, CODEC);
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultrawarm;
    private final boolean natural;
    private final boolean shrunk;
    private final boolean hasEnderDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final BiomeAccessType biomeAccessType;
    private final Identifier infiniburn;
    private final float ambientLight;
    private final transient float[] field_24767;

    public static DimensionType getOverworldDimensionType() {
        return OVERWORLD;
    }

    @Environment(value=EnvType.CLIENT)
    public static DimensionType getOverworldCavesDimensionType() {
        return OVERWORLD_CAVES;
    }

    protected DimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean shrunk, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, Identifier infiniburn, float ambientLight) {
        this(fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, shrunk, false, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, VoronoiBiomeAccessType.INSTANCE, infiniburn, ambientLight);
    }

    protected DimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean shrunk, boolean hasEnderDragonFight, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int logicalHeight, BiomeAccessType biomeAccessType, Identifier infiniburn, float ambientLight) {
        this.fixedTime = fixedTime;
        this.hasSkyLight = hasSkylight;
        this.hasCeiling = hasCeiling;
        this.ultrawarm = ultrawarm;
        this.natural = natural;
        this.shrunk = shrunk;
        this.hasEnderDragonFight = hasEnderDragonFight;
        this.piglinSafe = piglinSafe;
        this.bedWorks = bedWorks;
        this.respawnAnchorWorks = respawnAnchorWorks;
        this.hasRaids = hasRaids;
        this.logicalHeight = logicalHeight;
        this.biomeAccessType = biomeAccessType;
        this.infiniburn = infiniburn;
        this.ambientLight = ambientLight;
        this.field_24767 = DimensionType.method_28515(ambientLight);
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
        Optional optional = dynamic.asNumber().result();
        if (optional.isPresent()) {
            int i = ((Number)optional.get()).intValue();
            if (i == -1) {
                return DataResult.success(World.NETHER);
            }
            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }
            if (i == 1) {
                return DataResult.success(World.END);
            }
        }
        return World.CODEC.parse(dynamic);
    }

    public static class_5455.class_5457 addRegistryDefaults(class_5455.class_5457 registryTracker) {
        MutableRegistry<DimensionType> lv = registryTracker.method_30530(Registry.DIMENSION_TYPE_KEY);
        lv.add(OVERWORLD_REGISTRY_KEY, OVERWORLD);
        lv.add(OVERWORLD_CAVES_REGISTRY_KEY, OVERWORLD_CAVES);
        lv.add(THE_NETHER_REGISTRY_KEY, THE_NETHER);
        lv.add(THE_END_REGISTRY_KEY, THE_END);
        return registryTracker;
    }

    private static ChunkGenerator createEndGenerator(long seed) {
        return new SurfaceChunkGenerator(new TheEndBiomeSource(seed), seed, ChunkGeneratorType.Preset.END.getChunkGeneratorType());
    }

    private static ChunkGenerator createNetherGenerator(long seed) {
        return new SurfaceChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(seed), seed, ChunkGeneratorType.Preset.NETHER.getChunkGeneratorType());
    }

    public static SimpleRegistry<DimensionOptions> method_28517(long seed) {
        SimpleRegistry<DimensionOptions> lv = new SimpleRegistry<DimensionOptions>(Registry.DIMENSION_OPTIONS, Lifecycle.experimental());
        lv.add(DimensionOptions.NETHER, new DimensionOptions(() -> THE_NETHER, DimensionType.createNetherGenerator(seed)));
        lv.add(DimensionOptions.END, new DimensionOptions(() -> THE_END, DimensionType.createEndGenerator(seed)));
        lv.markLoaded(DimensionOptions.NETHER);
        lv.markLoaded(DimensionOptions.END);
        return lv;
    }

    @Deprecated
    public String getSuffix() {
        if (this == THE_END) {
            return "_end";
        }
        return "";
    }

    public static File getSaveDirectory(RegistryKey<World> worldRef, File root) {
        if (worldRef == World.OVERWORLD) {
            return root;
        }
        if (worldRef == World.END) {
            return new File(root, "DIM1");
        }
        if (worldRef == World.NETHER) {
            return new File(root, "DIM-1");
        }
        return new File(root, "dimensions/" + worldRef.getValue().getNamespace() + "/" + worldRef.getValue().getPath());
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

    public boolean isPiglinSafe() {
        return this.piglinSafe;
    }

    public boolean isBedWorking() {
        return this.bedWorks;
    }

    public boolean isRespawnAnchorWorking() {
        return this.respawnAnchorWorks;
    }

    public boolean hasRaids() {
        return this.hasRaids;
    }

    public int getLogicalHeight() {
        return this.logicalHeight;
    }

    public boolean hasEnderDragonFight() {
        return this.hasEnderDragonFight;
    }

    public BiomeAccessType getBiomeAccessType() {
        return this.biomeAccessType;
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
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

    public Tag<Block> getInfiniburnBlocks() {
        Tag<Block> lv = BlockTags.getTagGroup().getTag(this.infiniburn);
        return lv != null ? lv : BlockTags.INFINIBURN_OVERWORLD;
    }
}

