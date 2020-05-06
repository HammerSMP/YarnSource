/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;

public class ReadOnlyChunk
extends ProtoChunk {
    private final WorldChunk wrapped;

    public ReadOnlyChunk(WorldChunk arg) {
        super(arg.getPos(), UpgradeData.NO_UPGRADE_DATA);
        this.wrapped = arg;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return this.wrapped.getBlockEntity(arg);
    }

    @Override
    @Nullable
    public BlockState getBlockState(BlockPos arg) {
        return this.wrapped.getBlockState(arg);
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return this.wrapped.getFluidState(arg);
    }

    @Override
    public int getMaxLightLevel() {
        return this.wrapped.getMaxLightLevel();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos arg, BlockState arg2, boolean bl) {
        return null;
    }

    @Override
    public void setBlockEntity(BlockPos arg, BlockEntity arg2) {
    }

    @Override
    public void addEntity(Entity arg) {
    }

    @Override
    public void setStatus(ChunkStatus arg) {
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.wrapped.getSectionArray();
    }

    @Override
    @Nullable
    public LightingProvider getLightingProvider() {
        return this.wrapped.getLightingProvider();
    }

    @Override
    public void setHeightmap(Heightmap.Type arg, long[] ls) {
    }

    private Heightmap.Type transformHeightmapType(Heightmap.Type arg) {
        if (arg == Heightmap.Type.WORLD_SURFACE_WG) {
            return Heightmap.Type.WORLD_SURFACE;
        }
        if (arg == Heightmap.Type.OCEAN_FLOOR_WG) {
            return Heightmap.Type.OCEAN_FLOOR;
        }
        return arg;
    }

    @Override
    public int sampleHeightmap(Heightmap.Type arg, int i, int j) {
        return this.wrapped.sampleHeightmap(this.transformHeightmapType(arg), i, j);
    }

    @Override
    public ChunkPos getPos() {
        return this.wrapped.getPos();
    }

    @Override
    public void setLastSaveTime(long l) {
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(String string) {
        return this.wrapped.getStructureStart(string);
    }

    @Override
    public void setStructureStart(String string, StructureStart arg) {
    }

    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return this.wrapped.getStructureStarts();
    }

    @Override
    public void setStructureStarts(Map<String, StructureStart> map) {
    }

    @Override
    public LongSet getStructureReferences(String string) {
        return this.wrapped.getStructureReferences(string);
    }

    @Override
    public void addStructureReference(String string, long l) {
    }

    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.wrapped.getStructureReferences();
    }

    @Override
    public void setStructureReferences(Map<String, LongSet> map) {
    }

    @Override
    public BiomeArray getBiomeArray() {
        return this.wrapped.getBiomeArray();
    }

    @Override
    public void setShouldSave(boolean bl) {
    }

    @Override
    public boolean needsSaving() {
        return false;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.wrapped.getStatus();
    }

    @Override
    public void removeBlockEntity(BlockPos arg) {
    }

    @Override
    public void markBlockForPostProcessing(BlockPos arg) {
    }

    @Override
    public void addPendingBlockEntityTag(CompoundTag arg) {
    }

    @Override
    @Nullable
    public CompoundTag getBlockEntityTagAt(BlockPos arg) {
        return this.wrapped.getBlockEntityTagAt(arg);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos arg) {
        return this.wrapped.method_20598(arg);
    }

    @Override
    public void setBiomes(BiomeArray arg) {
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.wrapped.getLightSourcesStream();
    }

    @Override
    public ChunkTickScheduler<Block> getBlockTickScheduler() {
        return new ChunkTickScheduler<Block>(arg -> arg.getDefaultState().isAir(), this.getPos());
    }

    @Override
    public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
        return new ChunkTickScheduler<Fluid>(arg -> arg == Fluids.EMPTY, this.getPos());
    }

    @Override
    public BitSet getCarvingMask(GenerationStep.Carver arg) {
        return this.wrapped.getCarvingMask(arg);
    }

    public WorldChunk getWrappedChunk() {
        return this.wrapped;
    }

    @Override
    public boolean isLightOn() {
        return this.wrapped.isLightOn();
    }

    @Override
    public void setLightOn(boolean bl) {
        this.wrapped.setLightOn(bl);
    }

    @Override
    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    @Override
    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

