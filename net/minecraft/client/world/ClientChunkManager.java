/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.world;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientChunkManager
extends ChunkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final WorldChunk emptyChunk;
    private final LightingProvider lightingProvider;
    private volatile ClientChunkMap chunks;
    private final ClientWorld world;

    public ClientChunkManager(ClientWorld arg, int i) {
        this.world = arg;
        this.emptyChunk = new EmptyChunk((World)arg, new ChunkPos(0, 0));
        this.lightingProvider = new LightingProvider(this, true, arg.getDimension().hasSkyLight());
        this.chunks = new ClientChunkMap(ClientChunkManager.getChunkMapRadius(i));
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    private static boolean positionEquals(@Nullable WorldChunk arg, int i, int j) {
        if (arg == null) {
            return false;
        }
        ChunkPos lv = arg.getPos();
        return lv.x == i && lv.z == j;
    }

    public void unload(int i, int j) {
        if (!this.chunks.isInRadius(i, j)) {
            return;
        }
        int k = this.chunks.getIndex(i, j);
        WorldChunk lv = this.chunks.getChunk(k);
        if (ClientChunkManager.positionEquals(lv, i, j)) {
            this.chunks.compareAndSet(k, lv, null);
        }
    }

    @Override
    @Nullable
    public WorldChunk getChunk(int i, int j, ChunkStatus arg, boolean bl) {
        WorldChunk lv;
        if (this.chunks.isInRadius(i, j) && ClientChunkManager.positionEquals(lv = this.chunks.getChunk(this.chunks.getIndex(i, j)), i, j)) {
            return lv;
        }
        if (bl) {
            return this.emptyChunk;
        }
        return null;
    }

    @Override
    public BlockView getWorld() {
        return this.world;
    }

    @Nullable
    public WorldChunk loadChunkFromPacket(int i, int j, @Nullable BiomeArray arg, PacketByteBuf arg2, CompoundTag arg3, int k, boolean bl) {
        if (!this.chunks.isInRadius(i, j)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)i, (Object)j);
            return null;
        }
        int l = this.chunks.getIndex(i, j);
        WorldChunk lv = (WorldChunk)this.chunks.chunks.get(l);
        if (bl || !ClientChunkManager.positionEquals(lv, i, j)) {
            if (arg == null) {
                LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", (Object)i, (Object)j);
                return null;
            }
            lv = new WorldChunk(this.world, new ChunkPos(i, j), arg);
            lv.loadFromPacket(arg, arg2, arg3, k);
            this.chunks.set(l, lv);
        } else {
            lv.loadFromPacket(arg, arg2, arg3, k);
        }
        ChunkSection[] lvs = lv.getSectionArray();
        LightingProvider lv2 = this.getLightingProvider();
        lv2.setLightEnabled(new ChunkPos(i, j), true);
        for (int m = 0; m < lvs.length; ++m) {
            ChunkSection lv3 = lvs[m];
            lv2.updateSectionStatus(ChunkSectionPos.from(i, m, j), ChunkSection.isEmpty(lv3));
        }
        this.world.resetChunkColor(i, j);
        return lv;
    }

    public void tick(BooleanSupplier booleanSupplier) {
    }

    public void setChunkMapCenter(int i, int j) {
        this.chunks.centerChunkX = i;
        this.chunks.centerChunkZ = j;
    }

    public void updateLoadDistance(int i) {
        int k;
        int j = this.chunks.radius;
        if (j != (k = ClientChunkManager.getChunkMapRadius(i))) {
            ClientChunkMap lv = new ClientChunkMap(k);
            lv.centerChunkX = this.chunks.centerChunkX;
            lv.centerChunkZ = this.chunks.centerChunkZ;
            for (int l = 0; l < this.chunks.chunks.length(); ++l) {
                WorldChunk lv2 = (WorldChunk)this.chunks.chunks.get(l);
                if (lv2 == null) continue;
                ChunkPos lv3 = lv2.getPos();
                if (!lv.isInRadius(lv3.x, lv3.z)) continue;
                lv.set(lv.getIndex(lv3.x, lv3.z), lv2);
            }
            this.chunks = lv;
        }
    }

    private static int getChunkMapRadius(int i) {
        return Math.max(2, i) + 3;
    }

    @Override
    public String getDebugString() {
        return "Client Chunk Cache: " + this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
    }

    public int getLoadedChunkCount() {
        return this.chunks.loadedChunkCount;
    }

    @Override
    public void onLightUpdate(LightType arg, ChunkSectionPos arg2) {
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(arg2.getSectionX(), arg2.getSectionY(), arg2.getSectionZ());
    }

    @Override
    public boolean shouldTickBlock(BlockPos arg) {
        return this.isChunkLoaded(arg.getX() >> 4, arg.getZ() >> 4);
    }

    @Override
    public boolean shouldTickChunk(ChunkPos arg) {
        return this.isChunkLoaded(arg.x, arg.z);
    }

    @Override
    public boolean shouldTickEntity(Entity arg) {
        return this.isChunkLoaded(MathHelper.floor(arg.getX()) >> 4, MathHelper.floor(arg.getZ()) >> 4);
    }

    @Override
    @Nullable
    public /* synthetic */ Chunk getChunk(int i, int j, ChunkStatus arg, boolean bl) {
        return this.getChunk(i, j, arg, bl);
    }

    @Environment(value=EnvType.CLIENT)
    final class ClientChunkMap {
        private final AtomicReferenceArray<WorldChunk> chunks;
        private final int radius;
        private final int diameter;
        private volatile int centerChunkX;
        private volatile int centerChunkZ;
        private int loadedChunkCount;

        private ClientChunkMap(int i) {
            this.radius = i;
            this.diameter = i * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
        }

        private int getIndex(int i, int j) {
            return Math.floorMod(j, this.diameter) * this.diameter + Math.floorMod(i, this.diameter);
        }

        protected void set(int i, @Nullable WorldChunk arg) {
            WorldChunk lv = this.chunks.getAndSet(i, arg);
            if (lv != null) {
                --this.loadedChunkCount;
                ClientChunkManager.this.world.unloadBlockEntities(lv);
            }
            if (arg != null) {
                ++this.loadedChunkCount;
            }
        }

        protected WorldChunk compareAndSet(int i, WorldChunk arg, @Nullable WorldChunk arg2) {
            if (this.chunks.compareAndSet(i, arg, arg2) && arg2 == null) {
                --this.loadedChunkCount;
            }
            ClientChunkManager.this.world.unloadBlockEntities(arg);
            return arg;
        }

        private boolean isInRadius(int i, int j) {
            return Math.abs(i - this.centerChunkX) <= this.radius && Math.abs(j - this.centerChunkZ) <= this.radius;
        }

        @Nullable
        protected WorldChunk getChunk(int i) {
            return this.chunks.get(i);
        }
    }
}

