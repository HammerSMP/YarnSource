/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class StrongholdFeature
extends StructureFeature<DefaultFeatureConfig> {
    private boolean stateStillValid;
    private ChunkPos[] startPositions;
    private final List<StructureStart> starts = Lists.newArrayList();
    private long lastSeed;

    public StrongholdFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean method_27217(BiomeAccess arg, ChunkGenerator arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4) {
        ChunkPos lv = this.method_27218(arg2.getConfig(), l, arg3, i, j);
        return this.shouldStartAt(arg, arg2, l, arg3, i, j, arg4, lv);
    }

    @Override
    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        if (this.lastSeed != l) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(arg2, l);
            this.stateStillValid = true;
        }
        for (ChunkPos lv : this.startPositions) {
            if (i != lv.x || j != lv.z) continue;
            return true;
        }
        return false;
    }

    private void invalidateState() {
        this.stateStillValid = false;
        this.startPositions = null;
        this.starts.clear();
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Stronghold";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    @Nullable
    public BlockPos locateStructure(ServerWorld arg, ChunkGenerator arg2, BlockPos arg3, int i, boolean bl) {
        if (!arg2.hasStructure(this)) {
            return null;
        }
        if (this.lastSeed != arg.getSeed()) {
            this.invalidateState();
        }
        if (!this.stateStillValid) {
            this.initialize(arg2, arg.getSeed());
            this.stateStillValid = true;
        }
        BlockPos lv = null;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        double d = Double.MAX_VALUE;
        for (ChunkPos lv3 : this.startPositions) {
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

    private void initialize(ChunkGenerator arg, long l) {
        this.lastSeed = l;
        ArrayList list = Lists.newArrayList();
        for (Biome lv : Registry.BIOME) {
            if (lv == null || !arg.hasStructure(lv, this)) continue;
            list.add(lv);
        }
        int i = arg.getConfig().getStrongholdSpacing();
        int j = arg.getConfig().getStrongholdCount();
        int k = arg.getConfig().getStrongholdSpread();
        this.startPositions = new ChunkPos[j];
        int m = 0;
        for (StructureStart lv2 : this.starts) {
            if (m >= this.startPositions.length) continue;
            this.startPositions[m++] = new ChunkPos(lv2.getChunkX(), lv2.getChunkZ());
        }
        Random random = new Random();
        random.setSeed(l);
        double d = random.nextDouble() * Math.PI * 2.0;
        int n = m;
        if (n < this.startPositions.length) {
            int o = 0;
            int p = 0;
            for (int q = 0; q < this.startPositions.length; ++q) {
                double e = (double)(4 * i + i * p * 6) + (random.nextDouble() - 0.5) * ((double)i * 2.5);
                int r = (int)Math.round(Math.cos(d) * e);
                int s = (int)Math.round(Math.sin(d) * e);
                BlockPos lv3 = arg.getBiomeSource().locateBiome((r << 4) + 8, arg.getSeaLevel(), (s << 4) + 8, 112, list, random);
                if (lv3 != null) {
                    r = lv3.getX() >> 4;
                    s = lv3.getZ() >> 4;
                }
                if (q >= n) {
                    this.startPositions[q] = new ChunkPos(r, s);
                }
                d += Math.PI * 2 / (double)k;
                if (++o != k) continue;
                o = 0;
                k += 2 * k / (++p + 1);
                k = Math.min(k, this.startPositions.length - q);
                d += random.nextDouble() * Math.PI * 2.0;
            }
        }
    }

    public static class Start
    extends StructureStart {
        private final long field_24559;

        public Start(StructureFeature<?> arg, int i, int j, BlockBox arg2, int k, long l) {
            super(arg, i, j, arg2, k, l);
            this.field_24559 = l;
        }

        @Override
        public void init(ChunkGenerator arg, StructureManager arg2, int i, int j, Biome arg3) {
            StrongholdGenerator.Start lv;
            int k = 0;
            do {
                this.children.clear();
                this.boundingBox = BlockBox.empty();
                this.random.setCarverSeed(this.field_24559 + (long)k++, i, j);
                StrongholdGenerator.init();
                lv = new StrongholdGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
                this.children.add(lv);
                lv.placeJigsaw(lv, this.children, this.random);
                List<StructurePiece> list = lv.field_15282;
                while (!list.isEmpty()) {
                    int l = this.random.nextInt(list.size());
                    StructurePiece lv2 = list.remove(l);
                    lv2.placeJigsaw(lv, this.children, this.random);
                }
                this.setBoundingBoxFromChildren();
                this.method_14978(arg.getSeaLevel(), this.random, 10);
            } while (this.children.isEmpty() || lv.field_15283 == null);
            ((StrongholdFeature)this.getFeature()).starts.add(this);
        }
    }
}

