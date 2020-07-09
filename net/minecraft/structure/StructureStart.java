/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.class_5455;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart<C extends FeatureConfig> {
    public static final StructureStart<?> DEFAULT = new StructureStart<MineshaftFeatureConfig>(StructureFeature.MINESHAFT, 0, 0, BlockBox.empty(), 0, 0L){

        @Override
        public void init(class_5455 arg, ChunkGenerator arg2, StructureManager arg3, int i, int j, Biome arg4, MineshaftFeatureConfig arg5) {
        }
    };
    private final StructureFeature<C> feature;
    protected final List<StructurePiece> children = Lists.newArrayList();
    protected BlockBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private int references;
    protected final ChunkRandom random;

    public StructureStart(StructureFeature<C> arg, int i, int j, BlockBox arg2, int k, long l) {
        this.feature = arg;
        this.chunkX = i;
        this.chunkZ = j;
        this.references = k;
        this.random = new ChunkRandom();
        this.random.setCarverSeed(l, i, j);
        this.boundingBox = arg2;
    }

    public abstract void init(class_5455 var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, C var7);

    public BlockBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getChildren() {
        return this.children;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generateStructure(ServerWorldAccess arg, StructureAccessor arg2, ChunkGenerator arg3, Random random, BlockBox arg4, ChunkPos arg5) {
        List<StructurePiece> list = this.children;
        synchronized (list) {
            if (this.children.isEmpty()) {
                return;
            }
            BlockBox lv = this.children.get((int)0).boundingBox;
            Vec3i lv2 = lv.getCenter();
            BlockPos lv3 = new BlockPos(lv2.getX(), lv.minY, lv2.getZ());
            Iterator<StructurePiece> iterator = this.children.iterator();
            while (iterator.hasNext()) {
                StructurePiece lv4 = iterator.next();
                if (!lv4.getBoundingBox().intersects(arg4) || lv4.generate(arg, arg2, arg3, random, arg4, arg5, lv3)) continue;
                iterator.remove();
            }
            this.setBoundingBoxFromChildren();
        }
    }

    protected void setBoundingBoxFromChildren() {
        this.boundingBox = BlockBox.empty();
        for (StructurePiece lv : this.children) {
            this.boundingBox.encompass(lv.getBoundingBox());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompoundTag toTag(int i, int j) {
        CompoundTag lv = new CompoundTag();
        if (!this.hasChildren()) {
            lv.putString("id", "INVALID");
            return lv;
        }
        lv.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
        lv.putInt("ChunkX", i);
        lv.putInt("ChunkZ", j);
        lv.putInt("references", this.references);
        lv.put("BB", this.boundingBox.toNbt());
        ListTag lv2 = new ListTag();
        List<StructurePiece> list = this.children;
        synchronized (list) {
            for (StructurePiece lv3 : this.children) {
                lv2.add(lv3.getTag());
            }
        }
        lv.put("Children", lv2);
        return lv;
    }

    protected void method_14978(int i, Random random, int j) {
        int k = i - j;
        int l = this.boundingBox.getBlockCountY() + 1;
        if (l < k) {
            l += random.nextInt(k - l);
        }
        int m = l - this.boundingBox.maxY;
        this.boundingBox.offset(0, m, 0);
        for (StructurePiece lv : this.children) {
            lv.translate(0, m, 0);
        }
    }

    protected void method_14976(Random random, int i, int j) {
        int m;
        int k = j - i + 1 - this.boundingBox.getBlockCountY();
        if (k > 1) {
            int l = i + random.nextInt(k);
        } else {
            m = i;
        }
        int n = m - this.boundingBox.minY;
        this.boundingBox.offset(0, n, 0);
        for (StructurePiece lv : this.children) {
            lv.translate(0, n, 0);
        }
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public BlockPos getPos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }

    public boolean isInExistingChunk() {
        return this.references < this.getReferenceCountToBeInExistingChunk();
    }

    public void incrementReferences() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getReferenceCountToBeInExistingChunk() {
        return 1;
    }

    public StructureFeature<?> getFeature() {
        return this.feature;
    }
}

