/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5455;
import net.minecraft.class_5470;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
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
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator {
    public static final Codec<ChunkGenerator> field_24746;
    protected final BiomeSource biomeSource;
    protected final BiomeSource field_24747;
    private final StructuresConfig config;
    private final long field_24748;
    private final List<ChunkPos> field_24749 = Lists.newArrayList();

    public ChunkGenerator(BiomeSource arg, StructuresConfig arg2) {
        this(arg, arg, arg2, 0L);
    }

    public ChunkGenerator(BiomeSource arg, BiomeSource arg2, StructuresConfig arg3, long l) {
        this.biomeSource = arg;
        this.field_24747 = arg2;
        this.config = arg3;
        this.field_24748 = l;
    }

    private void method_28509() {
        if (!this.field_24749.isEmpty()) {
            return;
        }
        StrongholdConfig lv = this.config.getStronghold();
        if (lv == null || lv.getCount() == 0) {
            return;
        }
        ArrayList list = Lists.newArrayList();
        for (Biome lv2 : this.biomeSource.getBiomes()) {
            if (!lv2.hasStructureFeature(StructureFeature.STRONGHOLD)) continue;
            list.add(lv2);
        }
        int i = lv.getDistance();
        int j = lv.getCount();
        int k = lv.getSpread();
        Random random = new Random();
        random.setSeed(this.field_24748);
        double d = random.nextDouble() * Math.PI * 2.0;
        int l = 0;
        int m = 0;
        for (int n = 0; n < j; ++n) {
            int p;
            double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
            int o = (int)Math.round(Math.cos(d) * e);
            BlockPos lv3 = this.biomeSource.locateBiome((o << 4) + 8, 0, ((p = (int)Math.round(Math.sin(d) * e)) << 4) + 8, 112, list, random);
            if (lv3 != null) {
                o = lv3.getX() >> 4;
                p = lv3.getZ() >> 4;
            }
            this.field_24749.add(new ChunkPos(o, p));
            d += Math.PI * 2 / (double)k;
            if (++l != k) continue;
            l = 0;
            k += 2 * k / (++m + 1);
            k = Math.min(k, j - n);
            d += random.nextDouble() * Math.PI * 2.0;
        }
    }

    protected abstract Codec<? extends ChunkGenerator> method_28506();

    @Environment(value=EnvType.CLIENT)
    public abstract ChunkGenerator withSeed(long var1);

    public void populateBiomes(Registry<Biome> arg, Chunk arg2) {
        ChunkPos lv = arg2.getPos();
        ((ProtoChunk)arg2).setBiomes(new BiomeArray(arg, lv, this.field_24747));
    }

    public void carve(long l, BiomeAccess arg, Chunk arg2, GenerationStep.Carver arg3) {
        BiomeAccess lv = arg.withSource(this.biomeSource);
        ChunkRandom lv2 = new ChunkRandom();
        int i = 8;
        ChunkPos lv3 = arg2.getPos();
        int j = lv3.x;
        int k = lv3.z;
        Biome lv4 = this.biomeSource.getBiomeForNoiseGen(lv3.x << 2, 0, lv3.z << 2);
        BitSet bitSet = ((ProtoChunk)arg2).getOrCreateCarvingMask(arg3);
        for (int m = j - 8; m <= j + 8; ++m) {
            for (int n = k - 8; n <= k + 8; ++n) {
                List<Supplier<ConfiguredCarver<?>>> list = lv4.getCarversForStep(arg3);
                ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();
                while (listIterator.hasNext()) {
                    int o = listIterator.nextIndex();
                    ConfiguredCarver<?> lv5 = listIterator.next().get();
                    lv2.setCarverSeed(l + (long)o, m, n);
                    if (!lv5.shouldCarve(lv2, m, n)) continue;
                    lv5.carve(arg2, lv::getBiome, lv2, this.getSeaLevel(), m, n, j, k, bitSet);
                }
            }
        }
    }

    @Nullable
    public BlockPos locateStructure(ServerWorld arg, StructureFeature<?> arg2, BlockPos arg3, int i, boolean bl) {
        if (!this.biomeSource.hasStructureFeature(arg2)) {
            return null;
        }
        if (arg2 == StructureFeature.STRONGHOLD) {
            this.method_28509();
            BlockPos lv = null;
            double d = Double.MAX_VALUE;
            BlockPos.Mutable lv2 = new BlockPos.Mutable();
            for (ChunkPos lv3 : this.field_24749) {
                lv2.set((lv3.x << 4) + 8, 32, (lv3.z << 4) + 8);
                double e = lv2.getSquaredDistance(arg3);
                if (lv == null) {
                    lv = new BlockPos(lv2);
                    d = e;
                    continue;
                }
                if (!(e < d)) continue;
                lv = new BlockPos(lv2);
                d = e;
            }
            return lv;
        }
        return arg2.locateStructure(arg, arg.getStructureAccessor(), arg3, i, bl, arg.getSeed(), this.config.method_28600(arg2));
    }

    public void generateFeatures(ChunkRegion arg, StructureAccessor arg2) {
        int i = arg.getCenterChunkX();
        int j = arg.getCenterChunkZ();
        int k = i * 16;
        int l = j * 16;
        BlockPos lv = new BlockPos(k, 0, l);
        Biome lv2 = this.biomeSource.getBiomeForNoiseGen((i << 2) + 2, 2, (j << 2) + 2);
        ChunkRandom lv3 = new ChunkRandom();
        long m = lv3.setPopulationSeed(arg.getSeed(), k, l);
        try {
            lv2.generateFeatureStep(arg2, this, arg, m, lv3, lv);
        }
        catch (Exception exception) {
            CrashReport lv4 = CrashReport.create(exception, "Biome decoration");
            lv4.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Seed", m).add("Biome", lv2);
            throw new CrashException(lv4);
        }
    }

    public abstract void buildSurface(ChunkRegion var1, Chunk var2);

    public void populateEntities(ChunkRegion arg) {
    }

    public StructuresConfig getConfig() {
        return this.config;
    }

    public int getSpawnHeight() {
        return 64;
    }

    public BiomeSource getBiomeSource() {
        return this.field_24747;
    }

    public int getMaxY() {
        return 256;
    }

    public List<Biome.SpawnEntry> getEntitySpawnList(Biome arg, StructureAccessor arg2, SpawnGroup arg3, BlockPos arg4) {
        return arg.getEntitySpawnList(arg3);
    }

    public void setStructureStarts(class_5455 arg, StructureAccessor arg2, Chunk arg3, StructureManager arg4, long l) {
        ChunkPos lv = arg3.getPos();
        Biome lv2 = this.biomeSource.getBiomeForNoiseGen((lv.x << 2) + 2, 0, (lv.z << 2) + 2);
        this.method_28508(class_5470.STRONGHOLD, arg, arg2, arg3, arg4, l, lv, lv2);
        for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : lv2.method_28413()) {
            this.method_28508(supplier.get(), arg, arg2, arg3, arg4, l, lv, lv2);
        }
    }

    private void method_28508(ConfiguredStructureFeature<?, ?> arg, class_5455 arg2, StructureAccessor arg3, Chunk arg4, StructureManager arg5, long l, ChunkPos arg6, Biome arg7) {
        StructureStart<?> lv = arg3.getStructureStart(ChunkSectionPos.from(arg4.getPos(), 0), (StructureFeature<?>)arg.feature, arg4);
        int i = lv != null ? lv.getReferences() : 0;
        StructureStart<?> lv2 = arg.method_28622(arg2, this, this.biomeSource, arg5, l, arg6, arg7, i, this.config.method_28600((StructureFeature<?>)arg.feature));
        arg3.setStructureStart(ChunkSectionPos.from(arg4.getPos(), 0), (StructureFeature<?>)arg.feature, lv2, arg4);
    }

    public void addStructureReferences(ServerWorldAccess arg, StructureAccessor arg2, Chunk arg3) {
        int i = 8;
        int j = arg3.getPos().x;
        int k = arg3.getPos().z;
        int l = j << 4;
        int m = k << 4;
        ChunkSectionPos lv = ChunkSectionPos.from(arg3.getPos(), 0);
        for (int n = j - 8; n <= j + 8; ++n) {
            for (int o = k - 8; o <= k + 8; ++o) {
                long p = ChunkPos.toLong(n, o);
                for (StructureStart<?> lv2 : arg.getChunk(n, o).getStructureStarts().values()) {
                    try {
                        if (lv2 == StructureStart.DEFAULT || !lv2.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) continue;
                        arg2.addStructureReference(lv, lv2.getFeature(), p, arg3);
                        DebugInfoSender.sendStructureStart(arg, lv2);
                    }
                    catch (Exception exception) {
                        CrashReport lv3 = CrashReport.create(exception, "Generating structure reference");
                        CrashReportSection lv4 = lv3.addElement("Structure");
                        lv4.add("Id", () -> Registry.STRUCTURE_FEATURE.getId(lv2.getFeature()).toString());
                        lv4.add("Name", () -> lv2.getFeature().getName());
                        lv4.add("Class", () -> lv2.getFeature().getClass().getCanonicalName());
                        throw new CrashException(lv3);
                    }
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

    public boolean method_28507(ChunkPos arg) {
        this.method_28509();
        return this.field_24749.contains(arg);
    }

    static {
        Registry.register(Registry.CHUNK_GENERATOR, "noise", SurfaceChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.field_24769);
        Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.field_24768);
        field_24746 = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::method_28506, Function.identity());
    }
}

