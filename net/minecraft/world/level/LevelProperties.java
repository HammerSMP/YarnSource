/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.class_5268;
import net.minecraft.class_5285;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;

public class LevelProperties
implements class_5268,
class_5219 {
    private final String versionName;
    private final int versionId;
    private final boolean versionSnapshot;
    private final class_5285 generatorOptions;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long time;
    private long timeOfDay;
    private long lastPlayed;
    private long sizeOnDisk;
    @Nullable
    private final DataFixer dataFixer;
    private final int dataVersion;
    private boolean playerDataLoaded;
    private CompoundTag playerData;
    private final String levelName;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private GameMode gameMode;
    private final boolean hardcore;
    private final boolean commandsAllowed;
    private boolean initialized;
    private Difficulty difficulty = Difficulty.NORMAL;
    private boolean difficultyLocked;
    private WorldBorder.class_5200 field_24193 = WorldBorder.field_24122;
    private final Set<String> disabledDataPacks = Sets.newHashSet();
    private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
    private final Map<DimensionType, CompoundTag> worldData = Maps.newIdentityHashMap();
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    private UUID wanderingTraderId;
    private Set<String> serverBrands = Sets.newLinkedHashSet();
    private boolean modded;
    private final GameRules gameRules = new GameRules();
    private final Timer<MinecraftServer> scheduledEvents = new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE);

    public LevelProperties(CompoundTag arg, DataFixer dataFixer, int i, @Nullable CompoundTag arg2) {
        this.dataFixer = dataFixer;
        ListTag lv = arg.getList("ServerBrands", 8);
        for (int j = 0; j < lv.size(); ++j) {
            this.serverBrands.add(lv.getString(j));
        }
        this.modded = arg.getBoolean("WasModded");
        if (arg.contains("Version", 10)) {
            CompoundTag lv2 = arg.getCompound("Version");
            this.versionName = lv2.getString("Name");
            this.versionId = lv2.getInt("Id");
            this.versionSnapshot = lv2.getBoolean("Snapshot");
        } else {
            this.versionName = SharedConstants.getGameVersion().getName();
            this.versionId = SharedConstants.getGameVersion().getWorldVersion();
            this.versionSnapshot = !SharedConstants.getGameVersion().isStable();
        }
        this.gameMode = GameMode.byId(arg.getInt("GameType"));
        this.generatorOptions = class_5285.method_28023(arg, dataFixer, i);
        this.spawnX = arg.getInt("SpawnX");
        this.spawnY = arg.getInt("SpawnY");
        this.spawnZ = arg.getInt("SpawnZ");
        this.time = arg.getLong("Time");
        this.timeOfDay = arg.contains("DayTime", 99) ? arg.getLong("DayTime") : this.time;
        this.lastPlayed = arg.getLong("LastPlayed");
        this.sizeOnDisk = arg.getLong("SizeOnDisk");
        this.levelName = arg.getString("LevelName");
        this.version = arg.getInt("version");
        this.clearWeatherTime = arg.getInt("clearWeatherTime");
        this.rainTime = arg.getInt("rainTime");
        this.raining = arg.getBoolean("raining");
        this.thunderTime = arg.getInt("thunderTime");
        this.thundering = arg.getBoolean("thundering");
        this.hardcore = arg.getBoolean("hardcore");
        this.initialized = arg.contains("initialized", 99) ? arg.getBoolean("initialized") : true;
        this.commandsAllowed = arg.contains("allowCommands", 99) ? arg.getBoolean("allowCommands") : this.gameMode == GameMode.CREATIVE;
        this.dataVersion = i;
        if (arg2 != null) {
            this.playerData = arg2;
        }
        if (arg.contains("GameRules", 10)) {
            this.gameRules.load(arg.getCompound("GameRules"));
        }
        if (arg.contains("Difficulty", 99)) {
            this.difficulty = Difficulty.byOrdinal(arg.getByte("Difficulty"));
        }
        if (arg.contains("DifficultyLocked", 1)) {
            this.difficultyLocked = arg.getBoolean("DifficultyLocked");
        }
        this.field_24193 = WorldBorder.class_5200.method_27358(arg, WorldBorder.field_24122);
        if (arg.contains("DimensionData", 10)) {
            CompoundTag lv3 = arg.getCompound("DimensionData");
            for (String string : lv3.getKeys()) {
                this.worldData.put(DimensionType.byRawId(Integer.parseInt(string)), lv3.getCompound(string));
            }
        }
        if (arg.contains("DataPacks", 10)) {
            CompoundTag lv4 = arg.getCompound("DataPacks");
            ListTag lv5 = lv4.getList("Disabled", 8);
            for (int k = 0; k < lv5.size(); ++k) {
                this.disabledDataPacks.add(lv5.getString(k));
            }
            ListTag lv6 = lv4.getList("Enabled", 8);
            for (int l = 0; l < lv6.size(); ++l) {
                this.enabledDataPacks.add(lv6.getString(l));
            }
        }
        if (arg.contains("CustomBossEvents", 10)) {
            this.customBossEvents = arg.getCompound("CustomBossEvents");
        }
        if (arg.contains("ScheduledEvents", 9)) {
            this.scheduledEvents.fromTag(arg.getList("ScheduledEvents", 10));
        }
        if (arg.contains("WanderingTraderSpawnDelay", 99)) {
            this.wanderingTraderSpawnDelay = arg.getInt("WanderingTraderSpawnDelay");
        }
        if (arg.contains("WanderingTraderSpawnChance", 99)) {
            this.wanderingTraderSpawnChance = arg.getInt("WanderingTraderSpawnChance");
        }
        if (arg.containsUuidNew("WanderingTraderId")) {
            this.wanderingTraderId = arg.getUuidNew("WanderingTraderId");
        }
    }

    public LevelProperties(LevelInfo arg) {
        this.dataFixer = null;
        this.dataVersion = SharedConstants.getGameVersion().getWorldVersion();
        this.gameMode = arg.getGameMode();
        this.difficulty = arg.getDifficulty();
        this.hardcore = arg.hasStructures();
        this.generatorOptions = arg.getGeneratorOptions();
        this.commandsAllowed = arg.isHardcore();
        this.levelName = arg.getLevelName();
        this.version = 19133;
        this.initialized = false;
        this.versionName = SharedConstants.getGameVersion().getName();
        this.versionId = SharedConstants.getGameVersion().getWorldVersion();
        this.versionSnapshot = !SharedConstants.getGameVersion().isStable();
        this.gameRules.setAllValues(arg.getGameRules(), null);
    }

    @Override
    public CompoundTag cloneWorldTag(@Nullable CompoundTag arg) {
        this.loadPlayerData();
        if (arg == null) {
            arg = this.playerData;
        }
        CompoundTag lv = new CompoundTag();
        this.updateProperties(lv, arg);
        return lv;
    }

    private void updateProperties(CompoundTag arg, CompoundTag arg2) {
        ListTag lv = new ListTag();
        this.serverBrands.stream().map(StringTag::of).forEach(lv::add);
        arg.put("ServerBrands", lv);
        arg.putBoolean("WasModded", this.modded);
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("Name", SharedConstants.getGameVersion().getName());
        lv2.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        lv2.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        arg.put("Version", lv2);
        arg.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        CompoundTag lv3 = this.generatorOptions.method_28025();
        for (String string : lv3.getKeys()) {
            arg.put(string, lv3.get(string));
        }
        arg.putInt("GameType", this.gameMode.getId());
        arg.putInt("SpawnX", this.spawnX);
        arg.putInt("SpawnY", this.spawnY);
        arg.putInt("SpawnZ", this.spawnZ);
        arg.putLong("Time", this.time);
        arg.putLong("DayTime", this.timeOfDay);
        arg.putLong("SizeOnDisk", this.sizeOnDisk);
        arg.putLong("LastPlayed", Util.getEpochTimeMs());
        arg.putString("LevelName", this.levelName);
        arg.putInt("version", 19133);
        arg.putInt("clearWeatherTime", this.clearWeatherTime);
        arg.putInt("rainTime", this.rainTime);
        arg.putBoolean("raining", this.raining);
        arg.putInt("thunderTime", this.thunderTime);
        arg.putBoolean("thundering", this.thundering);
        arg.putBoolean("hardcore", this.hardcore);
        arg.putBoolean("allowCommands", this.commandsAllowed);
        arg.putBoolean("initialized", this.initialized);
        this.field_24193.method_27357(arg);
        arg.putByte("Difficulty", (byte)this.difficulty.getId());
        arg.putBoolean("DifficultyLocked", this.difficultyLocked);
        arg.put("GameRules", this.gameRules.toNbt());
        CompoundTag lv4 = new CompoundTag();
        for (Map.Entry<DimensionType, CompoundTag> entry : this.worldData.entrySet()) {
            lv4.put(String.valueOf(entry.getKey().getRawId()), entry.getValue());
        }
        arg.put("DimensionData", lv4);
        if (arg2 != null) {
            arg.put("Player", arg2);
        }
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (String string2 : this.enabledDataPacks) {
            listTag.add(StringTag.of(string2));
        }
        compoundTag.put("Enabled", listTag);
        ListTag lv7 = new ListTag();
        for (String string3 : this.disabledDataPacks) {
            lv7.add(StringTag.of(string3));
        }
        compoundTag.put("Disabled", lv7);
        arg.put("DataPacks", compoundTag);
        if (this.customBossEvents != null) {
            arg.put("CustomBossEvents", this.customBossEvents);
        }
        arg.put("ScheduledEvents", this.scheduledEvents.toTag());
        arg.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        arg.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            arg.putUuidNew("WanderingTraderId", this.wanderingTraderId);
        }
    }

    @Override
    public int getSpawnX() {
        return this.spawnX;
    }

    @Override
    public int getSpawnY() {
        return this.spawnY;
    }

    @Override
    public int getSpawnZ() {
        return this.spawnZ;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getTimeOfDay() {
        return this.timeOfDay;
    }

    private void loadPlayerData() {
        if (this.playerDataLoaded || this.playerData == null) {
            return;
        }
        if (this.dataVersion < SharedConstants.getGameVersion().getWorldVersion()) {
            if (this.dataFixer == null) {
                throw Util.throwOrPause(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }
            this.playerData = NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, this.playerData, this.dataVersion);
        }
        this.playerDataLoaded = true;
    }

    @Override
    public CompoundTag getPlayerData() {
        this.loadPlayerData();
        return this.playerData;
    }

    @Override
    public void setSpawnX(int i) {
        this.spawnX = i;
    }

    @Override
    public void setSpawnY(int i) {
        this.spawnY = i;
    }

    @Override
    public void setSpawnZ(int i) {
        this.spawnZ = i;
    }

    @Override
    public void setTime(long l) {
        this.time = l;
    }

    @Override
    public void setTimeOfDay(long l) {
        this.timeOfDay = l;
    }

    @Override
    public void setSpawnPos(BlockPos arg) {
        this.spawnX = arg.getX();
        this.spawnY = arg.getY();
        this.spawnZ = arg.getZ();
    }

    @Override
    public String getLevelName() {
        return this.levelName;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed() {
        return this.lastPlayed;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int i) {
        this.clearWeatherTime = i;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean bl) {
        this.thundering = bl;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int i) {
        this.thunderTime = i;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean bl) {
        this.raining = bl;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int i) {
        this.rainTime = i;
    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode arg) {
        this.gameMode = arg;
    }

    @Override
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean bl) {
        this.initialized = bl;
    }

    @Override
    public GameRules getGameRules() {
        return this.gameRules;
    }

    @Override
    public WorldBorder.class_5200 method_27422() {
        return this.field_24193;
    }

    @Override
    public void method_27415(WorldBorder.class_5200 arg) {
        this.field_24193 = arg;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(Difficulty arg) {
        this.difficulty = arg;
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean bl) {
        this.difficultyLocked = bl;
    }

    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void populateCrashReport(CrashReportSection arg) {
        class_5268.super.populateCrashReport(arg);
        class_5219.super.populateCrashReport(arg);
    }

    @Override
    public CompoundTag getWorldData(DimensionType arg) {
        CompoundTag lv = this.worldData.get(arg);
        if (lv == null) {
            return new CompoundTag();
        }
        return lv;
    }

    @Override
    public void setWorldData(DimensionType arg, CompoundTag arg2) {
        this.worldData.put(arg, arg2);
    }

    @Override
    public class_5285 method_28057() {
        return this.generatorOptions;
    }

    @Override
    public CompoundTag getWorldData() {
        return this.getWorldData(DimensionType.OVERWORLD);
    }

    @Override
    public void setWorldData(CompoundTag arg) {
        this.setWorldData(DimensionType.OVERWORLD, arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getVersionId() {
        return this.versionId;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isVersionSnapshot() {
        return this.versionSnapshot;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public String getVersionName() {
        return this.versionName;
    }

    @Override
    public Set<String> getDisabledDataPacks() {
        return this.disabledDataPacks;
    }

    @Override
    public Set<String> getEnabledDataPacks() {
        return this.enabledDataPacks;
    }

    @Override
    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable CompoundTag arg) {
        this.customBossEvents = arg;
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int i) {
        this.wanderingTraderSpawnDelay = i;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int i) {
        this.wanderingTraderSpawnChance = i;
    }

    @Override
    public void setWanderingTraderId(UUID uUID) {
        this.wanderingTraderId = uUID;
    }

    @Override
    public void addServerBrand(String string, boolean bl) {
        this.serverBrands.add(string);
        this.modded |= bl;
    }

    @Override
    public boolean isModded() {
        return this.modded;
    }

    @Override
    public Set<String> getServerBrands() {
        return ImmutableSet.copyOf(this.serverBrands);
    }

    @Override
    public class_5268 method_27859() {
        return this;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public LevelInfo getLevelInfo() {
        return new LevelInfo(this.levelName, this.gameMode, this.hardcore, this.difficulty, this.commandsAllowed, this.gameRules.copy(), this.generatorOptions);
    }
}

