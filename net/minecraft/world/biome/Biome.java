/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.class_5458;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MapCodec<Biome> field_25819 = RecordCodecBuilder.mapCodec(instance -> 
	    instance.group((App)Precipitation.field_24680.fieldOf("precipitation").forGetter(arg -> 
	    arg.precipitation), (App)Category.field_24678.fieldOf("category").forGetter(arg -> 
	    arg.category), (App)Codec.FLOAT.fieldOf("depth").forGetter(arg -> 
	    Float.valueOf(arg.depth)), (App)Codec.FLOAT.fieldOf("scale").forGetter(arg -> 
	    Float.valueOf(arg.scale)), (App)Codec.FLOAT.fieldOf("temperature").forGetter(arg -> 
	    Float.valueOf(arg.temperature)), (App)Codec.FLOAT.fieldOf("downfall").forGetter(arg -> 
	    Float.valueOf(arg.downfall)), (App)BiomeEffects.CODEC.fieldOf("effects").forGetter(arg -> 
	    arg.effects), (App)Codec.INT.fieldOf("sky_color").forGetter(arg -> 
	    arg.skyColor), (App)ConfiguredSurfaceBuilder.field_25015.fieldOf("surface_builder").forGetter(arg -> 
	    arg.surfaceBuilder), (App)Codec.simpleMap(GenerationStep.Carver.field_24770, (Codec)ConfiguredCarver.field_24828.listOf().promotePartial(Util.method_29188("Carver: ", ((Logger)LOGGER)::error)), (Keyable)StringIdentifiable.method_28142(GenerationStep.Carver.values())).fieldOf("carvers").forGetter(arg -> 
	    arg.carvers), (App)ConfiguredFeature.CODEC.listOf().promotePartial(Util.method_29188("Feature: ", ((Logger)LOGGER)::error)).listOf().fieldOf("features").forGetter(arg -> 
	    arg.features), (App)ConfiguredStructureFeature.TYPE_CODEC.listOf().promotePartial(Util.method_29188("Structure start: ", ((Logger)LOGGER)::error)).fieldOf("starts").forGetter(arg -> 
	    arg.noisePoints), (App)Codec.simpleMap(SpawnGroup.field_24655, (Codec)SpawnEntry.CODEC.listOf().promotePartial(Util.method_29188("Spawn data: ", ((Logger)LOGGER)::error)), (Keyable)StringIdentifiable.method_28142(SpawnGroup.values())).fieldOf("spawners").forGetter(arg -> 
	    arg.spawns), (App)Codec.STRING.optionalFieldOf("parent").forGetter(arg -> 
	    Optional.ofNullable(arg.parent)), (App)Codec.simpleMap(Registry.ENTITY_TYPE, SpawnDensity.field_25820, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter(arg -> 
        arg.spawnDensities)).apply((Applicative)instance, Biome::new
    ));
        
    public static final Codec<Supplier<Biome>> field_24677 = RegistryElementCodec.of(Registry.BIOME_KEY, field_25819);
    public static final Set<Biome> BIOMES = Sets.newHashSet();
    protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), (List<Integer>)ImmutableList.of((Object)0));
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), (List<Integer>)ImmutableList.of((Object)0));
    private final float depth;
    private final float scale;
    private final float temperature;
    private final float downfall;
    private final int skyColor;
    @Nullable
    protected final String parent;
    private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
    private final Category category;
    private final Precipitation precipitation;
    private final BiomeEffects effects;
    private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
    private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
    private final List<ConfiguredFeature<?, ?>> flowerFeatures;
    private final List<Supplier<ConfiguredStructureFeature<?, ?>>> noisePoints;
    private final Map<SpawnGroup, List<SpawnEntry>> spawns;
    private final Map<EntityType<?>, SpawnDensity> spawnDensities;
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> 
	 Util.make(() -> 
	 {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));

    protected Biome(Settings arg) {
        if (arg.surfaceBuilder == null || arg.precipitation == null || arg.category == null || arg.depth == null || arg.scale == null || arg.temperature == null || arg.downfall == null || arg.specialEffects == null) {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + arg);
        }
        this.surfaceBuilder = arg.surfaceBuilder;
        this.precipitation = arg.precipitation;
        this.category = arg.category;
        this.depth = arg.depth.floatValue();
        this.scale = arg.scale.floatValue();
        this.temperature = arg.temperature.floatValue();
        this.downfall = arg.downfall.floatValue();
        this.skyColor = this.calculateSkyColor();
        this.parent = arg.parent;
        this.effects = arg.specialEffects;
        this.carvers = Maps.newHashMap();
        this.noisePoints = Lists.newArrayList();
        this.features = Lists.newArrayList();
        this.spawns = Maps.newHashMap();
        for (SpawnGroup lv : SpawnGroup.values()) {
            this.spawns.put(lv, Lists.newArrayList());
        }
        this.spawnDensities = Maps.newHashMap();
        this.flowerFeatures = Lists.newArrayList();
    }

    private Biome(Precipitation arg2, Category arg22, float f, float g, float h, float i, BiomeEffects arg3, int j, Supplier<ConfiguredSurfaceBuilder<?>> supplier, Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> map, List<List<Supplier<ConfiguredFeature<?, ?>>>> list, List<Supplier<ConfiguredStructureFeature<?, ?>>> list2, Map<SpawnGroup, List<SpawnEntry>> map2, Optional<String> optional, Map<EntityType<?>, SpawnDensity> map3) {
        this.precipitation = arg2;
        this.category = arg22;
        this.depth = f;
        this.scale = g;
        this.temperature = h;
        this.downfall = i;
        this.effects = arg3;
        this.skyColor = j;
        this.surfaceBuilder = supplier;
        this.carvers = map;
        this.features = list;
        this.noisePoints = list2;
        this.spawns = map2;
        this.parent = optional.orElse(null);
        this.spawnDensities = map3;
        this.flowerFeatures = list.stream().flatMap(Collection::stream).map(Supplier::get).filter(arg -> 
	 arg.feature == Feature.DECORATED_FLOWER).collect(Collectors.toList());
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    private int calculateSkyColor() {
        float f = this.temperature;
        f /= 3.0f;
        f = MathHelper.clamp(f, -1.0f, 1.0f);
        return MathHelper.hsvToRgb(0.62222224f - f * 0.05f, 0.5f + f * 0.1f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return this.skyColor;
    }

    public void addSpawn(SpawnGroup arg, SpawnEntry arg2) {
        this.spawns.get(arg).add(arg2);
    }

    protected void addSpawnDensity(EntityType<?> arg, double d, double e) {
        this.spawnDensities.put(arg, new SpawnDensity(e, d));
    }

    public List<SpawnEntry> getEntitySpawnList(SpawnGroup arg) {
        return this.spawns.get(arg);
    }

    @Nullable
    public SpawnDensity getSpawnDensity(EntityType<?> arg) {
        return this.spawnDensities.get(arg);
    }

    public Precipitation getPrecipitation() {
        return this.precipitation;
    }

    public boolean hasHighHumidity() {
        return this.getRainfall() > 0.85f;
    }

    public float getMaxSpawnChance() {
        return 0.1f;
    }

    protected float computeTemperature(BlockPos arg) {
        if (arg.getY() > 64) {
            float f = (float)(TEMPERATURE_NOISE.sample((float)arg.getX() / 8.0f, (float)arg.getZ() / 8.0f, false) * 4.0);
            return this.getTemperature() - (f + (float)arg.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return this.getTemperature();
    }

    public final float getTemperature(BlockPos arg) {
        long l = arg.asLong();
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = this.temperatureCache.get();
        float f = long2FloatLinkedOpenHashMap.get(l);
        if (!Float.isNaN(f)) {
            return f;
        }
        float g = this.computeTemperature(arg);
        if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
        }
        long2FloatLinkedOpenHashMap.put(l, g);
        return g;
    }

    public boolean canSetIce(WorldView arg, BlockPos arg2) {
        return this.canSetIce(arg, arg2, true);
    }

    public boolean canSetIce(WorldView arg, BlockPos arg2, boolean bl) {
        if (this.getTemperature(arg2) >= 0.15f) {
            return false;
        }
        if (arg2.getY() >= 0 && arg2.getY() < 256 && arg.getLightLevel(LightType.BLOCK, arg2) < 10) {
            BlockState lv = arg.getBlockState(arg2);
            FluidState lv2 = arg.getFluidState(arg2);
            if (lv2.getFluid() == Fluids.WATER && lv.getBlock() instanceof FluidBlock) {
                boolean bl2;
                if (!bl) {
                    return true;
                }
                boolean bl3 = bl2 = arg.isWater(arg2.west()) && arg.isWater(arg2.east()) && arg.isWater(arg2.north()) && arg.isWater(arg2.south());
                if (!bl2) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canSetSnow(WorldView arg, BlockPos arg2) {
        BlockState lv;
        if (this.getTemperature(arg2) >= 0.15f) {
            return false;
        }
        return arg2.getY() >= 0 && arg2.getY() < 256 && arg.getLightLevel(LightType.BLOCK, arg2) < 10 && (lv = arg.getBlockState(arg2)).isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(arg, arg2);
    }

    public void addFeature(GenerationStep.Feature arg, ConfiguredFeature<?, ?> arg2) {
        this.method_30350(arg.ordinal(), () -> 
	 arg2);
    }

    public void method_30350(int i, Supplier<ConfiguredFeature<?, ?>> supplier) {
        if (supplier.get().feature == Feature.DECORATED_FLOWER) {
            this.flowerFeatures.add(supplier.get());
        }
        while (this.features.size() <= i) {
            this.features.add(Lists.newArrayList());
        }
        this.features.get(i).add(supplier);
    }

    public <C extends CarverConfig> void addCarver(GenerationStep.Carver arg2, ConfiguredCarver<C> arg22) {
        this.carvers.computeIfAbsent(arg2, arg -> 
	 Lists.newArrayList()).add(() -> 
	 arg22);
    }

    public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver arg) {
        return (List)this.carvers.getOrDefault(arg, (List<Supplier<ConfiguredCarver<?>>>)ImmutableList.of());
    }

    public void addStructureFeature(ConfiguredStructureFeature<?, ?> arg) {
        this.noisePoints.add(() -> 
	 arg);
    }

    public boolean hasStructureFeature(StructureFeature<?> arg) {
        return this.noisePoints.stream().anyMatch(supplier -> 
	 ((ConfiguredStructureFeature)supplier.get()).feature == arg);
    }

    public Iterable<Supplier<ConfiguredStructureFeature<?, ?>>> method_28413() {
        return this.noisePoints;
    }

    public ConfiguredStructureFeature<?, ?> method_28405(ConfiguredStructureFeature<?, ?> arg) {
        return (ConfiguredStructureFeature)DataFixUtils.orElse(this.noisePoints.stream().map(Supplier::get).filter(arg2 -> 
	 arg2.feature == arg.feature).findAny(), arg);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<List<Supplier<ConfiguredFeature<?, ?>>>> method_30357() {
        return this.features;
    }

    public void generateFeatureStep(StructureAccessor arg, ChunkGenerator arg2, ChunkRegion arg3, long l, ChunkRandom arg4, BlockPos arg52) {
        for (int i = 0; i < this.features.size(); ++i) {
            int j = 0;
            if (arg.shouldGenerateStructures()) {
                for (StructureFeature structureFeature : Registry.STRUCTURE_FEATURE) {
                    if (structureFeature.method_28663().ordinal() != i) continue;
                    arg4.setDecoratorSeed(l, j, i);
                    int k = arg52.getX() >> 4;
                    int m = arg52.getZ() >> 4;
                    int n = k << 4;
                    int o = m << 4;
                    try {
                        arg.getStructuresWithChildren(ChunkSectionPos.from(arg52), structureFeature).forEach(arg5 -> 
	 arg5.generateStructure(arg3, arg, arg2, arg4, new BlockBox(n, o, n + 15, o + 15), new ChunkPos(k, m)));
                    }
                    catch (Exception exception) {
                        CrashReport lv2 = CrashReport.create(exception, "Feature placement");
                        lv2.addElement("Feature").add("Id", Registry.STRUCTURE_FEATURE.getId(structureFeature)).add("Description", () -> 
	 lv.toString());
                        throw new CrashException(lv2);
                    }
                    ++j;
                }
            }
            for (Supplier<ConfiguredFeature<?, ?>> supplier : this.features.get(i)) {
                ConfiguredFeature<?, ?> lv3 = supplier.get();
                arg4.setDecoratorSeed(l, j, i);
                try {
                    lv3.generate(arg3, arg2, arg4, arg52);
                }
                catch (Exception exception2) {
                    CrashReport lv4 = CrashReport.create(exception2, "Feature placement");
                    lv4.addElement("Feature").add("Id", Registry.FEATURE.getId((Feature<?>)lv3.feature)).add("Config", lv3.config).add("Description", () -> 
	 arg.feature.toString());
                    throw new CrashException(lv4);
                }
                ++j;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getFogColor() {
        return this.effects.getFogColor();
    }

    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double d, double e) {
        double f = MathHelper.clamp(this.getTemperature(), 0.0f, 1.0f);
        double g = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return GrassColors.getColor(f, g);
    }

    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        double d = MathHelper.clamp(this.getTemperature(), 0.0f, 1.0f);
        double e = MathHelper.clamp(this.getRainfall(), 0.0f, 1.0f);
        return FoliageColors.getColor(d, e);
    }

    public void buildSurface(Random random, Chunk arg, int i, int j, int k, double d, BlockState arg2, BlockState arg3, int l, long m) {
        ConfiguredSurfaceBuilder<?> lv = this.surfaceBuilder.get();
        lv.initSeed(m);
        lv.generate(random, arg, this, i, j, k, d, arg2, arg3, l, m);
    }

    public TemperatureGroup getTemperatureGroup() {
        if (this.category == Category.OCEAN) {
            return TemperatureGroup.OCEAN;
        }
        if ((double)this.getTemperature() < 0.2) {
            return TemperatureGroup.COLD;
        }
        if ((double)this.getTemperature() < 1.0) {
            return TemperatureGroup.MEDIUM;
        }
        return TemperatureGroup.WARM;
    }

    public final float getDepth() {
        return this.depth;
    }

    public final float getRainfall() {
        return this.downfall;
    }

    public final float getScale() {
        return this.scale;
    }

    public final float getTemperature() {
        return this.temperature;
    }

    public BiomeEffects getEffects() {
        return this.effects;
    }

    @Environment(value=EnvType.CLIENT)
    public final int getWaterColor() {
        return this.effects.getWaterColor();
    }

    @Environment(value=EnvType.CLIENT)
    public final int getWaterFogColor() {
        return this.effects.getWaterFogColor();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.effects.getParticleConfig();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<SoundEvent> getLoopSound() {
        return this.effects.getLoopSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeMoodSound> getMoodSound() {
        return this.effects.getMoodSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.effects.getAdditionsSound();
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<MusicSound> method_27343() {
        return this.effects.method_27345();
    }

    public final Category getCategory() {
        return this.category;
    }

    public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceBuilder.get().getConfig();
    }

    @Nullable
    public String getParent() {
        return this.parent;
    }

    public String toString() {
        Identifier lv = class_5458.field_25933.getId(this);
        return lv == null ? super.toString() : lv.toString();
    }

    public static class MixedNoisePoint {
        public static final Codec<MixedNoisePoint> CODEC = RecordCodecBuilder.create(instance -> 
	        instance.group((App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("temperature").forGetter(arg -> 
	        Float.valueOf(arg.temperature)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("humidity").forGetter(arg -> 
	        Float.valueOf(arg.humidity)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("altitude").forGetter(arg -> 
	        Float.valueOf(arg.altitude)), (App)Codec.floatRange((float)-2.0f, (float)2.0f).fieldOf("weirdness").forGetter(arg -> 
	        Float.valueOf(arg.weirdness)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("offset").forGetter(arg -> 
	        Float.valueOf(arg.weight))).apply((Applicative)instance, MixedNoisePoint::new));
        private final float temperature;
        private final float humidity;
        private final float altitude;
        private final float weirdness;
        private final float weight;

        public MixedNoisePoint(float f, float g, float h, float i, float j) {
            this.temperature = f;
            this.humidity = g;
            this.altitude = h;
            this.weirdness = i;
            this.weight = j;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            MixedNoisePoint lv = (MixedNoisePoint)object;
            if (Float.compare(lv.temperature, this.temperature) != 0) {
                return false;
            }
            if (Float.compare(lv.humidity, this.humidity) != 0) {
                return false;
            }
            if (Float.compare(lv.altitude, this.altitude) != 0) {
                return false;
            }
            return Float.compare(lv.weirdness, this.weirdness) == 0;
        }

        public int hashCode() {
            int i = this.temperature != 0.0f ? Float.floatToIntBits(this.temperature) : 0;
            i = 31 * i + (this.humidity != 0.0f ? Float.floatToIntBits(this.humidity) : 0);
            i = 31 * i + (this.altitude != 0.0f ? Float.floatToIntBits(this.altitude) : 0);
            i = 31 * i + (this.weirdness != 0.0f ? Float.floatToIntBits(this.weirdness) : 0);
            return i;
        }

        public float calculateDistanceTo(MixedNoisePoint arg) {
            return (this.temperature - arg.temperature) * (this.temperature - arg.temperature) + (this.humidity - arg.humidity) * (this.humidity - arg.humidity) + (this.altitude - arg.altitude) * (this.altitude - arg.altitude) + (this.weirdness - arg.weirdness) * (this.weirdness - arg.weirdness) + (this.weight - arg.weight) * (this.weight - arg.weight);
        }
    }

    public static class Settings {
        @Nullable
        private Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
        @Nullable
        private Precipitation precipitation;
        @Nullable
        private Category category;
        @Nullable
        private Float depth;
        @Nullable
        private Float scale;
        @Nullable
        private Float temperature;
        @Nullable
        private Float downfall;
        @Nullable
        private String parent;
        @Nullable
        private BiomeEffects specialEffects;

        public Settings configureSurfaceBuilder(ConfiguredSurfaceBuilder<?> arg) {
            return this.surfaceBuilder(() -> arg);
        }

        public Settings surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> supplier) {
            this.surfaceBuilder = supplier;
            return this;
        }

        public Settings precipitation(Precipitation arg) {
            this.precipitation = arg;
            return this;
        }

        public Settings category(Category arg) {
            this.category = arg;
            return this;
        }

        public Settings depth(float f) {
            this.depth = Float.valueOf(f);
            return this;
        }

        public Settings scale(float f) {
            this.scale = Float.valueOf(f);
            return this;
        }

        public Settings temperature(float f) {
            this.temperature = Float.valueOf(f);
            return this;
        }

        public Settings downfall(float f) {
            this.downfall = Float.valueOf(f);
            return this;
        }

        public Settings parent(@Nullable String string) {
            this.parent = string;
            return this;
        }

        public Settings effects(BiomeEffects arg) {
            this.specialEffects = arg;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nparent='" + this.parent + '\'' + "\n" + '}';
        }
    }

    public static class SpawnEntry
    extends WeightedPicker.Entry {
        public static final Codec<SpawnEntry> CODEC = RecordCodecBuilder.create(instance -> 
	 instance.group((App)Registry.ENTITY_TYPE.fieldOf("type").forGetter(arg -> 
	 arg.type), (App)Codec.INT.fieldOf("weight").forGetter(arg -> 
	 arg.weight), (App)Codec.INT.fieldOf("minCount").forGetter(arg -> 
	 arg.minGroupSize), (App)Codec.INT.fieldOf("maxCount").forGetter(arg -> 
	 arg.maxGroupSize)).apply((Applicative)instance, SpawnEntry::new));
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> arg, int i, int j, int k) {
            super(i);
            this.type = arg.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : arg;
            this.minGroupSize = j;
            this.maxGroupSize = k;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
        }
    }

    public static class SpawnDensity {
        public static final Codec<SpawnDensity> field_25820 = RecordCodecBuilder.create(instance -> 
	 instance.group((App)Codec.DOUBLE.fieldOf("energy_budget").forGetter(SpawnDensity::getGravityLimit), (App)Codec.DOUBLE.fieldOf("charge").forGetter(SpawnDensity::getMass)).apply((Applicative)instance, SpawnDensity::new));
        private final double gravityLimit;
        private final double mass;

        public SpawnDensity(double d, double e) {
            this.gravityLimit = d;
            this.mass = e;
        }

        public double getGravityLimit() {
            return this.gravityLimit;
        }

        public double getMass() {
            return this.mass;
        }
    }

    public static enum Precipitation implements StringIdentifiable
    {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<Precipitation> field_24680;
        private static final Map<String, Precipitation> NAME_MAP;
        private final String name;

        private Precipitation(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Precipitation method_28431(String string) {
            return NAME_MAP.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24680 = StringIdentifiable.createCodec(Precipitation::values, Precipitation::method_28431);
            NAME_MAP = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, arg -> 
	 arg));
        }
    }

    public static enum Category implements StringIdentifiable
    {
        NONE("none"),
        TAIGA("taiga"),
        EXTREME_HILLS("extreme_hills"),
        JUNGLE("jungle"),
        MESA("mesa"),
        PLAINS("plains"),
        SAVANNA("savanna"),
        ICY("icy"),
        THEEND("the_end"),
        BEACH("beach"),
        FOREST("forest"),
        OCEAN("ocean"),
        DESERT("desert"),
        RIVER("river"),
        SWAMP("swamp"),
        MUSHROOM("mushroom"),
        NETHER("nether");

        public static final Codec<Category> field_24678;
        private static final Map<String, Category> NAME_MAP;
        private final String name;

        private Category(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Category method_28424(String string) {
            return NAME_MAP.get(string);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            field_24678 = StringIdentifiable.createCodec(Category::values, Category::method_28424);
            NAME_MAP = Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, arg -> 
	 arg));
        }
    }

    public static enum TemperatureGroup {
        OCEAN("ocean"),
        COLD("cold"),
        MEDIUM("medium"),
        WARM("warm");

        private static final Map<String, TemperatureGroup> NAME_MAP;
        private final String name;

        private TemperatureGroup(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(TemperatureGroup.values()).collect(Collectors.toMap(TemperatureGroup::getName, arg -> 
	 arg));
        }
    }
}

