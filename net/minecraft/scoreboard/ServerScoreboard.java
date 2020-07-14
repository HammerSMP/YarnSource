/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerScoreboard
extends Scoreboard {
    private final MinecraftServer server;
    private final Set<ScoreboardObjective> objectives = Sets.newHashSet();
    private Runnable[] updateListeners = new Runnable[0];

    public ServerScoreboard(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void updateScore(ScoreboardPlayerScore score) {
        super.updateScore(score);
        if (this.objectives.contains(score.getObjective())) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScore()));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String playerName) {
        super.updatePlayerScore(playerName);
        this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, null, playerName, 0));
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
        super.updatePlayerScore(playerName, objective);
        if (this.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, objective.getName(), playerName, 0));
        }
        this.runUpdateListeners();
    }

    @Override
    public void setObjectiveSlot(int slot, @Nullable ScoreboardObjective objective) {
        ScoreboardObjective lv = this.getObjectiveForSlot(slot);
        super.setObjectiveSlot(slot, objective);
        if (lv != objective && lv != null) {
            if (this.getSlot(lv) > 0) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
            } else {
                this.removeScoreboardObjective(lv);
            }
        }
        if (objective != null) {
            if (this.objectives.contains(objective)) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
            } else {
                this.addScoreboardObjective(objective);
            }
        }
        this.runUpdateListeners();
    }

    @Override
    public boolean addPlayerToTeam(String playerName, Team arg) {
        if (super.addPlayerToTeam(playerName, arg)) {
            this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, Arrays.asList(playerName), 3));
            this.runUpdateListeners();
            return true;
        }
        return false;
    }

    @Override
    public void removePlayerFromTeam(String playerName, Team arg) {
        super.removePlayerFromTeam(playerName, arg);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, Arrays.asList(playerName), 4));
        this.runUpdateListeners();
    }

    @Override
    public void updateObjective(ScoreboardObjective objective) {
        super.updateObjective(objective);
        this.runUpdateListeners();
    }

    @Override
    public void updateExistingObjective(ScoreboardObjective objective) {
        super.updateExistingObjective(objective);
        if (this.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(objective, 2));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updateRemovedObjective(ScoreboardObjective objective) {
        super.updateRemovedObjective(objective);
        if (this.objectives.contains(objective)) {
            this.removeScoreboardObjective(objective);
        }
        this.runUpdateListeners();
    }

    @Override
    public void updateScoreboardTeamAndPlayers(Team arg) {
        super.updateScoreboardTeamAndPlayers(arg);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, 0));
        this.runUpdateListeners();
    }

    @Override
    public void updateScoreboardTeam(Team arg) {
        super.updateScoreboardTeam(arg);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, 2));
        this.runUpdateListeners();
    }

    @Override
    public void updateRemovedTeam(Team arg) {
        super.updateRemovedTeam(arg);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, 1));
        this.runUpdateListeners();
    }

    public void addUpdateListener(Runnable listener) {
        this.updateListeners = Arrays.copyOf(this.updateListeners, this.updateListeners.length + 1);
        this.updateListeners[this.updateListeners.length - 1] = listener;
    }

    protected void runUpdateListeners() {
        for (Runnable runnable : this.updateListeners) {
            runnable.run();
        }
    }

    public List<Packet<?>> createChangePackets(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, objective));
        }
        for (ScoreboardPlayerScore lv : this.getAllPlayerScores(objective)) {
            list.add(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, lv.getObjective().getName(), lv.getPlayerName(), lv.getScore()));
        }
        return list;
    }

    public void addScoreboardObjective(ScoreboardObjective objective) {
        List<Packet<?>> list = this.createChangePackets(objective);
        for (ServerPlayerEntity lv : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> lv2 : list) {
                lv.networkHandler.sendPacket(lv2);
            }
        }
        this.objectives.add(objective);
    }

    public List<Packet<?>> createRemovePackets(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, objective));
        }
        return list;
    }

    public void removeScoreboardObjective(ScoreboardObjective objective) {
        List<Packet<?>> list = this.createRemovePackets(objective);
        for (ServerPlayerEntity lv : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> lv2 : list) {
                lv.networkHandler.sendPacket(lv2);
            }
        }
        this.objectives.remove(objective);
    }

    public int getSlot(ScoreboardObjective objective) {
        int i = 0;
        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveForSlot(j) != objective) continue;
            ++i;
        }
        return i;
    }

    public static enum UpdateMode {
        CHANGE,
        REMOVE;

    }
}

