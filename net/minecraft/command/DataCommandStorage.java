/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

public class DataCommandStorage {
    private final Map<String, PersistentState> storages = Maps.newHashMap();
    private final PersistentStateManager stateManager;

    public DataCommandStorage(PersistentStateManager arg) {
        this.stateManager = arg;
    }

    private PersistentState createStorage(String string, String string2) {
        PersistentState lv = new PersistentState(string2);
        this.storages.put(string, lv);
        return lv;
    }

    public CompoundTag get(Identifier arg) {
        String string2;
        String string = arg.getNamespace();
        PersistentState lv = this.stateManager.get(() -> this.method_22549(string, string2 = DataCommandStorage.getSaveKey(string)), string2);
        return lv != null ? lv.get(arg.getPath()) : new CompoundTag();
    }

    public void set(Identifier arg, CompoundTag arg2) {
        String string = arg.getNamespace();
        String string2 = DataCommandStorage.getSaveKey(string);
        this.stateManager.getOrCreate(() -> this.createStorage(string, string2), string2).set(arg.getPath(), arg2);
    }

    public Stream<Identifier> getIds() {
        return this.storages.entrySet().stream().flatMap(entry -> ((PersistentState)entry.getValue()).getIds((String)entry.getKey()));
    }

    private static String getSaveKey(String string) {
        return "command_storage_" + string;
    }

    private /* synthetic */ PersistentState method_22549(String string, String string2) {
        return this.createStorage(string, string2);
    }

    static class PersistentState
    extends net.minecraft.world.PersistentState {
        private final Map<String, CompoundTag> map = Maps.newHashMap();

        public PersistentState(String string) {
            super(string);
        }

        @Override
        public void fromTag(CompoundTag arg) {
            CompoundTag lv = arg.getCompound("contents");
            for (String string : lv.getKeys()) {
                this.map.put(string, lv.getCompound(string));
            }
        }

        @Override
        public CompoundTag toTag(CompoundTag arg) {
            CompoundTag lv = new CompoundTag();
            this.map.forEach((string, arg2) -> lv.put((String)string, arg2.copy()));
            arg.put("contents", lv);
            return arg;
        }

        public CompoundTag get(String string) {
            CompoundTag lv = this.map.get(string);
            return lv != null ? lv : new CompoundTag();
        }

        public void set(String string, CompoundTag arg) {
            if (arg.isEmpty()) {
                this.map.remove(string);
            } else {
                this.map.put(string, arg);
            }
            this.markDirty();
        }

        public Stream<Identifier> getIds(String string) {
            return this.map.keySet().stream().map(string2 -> new Identifier(string, (String)string2));
        }
    }
}

