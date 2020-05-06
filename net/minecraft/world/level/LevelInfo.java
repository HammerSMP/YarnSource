/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level;

import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorOptions;

public final class LevelInfo {
    private final String field_24105;
    private final long seed;
    private final GameMode gameMode;
    private final boolean structures;
    private final boolean hardcore;
    private final LevelGeneratorOptions generatorOptions;
    private final Difficulty field_24106;
    private boolean commands;
    private boolean bonusChest;
    private final GameRules field_24107;

    public LevelInfo(String string, long l, GameMode arg, boolean bl, boolean bl2, Difficulty arg2, LevelGeneratorOptions arg3) {
        this(string, l, arg, bl, bl2, arg2, arg3, new GameRules());
    }

    public LevelInfo(String string, long l, GameMode arg, boolean bl, boolean bl2, Difficulty arg2, LevelGeneratorOptions arg3, GameRules arg4) {
        this.field_24105 = string;
        this.seed = l;
        this.gameMode = arg;
        this.structures = bl;
        this.hardcore = bl2;
        this.generatorOptions = arg3;
        this.field_24106 = arg2;
        this.field_24107 = arg4;
    }

    public LevelInfo setBonusChest() {
        this.bonusChest = true;
        return this;
    }

    public LevelInfo enableCommands() {
        this.commands = true;
        return this;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public long getSeed() {
        return this.seed;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public boolean hasStructures() {
        return this.structures;
    }

    public LevelGeneratorOptions getGeneratorOptions() {
        return this.generatorOptions;
    }

    public boolean allowCommands() {
        return this.commands;
    }

    public String method_27339() {
        return this.field_24105;
    }

    public Difficulty method_27340() {
        return this.field_24106;
    }

    public GameRules method_27341() {
        return this.field_24107;
    }
}

