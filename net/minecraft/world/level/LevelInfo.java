/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level;

import net.minecraft.class_5285;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public final class LevelInfo {
    private final String name;
    private final GameMode gameMode;
    private final boolean structures;
    private final Difficulty difficulty;
    private final boolean hardcore;
    private final GameRules gameRules;
    private final class_5285 generatorOptions;

    public LevelInfo(String string, GameMode arg, boolean bl, Difficulty arg2, boolean bl2, GameRules arg3, class_5285 arg4) {
        this.name = string;
        this.gameMode = arg;
        this.structures = bl;
        this.difficulty = arg2;
        this.hardcore = bl2;
        this.gameRules = arg3;
        this.generatorOptions = arg4;
    }

    public class_5285 getGeneratorOptions() {
        return this.generatorOptions;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean hasStructures() {
        return this.structures;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public String getLevelName() {
        return this.name;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }
}

