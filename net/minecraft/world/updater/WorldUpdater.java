/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.datafixers.DataFixer
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatMaps
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.updater;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpdater {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadFactory UPDATE_THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final String levelName;
    private final boolean eraseCache;
    private final LevelStorage.Session field_24083;
    private final Thread updateThread;
    private final DataFixer field_24084;
    private volatile boolean keepUpgradingChunks = true;
    private volatile boolean isDone;
    private volatile float progress;
    private volatile int totalChunkCount;
    private volatile int upgradedChunkCount;
    private volatile int skippedChunkCount;
    private final Object2FloatMap<DimensionType> dimensionProgress = Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap(Util.identityHashStrategy()));
    private volatile Text status = new TranslatableText("optimizeWorld.stage.counting");
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final PersistentStateManager persistentStateManager;

    public WorldUpdater(LevelStorage.Session arg, DataFixer dataFixer, class_5219 arg2, boolean bl) {
        this.levelName = arg2.getLevelName();
        this.eraseCache = bl;
        this.field_24084 = dataFixer;
        this.field_24083 = arg;
        arg.method_27425(arg2);
        this.persistentStateManager = new PersistentStateManager(new File(this.field_24083.method_27424(DimensionType.OVERWORLD), "data"), dataFixer);
        this.updateThread = UPDATE_THREAD_FACTORY.newThread(this::updateWorld);
        this.updateThread.setUncaughtExceptionHandler((thread, throwable) -> {
            LOGGER.error("Error upgrading world", throwable);
            this.status = new TranslatableText("optimizeWorld.stage.failed");
            this.isDone = true;
        });
        this.updateThread.start();
    }

    public void cancel() {
        this.keepUpgradingChunks = false;
        try {
            this.updateThread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void updateWorld() {
        this.totalChunkCount = 0;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (DimensionType lv : DimensionType.getAll()) {
            List<ChunkPos> list = this.getChunkPositions(lv);
            builder.put((Object)lv, list.listIterator());
            this.totalChunkCount += list.size();
        }
        if (this.totalChunkCount == 0) {
            this.isDone = true;
            return;
        }
        float f = this.totalChunkCount;
        ImmutableMap immutableMap = builder.build();
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        for (DimensionType lv2 : DimensionType.getAll()) {
            File file = this.field_24083.method_27424(lv2);
            builder2.put((Object)lv2, (Object)new VersionedChunkStorage(new File(file, "region"), this.field_24084, true));
        }
        ImmutableMap immutableMap2 = builder2.build();
        long l = Util.getMeasuringTimeMs();
        this.status = new TranslatableText("optimizeWorld.stage.upgrading");
        while (this.keepUpgradingChunks) {
            boolean bl = false;
            float g = 0.0f;
            for (DimensionType lv3 : DimensionType.getAll()) {
                ListIterator listIterator = (ListIterator)immutableMap.get((Object)lv3);
                VersionedChunkStorage lv4 = (VersionedChunkStorage)immutableMap2.get((Object)lv3);
                if (listIterator.hasNext()) {
                    ChunkPos lv5 = (ChunkPos)listIterator.next();
                    boolean bl2 = false;
                    try {
                        CompoundTag lv6 = lv4.getNbt(lv5);
                        if (lv6 != null) {
                            boolean bl3;
                            int i = VersionedChunkStorage.getDataVersion(lv6);
                            CompoundTag lv7 = lv4.updateChunkTag(lv3, () -> this.persistentStateManager, lv6);
                            CompoundTag lv8 = lv7.getCompound("Level");
                            ChunkPos lv9 = new ChunkPos(lv8.getInt("xPos"), lv8.getInt("zPos"));
                            if (!lv9.equals(lv5)) {
                                LOGGER.warn("Chunk {} has invalid position {}", (Object)lv5, (Object)lv9);
                            }
                            boolean bl4 = bl3 = i < SharedConstants.getGameVersion().getWorldVersion();
                            if (this.eraseCache) {
                                bl3 = bl3 || lv8.contains("Heightmaps");
                                lv8.remove("Heightmaps");
                                bl3 = bl3 || lv8.contains("isLightOn");
                                lv8.remove("isLightOn");
                            }
                            if (bl3) {
                                lv4.setTagAt(lv5, lv7);
                                bl2 = true;
                            }
                        }
                    }
                    catch (CrashException lv10) {
                        Throwable throwable = lv10.getCause();
                        if (throwable instanceof IOException) {
                            LOGGER.error("Error upgrading chunk {}", (Object)lv5, (Object)throwable);
                        }
                        throw lv10;
                    }
                    catch (IOException iOException) {
                        LOGGER.error("Error upgrading chunk {}", (Object)lv5, (Object)iOException);
                    }
                    if (bl2) {
                        ++this.upgradedChunkCount;
                    } else {
                        ++this.skippedChunkCount;
                    }
                    bl = true;
                }
                float h = (float)listIterator.nextIndex() / f;
                this.dimensionProgress.put((Object)lv3, h);
                g += h;
            }
            this.progress = g;
            if (bl) continue;
            this.keepUpgradingChunks = false;
        }
        this.status = new TranslatableText("optimizeWorld.stage.finished");
        for (VersionedChunkStorage lv11 : immutableMap2.values()) {
            try {
                lv11.close();
            }
            catch (IOException iOException2) {
                LOGGER.error("Error upgrading chunk", (Throwable)iOException2);
            }
        }
        this.persistentStateManager.save();
        l = Util.getMeasuringTimeMs() - l;
        LOGGER.info("World optimizaton finished after {} ms", (Object)l);
        this.isDone = true;
    }

    private List<ChunkPos> getChunkPositions(DimensionType arg) {
        File file2 = this.field_24083.method_27424(arg);
        File file22 = new File(file2, "region");
        File[] files = file22.listFiles((file, string) -> string.endsWith(".mca"));
        if (files == null) {
            return ImmutableList.of();
        }
        ArrayList list = Lists.newArrayList();
        for (File file3 : files) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
            if (!matcher.matches()) continue;
            int i = Integer.parseInt(matcher.group(1)) << 5;
            int j = Integer.parseInt(matcher.group(2)) << 5;
            try (RegionFile lv = new RegionFile(file3, file22, true);){
                for (int k = 0; k < 32; ++k) {
                    for (int l = 0; l < 32; ++l) {
                        ChunkPos lv2 = new ChunkPos(k + i, l + j);
                        if (!lv.isChunkValid(lv2)) continue;
                        list.add(lv2);
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return list;
    }

    public boolean isDone() {
        return this.isDone;
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgress(DimensionType arg) {
        return this.dimensionProgress.getFloat((Object)arg);
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunkCount() {
        return this.totalChunkCount;
    }

    public int getUpgradedChunkCount() {
        return this.upgradedChunkCount;
    }

    public int getSkippedChunkCount() {
        return this.skippedChunkCount;
    }

    public Text getStatus() {
        return this.status;
    }
}

