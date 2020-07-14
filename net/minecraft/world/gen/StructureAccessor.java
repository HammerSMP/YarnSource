/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  javax.annotation.Nullable
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
    private final WorldAccess world;
    private final GeneratorOptions options;

    public StructureAccessor(WorldAccess world, GeneratorOptions options) {
        this.world = world;
        this.options = options;
    }

    public StructureAccessor method_29951(ChunkRegion region) {
        if (region.getWorld() != this.world) {
            throw new IllegalStateException("Using invalid feature manager (source level: " + region.getWorld() + ", region: " + region);
        }
        return new StructureAccessor(region, this.options);
    }

    public Stream<? extends StructureStart<?>> getStructuresWithChildren(ChunkSectionPos pos2, StructureFeature<?> feature) {
        return this.world.getChunk(pos2.getSectionX(), pos2.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(feature).stream().map(pos -> ChunkSectionPos.from(new ChunkPos((long)pos), 0)).map(pos -> this.getStructureStart((ChunkSectionPos)pos, feature, this.world.getChunk(pos.getSectionX(), pos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS))).filter(structureStart -> structureStart != null && structureStart.hasChildren());
    }

    @Nullable
    public StructureStart<?> getStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureHolder holder) {
        return holder.getStructureStart(feature);
    }

    public void setStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureStart<?> structureStart, StructureHolder holder) {
        holder.setStructureStart(feature, structureStart);
    }

    public void addStructureReference(ChunkSectionPos pos, StructureFeature<?> feature, long reference, StructureHolder holder) {
        holder.addStructureReference(feature, reference);
    }

    public boolean shouldGenerateStructures() {
        return this.options.shouldGenerateStructures();
    }

    public StructureStart<?> method_28388(BlockPos arg, boolean bl, StructureFeature<?> arg2) {
        return (StructureStart)DataFixUtils.orElse(this.getStructuresWithChildren(ChunkSectionPos.from(arg), arg2).filter(structureStart -> structureStart.getBoundingBox().contains(arg)).filter(structureStart -> !bl || structureStart.getChildren().stream().anyMatch(piece -> piece.getBoundingBox().contains(arg))).findFirst(), StructureStart.DEFAULT);
    }
}

