/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class StructurePlacementData {
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private BlockPos position = BlockPos.ORIGIN;
    private boolean ignoreEntities;
    @Nullable
    private ChunkPos chunkPosition;
    @Nullable
    private BlockBox boundingBox;
    private boolean placeFluids = true;
    @Nullable
    private Random random;
    @Nullable
    private int field_15575;
    private final List<StructureProcessor> processors = Lists.newArrayList();
    private boolean updateNeighbors;
    private boolean field_24043;

    public StructurePlacementData copy() {
        StructurePlacementData lv = new StructurePlacementData();
        lv.mirror = this.mirror;
        lv.rotation = this.rotation;
        lv.position = this.position;
        lv.ignoreEntities = this.ignoreEntities;
        lv.chunkPosition = this.chunkPosition;
        lv.boundingBox = this.boundingBox;
        lv.placeFluids = this.placeFluids;
        lv.random = this.random;
        lv.field_15575 = this.field_15575;
        lv.processors.addAll(this.processors);
        lv.updateNeighbors = this.updateNeighbors;
        lv.field_24043 = this.field_24043;
        return lv;
    }

    public StructurePlacementData setMirror(BlockMirror arg) {
        this.mirror = arg;
        return this;
    }

    public StructurePlacementData setRotation(BlockRotation arg) {
        this.rotation = arg;
        return this;
    }

    public StructurePlacementData setPosition(BlockPos arg) {
        this.position = arg;
        return this;
    }

    public StructurePlacementData setIgnoreEntities(boolean bl) {
        this.ignoreEntities = bl;
        return this;
    }

    public StructurePlacementData setChunkPosition(ChunkPos arg) {
        this.chunkPosition = arg;
        return this;
    }

    public StructurePlacementData setBoundingBox(BlockBox arg) {
        this.boundingBox = arg;
        return this;
    }

    public StructurePlacementData setRandom(@Nullable Random random) {
        this.random = random;
        return this;
    }

    public StructurePlacementData setUpdateNeighbors(boolean bl) {
        this.updateNeighbors = bl;
        return this;
    }

    public StructurePlacementData clearProcessors() {
        this.processors.clear();
        return this;
    }

    public StructurePlacementData addProcessor(StructureProcessor arg) {
        this.processors.add(arg);
        return this;
    }

    public StructurePlacementData removeProcessor(StructureProcessor arg) {
        this.processors.remove(arg);
        return this;
    }

    public BlockMirror getMirror() {
        return this.mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public Random getRandom(@Nullable BlockPos arg) {
        if (this.random != null) {
            return this.random;
        }
        if (arg == null) {
            return new Random(Util.getMeasuringTimeMs());
        }
        return new Random(MathHelper.hashCode(arg));
    }

    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    @Nullable
    public BlockBox getBoundingBox() {
        if (this.boundingBox == null && this.chunkPosition != null) {
            this.calculateBoundingBox();
        }
        return this.boundingBox;
    }

    public boolean shouldUpdateNeighbors() {
        return this.updateNeighbors;
    }

    public List<StructureProcessor> getProcessors() {
        return this.processors;
    }

    void calculateBoundingBox() {
        if (this.chunkPosition != null) {
            this.boundingBox = this.getChunkBlockBox(this.chunkPosition);
        }
    }

    public boolean shouldPlaceFluids() {
        return this.placeFluids;
    }

    public Structure.PalettedBlockInfoList getRandomBlockInfos(List<Structure.PalettedBlockInfoList> list, @Nullable BlockPos arg) {
        int i = list.size();
        if (i == 0) {
            throw new IllegalStateException("No palettes");
        }
        return list.get(this.getRandom(arg).nextInt(i));
    }

    @Nullable
    private BlockBox getChunkBlockBox(@Nullable ChunkPos arg) {
        if (arg == null) {
            return this.boundingBox;
        }
        int i = arg.x * 16;
        int j = arg.z * 16;
        return new BlockBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
    }

    public StructurePlacementData method_27264(boolean bl) {
        this.field_24043 = bl;
        return this;
    }

    public boolean method_27265() {
        return this.field_24043;
    }
}

