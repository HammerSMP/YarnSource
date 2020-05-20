/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator {
    protected final BiomeSource biomeSource;
    private final ChunkGeneratorConfig config;

    public ChunkGenerator(BiomeSource arg, ChunkGeneratorConfig arg2) {
        this.biomeSource = arg;
        this.config = arg2;
    }

    @Environment(value=EnvType.CLIENT)
    public abstract ChunkGenerator create(long var1);

    public void populateBiomes(Chunk arg) {
        ChunkPos lv = arg.getPos();
        ((ProtoChunk)arg).setBiomes(new BiomeArray(lv, this.biomeSource));
    }

    protected Biome getDecorationBiome(BiomeAccess arg, BlockPos arg2) {
        return arg.getBiome(arg2);
    }

    public void carve(long l, BiomeAccess arg, Chunk arg22, GenerationStep.Carver arg3) {
        ChunkRandom lv = new ChunkRandom();
        int i = 8;
        ChunkPos lv2 = arg22.getPos();
        int j = lv2.x;
        int k = lv2.z;
        Biome lv3 = this.getDecorationBiome(arg, lv2.getCenterBlockPos());
        BitSet bitSet = arg22.getCarvingMask(arg3);
        for (int m = j - 8; m <= j + 8; ++m) {
            for (int n = k - 8; n <= k + 8; ++n) {
                List<ConfiguredCarver<?>> list = lv3.getCarversForStep(arg3);
                ListIterator<ConfiguredCarver<?>> listIterator = list.listIterator();
                while (listIterator.hasNext()) {
                    int o = listIterator.nextIndex();
                    ConfiguredCarver<?> lv4 = listIterator.next();
                    lv.setCarverSeed(l + (long)o, m, n);
                    if (!lv4.shouldCarve(lv, m, n)) continue;
                    lv4.carve(arg22, arg2 -> this.getDecorationBiome(arg, (BlockPos)arg2), lv, this.getSeaLevel(), m, n, j, k, bitSet);
                }
            }
        }
    }

    @Nullable
    public BlockPos locateStructure(ServerWorld arg, String string, BlockPos arg2, int i, boolean bl) {
        StructureFeature lv = (StructureFeature)Feature.STRUCTURES.get((Object)string.toLowerCase(Locale.ROOT));
        if (lv != null) {
            return lv.locateStructure(arg, this, arg2, i, bl);
        }
        return null;
    }

    public void generateFeatures(ChunkRegion arg, StructureAccessor arg2) {
        int i = arg.getCenterChunkX();
        int j = arg.getCenterChunkZ();
        int k = i * 16;
        int l = j * 16;
        BlockPos lv = new BlockPos(k, 0, l);
        Biome lv2 = this.getDecorationBiome(arg.getBiomeAccess(), lv.add(8, 8, 8));
        ChunkRandom lv3 = new ChunkRandom();
        long m = lv3.setPopulationSeed(arg.getSeed(), k, l);
        for (GenerationStep.Feature lv4 : GenerationStep.Feature.values()) {
            try {
                lv2.generateFeatureStep(lv4, arg2, this, arg, m, lv3, lv);
            }
            catch (Exception exception) {
                CrashReport lv5 = CrashReport.create(exception, "Biome decoration");
                lv5.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Step", (Object)lv4).add("Seed", m).add("Biome", Registry.BIOME.getId(lv2));
                throw new CrashException(lv5);
            }
        }
    }

    public abstract void buildSurface(ChunkRegion var1, Chunk var2);

    public void populateEntities(ChunkRegion arg) {
    }

    public ChunkGeneratorConfig getConfig() {
        return this.config;
    }

    public int getSpawnHeight() {
        return 64;
    }

    public void spawnEntities(ServerWorld arg, boolean bl, boolean bl2) {
    }

    public boolean hasStructure(Biome arg, StructureFeature<? extends FeatureConfig> arg2) {
        return arg.hasStructureFeature(arg2);
    }

    @Nullable
    public <C extends FeatureConfig> C getStructureConfig(Biome arg, StructureFeature<C> arg2) {
        return arg.getStructureFeatureConfig(arg2);
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }

    public int getMaxY() {
        return 256;
    }

    public List<Biome.SpawnEntry> getEntitySpawnList(Biome arg, StructureAccessor arg2, SpawnGroup arg3, BlockPos arg4) {
        return arg.getEntitySpawnList(arg3);
    }

    public void setStructureStarts(StructureAccessor arg, BiomeAccess arg2, Chunk arg3, ChunkGenerator arg4, StructureManager arg5, long l) {
        for (StructureFeature lv : Feature.STRUCTURES.values()) {
            if (!arg4.hasStructure(lv)) continue;
            StructureStart lv2 = arg.getStructureStart(ChunkSectionPos.from(arg3.getPos(), 0), lv, arg3);
            int i = lv2 != null ? lv2.getReferences() : 0;
            ChunkRandom lv3 = new ChunkRandom();
            ChunkPos lv4 = arg3.getPos();
            StructureStart lv5 = StructureStart.DEFAULT;
            Biome lv6 = arg4.getDecorationBiome(arg2, new BlockPos(lv4.getStartX() + 9, 0, lv4.getStartZ() + 9));
            if (lv.method_27217(arg2, arg4, l, lv3, lv4.x, lv4.z, lv6)) {
                StructureStart lv7 = lv.getStructureStartFactory().create(lv, lv4.x, lv4.z, BlockBox.empty(), i, l);
                lv7.init(this, arg5, lv4.x, lv4.z, lv6);
                lv5 = lv7.hasChildren() ? lv7 : StructureStart.DEFAULT;
            }
            arg.setStructureStart(ChunkSectionPos.from(arg3.getPos(), 0), lv, lv5, arg3);
        }
    }

    public boolean hasStructure(StructureFeature<?> arg) {
        return this.getBiomeSource().hasStructureFeature(arg);
    }

    public void addStructureReferences(WorldAccess arg, StructureAccessor arg2, Chunk arg3) {
        int i = 8;
        int j = arg3.getPos().x;
        int k = arg3.getPos().z;
        int l = j << 4;
        int m = k << 4;
        ChunkSectionPos lv = ChunkSectionPos.from(arg3.getPos(), 0);
        for (int n = j - 8; n <= j + 8; ++n) {
            for (int o = k - 8; o <= k + 8; ++o) {
                long p = ChunkPos.toLong(n, o);
                for (Map.Entry<String, StructureStart> entry : arg.getChunk(n, o).getStructureStarts().entrySet()) {
                    StructureStart lv2 = entry.getValue();
                    if (lv2 == StructureStart.DEFAULT || !lv2.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) continue;
                    arg2.addStructureReference(lv, lv2.getFeature(), p, arg3);
                    DebugInfoSender.sendStructureStart(arg, lv2);
                }
            }
        }
    }

    public abstract void populateNoise(WorldAccess var1, StructureAccessor var2, Chunk var3);

    public int getSeaLevel() {
        return 63;
    }

    public abstract int getHeight(int var1, int var2, Heightmap.Type var3);

    public abstract BlockView getColumnSample(int var1, int var2);

    public int getHeightOnGround(int i, int j, Heightmap.Type arg) {
        return this.getHeight(i, j, arg);
    }

    public int getHeightInGround(int i, int j, Heightmap.Type arg) {
        return this.getHeight(i, j, arg) - 1;
    }
}

