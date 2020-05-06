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
    public boolean containsObjective(String string) {
        return this.objectives.containsKey(string);
    }

    public ScoreboardObjective getObjective(String string) {
        return this.objectives.get(string);
    }

    @Nullable
    public ScoreboardObjective getNullableObjective(@Nullable String string) {
        return this.objectives.get(string);
    }

    public ScoreboardObjective addObjective(String string, ScoreboardCriterion arg2, Text arg22, ScoreboardCriterion.RenderType arg3) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
        }
        if (this.objectives.containsKey(string)) {
            throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
        }
        ScoreboardObjective lv = new ScoreboardObjective(this, string, arg2, arg22, arg3);
        this.objectivesByCriterion.computeIfAbsent(arg2, arg -> Lists.newArrayList()).add(lv);
        this.objectives.put(string, lv);
        this.updateObjective(lv);
        return lv;
    }

    public final void forEachScore(ScoreboardCriterion arg2, String string, Consumer<ScoreboardPlayerScore> consumer) {
        this.objectivesByCriterion.getOrDefault(arg2, Collections.emptyList()).forEach(arg -> consumer.accept(this.getPlayerScore(string, (ScoreboardObjective)arg)));
    }

    public boolean playerHasObjective(String string, ScoreboardObjective arg) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(string);
        if (map == null) {
            return false;
        }
        ScoreboardPlayerScore lv = map.get(arg);
        return lv != null;
    }

    public ScoreboardPlayerScore getPlayerScore(String string2, ScoreboardObjective arg2) {
        if (string2.length() > 40) {
            throw new IllegalArgumentException("The player name '" + string2 + "' is too long!");
        }
        Map map = this.playerObjectives.computeIfAbsent(string2, string -> Maps.newHashMap());
        return map.computeIfAbsent(arg2, arg -> {
            ScoreboardPlayerScore lv = new ScoreboardPlayerScore(this, (ScoreboardObjective)arg, string2);
            lv.setScore(0);
            return lv;
        });
    }

    public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective arg) {
        ArrayList list = Lists.newArrayList();
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            ScoreboardPlayerScore lv = map.get(arg);
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

    public void resetPlayerScore(String string, @Nullable ScoreboardObjective arg) {
        if (arg == null) {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.remove(string);
            if (map != null) {
                this.updatePlayerScore(string);
            }
        } else {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = this.playerObjectives.get(string);
            if (map2 != null) {
                ScoreboardPlayerScore lv = map2.remove(arg);
                if (map2.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardPlayerScore> map3 = this.playerObjectives.remove(string);
                    if (map3 != null) {
                        this.updatePlayerScore(string);
                    }
                } else if (lv != null) {
                    this.updatePlayerScore(string, arg);
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

    public void removeObjective(ScoreboardObjective arg) {
        this.objectives.remove(arg.getName());
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != arg) continue;
            this.setObjectiveSlot(i, null);
        }
        List<ScoreboardObjective> list = this.objectivesByCriterion.get(arg.getCriterion());
        if (list != null) {
            list.remove(arg);
        }
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            map.remove(arg);
        }
        this.updateRemovedObjective(arg);
    }

    public void setObjectiveSlot(int i, @Nullable ScoreboardObjective arg) {
        this.objectiveSlots[i] = arg;
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

    public boolean addPlayerToTeam(String string, Team arg) {
        if (string.length() > 40) {
            throw new IllegalArgumentException("The player name '" + string + "' is too long!");
        }
        if (this.getPlayerTeam(string) != null) {
            this.clearPlayerTeam(string);
        }
        this.teamsByPlayer.put(string, arg);
        return arg.getPlayerList().add(string);
    }

    public boolean clearPlayerTeam(String string) {
        Team lv = this.getPlayerTeam(string);
        if (lv != null) {
            this.removePlayerFromTeam(string, lv);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String string, Team arg) {
        if (this.getPlayerTeam(string) != arg) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + arg.getName() + "'.");
        }
        this.teamsByPlayer.remove(string);
        arg.getPlayerList().remove(string);
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

    public void updateObjective(ScoreboardObjective arg) {
    }

    public void updateExistingObjective(ScoreboardObjective arg) {
    }

    public void updateRemovedObjective(ScoreboardObjective arg) {
    }

    public void updateScore(ScoreboardPlayerScore arg) {
    }

    public void updatePlayerScore(String string) {
    }

    public void updatePlayerScore(String string, ScoreboardObjective arg) {
    }

    public void updateScoreboardTeamAndPlayers(Team arg) {
    }

    public void updateScoreboardTeam(Team arg) {
    }

    public void updateRemovedTeam(Team arg) {
    }

    public static String getDisplaySlotName(int i) {
        Formatting lv;
        switch (i) {
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
        if (i >= 3 && i <= 18 && (lv = Formatting.byColorIndex(i - 3)) != null && lv != Formatting.RESET) {
            return "sidebar.team." + lv.getName();
        }
        return null;
    }

    public static int getDisplaySlotId(String string) {
        String string2;
        Formatting lv;
        if ("list".equalsIgnoreCase(string)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(string)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(string)) {
            return 2;
        }
        if (string.startsWith("sidebar.team.") && (lv = Formatting.byName(string2 = string.substring("sidebar.team.".length()))) != null && lv.getColorIndex() >= 0) {
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
        this.playerObjectives.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(arg -> arg.getObjective() != null).forEach(arg2 -> {
            CompoundTag lv = new CompoundTag();
            lv.putString("Name", arg2.getPlayerName());
            lv.putString("Objective", arg2.getObjective().getName());
            lv.putInt("Score", arg2.getScore());
            lv.putBoolean("Locked", arg2.isLocked());
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

