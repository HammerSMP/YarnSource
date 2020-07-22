/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Scoreboard {
    private final Map<String, ScoreboardObjective> objectives = Maps.newHashMap();
    private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.newHashMap();
    private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.newHashMap();
    private final ScoreboardObjective[] objectiveSlots = new ScoreboardObjective[19];
    private final Map<String, Team> teams = Maps.newHashMap();
    private final Map<String, Team> teamsByPlayer = Maps.newHashMap();
    private static String[] displaySlotNames;

    @Environment(value=EnvType.CLIENT)
    public boolean containsObjective(String name) {
        return this.objectives.containsKey(name);
    }

    public ScoreboardObjective getObjective(String name) {
        return this.objectives.get(name);
    }

    @Nullable
    public ScoreboardObjective getNullableObjective(@Nullable String name) {
        return this.objectives.get(name);
    }

    public ScoreboardObjective addObjective(String name, ScoreboardCriterion criterion2, Text displayName, ScoreboardCriterion.RenderType renderType) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        }
        if (this.objectives.containsKey(name)) {
            throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
        }
        ScoreboardObjective lv = new ScoreboardObjective(this, name, criterion2, displayName, renderType);
        this.objectivesByCriterion.computeIfAbsent(criterion2, criterion -> Lists.newArrayList()).add(lv);
        this.objectives.put(name, lv);
        this.updateObjective(lv);
        return lv;
    }

    public final void forEachScore(ScoreboardCriterion criterion, String player, Consumer<ScoreboardPlayerScore> action) {
        this.objectivesByCriterion.getOrDefault(criterion, Collections.emptyList()).forEach(objective -> action.accept(this.getPlayerScore(player, (ScoreboardObjective)objective)));
    }

    public boolean playerHasObjective(String playerName, ScoreboardObjective objective) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(playerName);
        if (map == null) {
            return false;
        }
        ScoreboardPlayerScore lv = map.get(objective);
        return lv != null;
    }

    public ScoreboardPlayerScore getPlayerScore(String player, ScoreboardObjective objective2) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        }
        Map map = this.playerObjectives.computeIfAbsent(player, string -> Maps.newHashMap());
        return map.computeIfAbsent(objective2, objective -> {
            ScoreboardPlayerScore lv = new ScoreboardPlayerScore(this, (ScoreboardObjective)objective, player);
            lv.setScore(0);
            return lv;
        });
    }

    public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective objective) {
        ArrayList list = Lists.newArrayList();
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            ScoreboardPlayerScore lv = map.get(objective);
            if (lv == null) continue;
            list.add(lv);
        }
        list.sort(ScoreboardPlayerScore.COMPARATOR);
        return list;
    }

    public Collection<ScoreboardObjective> getObjectives() {
        return this.objectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectives.keySet();
    }

    public Collection<String> getKnownPlayers() {
        return Lists.newArrayList(this.playerObjectives.keySet());
    }

    public void resetPlayerScore(String playerName, @Nullable ScoreboardObjective objective) {
        if (objective == null) {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.remove(playerName);
            if (map != null) {
                this.updatePlayerScore(playerName);
            }
        } else {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = this.playerObjectives.get(playerName);
            if (map2 != null) {
                ScoreboardPlayerScore lv = map2.remove(objective);
                if (map2.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.remove(playerName);
                    if (map3 != null) {
                        this.updatePlayerScore(playerName);
                    }
                } else if (lv != null) {
                    this.updatePlayerScore(playerName, objective);
                }
            }
        }
    }

    public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(String string) {
        HashMap map = this.playerObjectives.get(string);
        if (map == null) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public void removeObjective(ScoreboardObjective objective) {
        this.objectives.remove(objective.getName());
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != objective) continue;
            this.setObjectiveSlot(i, null);
        }
        List<ScoreboardObjective> list = this.objectivesByCriterion.get(objective.getCriterion());
        if (list != null) {
            list.remove(objective);
        }
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            map.remove(objective);
        }
        this.updateRemovedObjective(objective);
    }

    public void setObjectiveSlot(int slot, @Nullable ScoreboardObjective objective) {
        this.objectiveSlots[slot] = objective;
    }

    @Nullable
    public ScoreboardObjective getObjectiveForSlot(int i) {
        return this.objectiveSlots[i];
    }

    public Team getTeam(String string) {
        return this.teams.get(string);
    }

    public Team addTeam(String string) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The team name '" + string + "' is too long!");
        }
        Team lv = this.getTeam(string);
        if (lv != null) {
            throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
        }
        lv = new Team(this, string);
        this.teams.put(string, lv);
        this.updateScoreboardTeamAndPlayers(lv);
        return lv;
    }

    public void removeTeam(Team arg) {
        this.teams.remove(arg.getName());
        for (String string : arg.getPlayerList()) {
            this.teamsByPlayer.remove(string);
        }
        this.updateRemovedTeam(arg);
    }

    public boolean addPlayerToTeam(String playerName, Team arg) {
        if (playerName.length() > 40) {
            throw new IllegalArgumentException("The player name '" + playerName + "' is too long!");
        }
        if (this.getPlayerTeam(playerName) != null) {
            this.clearPlayerTeam(playerName);
        }
        this.teamsByPlayer.put(playerName, arg);
        return arg.getPlayerList().add(playerName);
    }

    public boolean clearPlayerTeam(String string) {
        Team lv = this.getPlayerTeam(string);
        if (lv != null) {
            this.removePlayerFromTeam(string, lv);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String playerName, Team arg) {
        if (this.getPlayerTeam(playerName) != arg) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + arg.getName() + "'.");
        }
        this.teamsByPlayer.remove(playerName);
        arg.getPlayerList().remove(playerName);
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<Team> getTeams() {
        return this.teams.values();
    }

    @Nullable
    public Team getPlayerTeam(String string) {
        return this.teamsByPlayer.get(string);
    }

    public void updateObjective(ScoreboardObjective objective) {
    }

    public void updateExistingObjective(ScoreboardObjective objective) {
    }

    public void updateRemovedObjective(ScoreboardObjective objective) {
    }

    public void updateScore(ScoreboardPlayerScore score) {
    }

    public void updatePlayerScore(String playerName) {
    }

    public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
    }

    public void updateScoreboardTeamAndPlayers(Team arg) {
    }

    public void updateScoreboardTeam(Team arg) {
    }

    public void updateRemovedTeam(Team arg) {
    }

    public static String getDisplaySlotName(int slotId) {
        Formatting lv;
        switch (slotId) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
        }
        if (slotId >= 3 && slotId <= 18 && (lv = Formatting.byColorIndex(slotId - 3)) != null && lv != Formatting.RESET) {
            return "sidebar.team." + lv.getName();
        }
        return null;
    }

    public static int getDisplaySlotId(String slotName) {
        String string2;
        Formatting lv;
        if ("list".equalsIgnoreCase(slotName)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(slotName)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(slotName)) {
            return 2;
        }
        if (slotName.startsWith("sidebar.team.") && (lv = Formatting.byName(string2 = slotName.substring("sidebar.team.".length()))) != null && lv.getColorIndex() >= 0) {
            return lv.getColorIndex() + 3;
        }
        return -1;
    }

    public static String[] getDisplaySlotNames() {
        if (displaySlotNames == null) {
            displaySlotNames = new String[19];
            for (int i = 0; i < 19; ++i) {
                Scoreboard.displaySlotNames[i] = Scoreboard.getDisplaySlotName(i);
            }
        }
        return displaySlotNames;
    }

    public void resetEntityScore(Entity arg) {
        if (arg == null || arg instanceof PlayerEntity || arg.isAlive()) {
            return;
        }
        String string = arg.getUuidAsString();
        this.resetPlayerScore(string, null);
        this.clearPlayerTeam(string);
    }

    protected ListTag toTag() {
        ListTag lv = new ListTag();
        this.playerObjectives.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(score -> score.getObjective() != null).forEach(score -> {
            CompoundTag lv = new CompoundTag();
            lv.putString("Name", score.getPlayerName());
            lv.putString("Objective", score.getObjective().getName());
            lv.putInt("Score", score.getScore());
            lv.putBoolean("Locked", score.isLocked());
            lv.add(lv);
        }));
        return lv;
    }

    protected void fromTag(ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            CompoundTag lv = arg.getCompound(i);
            ScoreboardObjective lv2 = this.getObjective(lv.getString("Objective"));
            String string = lv.getString("Name");
            if (string.length() > 40) {
                string = string.substring(0, 40);
            }
            ScoreboardPlayerScore lv3 = this.getPlayerScore(string, lv2);
            lv3.setScore(lv.getInt("Score"));
            if (!lv.contains("Locked")) continue;
            lv3.setLocked(lv.getBoolean("Locked"));
        }
    }
}

