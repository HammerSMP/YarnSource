/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMaps
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL) - 2;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
    private final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
    private final TicketDistanceLevelPropagator distanceFromTicketTracker = new TicketDistanceLevelPropagator();
    private final DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new DistanceFromNearestPlayerTracker(8);
    private final NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new NearbyChunkTicketUpdater(33);
    private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
    private final ChunkTaskPrioritySystem levelUpdateListener;
    private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> playerTicketThrottler;
    private final MessageListener<ChunkTaskPrioritySystem.UnblockingMessage> playerTicketThrottlerUnblocker;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final Executor mainThreadExecutor;
    private long age;

    protected ChunkTicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
        ChunkTaskPrioritySystem lv2;
        MessageListener<Runnable> lv = MessageListener.create("player ticket throttler", mainThreadExecutor::execute);
        this.levelUpdateListener = lv2 = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(lv), workerExecutor, 4);
        this.playerTicketThrottler = lv2.createExecutor(lv, true);
        this.playerTicketThrottlerUnblocker = lv2.createUnblockingExecutor(lv);
        this.mainThreadExecutor = mainThreadExecutor;
    }

    protected void purge() {
        ++this.age;
        ObjectIterator objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            if (((SortedArraySet)entry.getValue()).removeIf(arg -> arg.isExpired(this.age))) {
                this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), ChunkTicketManager.getLevel((SortedArraySet)entry.getValue()), false);
            }
            if (!((SortedArraySet)entry.getValue()).isEmpty()) continue;
            objectIterator.remove();
        }
    }

    private static int getLevel(SortedArraySet<ChunkTicket<?>> arg) {
        return !arg.isEmpty() ? arg.first().getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
    }

    protected abstract boolean isUnloaded(long var1);

    @Nullable
    protected abstract ChunkHolder getChunkHolder(long var1);

    @Nullable
    protected abstract ChunkHolder setLevel(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean tick(ThreadedAnvilChunkStorage chunkStorage) {
        boolean bl;
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
        boolean bl2 = bl = i != 0;
        if (bl) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(arg2 -> arg2.tick(chunkStorage));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longIterator = this.chunkPositions.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                if (!this.getTicketSet(l).stream().anyMatch(arg -> arg.getType() == ChunkTicketType.PLAYER)) continue;
                ChunkHolder lv = chunkStorage.getCurrentChunkHolder(l);
                if (lv == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = lv.getEntityTickingFuture();
                completableFuture.thenAccept(either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {}, l, false))));
            }
            this.chunkPositions.clear();
        }
        return bl;
    }

    private void addTicket(long position, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> lv = this.getTicketSet(position);
        int i = ChunkTicketManager.getLevel(lv);
        ChunkTicket<?> lv2 = lv.addAndGet(ticket);
        lv2.setTickCreated(this.age);
        if (ticket.getLevel() < i) {
            this.distanceFromTicketTracker.updateLevel(position, ticket.getLevel(), true);
        }
    }

    private void removeTicket(long pos, ChunkTicket<?> ticket) {
        SortedArraySet<ChunkTicket<?>> lv = this.getTicketSet(pos);
        if (lv.remove(ticket)) {
            // empty if block
        }
        if (lv.isEmpty()) {
            this.ticketsByPosition.remove(pos);
        }
        this.distanceFromTicketTracker.updateLevel(pos, ChunkTicketManager.getLevel(lv), false);
    }

    public <T> void addTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<T>(type, level, argument));
    }

    public <T> void removeTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
        ChunkTicket<T> lv = new ChunkTicket<T>(type, level, argument);
        this.removeTicket(pos.toLong(), lv);
    }

    public <T> void addTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        this.addTicket(pos.toLong(), new ChunkTicket<T>(type, 33 - radius, argument));
    }

    public <T> void removeTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
        ChunkTicket<T> lv = new ChunkTicket<T>(type, 33 - radius, argument);
        this.removeTicket(pos.toLong(), lv);
    }

    private SortedArraySet<ChunkTicket<?>> getTicketSet(long position) {
        return (SortedArraySet)this.ticketsByPosition.computeIfAbsent(position, l -> SortedArraySet.create(4));
    }

    protected void setChunkForced(ChunkPos pos, boolean forced) {
        ChunkTicket<ChunkPos> lv = new ChunkTicket<ChunkPos>(ChunkTicketType.field_14031, 31, pos);
        if (forced) {
            this.addTicket(pos.toLong(), lv);
        } else {
            this.removeTicket(pos.toLong(), lv);
        }
    }

    public void handleChunkEnter(ChunkSectionPos pos, ServerPlayerEntity player) {
        long l2 = pos.toChunkPos().toLong();
        ((ObjectSet)this.playersByChunkPos.computeIfAbsent(l2, l -> new ObjectOpenHashSet())).add((Object)player);
        this.distanceFromNearestPlayerTracker.updateLevel(l2, 0, true);
        this.nearbyChunkTicketUpdater.updateLevel(l2, 0, true);
    }

    public void handleChunkLeave(ChunkSectionPos pos, ServerPlayerEntity player) {
        long l = pos.toChunkPos().toLong();
        ObjectSet objectSet = (ObjectSet)this.playersByChunkPos.get(l);
        objectSet.remove((Object)player);
        if (objectSet.isEmpty()) {
            this.playersByChunkPos.remove(l);
            this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
        }
    }

    protected String getTicket(long pos) {
        String string2;
        SortedArraySet lv = (SortedArraySet)this.ticketsByPosition.get(pos);
        if (lv == null || lv.isEmpty()) {
            String string = "no_ticket";
        } else {
            string2 = ((ChunkTicket)lv.first()).toString();
        }
        return string2;
    }

    protected void setWatchDistance(int viewDistance) {
        this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
    }

    public int getSpawningChunkCount() {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
    }

    public boolean method_20800(long l) {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(l);
    }

    public String toDumpString() {
        return this.levelUpdateListener.getDebugString();
    }

    class TicketDistanceLevelPropagator
    extends ChunkPosDistanceLevelPropagator {
        public TicketDistanceLevelPropagator() {
            super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
        }

        @Override
        protected int getInitialLevel(long id) {
            SortedArraySet lv = (SortedArraySet)ChunkTicketManager.this.ticketsByPosition.get(id);
            if (lv == null) {
                return Integer.MAX_VALUE;
            }
            if (lv.isEmpty()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)lv.first()).getLevel();
        }

        @Override
        protected int getLevel(long id) {
            ChunkHolder lv;
            if (!ChunkTicketManager.this.isUnloaded(id) && (lv = ChunkTicketManager.this.getChunkHolder(id)) != null) {
                return lv.getLevel();
            }
            return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }

        @Override
        protected void setLevel(long id, int level) {
            int j;
            ChunkHolder lv = ChunkTicketManager.this.getChunkHolder(id);
            int n = j = lv == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : lv.getLevel();
            if (j == level) {
                return;
            }
            if ((lv = ChunkTicketManager.this.setLevel(id, level, lv, j)) != null) {
                ChunkTicketManager.this.chunkHolders.add(lv);
            }
        }

        public int update(int distance) {
            return this.applyPendingUpdates(distance);
        }
    }

    class NearbyChunkTicketUpdater
    extends DistanceFromNearestPlayerTracker {
        private int watchDistance;
        private final Long2IntMap distances;
        private final LongSet positionsAffected;

        protected NearbyChunkTicketUpdater(int i) {
            super(i);
            this.distances = Long2IntMaps.synchronize((Long2IntMap)new Long2IntOpenHashMap());
            this.positionsAffected = new LongOpenHashSet();
            this.watchDistance = 0;
            this.distances.defaultReturnValue(i + 2);
        }

        @Override
        protected void onDistanceChange(long pos, int oldDistance, int distance) {
            this.positionsAffected.add(pos);
        }

        public void setWatchDistance(int watchDistance) {
            for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                byte b = entry.getByteValue();
                long l = entry.getLongKey();
                this.updateTicket(l, b, this.isWithinViewDistance(b), b <= watchDistance - 2);
            }
            this.watchDistance = watchDistance;
        }

        private void updateTicket(long pos, int distance, boolean oldWithinViewDistance, boolean withinViewDistance) {
            if (oldWithinViewDistance != withinViewDistance) {
                ChunkTicket<ChunkPos> lv = new ChunkTicket<ChunkPos>(ChunkTicketType.PLAYER, NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos));
                if (withinViewDistance) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        if (this.isWithinViewDistance(this.getLevel(pos))) {
                            ChunkTicketManager.this.addTicket(pos, lv);
                            ChunkTicketManager.this.chunkPositions.add(pos);
                        } else {
                            ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {}, pos, false));
                        }
                    }), pos, () -> distance));
                } else {
                    ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, lv)), pos, true));
                }
            }
        }

        @Override
        public void updateLevels() {
            super.updateLevels();
            if (!this.positionsAffected.isEmpty()) {
                LongIterator longIterator = this.positionsAffected.iterator();
                while (longIterator.hasNext()) {
                    int j;
                    long l = longIterator.nextLong();
                    int i2 = this.distances.get(l);
                    if (i2 == (j = this.getLevel(l))) continue;
                    ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, i -> {
                        if (i >= this.distances.defaultReturnValue()) {
                            this.distances.remove(l);
                        } else {
                            this.distances.put(l, i);
                        }
                    });
                    this.updateTicket(l, j, this.isWithinViewDistance(i2), this.isWithinViewDistance(j));
                }
                this.positionsAffected.clear();
            }
        }

        private boolean isWithinViewDistance(int distance) {
            return distance <= this.watchDistance - 2;
        }
    }

    class DistanceFromNearestPlayerTracker
    extends ChunkPosDistanceLevelPropagator {
        protected final Long2ByteMap distanceFromNearestPlayer;
        protected final int maxDistance;

        protected DistanceFromNearestPlayerTracker(int i) {
            super(i + 2, 16, 256);
            this.distanceFromNearestPlayer = new Long2ByteOpenHashMap();
            this.maxDistance = i;
            this.distanceFromNearestPlayer.defaultReturnValue((byte)(i + 2));
        }

        @Override
        protected int getLevel(long id) {
            return this.distanceFromNearestPlayer.get(id);
        }

        @Override
        protected void setLevel(long id, int level) {
            byte c;
            if (level > this.maxDistance) {
                byte b = this.distanceFromNearestPlayer.remove(id);
            } else {
                c = this.distanceFromNearestPlayer.put(id, (byte)level);
            }
            this.onDistanceChange(id, c, level);
        }

        protected void onDistanceChange(long pos, int oldDistance, int distance) {
        }

        @Override
        protected int getInitialLevel(long id) {
            return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
        }

        private boolean isPlayerInChunk(long chunkPos) {
            ObjectSet objectSet = (ObjectSet)ChunkTicketManager.this.playersByChunkPos.get(chunkPos);
            return objectSet != null && !objectSet.isEmpty();
        }

        public void updateLevels() {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }
}

