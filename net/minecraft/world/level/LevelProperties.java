/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
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
import com.mojang.serialization.OptionalDynamic;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.class_5268;
import net.minecraft.class_5285;
import net.minecraft.class_5315;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelProperties
implements class_5268,
class_5219 {
    private static final Logger field_25029 = LogManager.getLogger();
    private LevelInfo field_25030;
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
    private WorldBorder.class_5200 field_24193;
    private final Set<String> disabledDataPacks;
    private final Set<String> enabledDataPacks;
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

    private LevelProperties(@Nullable DataFixer dataFixer, int i, @Nullable CompoundTag arg, boolean bl, int j, int k, int l, long m, long n, int o, int p, int q, boolean bl2, int r, boolean bl3, boolean bl4, boolean bl5, WorldBorder.class_5200 arg2, int s, int t, @Nullable UUID uUID, LinkedHashSet<String> linkedHashSet, LinkedHashSet<String> linkedHashSet2, Set<String> set, Timer<MinecraftServer> arg3, @Nullable CompoundTag arg4, CompoundTag arg5, LevelInfo arg6) {
        this.dataFixer = dataFixer;
        this.modded = bl;
        this.field_25030 = arg6;
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
        this.field_24193 = arg2;
        this.wanderingTraderSpawnDelay = s;
        this.wanderingTraderSpawnChance = t;
        this.wanderingTraderId = uUID;
        this.serverBrands = linkedHashSet;
        this.playerData = arg;
        this.dataVersion = i;
        this.scheduledEvents = arg3;
        this.enabledDataPacks = linkedHashSet2;
        this.disabledDataPacks = set;
        this.customBossEvents = arg4;
        this.field_25031 = arg5;
    }

    public LevelProperties(LevelInfo arg) {
        this(null, SharedConstants.getGameVersion().getWorldVersion(), null, false, 0, 0, 0, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.field_24122, 0, 0, null, Sets.newLinkedHashSet(), Sets.newLinkedHashSet(), Sets.newHashSet(), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE), null, new CompoundTag(), arg.method_28385());
    }

    public static LevelProperties method_29029(Dynamic<Tag> dynamic2, DataFixer dataFixer, int i, @Nullable CompoundTag arg, LevelInfo arg2, class_5315 arg3) {
        long l = dynamic2.get("Time").asLong(0L);
        OptionalDynamic optionalDynamic = dynamic2.get("DataPacks");
        CompoundTag lv = (CompoundTag)dynamic2.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> (Tag)dynamic2.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
        return new LevelProperties(dataFixer, i, arg, dynamic2.get("WasModded").asBoolean(false), dynamic2.get("SpawnX").asInt(0), dynamic2.get("SpawnY").asInt(0), dynamic2.get("SpawnZ").asInt(0), l, dynamic2.get("DayTime").asLong(l), arg3.method_29022(), dynamic2.get("clearWeatherTime").asInt(0), dynamic2.get("rainTime").asInt(0), dynamic2.get("raining").asBoolean(false), dynamic2.get("thunderTime").asInt(0), dynamic2.get("thundering").asBoolean(false), dynamic2.get("initialized").asBoolean(true), dynamic2.get("DifficultyLocked").asBoolean(false), WorldBorder.class_5200.method_27358(dynamic2, WorldBorder.field_24122), dynamic2.get("WanderingTraderSpawnDelay").asInt(0), dynamic2.get("WanderingTraderSpawnChance").asInt(0), dynamic2.get("WanderingTraderId").read(DynamicSerializableUuid.field_25122).result().map(DynamicSerializableUuid::getUuid).orElse(null), dynamic2.get("ServerBrands").asStream().flatMap(dynamic -> Util.stream(dynamic.asString().result())).collect(Collectors.toCollection(Sets::newLinkedHashSet)), optionalDynamic.get("Enabled").asStream().flatMap(dynamic -> Util.stream(dynamic.asString().result())).collect(Collectors.toCollection(Sets::newLinkedHashSet)), optionalDynamic.get("Disabled").asStream().flatMap(dynamic -> Util.stream(dynamic.asString().result())).collect(Collectors.toSet()), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE, dynamic2.get("ScheduledEvents").asStream()), (CompoundTag)dynamic2.get("CustomBossEvents").orElseEmptyMap().getValue(), lv, arg2);
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

    private void updateProperties(CompoundTag arg, CompoundTag arg22) {
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
        class_5285.field_24826.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.field_25030.getGeneratorOptions()).resultOrPartial(Util.method_29188("WorldGenSettings: ", ((Logger)field_25029)::error)).ifPresent(arg2 -> arg.put("WorldGenSettings", (Tag)arg2));
        arg.putInt("GameType", this.field_25030.getGameMode().getId());
        arg.putInt("SpawnX", this.spawnX);
        arg.putInt("SpawnY", this.spawnY);
        arg.putInt("SpawnZ", this.spawnZ);
        arg.putLong("Time", this.time);
        arg.putLong("DayTime", this.timeOfDay);
        arg.putLong("LastPlayed", Util.getEpochTimeMs());
        arg.putString("LevelName", this.field_25030.getLevelName());
        arg.putInt("version", 19133);
        arg.putInt("clearWeatherTime", this.clearWeatherTime);
        arg.putInt("rainTime", this.rainTime);
        arg.putBoolean("raining", this.raining);
        arg.putInt("thunderTime", this.thunderTime);
        arg.putBoolean("thundering", this.thundering);
        arg.putBoolean("hardcore", this.field_25030.hasStructures());
        arg.putBoolean("allowCommands", this.field_25030.isHardcore());
        arg.putBoolean("initialized", this.initialized);
        this.field_24193.method_27357(arg);
        arg.putByte("Difficulty", (byte)this.field_25030.getDifficulty().getId());
        arg.putBoolean("DifficultyLocked", this.difficultyLocked);
        arg.put("GameRules", this.field_25030.getGameRules().toNbt());
        arg.put("DragonFight", this.field_25031);
        if (arg22 != null) {
            arg.put("Player", arg22);
        }
        CompoundTag lv3 = new CompoundTag();
        ListTag lv4 = new ListTag();
        for (String string : this.enabledDataPacks) {
            lv4.add(StringTag.of(string));
        }
        lv3.put("Enabled", lv4);
        ListTag lv5 = new ListTag();
        for (String string2 : this.disabledDataPacks) {
            lv5.add(StringTag.of(string2));
        }
        lv3.put("Disabled", lv5);
        arg.put("DataPacks", lv3);
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
    public WorldBorder.class_5200 method_27422() {
        return this.field_24193;
    }

    @Override
    public void method_27415(WorldBorder.class_5200 arg) {
        this.field_24193 = arg;
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
        class_5268.super.populateCrashReport(arg);
        class_5219.super.populateCrashReport(arg);
    }

    @Override
    public class_5285 method_28057() {
        return this.field_25030.getGeneratorOptions();
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
        return this.field_25030.method_28385();
    }
}

