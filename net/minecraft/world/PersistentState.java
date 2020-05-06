/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PersistentState {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String key;
    private boolean dirty;

    public PersistentState(String string) {
        this.key = string;
    }

    public abstract void fromTag(CompoundTag var1);

    public abstract CompoundTag toTag(CompoundTag var1);

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean bl) {
        this.dirty = bl;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public String getId() {
        return this.key;
    }

    public void save(File file) {
        if (!this.isDirty()) {
            return;
        }
        CompoundTag lv = new CompoundTag();
        lv.put("data", this.toTag(new CompoundTag()));
        lv.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
            NbtIo.writeCompressed(lv, fileOutputStream);
        }
        catch (IOException iOException) {
            LOGGER.error("Could not save data {}", (Object)this, (Object)iOException);
        }
        this.setDirty(false);
    }
}

