/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5268;
import net.minecraft.class_5285;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;

public interface class_5219 {
    public Set<String> getDisabledDataPacks();

    public Set<String> getEnabledDataPacks();

    public boolean isModded();

    public Set<String> getServerBrands();

    public void addServerBrand(String var1, boolean var2);

    default public void populateCrashReport(CrashReportSection arg) {
        arg.add("Known server brands", () -> String.join((CharSequence)", ", this.getServerBrands()));
        arg.add("Level was modded", () -> Boolean.toString(this.isModded()));
        arg.add("Level storage version", () -> {
            int i = this.getVersion();
            return String.format("0x%05X - %s", i, this.getFormatName(i));
        });
    }

    default public String getFormatName(int i) {
        switch (i) {
            case 19133: {
                return "Anvil";
            }
            case 19132: {
                return "McRegion";
            }
        }
        return "Unknown?";
    }

    @Nullable
    public CompoundTag getCustomBossEvents();

    public void setCustomBossEvents(@Nullable CompoundTag var1);

    public class_5268 method_27859();

    @Environment(value=EnvType.CLIENT)
    public LevelInfo getLevelInfo();

    public CompoundTag cloneWorldTag(@Nullable CompoundTag var1);

    public boolean isHardcore();

    public int getVersion();

    public String getLevelName();

    public GameMode getGameMode();

    public void setGameMode(GameMode var1);

    public boolean areCommandsAllowed();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRules();

    public CompoundTag getPlayerData();

    public CompoundTag method_29036();

    public void method_29037(CompoundTag var1);

    public class_5285 method_28057();
}

