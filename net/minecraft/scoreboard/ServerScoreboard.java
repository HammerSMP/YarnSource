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

    public ServerScoreboard(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    @Override
    public void updateScore(ScoreboardPlayerScore arg) {
        super.updateScore(arg);
        if (this.objectives.contains(arg.getObjective())) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, arg.getObjective().getName(), arg.getPlayerName(), arg.getScore()));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String string) {
        super.updatePlayerScore(string);
        this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, null, string, 0));
        this.runUpdateListeners();
    }

    @Override
    public void updatePlayerScore(String string, ScoreboardObjective arg) {
        super.updatePlayerScore(string, arg);
        if (this.objectives.contains(arg)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.REMOVE, arg.getName(), string, 0));
        }
        this.runUpdateListeners();
    }

    @Override
    public void setObjectiveSlot(int i, @Nullable ScoreboardObjective arg) {
        ScoreboardObjective lv = this.getObjectiveForSlot(i);
        super.setObjectiveSlot(i, arg);
        if (lv != arg && lv != null) {
            if (this.getSlot(lv) > 0) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, arg));
            } else {
                this.removeScoreboardObjective(lv);
            }
        }
        if (arg != null) {
            if (this.objectives.contains(arg)) {
                this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(i, arg));
            } else {
                this.addScoreboardObjective(arg);
            }
        }
        this.runUpdateListeners();
    }

    @Override
    public boolean addPlayerToTeam(String string, Team arg) {
        if (super.addPlayerToTeam(string, arg)) {
            this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, Arrays.asList(string), 3));
            this.runUpdateListeners();
            return true;
        }
        return false;
    }

    @Override
    public void removePlayerFromTeam(String string, Team arg) {
        super.removePlayerFromTeam(string, arg);
        this.server.getPlayerManager().sendToAll(new TeamS2CPacket(arg, Arrays.asList(string), 4));
        this.runUpdateListeners();
    }

    @Override
    public void updateObjective(ScoreboardObjective arg) {
        super.updateObjective(arg);
        this.runUpdateListeners();
    }

    @Override
    public void updateExistingObjective(ScoreboardObjective arg) {
        super.updateExistingObjective(arg);
        if (this.objectives.contains(arg)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(arg, 2));
        }
        this.runUpdateListeners();
    }

    @Override
    public void updateRemovedObjective(ScoreboardObjective arg) {
        super.updateRemovedObjective(arg);
        if (this.objectives.contains(arg)) {
            this.removeScoreboardObjective(arg);
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

    public void addUpdateListener(Runnable runnable) {
        this.updateListeners = Arrays.copyOf(this.updateListeners, this.updateListeners.length + 1);
        this.updateListeners[this.updateListeners.length - 1] = runnable;
    }

    protected void runUpdateListeners() {
        for (Runnable runnable : this.updateListeners) {
            runnable.run();
        }
    }

    public List<Packet<?>> createChangePackets(ScoreboardObjective arg) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(arg, 0));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != arg) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, arg));
        }
        for (ScoreboardPlayerScore lv : this.getAllPlayerScores(arg)) {
            list.add(new ScoreboardPlayerUpdateS2CPacket(UpdateMode.CHANGE, lv.getObjective().getName(), lv.getPlayerName(), lv.getScore()));
        }
        return list;
    }

    public void addScoreboardObjective(ScoreboardObjective arg) {
        List<Packet<?>> list = this.createChangePackets(arg);
        for (ServerPlayerEntity lv : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> lv2 : list) {
                lv.networkHandler.sendPacket(lv2);
            }
        }
        this.objectives.add(arg);
    }

    public List<Packet<?>> createRemovePackets(ScoreboardObjective arg) {
        ArrayList list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(arg, 1));
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != arg) continue;
            list.add(new ScoreboardDisplayS2CPacket(i, arg));
        }
        return list;
    }

    public void removeScoreboardObjective(ScoreboardObjective arg) {
        List<Packet<?>> list = this.createRemovePackets(arg);
        for (ServerPlayerEntity lv : this.server.getPlayerManager().getPlayerList()) {
            for (Packet<?> lv2 : list) {
                lv.networkHandler.sendPacket(lv2);
            }
        }
        this.objectives.remove(arg);
    }

    public int getSlot(ScoreboardObjective arg) {
        int i = 0;
        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveForSlot(j) != arg) continue;
            ++i;
        }
        return i;
    }

    public static enum UpdateMode {
        CHANGE,
        REMOVE;

    }
}

