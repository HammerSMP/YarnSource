/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameSession
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class ClientGameSession
implements GameSession {
    private final int playerCount;
    private final boolean remoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID sessionId;

    public ClientGameSession(ClientWorld arg, ClientPlayerEntity arg2, ClientPlayNetworkHandler arg3) {
        this.playerCount = arg3.getPlayerList().size();
        this.remoteServer = !arg3.getConnection().isLocal();
        this.difficulty = arg.getDifficulty().getName();
        PlayerListEntry lv = arg3.getPlayerListEntry(arg2.getUuid());
        this.gameMode = lv != null ? lv.getGameMode().getName() : "unknown";
        this.sessionId = arg3.getSessionId();
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public boolean isRemoteServer() {
        return this.remoteServer;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public UUID getSessionId() {
        return this.sessionId;
    }
}

