/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_5455;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> {
    public static final BiMap<String, StructureFeature<?>> STRUCTURES = HashBiMap.create();
    private static final Map<StructureFeature<?>, GenerationStep.Feature> STRUCTURE_TO_GENERATION_STEP = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<StructurePoolFeatureConfig> PILLAGER_OUTPOST = StructureFeature.register("Pillager_Outpost", new PillagerOutpostFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = StructureFeature.register("Mineshaft", new MineshaftFeature(MineshaftFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> MANSION = StructureFeature.register("Mansion", new WoodlandMansionFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_PYRAMID = StructureFeature.register("Jungle_Pyramid", new JungleTempleFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = StructureFeature.register("Desert_Pyramid", new DesertPyramidFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> IGLOO = StructureFeature.register("Igloo", new IglooFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = StructureFeature.register("Ruined_Portal", new RuinedPortalFeature(RuinedPortalFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = StructureFeature.register("Shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final SwampHutFeature SWAMP_HUT = StructureFeature.register("Swamp_Hut", new SwampHutFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = StructureFeature.register("Stronghold", new StrongholdFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.STRONGHOLDS);
    public static final StructureFeature<DefaultFeatureConfig> MONUMENT = StructureFeature.register("Monument", new OceanMonumentFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = StructureFeature.register("Ocean_Ruin", new OceanRuinFeature(OceanRuinFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> FORTRESS = StructureFeature.register("Fortress", new NetherFortressFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<DefaultFeatureConfig> END_CITY = StructureFeature.register("EndCity", new EndCityFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<ProbabilityConfig> BURIED_TREASURE = StructureFeature.register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = StructureFeature.register("Village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> NETHER_FOSSIL = StructureFeature.register("Nether_Fossil", new NetherFossilFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<StructurePoolFeatureConfig> BASTION_REMNANT = StructureFeature.register("Bastion_Remnant", new BastionRemnantFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final List<StructureFeature<?>> field_24861 = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
    private static final Map<String, String> field_25839 = ImmutableMap.builder().put((Object)"nvi", (Object)"jigsaw").put((Object)"pcp", (Object)"jigsaw").put((Object)"bastionremnant", (Object)"jigsaw").put((Object)"runtime", (Object)"jigsaw").build();
    private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;

    private static <F extends StructureFeature<?>> F register(String name, F structureFeature, GenerationStep.Feature step) {
        STRUCTURES.put((Object)name.toLowerCase(Locale.ROOT), structureFeature);
        STRUCTURE_TO_GENERATION_STEP.put(structureFeature, step);
        return (F)Registry.register(Registry.STRUCTURE_FEATURE, name.toLowerCase(Locale.ROOT), structureFeature);
    }

    public StructureFeature(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap(arg -> new ConfiguredStructureFeature<FeatureConfig, StructureFeature>(this, (FeatureConfig)arg), arg -> arg.config).codec();
    }

    public GenerationStep.Feature getGenerationStep() {
        return STRUCTURE_TO_GENERATION_STEP.get(this);
    }

    public static void method_28664() {
    }

    @Nullable
    public static StructureStart<?> readStructureStart(StructureManager arg, CompoundTag tag, long worldSeed) {
        String string = tag.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        StructureFeature<?> lv = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
        if (lv == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        int i = tag.getInt("ChunkX");
        int j = tag.getInt("ChunkZ");
        int k = tag.getInt("references");
        BlockBox lv2 = tag.contains("BB") ? new BlockBox(tag.getIntArray("BB")) : BlockBox.empty();
        ListTag lv3 = tag.getList("Children", 10);
        try {
            StructureStart<?> lv4 = super.createStart(i, j, lv2, k, worldSeed);
            for (int m = 0; m < lv3.size(); ++m) {
                CompoundTag lv5 = lv3.getCompound(m);
                String string2 = lv5.getString("id").toLowerCase(Locale.ROOT);
                String string3 = field_25839.getOrDefault(string2, string2);
                StructurePieceType lv6 = Registry.STRUCTURE_PIECE.get(new Identifier(string3));
                if (lv6 == null) {
                    LOGGER.error("Unknown structure piece id: {}", (Object)string3);
                    continue;
                }
                try {
                    StructurePiece lv7 = lv6.load(arg, lv5);
                    lv4.getChildren().add(lv7);
                    continue;
                }
                catch (Exception exception) {
                    LOGGER.error("Exception loading structure piece with id {}", (Object)string3, (Object)exception);
                }
            }
            return lv4;
        }
        catch (Exception exception2) {
            LOGGER.error("Failed Start with id {}", (Object)string, (Object)exception2);
            return null;
        }
    }

    public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> getCodec() {
        return this.codec;
    }

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C config) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, config);
    }

    @Nullable
    public BlockPos locateStructure(WorldView arg, StructureAccessor arg2, BlockPos searchStartPos, int searchRadius, boolean skipExistingChunks, long worldSeed, StructureConfig config) {
        int j = config.getSpacing();
        int k = searchStartPos.getX() >> 4;
        int m = searchStartPos.getZ() >> 4;
        ChunkRandom lv = new ChunkRandom();
        block0: for (int n = 0; n <= searchRadius; ++n) {
            for (int o = -n; o <= n; ++o) {
                boolean bl2 = o == -n || o == n;
                for (int p = -n; p <= n; ++p) {
                    boolean bl3;
                    boolean bl = bl3 = p == -n || p == n;
                    if (!bl2 && !bl3) continue;
                    int q = k + j * o;
                    int r = m + j * p;
                    ChunkPos lv2 = this.getStartChunk(config, worldSeed, lv, q, r);
                    Chunk lv3 = arg.getChunk(lv2.x, lv2.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart<?> lv4 = arg2.getStructureStart(ChunkSectionPos.from(lv3.getPos(), 0), this, lv3);
                    if (lv4 != null && lv4.hasChildren()) {
                        if (skipExistingChunks && lv4.isInExistingChunk()) {
                            lv4.incrementReferences();
                            return lv4.getPos();
                        }
                        if (!skipExistingChunks) {
                            return lv4.getPos();
                        }
                    }
                    if (n == 0) break;
                }
                if (n == 0) continue block0;
            }
        }
        return null;
    }

    protected boolean isUniformDistribution() {
        return true;
    }

    public final ChunkPos getStartChunk(StructureConfig config, long worldSeed, ChunkRandom placementRandom, int chunkX, int chunkY) {
        int s;
        int r;
        int k = config.getSpacing();
        int m = config.getSeparation();
        int n = Math.floorDiv(chunkX, k);
        int o = Math.floorDiv(chunkY, k);
        placementRandom.setRegionSeed(worldSeed, n, o, config.getSalt());
        if (this.isUniformDistribution()) {
            int p = placementRandom.nextInt(k - m);
            int q = placementRandom.nextInt(k - m);
        } else {
            r = (placementRandom.nextInt(k - m) + placementRandom.nextInt(k - m)) / 2;
            s = (placementRandom.nextInt(k - m) + placementRandom.nextInt(k - m)) / 2;
        }
        return new ChunkPos(n * k + r, o * k + s);
    }

    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long worldSeed, ChunkRandom arg3, int chunkX, int chunkZ, Biome arg4, ChunkPos chunkPos, C arg6) {
        return true;
    }

    private StructureStart<C> createStart(int chunkX, int chunkZ, BlockBox boundingBox, int referenceCount, long worldSeed) {
        return this.getStructureStartFactory().create(this, chunkX, chunkZ, boundingBox, referenceCount, worldSeed);
    }

    public StructureStart<?> tryPlaceStart(class_5455 arg, ChunkGenerator arg2, BiomeSource arg3, StructureManager arg4, long worldSeed, ChunkPos arg5, Biome arg6, int referenceCount, ChunkRandom arg7, StructureConfig arg8, C arg9) {
        ChunkPos lv = this.getStartChunk(arg8, worldSeed, arg7, arg5.x, arg5.z);
        if (arg5.x == lv.x && arg5.z == lv.z && this.shouldStartAt(arg2, arg3, worldSeed, arg7, arg5.x, arg5.z, arg6, lv, arg9)) {
            StructureStart<C> lv2 = this.createStart(arg5.x, arg5.z, BlockBox.empty(), referenceCount, worldSeed);
            lv2.init(arg, arg2, arg4, arg5.x, arg5.z, arg6, arg9);
            if (lv2.hasChildren()) {
                return lv2;
            }
        }
        return StructureStart.DEFAULT;
    }

    public abstract StructureStartFactory<C> getStructureStartFactory();

    public String getName() {
        return (String)STRUCTURES.inverse().get((Object)this);
    }

    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return Collections.emptyList();
    }

    public List<Biome.SpawnEntry> getCreatureSpawns() {
        return Collections.emptyList();
    }

    public static interface StructureStartFactory<C extends FeatureConfig> {
        public StructureStart<C> create(StructureFeature<C> var1, int var2, int var3, BlockBox var4, int var5, long var6);
    }
}

