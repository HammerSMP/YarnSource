/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
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
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.class_5195;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Set<Biome> BIOMES = Sets.newHashSet();
    public static final IdList<Biome> PARENT_BIOME_ID_MAP = new IdList();
    protected static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(1234L), (List<Integer>)ImmutableList.of((Object)0));
    public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(2345L), (List<Integer>)ImmutableList.of((Object)0));
    @Nullable
    protected String translationKey;
    protected final float depth;
    protected final float scale;
    protected final float temperature;
    protected final float downfall;
    private final int skyColor;
    @Nullable
    protected final String parent;
    protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
    protected final Category category;
    protected final Precipitation precipitation;
    protected final BiomeEffects effects;
    protected final Map<GenerationStep.Carver, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();
    protected final Map<GenerationStep.Feature, List<ConfiguredFeature<?, ?>>> features = Maps.newHashMap();
    protected final List<ConfiguredFeature<?, ?>> flowerFeatures = Lists.newArrayList();
    protected final Map<StructureFeature<?>, FeatureConfig> structureFeatures = Maps.newHashMap();
    private final Map<EntityCategory, List<SpawnEntry>> spawns = Maps.newHashMap();
    private final Map<EntityType<?>, SpawnDensity> spawnDensities = Maps.newHashMap();
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25f){

            protected void rehash(int i) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    }));
    private final List<MixedNoisePoint> noisePoints;

    @Nullable
    public static Biome getModifiedBiome(Biome arg) {
        return PARENT_BIOME_ID_MAP.get(Registry.BIOME.getRawId(arg));
    }

    public static <C extends CarverConfig> ConfiguredCarver<C> configureCarver(Carver<C> arg, C arg2) {
        return new ConfiguredCarver<C>(arg, arg2);
    }

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
        this.noisePoints = arg.noises != null ? arg.noises : ImmutableList.of();
        this.effects = arg.specialEffects;
        for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
            this.features.put(feature, Lists.newArrayList());
        }
        for (Enum enum_ : EntityCategory.values()) {
            this.spawns.put((EntityCategory)enum_, Lists.newArrayList());
        }
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

    protected void addSpawn(EntityCategory arg, SpawnEntry arg2) {
        this.spawns.get((Object)arg).add(arg2);
    }

    protected void addSpawnDensity(EntityType<?> arg, double d, double e) {
        this.spawnDensities.put(arg, new SpawnDensity(e, d));
    }

    public List<SpawnEntry> getEntitySpawnList(EntityCategory arg) {
        return this.spawns.get((Object)arg);
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

    public float getMaxSpawnLimit() {
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
        if (arg2.feature == Feature.DECORATED_FLOWER) {
            this.flowerFeatures.add(arg2);
        }
        this.features.get((Object)arg).add(arg2);
    }

    public <C extends CarverConfig> void addCarver(GenerationStep.Carver arg2, ConfiguredCarver<C> arg22) {
        this.carvers.computeIfAbsent(arg2, arg -> Lists.newArrayList()).add(arg22);
    }

    public List<ConfiguredCarver<?>> getCarversForStep(GenerationStep.Carver arg2) {
        return this.carvers.computeIfAbsent(arg2, arg -> Lists.newArrayList());
    }

    public <C extends FeatureConfig> void addStructureFeature(ConfiguredFeature<C, ? extends StructureFeature<C>> arg) {
        this.structureFeatures.put((StructureFeature<?>)arg.feature, (FeatureConfig)arg.config);
    }

    public <C extends FeatureConfig> boolean hasStructureFeature(StructureFeature<C> arg) {
        return this.structureFeatures.containsKey(arg);
    }

    @Nullable
    public <C extends FeatureConfig> C getStructureFeatureConfig(StructureFeature<C> arg) {
        return (C)this.structureFeatures.get(arg);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<ConfiguredFeature<?, ?>> getFeaturesForStep(GenerationStep.Feature arg) {
        return this.features.get((Object)arg);
    }

    public void generateFeatureStep(GenerationStep.Feature arg, StructureAccessor arg2, ChunkGenerator<? extends ChunkGeneratorConfig> arg3, IWorld arg4, long l, ChunkRandom arg5, BlockPos arg6) {
        int i = 0;
        for (ConfiguredFeature<?, ?> lv : this.features.get((Object)arg)) {
            arg5.setDecoratorSeed(l, i, arg.ordinal());
            try {
                lv.generate(arg4, arg2, arg3, arg5, arg6);
            }
            catch (Exception exception) {
                CrashReport lv2 = CrashReport.create(exception, "Feature placement");
                lv2.addElement("Feature").add("Id", Registry.FEATURE.getId((Feature<?>)lv.feature)).add("Config", lv.config).add("Description", () -> arg.feature.toString());
                throw new CrashException(lv2);
            }
            ++i;
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
        this.surfaceBuilder.initSeed(m);
        this.surfaceBuilder.generate(random, arg, this, i, j, k, d, arg2, arg3, l, m);
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

    public Text getName() {
        return new TranslatableText(this.getTranslationKey());
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("biome", Registry.BIOME.getId(this));
        }
        return this.translationKey;
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
    public Optional<class_5195> method_27343() {
        return this.effects.method_27345();
    }

    public final Category getCategory() {
        return this.category;
    }

    public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceConfig getSurfaceConfig() {
        return this.surfaceBuilder.getConfig();
    }

    public Stream<MixedNoisePoint> streamNoises() {
        return this.noisePoints.stream();
    }

    @Nullable
    public String getParent() {
        return this.parent;
    }

    public static class MixedNoisePoint {
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
        private ConfiguredSurfaceBuilder<?> surfaceBuilder;
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
        private List<MixedNoisePoint> noises;
        @Nullable
        private BiomeEffects specialEffects;

        public <SC extends SurfaceConfig> Settings configureSurfaceBuilder(SurfaceBuilder<SC> arg, SC arg2) {
            this.surfaceBuilder = new ConfiguredSurfaceBuilder<SC>(arg, arg2);
            return this;
        }

        public Settings surfaceBuilder(ConfiguredSurfaceBuilder<?> arg) {
            this.surfaceBuilder = arg;
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

        public Settings noises(List<MixedNoisePoint> list) {
            this.noises = list;
            return this;
        }

        public Settings effects(BiomeEffects arg) {
            this.specialEffects = arg;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + (Object)((Object)this.precipitation) + ",\nbiomeCategory=" + (Object)((Object)this.category) + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nparent='" + this.parent + '\'' + "\n" + '}';
        }
    }

    public static class SpawnEntry
    extends WeightedPicker.Entry {
        public final EntityType<?> type;
        public final int minGroupSize;
        public final int maxGroupSize;

        public SpawnEntry(EntityType<?> arg, int i, int j, int k) {
            super(i);
            this.type = arg;
            this.minGroupSize = j;
            this.maxGroupSize = k;
        }

        public String toString() {
            return EntityType.getId(this.type) + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
        }
    }

    public static class SpawnDensity {
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

    public static enum Precipitation {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        private static final Map<String, Precipitation> NAME_MAP;
        private final String name;

        private Precipitation(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(Precipitation.values()).collect(Collectors.toMap(Precipitation::getName, arg -> arg));
        }
    }

    public static enum Category {
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

        private static final Map<String, Category> NAME_MAP;
        private final String name;

        private Category(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        static {
            NAME_MAP = Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, arg -> arg));
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
            NAME_MAP = Arrays.stream(TemperatureGroup.values()).collect(Collectors.toMap(TemperatureGroup::getName, arg -> arg));
        }
    }
}

