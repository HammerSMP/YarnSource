/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5455;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelProperties
implements ServerWorldProperties,
SaveProperties {
    private static final Logger LOGGER = LogManager.getLogger();
    private LevelInfo field_25030;
    private final GeneratorOptions field_25425;
    private final Lifecycle field_25426;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long time;
    private long timeOfDay;
    @Nullable
    private final DataFixer dataFixer;
    private final int dataVersion;
    private boolean playerDataLoaded;
    @Nullable
    private CompoundTag playerData;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Properties worldBorder;
    private CompoundTag field_25031;
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> serverBrands;
    private boolean modded;
    private final Timer<MinecraftServer> scheduledEvents;

    private LevelProperties(@Nullable DataFixer dataFixer, int i, @Nullable CompoundTag arg, boolean bl, int j, int k, int l, long m, long n, int o, int p, int q, boolean bl2, int r, boolean bl3, boolean bl4, boolean bl5, WorldBorder.Properties arg2, int s, int t, @Nullable UUID uUID, LinkedHashSet<String> linkedHashSet, Timer<MinecraftServer> arg3, @Nullable CompoundTag arg4, CompoundTag arg5, LevelInfo arg6, GeneratorOptions arg7, Lifecycle lifecycle) {
        this.dataFixer = dataFixer;
        this.modded = bl;
        this.spawnX = j;
        this.spawnY = k;
        this.spawnZ = l;
        this.time = m;
        this.timeOfDay = n;
        this.version = o;
        this.clearWeatherTime = p;
        this.rainTime = q;
        this.raining = bl2;
        this.thunderTime = r;
        this.thundering = bl3;
        this.initialized = bl4;
        this.difficultyLocked = bl5;
        this.worldBorder = arg2;
        this.wanderingTraderSpawnDelay = s;
        this.wanderingTraderSpawnChance = t;
        this.wanderingTraderId = uUID;
        this.serverBrands = linkedHashSet;
        this.playerData = arg;
        this.dataVersion = i;
        this.scheduledEvents = arg3;
        this.customBossEvents = arg4;
        this.field_25031 = arg5;
        this.field_25030 = arg6;
        this.field_25425 = arg7;
        this.field_25426 = lifecycle;
    }

    public LevelProperties(LevelInfo arg, GeneratorOptions arg2, Lifecycle lifecycle) {
        this(null, SharedConstants.getGameVersion().getWorldVersion(), null, false, 0, 0, 0, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_BORDER, 0, 0, null, Sets.newLinkedHashSet(), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE), null, new CompoundTag(), arg.method_28385(), arg2, lifecycle);
    }

    public static LevelProperties method_29029(Dynamic<Tag> dynamic2, DataFixer dataFixer, int i, @Nullable CompoundTag arg, LevelInfo arg2, SaveVersionInfo arg3, GeneratorOptions arg4, Lifecycle lifecycle) {
        long l = dynamic2.get("Time").asLong(0L);
        CompoundTag lv = (CompoundTag)dynamic2.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> (Tag)dynamic2.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
        return new LevelProperties(dataFixer, i, arg, dynamic2.get("WasModded").asBoolean(false), dynamic2.get("SpawnX").asInt(0), dynamic2.get("SpawnY").asInt(0), dynamic2.get("SpawnZ").asInt(0), l, dynamic2.get("DayTime").asLong(l), arg3.getLevelFormatVersion(), dynamic2.get("clearWeatherTime").asInt(0), dynamic2.get("rainTime").asInt(0), dynamic2.get("raining").asBoolean(false), dynamic2.get("thunderTime").asInt(0), dynamic2.get("thundering").asBoolean(false), dynamic2.get("initialized").asBoolean(true), dynamic2.get("DifficultyLocked").asBoolean(false), WorldBorder.Properties.fromDynamic(dynamic2, WorldBorder.DEFAULT_BORDER), dynamic2.get("WanderingTraderSpawnDelay").asInt(0), dynamic2.get("WanderingTraderSpawnChance").asInt(0), dynamic2.get("WanderingTraderId").read(DynamicSerializableUuid.field_25122).result().orElse(null), dynamic2.get("ServerBrands").asStream().flatMap(dynamic -> Util.stream(dynamic.asString().result())).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE, dynamic2.get("ScheduledEvents").asStream()), (CompoundTag)dynamic2.get("CustomBossEvents").orElseEmptyMap().getValue(), lv, arg2, arg4, lifecycle);
    }

    @Override
    public CompoundTag cloneWorldTag(class_5455 arg, @Nullable CompoundTag arg2) {
        this.loadPlayerData();
        if (arg2 == null) {
            arg2 = this.playerData;
        }
        CompoundTag lv = new CompoundTag();
        this.updateProperties(arg, lv, arg2);
        return lv;
    }

    private void updateProperties(class_5455 arg, CompoundTag arg22, @Nullable CompoundTag arg3) {
        ListTag lv = new ListTag();
        this.serverBrands.stream().map(StringTag::of).forEach(lv::add);
        arg22.put("ServerBrands", lv);
        arg22.putBoolean("WasModded", this.modded);
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("Name", SharedConstants.getGameVersion().getName());
        lv2.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        lv2.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        arg22.put("Version", lv2);
        arg22.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        RegistryReadingOps<Tag> lv3 = RegistryReadingOps.of(NbtOps.INSTANCE, arg);
        GeneratorOptions.CODEC.encodeStart(lv3, (Object)this.field_25425).resultOrPartial(Util.method_29188("WorldGenSettings: ", ((Logger)LOGGER)::error)).ifPresent(arg2 -> arg22.put("WorldGenSettings", (Tag)arg2));
        arg22.putInt("GameType", this.field_25030.getGameMode().getId());
        arg22.putInt("SpawnX", this.spawnX);
        arg22.putInt("SpawnY", this.spawnY);
        arg22.putInt("SpawnZ", this.spawnZ);
        arg22.putLong("Time", this.time);
        arg22.putLong("DayTime", this.timeOfDay);
        arg22.putLong("LastPlayed", Util.getEpochTimeMs());
        arg22.putString("LevelName", this.field_25030.getLevelName());
        arg22.putInt("version", 19133);
        arg22.putInt("clearWeatherTime", this.clearWeatherTime);
        arg22.putInt("rainTime", this.rainTime);
        arg22.putBoolean("raining", this.raining);
        arg22.putInt("thunderTime", this.thunderTime);
        arg22.putBoolean("thundering", this.thundering);
        arg22.putBoolean("hardcore", this.field_25030.hasStructures());
        arg22.putBoolean("allowCommands", this.field_25030.isHardcore());
        arg22.putBoolean("initialized", this.initialized);
        this.worldBorder.toTag(arg22);
        arg22.putByte("Difficulty", (byte)this.field_25030.getDifficulty().getId());
        arg22.putBoolean("DifficultyLocked", this.difficultyLocked);
        arg22.put("GameRules", this.field_25030.getGameRules().toNbt());
        arg22.put("DragonFight", this.field_25031);
        if (arg3 != null) {
            arg22.put("Player", arg3);
        }
        DataPackSettings.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.field_25030.method_29558()).result().ifPresent(arg2 -> arg22.put("DataPacks", (Tag)arg2));
        if (this.customBossEvents != null) {
            arg22.put("CustomBossEvents", this.customBossEvents);
        }
        arg22.put("ScheduledEvents", this.scheduledEvents.toTag());
        arg22.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        arg22.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            arg22.putUuid("WanderingTraderId", this.wanderingTraderId);
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
    public void method_29034(long l) {
        this.time = l;
    }

    @Override
    public void method_29035(long l) {
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
        return this.field_25030.getLevelName();
    }

    @Override
    public int getVersion() {
        return this.version;
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
        return this.field_25030.getGameMode();
    }

    @Override
    public void setGameMode(GameMode arg) {
        this.field_25030 = this.field_25030.method_28382(arg);
    }

    @Override
    public boolean isHardcore() {
        return this.field_25030.hasStructures();
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.field_25030.isHardcore();
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
        return this.field_25030.getGameRules();
    }

    @Override
    public WorldBorder.Properties getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.Properties arg) {
        this.worldBorder = arg;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.field_25030.getDifficulty();
    }

    @Override
    public void setDifficulty(Difficulty arg) {
        this.field_25030 = this.field_25030.method_28381(arg);
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
        ServerWorldProperties.super.populateCrashReport(arg);
        SaveProperties.super.populateCrashReport(arg);
    }

    @Override
    public GeneratorOptions getGeneratorOptions() {
        return this.field_25425;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Lifecycle method_29588() {
        return this.field_25426;
    }

    @Override
    public CompoundTag method_29036() {
        return this.field_25031;
    }

    @Override
    public void method_29037(CompoundTag arg) {
        this.field_25031 = arg;
    }

    @Override
    public DataPackSettings method_29589() {
        return this.field_25030.method_29558();
    }

    @Override
    public void method_29590(DataPackSettings arg) {
        this.field_25030 = this.field_25030.method_29557(arg);
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
    public ServerWorldProperties getMainWorldProperties() {
        return this;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public LevelInfo getLevelInfo() {
        return this.field_25030.method_28385();
    }
}

