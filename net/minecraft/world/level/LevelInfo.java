/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 */
package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import net.minecraft.resource.DataPackSettings;
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
    private final DataPackSettings field_25403;

    public LevelInfo(String name, GameMode arg, boolean bl, Difficulty arg2, boolean bl2, GameRules arg3, DataPackSettings arg4) {
        this.name = name;
        this.gameMode = arg;
        this.structures = bl;
        this.difficulty = arg2;
        this.hardcore = bl2;
        this.gameRules = arg3;
        this.field_25403 = arg4;
    }

    public static LevelInfo method_28383(Dynamic<?> dynamic, DataPackSettings arg) {
        GameMode lv;
        return new LevelInfo(dynamic.get("LevelName").asString(""), lv, dynamic.get("hardcore").asBoolean(false), dynamic.get("Difficulty").asNumber().map(number -> Difficulty.byOrdinal(number.byteValue())).result().orElse(Difficulty.NORMAL), dynamic.get("allowCommands").asBoolean((lv = GameMode.byId(dynamic.get("GameType").asInt(0))) == GameMode.CREATIVE), new GameRules((DynamicLike<?>)dynamic.get("GameRules")), arg);
    }

    public String getLevelName() {
        return this.name;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean hasStructures() {
        return this.structures;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    public DataPackSettings method_29558() {
        return this.field_25403;
    }

    public LevelInfo method_28382(GameMode arg) {
        return new LevelInfo(this.name, arg, this.structures, this.difficulty, this.hardcore, this.gameRules, this.field_25403);
    }

    public LevelInfo method_28381(Difficulty arg) {
        return new LevelInfo(this.name, this.gameMode, this.structures, arg, this.hardcore, this.gameRules, this.field_25403);
    }

    public LevelInfo method_29557(DataPackSettings arg) {
        return new LevelInfo(this.name, this.gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules, arg);
    }

    public LevelInfo method_28385() {
        return new LevelInfo(this.name, this.gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules.copy(), this.field_25403);
    }
}

