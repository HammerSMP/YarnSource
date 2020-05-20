/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChunkStatus {
    private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
    private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    private static final NoGenTask STATUS_BUMP_NO_GEN_TASK = (arg, arg2, arg3, arg4, function, arg5) -> {
        if (arg5 instanceof ProtoChunk && !arg5.getStatus().isAtLeast(arg)) {
            ((ProtoChunk)arg5).setStatus(arg);
        }
        return CompletableFuture.completedFuture(Either.left((Object)arg5));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.register("empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus arg, ServerWorld arg2, ChunkGenerator arg3, StructureManager arg4, ServerLightingProvider arg5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk arg6) -> {
        if (!arg6.getStatus().isAtLeast(arg)) {
            if (arg2.getServer().method_27728().method_28057().method_28029()) {
                arg3.setStructureStarts(arg2.getStructureAccessor(), arg6, arg4, arg2.getSeed());
            }
            if (arg6 instanceof ProtoChunk) {
                ((ProtoChunk)arg6).setStatus(arg);
            }
        }
        return CompletableFuture.completedFuture(Either.left((Object)arg6));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.register("structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.addStructureReferences(new ChunkRegion(arg, list), arg.getStructureAccessor(), arg3));
    public static final ChunkStatus BIOMES = ChunkStatus.register("biomes", STRUCTURE_REFERENCES, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.populateBiomes(arg3));
    public static final ChunkStatus NOISE = ChunkStatus.register("noise", BIOMES, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.populateNoise(new ChunkRegion(arg, list), arg.getStructureAccessor(), arg3));
    public static final ChunkStatus SURFACE = ChunkStatus.register("surface", NOISE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.buildSurface(new ChunkRegion(arg, list), arg3));
    public static final ChunkStatus CARVERS = ChunkStatus.register("carvers", SURFACE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.carve(arg.getSeed(), arg.getBiomeAccess(), arg3, GenerationStep.Carver.AIR));
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.register("liquid_carvers", CARVERS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.carve(arg.getSeed(), arg.getBiomeAccess(), arg3, GenerationStep.Carver.LIQUID));
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus arg, ServerWorld arg2, ChunkGenerator arg3, StructureManager arg4, ServerLightingProvider arg5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk arg6) -> {
        ProtoChunk lv = (ProtoChunk)arg6;
        lv.setLightingProvider(arg5);
        if (!arg6.getStatus().isAtLeast(arg)) {
            Heightmap.populateHeightmaps(arg6, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            arg3.generateFeatures(new ChunkRegion(arg2, list), arg2.getStructureAccessor());
            lv.setStatus(arg);
        }
        return CompletableFuture.completedFuture(Either.left((Object)arg6));
    });
    public static final ChunkStatus LIGHT = ChunkStatus.register("light", FEATURES, 1, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (arg, arg2, arg3, arg4, arg5, function, list, arg6) -> ChunkStatus.getLightingFuture(arg, arg5, arg6), (arg, arg2, arg3, arg4, function, arg5) -> ChunkStatus.getLightingFuture(arg, arg4, arg5));
    public static final ChunkStatus SPAWN = ChunkStatus.register("spawn", LIGHT, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> arg2.populateEntities(new ChunkRegion(arg, list)));
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.register("heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld arg, ChunkGenerator arg2, List<Chunk> list, Chunk arg3) -> {});
    public static final ChunkStatus FULL = ChunkStatus.register("full", HEIGHTMAPS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.LEVELCHUNK, (arg, arg2, arg3, arg4, arg5, function, list, arg6) -> (CompletableFuture)function.apply(arg6), (arg, arg2, arg3, arg4, function, arg5) -> (CompletableFuture)function.apply(arg5));
    private static final List<ChunkStatus> DISTANCE_TO_TARGET_GENERATION_STATUS = ImmutableList.of((Object)FULL, (Object)FEATURES, (Object)LIQUID_CARVERS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS, (Object)STRUCTURE_STARTS);
    private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS = (IntList)Util.make(new IntArrayList(ChunkStatus.createOrderedList().size()), intArrayList -> {
        int i = 0;
        for (int j = ChunkStatus.createOrderedList().size() - 1; j >= 0; --j) {
            while (i + 1 < DISTANCE_TO_TARGET_GENERATION_STATUS.size() && j <= DISTANCE_TO_TARGET_GENERATION_STATUS.get(i + 1).getIndex()) {
                ++i;
            }
            intArrayList.add(0, i);
        }
    });
    private final String id;
    private final int index;
    private final ChunkStatus previous;
    private final Task task;
    private final NoGenTask noGenTask;
    private final int taskMargin;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Type> heightMapTypes;

    private static CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getLightingFuture(ChunkStatus arg, ServerLightingProvider arg2, Chunk arg3) {
        boolean bl = ChunkStatus.shouldExcludeBlockLight(arg, arg3);
        if (!arg3.getStatus().isAtLeast(arg)) {
            ((ProtoChunk)arg3).setStatus(arg);
        }
        return arg2.light(arg3, bl).thenApply(Either::left);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus arg, int i, EnumSet<Heightmap.Type> enumSet, ChunkType arg2, SimpleTask arg3) {
        return ChunkStatus.register(string, arg, i, enumSet, arg2, (Task)arg3);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus arg, int i, EnumSet<Heightmap.Type> enumSet, ChunkType arg2, Task arg3) {
        return ChunkStatus.register(string, arg, i, enumSet, arg2, arg3, STATUS_BUMP_NO_GEN_TASK);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus arg, int i, EnumSet<Heightmap.Type> enumSet, ChunkType arg2, Task arg3, NoGenTask arg4) {
        return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, arg, i, enumSet, arg2, arg3, arg4));
    }

    public static List<ChunkStatus> createOrderedList() {
        ChunkStatus lv;
        ArrayList list = Lists.newArrayList();
        for (lv = FULL; lv.getPrevious() != lv; lv = lv.getPrevious()) {
            list.add(lv);
        }
        list.add(lv);
        Collections.reverse(list);
        return list;
    }

    private static boolean shouldExcludeBlockLight(ChunkStatus arg, Chunk arg2) {
        return arg2.getStatus().isAtLeast(arg) && arg2.isLightOn();
    }

    public static ChunkStatus getTargetGenerationStatus(int i) {
        if (i >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
            return EMPTY;
        }
        if (i < 0) {
            return FULL;
        }
        return DISTANCE_TO_TARGET_GENERATION_STATUS.get(i);
    }

    public static int getMaxTargetGenerationRadius() {
        return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
    }

    public static int getTargetGenerationRadius(ChunkStatus arg) {
        return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(arg.getIndex());
    }

    ChunkStatus(String string, @Nullable ChunkStatus arg, int i, EnumSet<Heightmap.Type> enumSet, ChunkType arg2, Task arg3, NoGenTask arg4) {
        this.id = string;
        this.previous = arg == null ? this : arg;
        this.task = arg3;
        this.noGenTask = arg4;
        this.taskMargin = i;
        this.chunkType = arg2;
        this.heightMapTypes = enumSet;
        this.index = arg == null ? 0 : arg.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.id;
    }

    public ChunkStatus getPrevious() {
        return this.previous;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(ServerWorld arg, ChunkGenerator arg2, StructureManager arg3, ServerLightingProvider arg4, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list) {
        return this.task.doWork(this, arg, arg2, arg3, arg4, function, list, list.get(list.size() / 2));
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runNoGenTask(ServerWorld arg, StructureManager arg2, ServerLightingProvider arg3, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, Chunk arg4) {
        return this.noGenTask.doWork(this, arg, arg2, arg3, function, arg4);
    }

    public int getTaskMargin() {
        return this.taskMargin;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus get(String string) {
        return Registry.CHUNK_STATUS.get(Identifier.tryParse(string));
    }

    public EnumSet<Heightmap.Type> getHeightmapTypes() {
        return this.heightMapTypes;
    }

    public boolean isAtLeast(ChunkStatus arg) {
        return this.getIndex() >= arg.getIndex();
    }

    public String toString() {
        return Registry.CHUNK_STATUS.getId(this).toString();
    }

    public static enum ChunkType {
        PROTOCHUNK,
        LEVELCHUNK;

    }

    static interface SimpleTask
    extends Task {
        @Override
        default public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus arg, ServerWorld arg2, ChunkGenerator arg3, StructureManager arg4, ServerLightingProvider arg5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk arg6) {
            if (!arg6.getStatus().isAtLeast(arg)) {
                this.doWork(arg2, arg3, list, arg6);
                if (arg6 instanceof ProtoChunk) {
                    ((ProtoChunk)arg6).setStatus(arg);
                }
            }
            return CompletableFuture.completedFuture(Either.left((Object)arg6));
        }

        public void doWork(ServerWorld var1, ChunkGenerator var2, List<Chunk> var3, Chunk var4);
    }

    static interface NoGenTask {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, StructureManager var3, ServerLightingProvider var4, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var5, Chunk var6);
    }

    static interface Task {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, ChunkGenerator var3, StructureManager var4, ServerLightingProvider var5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var6, List<Chunk> var7, Chunk var8);
    }
}

