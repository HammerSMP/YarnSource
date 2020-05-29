/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.level;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.timer.Timer;

public class UnmodifiableLevelProperties
implements ServerWorldProperties {
    private final DimensionType dimensionType;
    private final SaveProperties field_24179;
    private final ServerWorldProperties properties;

    public UnmodifiableLevelProperties(DimensionType arg, SaveProperties arg2, ServerWorldProperties arg3) {
        this.dimensionType = arg;
        this.field_24179 = arg2;
        this.properties = arg3;
    }

    @Override
    public int getSpawnX() {
        return this.properties.getSpawnX();
    }

    @Override
    public int getSpawnY() {
        return this.properties.getSpawnY();
    }

    @Override
    public int getSpawnZ() {
        return this.properties.getSpawnZ();
    }

    @Override
    public long getTime() {
        return this.properties.getTime();
    }

    @Override
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    @Override
    public String getLevelName() {
        return this.field_24179.getLevelName();
    }

    @Override
    public int getClearWeatherTime() {
        return this.properties.getClearWeatherTime();
    }

    @Override
    public void setClearWeatherTime(int i) {
    }

    @Override
    public boolean isThundering() {
        return this.properties.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.properties.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.properties.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.properties.getRainTime();
    }

    @Override
    public GameMode getGameMode() {
        return this.field_24179.getGameMode();
    }

    @Override
    public void setSpawnX(int i) {
    }

    @Override
    public void setSpawnY(int i) {
    }

    @Override
    public void setSpawnZ(int i) {
    }

    @Override
    public void method_29034(long l) {
    }

    @Override
    public void method_29035(long l) {
    }

    @Override
    public void setSpawnPos(BlockPos arg) {
    }

    @Override
    public void setThundering(boolean bl) {
    }

    @Override
    public void setThunderTime(int i) {
    }

    @Override
    public void setRaining(boolean bl) {
    }

    @Override
    public void setRainTime(int i) {
    }

    @Override
    public void setGameMode(GameMode arg) {
    }

    @Override
    public boolean isHardcore() {
        return this.field_24179.isHardcore();
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.field_24179.areCommandsAllowed();
    }

    @Override
    public boolean isInitialized() {
        return this.properties.isInitialized();
    }

    @Override
    public void setInitialized(boolean bl) {
    }

    @Override
    public GameRules getGameRules() {
        return this.field_24179.getGameRules();
    }

    @Override
    public WorldBorder.Properties getWorldBorder() {
        return this.properties.getWorldBorder();
    }

    @Override
    public void setWorldBorder(WorldBorder.Properties arg) {
    }

    @Override
    public Difficulty getDifficulty() {
        return this.field_24179.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.field_24179.isDifficultyLocked();
    }

    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.properties.getScheduledEvents();
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int i) {
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnChance(int i) {
    }

    @Override
    public void setWanderingTraderId(UUID uUID) {
    }

    @Override
    public void populateCrashReport(CrashReportSection arg) {
        arg.add("Derived", true);
        this.properties.populateCrashReport(arg);
    }
}

