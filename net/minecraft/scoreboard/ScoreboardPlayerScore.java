/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardPlayerScore {
    public static final Comparator<ScoreboardPlayerScore> COMPARATOR = (arg, arg2) -> {
        if (arg.getScore() > arg2.getScore()) {
            return 1;
        }
        if (arg.getScore() < arg2.getScore()) {
            return -1;
        }
        return arg2.getPlayerName().compareToIgnoreCase(arg.getPlayerName());
    };
    private final Scoreboard scoreboard;
    @Nullable
    private final ScoreboardObjective objective;
    private final String playerName;
    private int score;
    private boolean locked;
    private boolean forceUpdate;

    public ScoreboardPlayerScore(Scoreboard arg, ScoreboardObjective arg2, String string) {
        this.scoreboard = arg;
        this.objective = arg2;
        this.playerName = string;
        this.locked = true;
        this.forceUpdate = true;
    }

    public void incrementScore(int i) {
        if (this.objective.getCriterion().isReadOnly()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.setScore(this.getScore() + i);
    }

    public void incrementScore() {
        this.incrementScore(1);
    }

    public int getScore() {
        return this.score;
    }

    public void clearScore() {
        this.setScore(0);
    }

    public void setScore(int i) {
        int j = this.score;
        this.score = i;
        if (j != i || this.forceUpdate) {
            this.forceUpdate = false;
            this.getScoreboard().updateScore(this);
        }
    }

    @Nullable
    public ScoreboardObjective getObjective() {
        return this.objective;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean bl) {
        this.locked = bl;
    }
}

