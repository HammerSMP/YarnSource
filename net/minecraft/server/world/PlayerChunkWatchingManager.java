/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 */
package net.minecraft.server.world;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerChunkWatchingManager {
    private final Object2BooleanMap<ServerPlayerEntity> watchingPlayers = new Object2BooleanOpenHashMap();

    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(long l) {
        return this.watchingPlayers.keySet().stream();
    }

    public void add(long l, ServerPlayerEntity arg, boolean watchDisabled) {
        this.watchingPlayers.put((Object)arg, watchDisabled);
    }

    public void remove(long l, ServerPlayerEntity arg) {
        this.watchingPlayers.removeBoolean((Object)arg);
    }

    public void disableWatch(ServerPlayerEntity arg) {
        this.watchingPlayers.replace((Object)arg, true);
    }

    public void enableWatch(ServerPlayerEntity arg) {
        this.watchingPlayers.replace((Object)arg, false);
    }

    public boolean method_21715(ServerPlayerEntity arg) {
        return this.watchingPlayers.getOrDefault((Object)arg, true);
    }

    public boolean isWatchDisabled(ServerPlayerEntity arg) {
        return this.watchingPlayers.getBoolean((Object)arg);
    }

    public void movePlayer(long prevPos, long currentPos, ServerPlayerEntity arg) {
    }
}

