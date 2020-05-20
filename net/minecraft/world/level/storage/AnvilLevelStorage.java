/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.AlphaChunkIo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilLevelStorage {
    private static final Logger LOGGER = LogManager.getLogger();

    static boolean convertLevel(LevelStorage.Session arg, ProgressListener arg2) {
        VanillaLayeredBiomeSource lv3;
        long l;
        arg2.progressStagePercentage(0);
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        File file = arg.method_27424(DimensionType.OVERWORLD);
        File file2 = arg.method_27424(DimensionType.THE_NETHER);
        File file3 = arg.method_27424(DimensionType.THE_END);
        LOGGER.info("Scanning folders...");
        AnvilLevelStorage.addRegionFiles(file, list);
        if (file2.exists()) {
            AnvilLevelStorage.addRegionFiles(file2, list2);
        }
        if (file3.exists()) {
            AnvilLevelStorage.addRegionFiles(file3, list3);
        }
        int i = list.size() + list2.size() + list3.size();
        LOGGER.info("Total conversion count is {}", (Object)i);
        class_5219 lv = arg.readLevelProperties();
        long l2 = l = lv != null ? lv.method_28057().method_28028() : 0L;
        if (lv != null && lv.method_28057().method_28034()) {
            FixedBiomeSource lv2 = new FixedBiomeSource(Biomes.PLAINS);
        } else {
            lv3 = new VanillaLayeredBiomeSource(l, false, 4);
        }
        AnvilLevelStorage.convertRegions(new File(file, "region"), list, lv3, 0, i, arg2);
        AnvilLevelStorage.convertRegions(new File(file2, "region"), list2, new FixedBiomeSource(Biomes.NETHER_WASTES), list.size(), i, arg2);
        AnvilLevelStorage.convertRegions(new File(file3, "region"), list3, new FixedBiomeSource(Biomes.THE_END), list.size() + list2.size(), i, arg2);
        AnvilLevelStorage.makeMcrLevelDatBackup(arg);
        arg.method_27425(lv);
        return true;
    }

    private static void makeMcrLevelDatBackup(LevelStorage.Session arg) {
        File file = arg.getDirectory(class_5218.LEVEL_DAT).toFile();
        if (!file.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
            return;
        }
        File file2 = new File(file.getParent(), "level.dat_mcr");
        if (!file.renameTo(file2)) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
        }
    }

    private static void convertRegions(File file, Iterable<File> iterable, BiomeSource arg, int i, int j, ProgressListener arg2) {
        for (File file2 : iterable) {
            AnvilLevelStorage.convertRegion(file, file2, arg, i, j, arg2);
            int k = (int)Math.round(100.0 * (double)(++i) / (double)j);
            arg2.progressStagePercentage(k);
        }
    }

    /*
     * WARNING - void declaration
     */
    private static void convertRegion(File file, File file2, BiomeSource arg, int i, int j, ProgressListener arg2) {
        String string = file2.getName();
        try (RegionFile lv = new RegionFile(file2, file, true);
             RegionFile lv2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"), file, true);){
            for (int k = 0; k < 32; ++k) {
                for (int l = 0; l < 32; ++l) {
                    void lv6;
                    ChunkPos lv3 = new ChunkPos(k, l);
                    if (!lv.hasChunk(lv3) || lv2.hasChunk(lv3)) continue;
                    try (DataInputStream dataInputStream = lv.getChunkInputStream(lv3);){
                        if (dataInputStream == null) {
                            LOGGER.warn("Failed to fetch input stream for chunk {}", (Object)lv3);
                            continue;
                        }
                        CompoundTag lv4 = NbtIo.read(dataInputStream);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to read data for chunk {}", (Object)lv3, (Object)iOException);
                        continue;
                    }
                    CompoundTag lv7 = lv6.getCompound("Level");
                    AlphaChunkIo.AlphaChunk lv8 = AlphaChunkIo.readAlphaChunk(lv7);
                    CompoundTag lv9 = new CompoundTag();
                    CompoundTag lv10 = new CompoundTag();
                    lv9.put("Level", lv10);
                    AlphaChunkIo.convertAlphaChunk(lv8, lv10, arg);
                    try (DataOutputStream dataOutputStream = lv2.getChunkOutputStream(lv3);){
                        NbtIo.write(lv9, (DataOutput)dataOutputStream);
                        continue;
                    }
                }
                int m = (int)Math.round(100.0 * (double)(i * 1024) / (double)(j * 1024));
                int n = (int)Math.round(100.0 * (double)((k + 1) * 32 + i * 1024) / (double)(j * 1024));
                if (n <= m) continue;
                arg2.progressStagePercentage(n);
            }
        }
        catch (IOException iOException2) {
            LOGGER.error("Failed to upgrade region file {}", (Object)file2, (Object)iOException2);
        }
    }

    private static void addRegionFiles(File file2, Collection<File> collection) {
        File file22 = new File(file2, "region");
        File[] files = file22.listFiles((file, string) -> string.endsWith(".mcr"));
        if (files != null) {
            Collections.addAll(collection, files);
        }
    }
}

