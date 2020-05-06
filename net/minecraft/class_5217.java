/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 */
package net.minecraft;

import com.google.common.hash.Hashing;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;

public interface class_5217 {
    public long getSeed();

    public static long method_27418(long l) {
        return Hashing.sha256().hashLong(l).asLong();
    }

    public int getSpawnX();

    public int getSpawnY();

    public int getSpawnZ();

    public long getTime();

    public long getTimeOfDay();

    public boolean isThundering();

    public boolean isRaining();

    public void setRaining(boolean var1);

    public boolean isHardcore();

    public LevelGeneratorType getGeneratorType();

    public LevelGeneratorOptions method_27421();

    public GameRules getGameRules();

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    default public void populateCrashReport(CrashReportSection arg) {
        arg.add("Level seed", () -> String.valueOf(this.getSeed()));
        arg.add("Level generator options", () -> this.method_27421().getDynamic().toString());
        arg.add("Level spawn location", () -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()));
        arg.add("Level time", () -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay()));
    }
}

