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
    private final MessageListener<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final Executor mainThreadExecutor;
    private long age;

    protected ChunkTicketManager(Executor executor, Executor executor2) {
        ChunkTaskPrioritySystem lv2;
        MessageListener<Runnable> lv = MessageListener.create("player ticket throttler", executor2::execute);
        this.levelUpdateListener = lv2 = new ChunkTaskPrioritySystem((List<MessageListener<?>>)ImmutableList.of(lv), executor, 4);
        this.playerTicketThrottler = lv2.createExecutor(lv, true);
        this.playerTicketThrottlerSorter = lv2.createSorterExecutor(lv);
        this.mainThreadExecutor = executor2;
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

    public boolean tick(ThreadedAnvilChunkStorage arg3) {
        boolean bl;
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
        boolean bl2 = bl = i != 0;
        if (bl) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(arg2 -> arg2.tick(arg3));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longIterator = this.chunkPositions.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                if (!this.getTicketSet(l).stream().anyMatch(arg -> arg.getType() == ChunkTicketType.PLAYER)) continue;
                ChunkHolder lv = arg3.getCurrentChunkHolder(l);
                if (lv == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = lv.getEntityTickingFuture();
                completableFuture.thenAccept(either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, l, false))));
            }
            this.chunkPositions.clear();
        }
        return bl;
    }

    private void addTicket(long l, ChunkTicket<?> arg) {
        SortedArraySet<ChunkTicket<?>> lv = this.getTicketSet(l);
        int i = ChunkTicketManager.getLevel(lv);
        ChunkTicket<?> lv2 = lv.addAndGet(arg);
        lv2.setTickCreated(this.age);
        if (arg.getLevel() < i) {
            this.distanceFromTicketTracker.updateLevel(l, arg.getLevel(), true);
        }
    }

    private void removeTicket(long l, ChunkTicket<?> arg) {
        SortedArraySet<ChunkTicket<?>> lv = this.getTicketSet(l);
        if (lv.remove(arg)) {
            // empty if block
        }
        if (lv.isEmpty()) {
            this.ticketsByPosition.remove(l);
        }
        this.distanceFromTicketTracker.updateLevel(l, ChunkTicketManager.getLevel(lv), false);
    }

    public <T> void addTicketWithLevel(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        this.addTicket(arg2.toLong(), new ChunkTicket<T>(arg, i, object));
    }

    public <T> void removeTicketWithLevel(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        ChunkTicket<T> lv = new ChunkTicket<T>(arg, i, object);
        this.removeTicket(arg2.toLong(), lv);
    }

    public <T> void addTicket(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        this.addTicket(arg2.toLong(), new ChunkTicket<T>(arg, 33 - i, object));
    }

    public <T> void removeTicket(ChunkTicketType<T> arg, ChunkPos arg2, int i, T object) {
        ChunkTicket<T> lv = new ChunkTicket<T>(arg, 33 - i, object);
        this.removeTicket(arg2.toLong(), lv);
    }

    private SortedArraySet<ChunkTicket<?>> getTicketSet(long l2) {
        return (SortedArraySet)this.ticketsByPosition.computeIfAbsent(l2, l -> SortedArraySet.create(4));
    }

    protected void setChunkForced(ChunkPos arg, boolean bl) {
        ChunkTicket<ChunkPos> lv = new ChunkTicket<ChunkPos>(ChunkTicketType.FORCED, 31, arg);
        if (bl) {
            this.addTicket(arg.toLong(), lv);
        } else {
            this.removeTicket(arg.toLong(), lv);
        }
    }

    public void handleChunkEnter(ChunkSectionPos arg, ServerPlayerEntity arg2) {
        long l2 = arg.toChunkPos().toLong();
        ((ObjectSet)this.playersByChunkPos.computeIfAbsent(l2, l -> new ObjectOpenHashSet())).add((Object)arg2);
        this.distanceFromNearestPlayerTracker.updateLevel(l2, 0, true);
        this.nearbyChunkTicketUpdater.updateLevel(l2, 0, true);
    }

    public void handleChunkLeave(ChunkSectionPos arg, ServerPlayerEntity arg2) {
        long l = arg.toChunkPos().toLong();
        ObjectSet objectSet = (ObjectSet)this.playersByChunkPos.get(l);
        objectSet.remove((Object)arg2);
        if (objectSet.isEmpty()) {
            this.playersByChunkPos.remove(l);
            this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
        }
    }

    protected String getTicket(long l) {
        String string2;
        SortedArraySet lv = (SortedArraySet)this.ticketsByPosition.get(l);
        if (lv == null || lv.isEmpty()) {
            String string = "no_ticket";
        } else {
            string2 = ((ChunkTicket)lv.first()).toString();
        }
        return string2;
    }

    protected void setWatchDistance(int i) {
        this.nearbyChunkTicketUpdater.setWatchDistance(i);
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
        protected int getInitialLevel(long l) {
            SortedArraySet lv = (SortedArraySet)ChunkTicketManager.this.ticketsByPosition.get(l);
            if (lv == null) {
                return Integer.MAX_VALUE;
            }
            if (lv.isEmpty()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)lv.first()).getLevel();
        }

        @Override
        protected int getLevel(long l) {
            ChunkHolder lv;
            if (!ChunkTicketManager.this.isUnloaded(l) && (lv = ChunkTicketManager.this.getChunkHolder(l)) != null) {
                return lv.getLevel();
            }
            return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }

        @Override
        protected void setLevel(long l, int i) {
            int j;
            ChunkHolder lv = ChunkTicketManager.this.getChunkHolder(l);
            int n = j = lv == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : lv.getLevel();
            if (j == i) {
                return;
            }
            if ((lv = ChunkTicketManager.this.setLevel(l, i, lv, j)) != null) {
                ChunkTicketManager.this.chunkHolders.add(lv);
            }
        }

        public int update(int i) {
            return this.applyPendingUpdates(i);
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
        protected void onDistanceChange(long l, int i, int j) {
            this.positionsAffected.add(l);
        }

        public void setWatchDistance(int i) {
            for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                byte b = entry.getByteValue();
                long l = entry.getLongKey();
                this.updateTicket(l, b, this.isWithinViewDistance(b), b <= i - 2);
            }
            this.watchDistance = i;
        }

        private void updateTicket(long l, int i, boolean bl, boolean bl2) {
            if (bl != bl2) {
                ChunkTicket<ChunkPos> lv = new ChunkTicket<ChunkPos>(ChunkTicketType.PLAYER, NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(l));
                if (bl2) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        if (this.isWithinViewDistance(this.getLevel(l))) {
                            ChunkTicketManager.this.addTicket(l, lv);
                            ChunkTicketManager.this.chunkPositions.add(l);
                        } else {
                            ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, l, false));
                        }
                    }), l, () -> i));
                } else {
                    ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(l, lv)), l, true));
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

        private boolean isWithinViewDistance(int i) {
            return i <= this.watchDistance - 2;
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
        protected int getLevel(long l) {
            return this.distanceFromNearestPlayer.get(l);
        }

        @Override
        protected void setLevel(long l, int i) {
            byte c;
            if (i > this.maxDistance) {
                byte b = this.distanceFromNearestPlayer.remove(l);
            } else {
                c = this.distanceFromNearestPlayer.put(l, (byte)i);
            }
            this.onDistanceChange(l, c, i);
        }

        protected void onDistanceChange(long l, int i, int j) {
        }

        @Override
        protected int getInitialLevel(long l) {
            return this.isPlayerInChunk(l) ? 0 : Integer.MAX_VALUE;
        }

        private boolean isPlayerInChunk(long l) {
            ObjectSet objectSet = (ObjectSet)ChunkTicketManager.this.playersByChunkPos.get(l);
            return objectSet != null && !objectSet.isEmpty();
        }

        public void updateLevels() {
            this.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }
}

