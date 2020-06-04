/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkUpdateState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;

public class FeatureUpdater {
    private static final Map<String, String> OLD_TO_NEW = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Village", "Village");
        hashMap.put("Mineshaft", "Mineshaft");
        hashMap.put("Mansion", "Mansion");
        hashMap.put("Igloo", "Temple");
        hashMap.put("Desert_Pyramid", "Temple");
        hashMap.put("Jungle_Pyramid", "Temple");
        hashMap.put("Swamp_Hut", "Temple");
        hashMap.put("Stronghold", "Stronghold");
        hashMap.put("Monument", "Monument");
        hashMap.put("Fortress", "Fortress");
        hashMap.put("EndCity", "EndCity");
    });
    private static final Map<String, String> ANCIENT_TO_OLD = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("Iglu", "Igloo");
        hashMap.put("TeDP", "Desert_Pyramid");
        hashMap.put("TeJP", "Jungle_Pyramid");
        hashMap.put("TeSH", "Swamp_Hut");
    });
    private final boolean needsUpdate;
    private final Map<String, Long2ObjectMap<CompoundTag>> featureIdToChunkTag = Maps.newHashMap();
    private final Map<String, ChunkUpdateState> updateStates = Maps.newHashMap();
    private final List<String> field_17658;
    private final List<String> field_17659;

    public FeatureUpdater(@Nullable PersistentStateManager arg, List<String> list, List<String> list2) {
        this.field_17658 = list;
        this.field_17659 = list2;
        this.init(arg);
        boolean bl = false;
        for (String string : this.field_17659) {
            bl |= this.featureIdToChunkTag.get(string) != null;
        }
        this.needsUpdate = bl;
    }

    public void markResolved(long l) {
        for (String string : this.field_17658) {
            ChunkUpdateState lv = this.updateStates.get(string);
            if (lv == null || !lv.isRemaining(l)) continue;
            lv.markResolved(l);
            lv.markDirty();
        }
    }

    public CompoundTag getUpdatedReferences(CompoundTag arg) {
        CompoundTag lv = arg.getCompound("Level");
        ChunkPos lv2 = new ChunkPos(lv.getInt("xPos"), lv.getInt("zPos"));
        if (this.needsUpdate(lv2.x, lv2.z)) {
            arg = this.getUpdatedStarts(arg, lv2);
        }
        CompoundTag lv3 = lv.getCompound("Structures");
        CompoundTag lv4 = lv3.getCompound("References");
        for (String string : this.field_17659) {
            StructureFeature lv5 = (StructureFeature)StructureFeature.STRUCTURES.get((Object)string.toLowerCase(Locale.ROOT));
            if (lv4.contains(string, 12) || lv5 == null) continue;
            int i = 8;
            LongArrayList longList = new LongArrayList();
            for (int j = lv2.x - 8; j <= lv2.x + 8; ++j) {
                for (int k = lv2.z - 8; k <= lv2.z + 8; ++k) {
                    if (!this.needsUpdate(j, k, string)) continue;
                    longList.add(ChunkPos.toLong(j, k));
                }
            }
            lv4.putLongArray(string, (List<Long>)longList);
        }
        lv3.put("References", lv4);
        lv.put("Structures", lv3);
        arg.put("Level", lv);
        return arg;
    }

    private boolean needsUpdate(int i, int j, String string) {
        if (!this.needsUpdate) {
            return false;
        }
        return this.featureIdToChunkTag.get(string) != null && this.updateStates.get(OLD_TO_NEW.get(string)).contains(ChunkPos.toLong(i, j));
    }

    private boolean needsUpdate(int i, int j) {
        if (!this.needsUpdate) {
            return false;
        }
        for (String string : this.field_17659) {
            if (this.featureIdToChunkTag.get(string) == null || !this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(ChunkPos.toLong(i, j))) continue;
            return true;
        }
        return false;
    }

    private CompoundTag getUpdatedStarts(CompoundTag arg, ChunkPos arg2) {
        CompoundTag lv = arg.getCompound("Level");
        CompoundTag lv2 = lv.getCompound("Structures");
        CompoundTag lv3 = lv2.getCompound("Starts");
        for (String string : this.field_17659) {
            CompoundTag lv4;
            Long2ObjectMap<CompoundTag> long2ObjectMap = this.featureIdToChunkTag.get(string);
            if (long2ObjectMap == null) continue;
            long l = arg2.toLong();
            if (!this.updateStates.get(OLD_TO_NEW.get(string)).isRemaining(l) || (lv4 = (CompoundTag)long2ObjectMap.get(l)) == null) continue;
            lv3.put(string, lv4);
        }
        lv2.put("Starts", lv3);
        lv.put("Structures", lv2);
        arg.put("Level", lv);
        return arg;
    }

    private void init(@Nullable PersistentStateManager arg) {
        if (arg == null) {
            return;
        }
        for (String string2 : this.field_17658) {
            CompoundTag lv = new CompoundTag();
            try {
                lv = arg.readTag(string2, 1493).getCompound("data").getCompound("Features");
                if (lv.isEmpty()) {
                    continue;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            for (String string22 : lv.getKeys()) {
                String string3;
                String string4;
                CompoundTag lv2 = lv.getCompound(string22);
                long l = ChunkPos.toLong(lv2.getInt("ChunkX"), lv2.getInt("ChunkZ"));
                ListTag lv3 = lv2.getList("Children", 10);
                if (!lv3.isEmpty() && (string4 = ANCIENT_TO_OLD.get(string3 = lv3.getCompound(0).getString("id"))) != null) {
                    lv2.putString("id", string4);
                }
                String string5 = lv2.getString("id");
                this.featureIdToChunkTag.computeIfAbsent(string5, string -> new Long2ObjectOpenHashMap()).put(l, (Object)lv2);
            }
            String string6 = string2 + "_index";
            ChunkUpdateState lv4 = arg.getOrCreate(() -> new ChunkUpdateState(string6), string6);
            if (lv4.getAll().isEmpty()) {
                ChunkUpdateState lv5 = new ChunkUpdateState(string6);
                this.updateStates.put(string2, lv5);
                for (String string7 : lv.getKeys()) {
                    CompoundTag lv6 = lv.getCompound(string7);
                    lv5.add(ChunkPos.toLong(lv6.getInt("ChunkX"), lv6.getInt("ChunkZ")));
                }
                lv5.markDirty();
                continue;
            }
            this.updateStates.put(string2, lv4);
        }
    }

    public static FeatureUpdater create(RegistryKey<World> arg, @Nullable PersistentStateManager arg2) {
        if (arg == World.OVERWORLD) {
            return new FeatureUpdater(arg2, (List<String>)ImmutableList.of((Object)"Monument", (Object)"Stronghold", (Object)"Village", (Object)"Mineshaft", (Object)"Temple", (Object)"Mansion"), (List<String>)ImmutableList.of((Object)"Village", (Object)"Mineshaft", (Object)"Mansion", (Object)"Igloo", (Object)"Desert_Pyramid", (Object)"Jungle_Pyramid", (Object)"Swamp_Hut", (Object)"Stronghold", (Object)"Monument"));
        }
        if (arg == World.NETHER) {
            ImmutableList list = ImmutableList.of((Object)"Fortress");
            return new FeatureUpdater(arg2, (List<String>)list, (List<String>)list);
        }
        if (arg == World.END) {
            ImmutableList list2 = ImmutableList.of((Object)"EndCity");
            return new FeatureUpdater(arg2, (List<String>)list2, (List<String>)list2);
        }
        throw new RuntimeException(String.format("Unknown dimension type : %s", arg));
    }
}

