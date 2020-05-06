/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentStateManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, PersistentState> loadedStates = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final File directory;

    public PersistentStateManager(File file, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.directory = file;
    }

    private File getFile(String string) {
        return new File(this.directory, string + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(Supplier<T> supplier, String string) {
        T lv = this.get(supplier, string);
        if (lv != null) {
            return lv;
        }
        PersistentState lv2 = (PersistentState)supplier.get();
        this.set(lv2);
        return (T)lv2;
    }

    @Nullable
    public <T extends PersistentState> T get(Supplier<T> supplier, String string) {
        PersistentState lv = this.loadedStates.get(string);
        if (lv == null && !this.loadedStates.containsKey(string)) {
            lv = this.readFromFile(supplier, string);
            this.loadedStates.put(string, lv);
        }
        return (T)lv;
    }

    @Nullable
    private <T extends PersistentState> T readFromFile(Supplier<T> supplier, String string) {
        try {
            File file = this.getFile(string);
            if (file.exists()) {
                PersistentState lv = (PersistentState)supplier.get();
                CompoundTag lv2 = this.readTag(string, SharedConstants.getGameVersion().getWorldVersion());
                lv.fromTag(lv2.getCompound("data"));
                return (T)lv;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)string, (Object)exception);
        }
        return null;
    }

    public void set(PersistentState arg) {
        this.loadedStates.put(arg.getId(), arg);
    }

    /*
     * WARNING - void declaration
     */
    public CompoundTag readTag(String string, int i) throws IOException {
        File file = this.getFile(string);
        try (PushbackInputStream pushbackInputStream = new PushbackInputStream(new FileInputStream(file), 2);){
            void lv3;
            Object object;
            if (this.isCompressed(pushbackInputStream)) {
                CompoundTag lv = NbtIo.readCompressed(pushbackInputStream);
            } else {
                DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
                object = null;
                try {
                    CompoundTag lv2 = NbtIo.read(dataInputStream);
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (dataInputStream != null) {
                        if (object != null) {
                            try {
                                dataInputStream.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            dataInputStream.close();
                        }
                    }
                }
            }
            int j = lv3.contains("DataVersion", 99) ? lv3.getInt("DataVersion") : 1343;
            object = NbtHelper.update(this.dataFixer, DataFixTypes.SAVED_DATA, (CompoundTag)lv3, j, i);
            return object;
        }
    }

    private boolean isCompressed(PushbackInputStream pushbackInputStream) throws IOException {
        int j;
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = pushbackInputStream.read(bs, 0, 2);
        if (i == 2 && (j = (bs[1] & 0xFF) << 8 | bs[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (i != 0) {
            pushbackInputStream.unread(bs, 0, i);
        }
        return bl;
    }

    public void save() {
        for (PersistentState lv : this.loadedStates.values()) {
            if (lv == null) continue;
            lv.save(this.getFile(lv.getId()));
        }
    }
}

