/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSaveHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final File playerDataDir;
    protected final DataFixer dataFixer;

    public WorldSaveHandler(LevelStorage.Session arg, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.playerDataDir = arg.getDirectory(WorldSavePath.PLAYERDATA).toFile();
        this.playerDataDir.mkdirs();
    }

    public void savePlayerData(PlayerEntity arg) {
        try {
            CompoundTag lv = arg.toTag(new CompoundTag());
            File file = File.createTempFile(arg.getUuidAsString() + "-", ".dat", this.playerDataDir);
            NbtIo.method_30614(lv, file);
            File file2 = new File(this.playerDataDir, arg.getUuidAsString() + ".dat");
            File file3 = new File(this.playerDataDir, arg.getUuidAsString() + ".dat_old");
            Util.method_27760(file2, file, file3);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", (Object)arg.getName().getString());
        }
    }

    @Nullable
    public CompoundTag loadPlayerData(PlayerEntity arg) {
        CompoundTag lv = null;
        try {
            File file = new File(this.playerDataDir, arg.getUuidAsString() + ".dat");
            if (file.exists() && file.isFile()) {
                lv = NbtIo.method_30613(file);
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load player data for {}", (Object)arg.getName().getString());
        }
        if (lv != null) {
            int i = lv.contains("DataVersion", 3) ? lv.getInt("DataVersion") : -1;
            arg.fromTag(NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, lv, i));
        }
        return lv;
    }

    public String[] getSavedPlayerIds() {
        String[] strings = this.playerDataDir.list();
        if (strings == null) {
            strings = new String[]{};
        }
        for (int i = 0; i < strings.length; ++i) {
            if (!strings[i].endsWith(".dat")) continue;
            strings[i] = strings[i].substring(0, strings[i].length() - 4);
        }
        return strings;
    }
}

