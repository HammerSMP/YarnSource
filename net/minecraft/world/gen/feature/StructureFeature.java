/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
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
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
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
    public static final StructureFeature<DefaultFeatureConfig> PILLAGER_OUTPOST = StructureFeature.register("Pillager_Outpost", new PillagerOutpostFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
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
    public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = StructureFeature.register("Buried_Treasure", new BuriedTreasureFeature(BuriedTreasureFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_STRUCTURES);
    public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = StructureFeature.register("Village", new VillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final StructureFeature<DefaultFeatureConfig> NETHER_FOSSIL = StructureFeature.register("Nether_Fossil", new NetherFossilFeature(DefaultFeatureConfig.CODEC), GenerationStep.Feature.UNDERGROUND_DECORATION);
    public static final StructureFeature<BastionRemnantFeatureConfig> BASTION_REMNANT = StructureFeature.register("Bastion_Remnant", new BastionRemnantFeature(BastionRemnantFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);
    public static final List<StructureFeature<?>> field_24861 = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
    private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> codec;

    private static <F extends StructureFeature<?>> F register(String string, F arg, GenerationStep.Feature arg2) {
        STRUCTURES.put((Object)string.toLowerCase(Locale.ROOT), arg);
        STRUCTURE_TO_GENERATION_STEP.put(arg, arg2);
        return (F)Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), arg);
    }

    public StructureFeature(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap(arg -> new ConfiguredStructureFeature<FeatureConfig, StructureFeature>(this, (FeatureConfig)arg), arg -> arg.config).codec();
    }

    public GenerationStep.Feature method_28663() {
        return STRUCTURE_TO_GENERATION_STEP.get(this);
    }

    public static void method_28664() {
    }

    @Nullable
    public static StructureStart<?> method_28660(StructureManager arg, CompoundTag arg2, long l) {
        String string = arg2.getString("id");
        if ("INVALID".equals(string)) {
            return StructureStart.DEFAULT;
        }
        StructureFeature<?> lv = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
        if (lv == null) {
            LOGGER.error("Unknown feature id: {}", (Object)string);
            return null;
        }
        int i = arg2.getInt("ChunkX");
        int j = arg2.getInt("ChunkZ");
        int k = arg2.getInt("references");
        BlockBox lv2 = arg2.contains("BB") ? new BlockBox(arg2.getIntArray("BB")) : BlockBox.empty();
        ListTag lv3 = arg2.getList("Children", 10);
        try {
            StructureStart<?> lv4 = super.method_28656(i, j, lv2, k, l);
            for (int m = 0; m < lv3.size(); ++m) {
                CompoundTag lv5 = lv3.getCompound(m);
                String string2 = lv5.getString("id");
                StructurePieceType lv6 = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
                if (lv6 == null) {
                    LOGGER.error("Unknown structure piece id: {}", (Object)string2);
                    continue;
                }
                try {
                    StructurePiece lv7 = lv6.load(arg, lv5);
                    lv4.getChildren().add(lv7);
                    continue;
                }
                catch (Exception exception) {
                    LOGGER.error("Exception loading structure piece with id {}", (Object)string2, (Object)exception);
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

    public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configure(C arg) {
        return new ConfiguredStructureFeature<C, StructureFeature>(this, arg);
    }

    @Nullable
    public BlockPos locateStructure(WorldView arg, StructureAccessor arg2, BlockPos arg3, int i, boolean bl, long l, StructureConfig arg4) {
        int j = arg4.getSpacing();
        int k = arg3.getX() >> 4;
        int m = arg3.getZ() >> 4;
        ChunkRandom lv = new ChunkRandom();
        block0: for (int n = 0; n <= i; ++n) {
            for (int o = -n; o <= n; ++o) {
                boolean bl2 = o == -n || o == n;
                for (int p = -n; p <= n; ++p) {
                    boolean bl3;
                    boolean bl4 = bl3 = p == -n || p == n;
                    if (!bl2 && !bl3) continue;
                    int q = k + j * o;
                    int r = m + j * p;
                    ChunkPos lv2 = this.method_27218(arg4, l, lv, q, r);
                    Chunk lv3 = arg.getChunk(lv2.x, lv2.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart<?> lv4 = arg2.getStructureStart(ChunkSectionPos.from(lv3.getPos(), 0), this, lv3);
                    if (lv4 != null && lv4.hasChildren()) {
                        if (bl && lv4.isInExistingChunk()) {
                            lv4.incrementReferences();
                            return lv4.getPos();
                        }
                        if (!bl) {
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

    protected boolean method_27219() {
        return true;
    }

    public final ChunkPos method_27218(StructureConfig arg, long l, ChunkRandom arg2, int i, int j) {
        int s;
        int r;
        int k = arg.getSpacing();
        int m = arg.getSeparation();
        int n = Math.floorDiv(i, k);
        int o = Math.floorDiv(j, k);
        arg2.setRegionSeed(l, n, o, arg.getSalt());
        if (this.method_27219()) {
            int p = arg2.nextInt(k - m);
            int q = arg2.nextInt(k - m);
        } else {
            r = (arg2.nextInt(k - m) + arg2.nextInt(k - m)) / 2;
            s = (arg2.nextInt(k - m) + arg2.nextInt(k - m)) / 2;
        }
        return new ChunkPos(n * k + r, o * k + s);
    }

    protected boolean shouldStartAt(ChunkGenerator arg, BiomeSource arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5, C arg6) {
        return true;
    }

    private StructureStart<C> method_28656(int i, int j, BlockBox arg, int k, long l) {
        return this.getStructureStartFactory().create(this, i, j, arg, k, l);
    }

    public StructureStart<?> method_28657(ChunkGenerator arg, BiomeSource arg2, StructureManager arg3, long l, ChunkPos arg4, Biome arg5, int i, ChunkRandom arg6, StructureConfig arg7, C arg8) {
        ChunkPos lv = this.method_27218(arg7, l, arg6, arg4.x, arg4.z);
        if (arg4.x == lv.x && arg4.z == lv.z && this.shouldStartAt(arg, arg2, l, arg6, arg4.x, arg4.z, arg5, lv, arg8)) {
            StructureStart<C> lv2 = this.method_28656(arg4.x, arg4.z, BlockBox.empty(), i, l);
            lv2.init(arg, arg3, arg4.x, arg4.z, arg5, arg8);
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

