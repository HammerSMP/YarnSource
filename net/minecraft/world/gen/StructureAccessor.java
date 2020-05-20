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
import net.minecraft.class_5285;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
    private final ServerWorld field_24404;
    private final class_5285 field_24497;

    public StructureAccessor(ServerWorld arg, class_5285 arg2) {
        this.field_24404 = arg;
        this.field_24497 = arg2;
    }

    public Stream<? extends StructureStart<?>> getStructuresWithChildren(ChunkSectionPos arg3, StructureFeature<?> arg22) {
        return this.field_24404.getChunk(arg3.getSectionX(), arg3.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(arg22.getName()).stream().map(arg -> ChunkSectionPos.from(new ChunkPos((long)arg), 0)).map(arg2 -> this.getStructureStart((ChunkSectionPos)arg2, arg22, this.field_24404.getChunk(arg2.getSectionX(), arg2.getSectionZ(), ChunkStatus.STRUCTURE_STARTS))).filter(arg -> arg != null && arg.hasChildren());
    }

    @Nullable
    public StructureStart<?> getStructureStart(ChunkSectionPos arg, StructureFeature<?> arg2, StructureHolder arg3) {
        return arg3.getStructureStart(arg2.getName());
    }

    public void setStructureStart(ChunkSectionPos arg, StructureFeature<?> arg2, StructureStart<?> arg3, StructureHolder arg4) {
        arg4.setStructureStart(arg2.getName(), arg3);
    }

    public void addStructureReference(ChunkSectionPos arg, StructureFeature<?> arg2, long l, StructureHolder arg3) {
        arg3.addStructureReference(arg2.getName(), l);
    }

    public boolean method_27834() {
        return this.field_24497.method_28029();
    }

    public StructureStart<?> method_28388(BlockPos arg, boolean bl, StructureFeature<?> arg23) {
        return (StructureStart)DataFixUtils.orElse(this.getStructuresWithChildren(ChunkSectionPos.from(arg), arg23).filter(arg2 -> arg2.getBoundingBox().contains(arg)).filter(arg22 -> !bl || arg22.getChildren().stream().anyMatch(arg2 -> arg2.getBoundingBox().contains(arg))).findFirst(), StructureStart.DEFAULT);
    }
}

