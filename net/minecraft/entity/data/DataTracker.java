/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<? extends Entity>, Integer> trackedEntities = Maps.newHashMap();
    private final Entity trackedEntity;
    private final Map<Integer, Entry<?>> entries = Maps.newHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean empty = true;
    private boolean dirty;

    public DataTracker(Entity trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public static <T> TrackedData<T> registerData(Class<? extends Entity> entityClass, TrackedDataHandler<T> dataHandler) {
        int k;
        if (LOGGER.isDebugEnabled()) {
            try {
                Class<?> class2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
                if (!class2.equals(entityClass)) {
                    LOGGER.debug("defineId called for: {} from {}", entityClass, class2, (Object)new RuntimeException());
                }
            }
            catch (ClassNotFoundException class2) {
                // empty catch block
            }
        }
        if (trackedEntities.containsKey(entityClass)) {
            int i = trackedEntities.get(entityClass) + 1;
        } else {
            int j = 0;
            Class<? extends Entity> class3 = entityClass;
            while (class3 != Entity.class) {
                if (!trackedEntities.containsKey(class3 = class3.getSuperclass())) continue;
                j = trackedEntities.get(class3) + 1;
                break;
            }
            k = j;
        }
        if (k > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + k + "! (Max is " + 254 + ")");
        }
        trackedEntities.put(entityClass, k);
        return dataHandler.create(k);
    }

    public <T> void startTracking(TrackedData<T> key, T initialValue) {
        int i = key.getId();
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
        }
        if (this.entries.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        }
        if (TrackedDataHandlerRegistry.getId(key.getType()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + key.getType() + " for " + i + "!");
        }
        this.addTrackedData(key, initialValue);
    }

    private <T> void addTrackedData(TrackedData<T> arg, T object) {
        Entry<T> lv = new Entry<T>(arg, object);
        this.lock.writeLock().lock();
        this.entries.put(arg.getId(), lv);
        this.empty = false;
        this.lock.writeLock().unlock();
    }

    /*
     * WARNING - void declaration
     */
    private <T> Entry<T> getEntry(TrackedData<T> arg) {
        void lv4;
        this.lock.readLock().lock();
        try {
            Entry<?> lv = this.entries.get(arg.getId());
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Getting synched entity data");
            CrashReportSection lv3 = lv2.addElement("Synched entity data");
            lv3.add("Data ID", arg);
            throw new CrashException(lv2);
        }
        finally {
            this.lock.readLock().unlock();
        }
        return lv4;
    }

    public <T> T get(TrackedData<T> arg) {
        return this.getEntry(arg).get();
    }

    public <T> void set(TrackedData<T> key, T object) {
        Entry<T> lv = this.getEntry(key);
        if (ObjectUtils.notEqual(object, lv.get())) {
            lv.set(object);
            this.trackedEntity.onTrackedDataSet(key);
            lv.setDirty(true);
            this.dirty = true;
        }
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public static void entriesToPacket(List<Entry<?>> list, PacketByteBuf arg) throws IOException {
        if (list != null) {
            int j = list.size();
            for (int i = 0; i < j; ++i) {
                DataTracker.writeEntryToPacket(arg, list.get(i));
            }
        }
        arg.writeByte(255);
    }

    @Nullable
    public List<Entry<?>> getDirtyEntries() {
        ArrayList list = null;
        if (this.dirty) {
            this.lock.readLock().lock();
            for (Entry<?> lv : this.entries.values()) {
                if (!lv.isDirty()) continue;
                lv.setDirty(false);
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(lv.copy());
            }
            this.lock.readLock().unlock();
        }
        this.dirty = false;
        return list;
    }

    @Nullable
    public List<Entry<?>> getAllEntries() {
        ArrayList list = null;
        this.lock.readLock().lock();
        for (Entry<?> lv : this.entries.values()) {
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(lv.copy());
        }
        this.lock.readLock().unlock();
        return list;
    }

    private static <T> void writeEntryToPacket(PacketByteBuf arg, Entry<T> arg2) throws IOException {
        TrackedData<T> lv = arg2.getData();
        int i = TrackedDataHandlerRegistry.getId(lv.getType());
        if (i < 0) {
            throw new EncoderException("Unknown serializer type " + lv.getType());
        }
        arg.writeByte(lv.getId());
        arg.writeVarInt(i);
        lv.getType().write(arg, arg2.get());
    }

    @Nullable
    public static List<Entry<?>> deserializePacket(PacketByteBuf arg) throws IOException {
        short i;
        ArrayList list = null;
        while ((i = arg.readUnsignedByte()) != 255) {
            int j;
            TrackedDataHandler<?> lv;
            if (list == null) {
                list = Lists.newArrayList();
            }
            if ((lv = TrackedDataHandlerRegistry.get(j = arg.readVarInt())) == null) {
                throw new DecoderException("Unknown serializer type " + j);
            }
            list.add(DataTracker.entryFromPacket(arg, i, lv));
        }
        return list;
    }

    private static <T> Entry<T> entryFromPacket(PacketByteBuf arg, int i, TrackedDataHandler<T> arg2) {
        return new Entry<T>(arg2.create(i), arg2.read(arg));
    }

    @Environment(value=EnvType.CLIENT)
    public void writeUpdatedEntries(List<Entry<?>> list) {
        this.lock.writeLock().lock();
        for (Entry<?> lv : list) {
            Entry<?> lv2 = this.entries.get(lv.getData().getId());
            if (lv2 == null) continue;
            this.copyToFrom(lv2, lv);
            this.trackedEntity.onTrackedDataSet(lv.getData());
        }
        this.lock.writeLock().unlock();
        this.dirty = true;
    }

    @Environment(value=EnvType.CLIENT)
    private <T> void copyToFrom(Entry<T> arg, Entry<?> arg2) {
        if (!Objects.equals(((Entry)arg2).data.getType(), ((Entry)arg).data.getType())) {
            throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", ((Entry)arg).data.getId(), this.trackedEntity, ((Entry)arg).value, ((Entry)arg).value.getClass(), ((Entry)arg2).value, ((Entry)arg2).value.getClass()));
        }
        arg.set(arg2.get());
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void clearDirty() {
        this.dirty = false;
        this.lock.readLock().lock();
        for (Entry<?> lv : this.entries.values()) {
            lv.setDirty(false);
        }
        this.lock.readLock().unlock();
    }

    public static class Entry<T> {
        private final TrackedData<T> data;
        private T value;
        private boolean dirty;

        public Entry(TrackedData<T> data, T value) {
            this.data = data;
            this.value = value;
            this.dirty = true;
        }

        public TrackedData<T> getData() {
            return this.data;
        }

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }

        public boolean isDirty() {
            return this.dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public Entry<T> copy() {
            return new Entry<T>(this.data, this.data.getType().copy(this.value));
        }
    }
}

