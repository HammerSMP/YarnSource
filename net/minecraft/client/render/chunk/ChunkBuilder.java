/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.common.primitives.Doubles
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final PriorityQueue<BuiltChunk.Task> rebuildQueue = Queues.newPriorityQueue();
    private final Queue<BlockBufferBuilderStorage> threadBuffers;
    private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
    private volatile int queuedTaskCount;
    private volatile int bufferCount;
    private final BlockBufferBuilderStorage buffers;
    private final TaskExecutor<Runnable> mailbox;
    private final Executor executor;
    private World world;
    private final WorldRenderer worldRenderer;
    private Vec3d cameraPosition = Vec3d.ZERO;

    public ChunkBuilder(World arg, WorldRenderer arg2, Executor executor, boolean bl, BlockBufferBuilderStorage arg3) {
        this.world = arg;
        this.worldRenderer = arg2;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = bl ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k, i));
        this.buffers = arg3;
        ArrayList list = Lists.newArrayListWithExpectedSize((int)l);
        try {
            for (int m = 0; m < l; ++m) {
                list.add(new BlockBufferBuilderStorage());
            }
        }
        catch (OutOfMemoryError outOfMemoryError) {
            LOGGER.warn("Allocated only {}/{} buffers", (Object)list.size(), (Object)l);
            int n = Math.min(list.size() * 2 / 3, list.size() - 1);
            for (int o = 0; o < n; ++o) {
                list.remove(list.size() - 1);
            }
            System.gc();
        }
        this.threadBuffers = Queues.newArrayDeque((Iterable)list);
        this.bufferCount = this.threadBuffers.size();
        this.executor = executor;
        this.mailbox = TaskExecutor.create(executor, "Chunk Renderer");
        this.mailbox.send(this::scheduleRunTasks);
    }

    public void setWorld(World arg) {
        this.world = arg;
    }

    private void scheduleRunTasks() {
        if (this.threadBuffers.isEmpty()) {
            return;
        }
        BuiltChunk.Task lv = this.rebuildQueue.poll();
        if (lv == null) {
            return;
        }
        BlockBufferBuilderStorage lv2 = this.threadBuffers.poll();
        this.queuedTaskCount = this.rebuildQueue.size();
        this.bufferCount = this.threadBuffers.size();
        ((CompletableFuture)CompletableFuture.runAsync(() -> {}, this.executor).thenCompose(arg3 -> lv.run(lv2))).whenComplete((arg2, throwable) -> {
            if (throwable != null) {
                CrashReport lv = CrashReport.create(throwable, "Batching chunks");
                MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().addDetailsToCrashReport(lv));
                return;
            }
            this.mailbox.send(() -> {
                if (arg2 == Result.SUCCESSFUL) {
                    lv2.clear();
                } else {
                    lv2.reset();
                }
                this.threadBuffers.add(lv2);
                this.bufferCount = this.threadBuffers.size();
                this.scheduleRunTasks();
            });
        });
    }

    public String getDebugString() {
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
    }

    public void setCameraPosition(Vec3d arg) {
        this.cameraPosition = arg;
    }

    public Vec3d getCameraPosition() {
        return this.cameraPosition;
    }

    public boolean upload() {
        Runnable runnable;
        boolean bl = false;
        while ((runnable = this.uploadQueue.poll()) != null) {
            runnable.run();
            bl = true;
        }
        return bl;
    }

    public void rebuild(BuiltChunk arg) {
        arg.rebuild();
    }

    public void reset() {
        this.clear();
    }

    public void send(BuiltChunk.Task arg) {
        this.mailbox.send(() -> {
            this.rebuildQueue.offer(arg);
            this.queuedTaskCount = this.rebuildQueue.size();
            this.scheduleRunTasks();
        });
    }

    public CompletableFuture<Void> scheduleUpload(BufferBuilder arg, VertexBuffer arg2) {
        return CompletableFuture.runAsync(() -> {}, this.uploadQueue::add).thenCompose(arg3 -> this.upload(arg, arg2));
    }

    private CompletableFuture<Void> upload(BufferBuilder arg, VertexBuffer arg2) {
        return arg2.submitUpload(arg);
    }

    private void clear() {
        while (!this.rebuildQueue.isEmpty()) {
            BuiltChunk.Task lv = this.rebuildQueue.poll();
            if (lv == null) continue;
            lv.cancel();
        }
        this.queuedTaskCount = 0;
    }

    public boolean isEmpty() {
        return this.queuedTaskCount == 0 && this.uploadQueue.isEmpty();
    }

    public void stop() {
        this.clear();
        this.mailbox.close();
        this.threadBuffers.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChunkData {
        public static final ChunkData EMPTY = new ChunkData(){

            @Override
            public boolean isVisibleThrough(Direction arg, Direction arg2) {
                return false;
            }
        };
        private final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet();
        private final Set<RenderLayer> initializedLayers = new ObjectArraySet();
        private boolean empty = true;
        private final List<BlockEntity> blockEntities = Lists.newArrayList();
        private ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
        @Nullable
        private BufferBuilder.State bufferState;

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isEmpty(RenderLayer arg) {
            return !this.nonEmptyLayers.contains(arg);
        }

        public List<BlockEntity> getBlockEntities() {
            return this.blockEntities;
        }

        public boolean isVisibleThrough(Direction arg, Direction arg2) {
            return this.occlusionGraph.isVisibleThrough(arg, arg2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Result {
        SUCCESSFUL,
        CANCELLED;

    }

    @Environment(value=EnvType.CLIENT)
    public class BuiltChunk {
        public final AtomicReference<ChunkData> data = new AtomicReference<ChunkData>(ChunkData.EMPTY);
        @Nullable
        private RebuildTask rebuildTask;
        @Nullable
        private SortTask sortTask;
        private final Set<BlockEntity> blockEntities = Sets.newHashSet();
        private final Map<RenderLayer, VertexBuffer> buffers = RenderLayer.getBlockLayers().stream().collect(Collectors.toMap(arg -> arg, arg -> new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL)));
        public Box boundingBox;
        private int rebuildFrame = -1;
        private boolean needsRebuild = true;
        private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
        private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], args -> {
            for (int i = 0; i < ((BlockPos.Mutable[])args).length; ++i) {
                args[i] = new BlockPos.Mutable();
            }
        });
        private boolean needsImportantRebuild;

        private boolean isChunkNonEmpty(BlockPos arg) {
            return ChunkBuilder.this.world.getChunk(arg.getX() >> 4, arg.getZ() >> 4, ChunkStatus.FULL, false) != null;
        }

        public boolean shouldBuild() {
            int i = 24;
            if (this.getSquaredCameraDistance() > 576.0) {
                return this.isChunkNonEmpty(this.neighborPositions[Direction.WEST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.NORTH.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.EAST.ordinal()]) && this.isChunkNonEmpty(this.neighborPositions[Direction.SOUTH.ordinal()]);
            }
            return true;
        }

        public boolean setRebuildFrame(int i) {
            if (this.rebuildFrame == i) {
                return false;
            }
            this.rebuildFrame = i;
            return true;
        }

        public VertexBuffer getBuffer(RenderLayer arg) {
            return this.buffers.get(arg);
        }

        public void setOrigin(int i, int j, int k) {
            if (i == this.origin.getX() && j == this.origin.getY() && k == this.origin.getZ()) {
                return;
            }
            this.clear();
            this.origin.set(i, j, k);
            this.boundingBox = new Box(i, j, k, i + 16, j + 16, k + 16);
            for (Direction lv : Direction.values()) {
                this.neighborPositions[lv.ordinal()].set(this.origin).move(lv, 16);
            }
        }

        protected double getSquaredCameraDistance() {
            Camera lv = MinecraftClient.getInstance().gameRenderer.getCamera();
            double d = this.boundingBox.minX + 8.0 - lv.getPos().x;
            double e = this.boundingBox.minY + 8.0 - lv.getPos().y;
            double f = this.boundingBox.minZ + 8.0 - lv.getPos().z;
            return d * d + e * e + f * f;
        }

        private void beginBufferBuilding(BufferBuilder arg) {
            arg.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        }

        public ChunkData getData() {
            return this.data.get();
        }

        private void clear() {
            this.cancel();
            this.data.set(ChunkData.EMPTY);
            this.needsRebuild = true;
        }

        public void delete() {
            this.clear();
            this.buffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getOrigin() {
            return this.origin;
        }

        public void scheduleRebuild(boolean bl) {
            boolean bl2 = this.needsRebuild;
            this.needsRebuild = true;
            this.needsImportantRebuild = bl | (bl2 && this.needsImportantRebuild);
        }

        public void cancelRebuild() {
            this.needsRebuild = false;
            this.needsImportantRebuild = false;
        }

        public boolean needsRebuild() {
            return this.needsRebuild;
        }

        public boolean needsImportantRebuild() {
            return this.needsRebuild && this.needsImportantRebuild;
        }

        public BlockPos getNeighborPosition(Direction arg) {
            return this.neighborPositions[arg.ordinal()];
        }

        public boolean scheduleSort(RenderLayer arg, ChunkBuilder arg2) {
            ChunkData lv = this.getData();
            if (this.sortTask != null) {
                this.sortTask.cancel();
            }
            if (!lv.initializedLayers.contains(arg)) {
                return false;
            }
            this.sortTask = new SortTask(this.getSquaredCameraDistance(), lv);
            arg2.send(this.sortTask);
            return true;
        }

        protected void cancel() {
            if (this.rebuildTask != null) {
                this.rebuildTask.cancel();
                this.rebuildTask = null;
            }
            if (this.sortTask != null) {
                this.sortTask.cancel();
                this.sortTask = null;
            }
        }

        public Task createRebuildTask() {
            this.cancel();
            BlockPos lv = this.origin.toImmutable();
            boolean i = true;
            ChunkRendererRegion lv2 = ChunkRendererRegion.create(ChunkBuilder.this.world, lv.add(-1, -1, -1), lv.add(16, 16, 16), 1);
            this.rebuildTask = new RebuildTask(this.getSquaredCameraDistance(), lv2);
            return this.rebuildTask;
        }

        public void scheduleRebuild(ChunkBuilder arg) {
            Task lv = this.createRebuildTask();
            arg.send(lv);
        }

        private void setNoCullingBlockEntities(Set<BlockEntity> set) {
            HashSet set2 = Sets.newHashSet(set);
            HashSet set3 = Sets.newHashSet(this.blockEntities);
            set2.removeAll(this.blockEntities);
            set3.removeAll(set);
            this.blockEntities.clear();
            this.blockEntities.addAll(set);
            ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set3, set2);
        }

        public void rebuild() {
            Task lv = this.createRebuildTask();
            lv.run(ChunkBuilder.this.buffers);
        }

        @Environment(value=EnvType.CLIENT)
        abstract class Task
        implements Comparable<Task> {
            protected final double distance;
            protected final AtomicBoolean cancelled = new AtomicBoolean(false);

            public Task(double d) {
                this.distance = d;
            }

            public abstract CompletableFuture<Result> run(BlockBufferBuilderStorage var1);

            public abstract void cancel();

            @Override
            public int compareTo(Task arg) {
                return Doubles.compare((double)this.distance, (double)arg.distance);
            }

            @Override
            public /* synthetic */ int compareTo(Object object) {
                return this.compareTo((Task)object);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class SortTask
        extends Task {
            private final ChunkData data;

            public SortTask(double d, ChunkData arg2) {
                super(d);
                this.data = arg2;
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage arg2) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d lv = ChunkBuilder.this.getCameraPosition();
                float f = (float)lv.x;
                float g = (float)lv.y;
                float h = (float)lv.z;
                BufferBuilder.State lv2 = this.data.bufferState;
                if (lv2 == null || !this.data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                BufferBuilder lv3 = arg2.get(RenderLayer.getTranslucent());
                BuiltChunk.this.beginBufferBuilding(lv3);
                lv3.restoreState(lv2);
                lv3.sortQuads(f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ());
                this.data.bufferState = lv3.popState();
                lv3.end();
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                CompletionStage completableFuture = ChunkBuilder.this.scheduleUpload(arg2.get(RenderLayer.getTranslucent()), BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())).thenApply(arg -> Result.CANCELLED);
                return ((CompletableFuture)completableFuture).handle((arg, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                    }
                    return this.cancelled.get() ? Result.CANCELLED : Result.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.cancelled.set(true);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class RebuildTask
        extends Task {
            @Nullable
            protected ChunkRendererRegion region;

            public RebuildTask(double d, @Nullable ChunkRendererRegion arg2) {
                super(d);
                this.region = arg2;
            }

            @Override
            public CompletableFuture<Result> run(BlockBufferBuilderStorage arg) {
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (!BuiltChunk.this.shouldBuild()) {
                    this.region = null;
                    BuiltChunk.this.scheduleRebuild(false);
                    this.cancelled.set(true);
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                Vec3d lv = ChunkBuilder.this.getCameraPosition();
                float f = (float)lv.x;
                float g = (float)lv.y;
                float h = (float)lv.z;
                ChunkData lv2 = new ChunkData();
                Set<BlockEntity> set = this.render(f, g, h, lv2, arg);
                BuiltChunk.this.setNoCullingBlockEntities(set);
                if (this.cancelled.get()) {
                    return CompletableFuture.completedFuture(Result.CANCELLED);
                }
                ArrayList list2 = Lists.newArrayList();
                lv2.initializedLayers.forEach(arg2 -> list2.add(ChunkBuilder.this.scheduleUpload(arg.get((RenderLayer)arg2), BuiltChunk.this.getBuffer((RenderLayer)arg2))));
                return Util.combine(list2).handle((list, throwable) -> {
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
                    }
                    if (this.cancelled.get()) {
                        return Result.CANCELLED;
                    }
                    BuiltChunk.this.data.set(lv2);
                    return Result.SUCCESSFUL;
                });
            }

            private Set<BlockEntity> render(float f, float g, float h, ChunkData arg, BlockBufferBuilderStorage arg2) {
                boolean i = true;
                BlockPos lv = BuiltChunk.this.origin.toImmutable();
                BlockPos lv2 = lv.add(15, 15, 15);
                ChunkOcclusionDataBuilder lv3 = new ChunkOcclusionDataBuilder();
                HashSet set = Sets.newHashSet();
                ChunkRendererRegion lv4 = this.region;
                this.region = null;
                MatrixStack lv5 = new MatrixStack();
                if (lv4 != null) {
                    BlockModelRenderer.enableBrightnessCache();
                    Random random = new Random();
                    BlockRenderManager lv6 = MinecraftClient.getInstance().getBlockRenderManager();
                    for (BlockPos lv7 : BlockPos.iterate(lv, lv2)) {
                        FluidState lv11;
                        BlockEntity lv10;
                        BlockState lv8 = lv4.getBlockState(lv7);
                        Block lv9 = lv8.getBlock();
                        if (lv8.isOpaqueFullCube(lv4, lv7)) {
                            lv3.markClosed(lv7);
                        }
                        if (lv9.hasBlockEntity() && (lv10 = lv4.getBlockEntity(lv7, WorldChunk.CreationType.CHECK)) != null) {
                            this.addBlockEntity(arg, set, lv10);
                        }
                        if (!(lv11 = lv4.getFluidState(lv7)).isEmpty()) {
                            RenderLayer lv12 = RenderLayers.getFluidLayer(lv11);
                            BufferBuilder lv13 = arg2.get(lv12);
                            if (arg.initializedLayers.add(lv12)) {
                                BuiltChunk.this.beginBufferBuilding(lv13);
                            }
                            if (lv6.renderFluid(lv7, lv4, lv13, lv11)) {
                                arg.empty = false;
                                arg.nonEmptyLayers.add(lv12);
                            }
                        }
                        if (lv8.getRenderType() == BlockRenderType.INVISIBLE) continue;
                        RenderLayer lv14 = RenderLayers.getBlockLayer(lv8);
                        BufferBuilder lv15 = arg2.get(lv14);
                        if (arg.initializedLayers.add(lv14)) {
                            BuiltChunk.this.beginBufferBuilding(lv15);
                        }
                        lv5.push();
                        lv5.translate(lv7.getX() & 0xF, lv7.getY() & 0xF, lv7.getZ() & 0xF);
                        if (lv6.renderBlock(lv8, lv7, lv4, lv5, lv15, true, random)) {
                            arg.empty = false;
                            arg.nonEmptyLayers.add(lv14);
                        }
                        lv5.pop();
                    }
                    if (arg.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
                        BufferBuilder lv16 = arg2.get(RenderLayer.getTranslucent());
                        lv16.sortQuads(f - (float)lv.getX(), g - (float)lv.getY(), h - (float)lv.getZ());
                        arg.bufferState = lv16.popState();
                    }
                    arg.initializedLayers.stream().map(arg2::get).forEach(BufferBuilder::end);
                    BlockModelRenderer.disableBrightnessCache();
                }
                arg.occlusionGraph = lv3.build();
                return set;
            }

            private <E extends BlockEntity> void addBlockEntity(ChunkData arg, Set<BlockEntity> set, E arg2) {
                BlockEntityRenderer<E> lv = BlockEntityRenderDispatcher.INSTANCE.get(arg2);
                if (lv != null) {
                    arg.blockEntities.add(arg2);
                    if (lv.rendersOutsideBoundingBox(arg2)) {
                        set.add(arg2);
                    }
                }
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.cancelled.compareAndSet(false, true)) {
                    BuiltChunk.this.scheduleRebuild(false);
                }
            }
        }
    }
}

