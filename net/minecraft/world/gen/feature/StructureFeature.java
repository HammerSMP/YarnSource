/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig>
extends Feature<C> {
    private static final Logger LOGGER = LogManager.getLogger();

    public StructureFeature(Function<Dynamic<?>, ? extends C> function) {
        super(function);
    }

    @Override
    public ConfiguredFeature<C, ? extends StructureFeature<C>> configure(C arg) {
        return new ConfiguredFeature<C, StructureFeature>(this, arg);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockPos arg42, C arg5) {
        if (!arg2.method_27834()) {
            return false;
        }
        int i = arg42.getX() >> 4;
        int j = arg42.getZ() >> 4;
        int k = i << 4;
        int l = j << 4;
        return arg2.getStructuresWithChildren(ChunkSectionPos.from(arg42), this).map(arg4 -> {
            arg4.generateStructure(arg, arg2, arg3, random, new BlockBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
            return null;
        }).count() != 0L;
    }

    protected StructureStart isInsideStructure(StructureAccessor arg, BlockPos arg23, boolean bl) {
        return arg.getStructuresWithChildren(ChunkSectionPos.from(arg23), this).filter(arg2 -> arg2.getBoundingBox().contains(arg23)).filter(arg22 -> !bl || arg22.getChildren().stream().anyMatch(arg2 -> arg2.getBoundingBox().contains(arg23))).findFirst().orElse(StructureStart.DEFAULT);
    }

    public boolean isApproximatelyInsideStructure(StructureAccessor arg, BlockPos arg2) {
        return this.isInsideStructure(arg, arg2, false).hasChildren();
    }

    public boolean isInsideStructure(StructureAccessor arg, BlockPos arg2) {
        return this.isInsideStructure(arg, arg2, true).hasChildren();
    }

    @Nullable
    public BlockPos locateStructure(ServerWorld arg, ChunkGenerator arg2, BlockPos arg3, int i, boolean bl) {
        if (!arg2.hasStructure(this)) {
            return null;
        }
        StructureAccessor lv = arg.getStructureAccessor();
        int j = this.getSpacing(arg2.getConfig());
        int k = arg3.getX() >> 4;
        int l = arg3.getZ() >> 4;
        ChunkRandom lv2 = new ChunkRandom();
        block0: for (int m = 0; m <= i; ++m) {
            for (int n = -m; n <= m; ++n) {
                boolean bl2 = n == -m || n == m;
                for (int o = -m; o <= m; ++o) {
                    boolean bl3;
                    boolean bl4 = bl3 = o == -m || o == m;
                    if (!bl2 && !bl3) continue;
                    int p = k + j * n;
                    int q = l + j * o;
                    ChunkPos lv3 = this.method_27218(arg2.getConfig(), arg.getSeed(), lv2, p, q);
                    Chunk lv4 = arg.getChunk(lv3.x, lv3.z, ChunkStatus.STRUCTURE_STARTS);
                    StructureStart lv5 = lv.getStructureStart(ChunkSectionPos.from(lv4.getPos(), 0), this, lv4);
                    if (lv5 != null && lv5.hasChildren()) {
                        if (bl && lv5.isInExistingChunk()) {
                            lv5.incrementReferences();
                            return lv5.getPos();
                        }
                        if (!bl) {
                            return lv5.getPos();
                        }
                    }
                    if (m == 0) break;
                }
                if (m == 0) continue block0;
            }
        }
        return null;
    }

    protected int getSpacing(ChunkGeneratorConfig arg) {
        return 1;
    }

    protected int getSeparation(ChunkGeneratorConfig arg) {
        return 0;
    }

    protected int getSeedModifier(ChunkGeneratorConfig arg) {
        return 0;
    }

    protected boolean method_27219() {
        return true;
    }

    public final ChunkPos method_27218(ChunkGeneratorConfig arg, long l, ChunkRandom arg2, int i, int j) {
        int s;
        int r;
        int k = this.getSpacing(arg);
        int m = this.getSeparation(arg);
        int n = Math.floorDiv(i, k);
        int o = Math.floorDiv(j, k);
        arg2.setRegionSeed(l, n, o, this.getSeedModifier(arg));
        if (this.method_27219()) {
            int p = arg2.nextInt(k - m);
            int q = arg2.nextInt(k - m);
        } else {
            r = (arg2.nextInt(k - m) + arg2.nextInt(k - m)) / 2;
            s = (arg2.nextInt(k - m) + arg2.nextInt(k - m)) / 2;
        }
        return new ChunkPos(n * k + r, o * k + s);
    }

    public boolean method_27217(BiomeAccess arg, ChunkGenerator arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4) {
        ChunkPos lv = this.method_27218(arg2.getConfig(), l, arg3, i, j);
        return i == lv.x && j == lv.z && arg2.hasStructure(arg4, this) && this.shouldStartAt(arg, arg2, l, arg3, i, j, arg4, lv);
    }

    protected boolean shouldStartAt(BiomeAccess arg, ChunkGenerator arg2, long l, ChunkRandom arg3, int i, int j, Biome arg4, ChunkPos arg5) {
        return true;
    }

    public abstract StructureStartFactory getStructureStartFactory();

    public abstract String getName();

    public abstract int getRadius();

    public static interface StructureStartFactory {
        public StructureStart create(StructureFeature<?> var1, int var2, int var3, BlockBox var4, int var5, long var6);
    }
}

