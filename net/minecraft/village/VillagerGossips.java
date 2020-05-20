/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.village.VillageGossipType;

public class VillagerGossips {
    private final Map<UUID, Reputation> entityReputation = Maps.newHashMap();

    public void decay() {
        Iterator<Reputation> iterator = this.entityReputation.values().iterator();
        while (iterator.hasNext()) {
            Reputation lv = iterator.next();
            lv.decay();
            if (!lv.isObsolete()) continue;
            iterator.remove();
        }
    }

    private Stream<GossipEntry> entries() {
        return this.entityReputation.entrySet().stream().flatMap(entry -> ((Reputation)entry.getValue()).entriesFor((UUID)entry.getKey()));
    }

    private Collection<GossipEntry> pickGossips(Random random, int i) {
        List list = this.entries().collect(Collectors.toList());
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        int[] is = new int[list.size()];
        int j = 0;
        for (int k = 0; k < list.size(); ++k) {
            GossipEntry lv = (GossipEntry)list.get(k);
            is[k] = (j += Math.abs(lv.getValue())) - 1;
        }
        Set set = Sets.newIdentityHashSet();
        for (int l = 0; l < i; ++l) {
            int m = random.nextInt(j);
            int n = Arrays.binarySearch(is, m);
            set.add(list.get(n < 0 ? -n - 1 : n));
        }
        return set;
    }

    private Reputation getReputationFor(UUID uUID2) {
        return this.entityReputation.computeIfAbsent(uUID2, uUID -> new Reputation());
    }

    public void shareGossipFrom(VillagerGossips arg2, Random random, int i) {
        Collection<GossipEntry> collection = arg2.pickGossips(random, i);
        collection.forEach(arg -> {
            int i = arg.value - arg.type.shareDecrement;
            if (i >= 2) {
                this.getReputationFor(arg.target.getUuid()).associatedGossip.mergeInt((Object)arg.type, i, VillagerGossips::max);
            }
        });
    }

    public int getReputationFor(UUID uUID, Predicate<VillageGossipType> predicate) {
        Reputation lv = this.entityReputation.get(uUID);
        return lv != null ? lv.getValueFor(predicate) : 0;
    }

    public void startGossip(UUID uUID, VillageGossipType arg, int i) {
        Reputation lv = this.getReputationFor(uUID);
        lv.associatedGossip.mergeInt((Object)arg, i, (integer, integer2) -> this.mergeReputation(arg, (int)integer, (int)integer2));
        lv.clamp(arg);
        if (lv.isObsolete()) {
            this.entityReputation.remove(uUID);
        }
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createList(this.entries().map(arg -> arg.serialize(dynamicOps)).map(Dynamic::getValue)));
    }

    public void deserialize(Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::deserialize).flatMap(dataResult -> Util.stream(dataResult.result())).forEach(arg -> this.getReputationFor(arg.target.getUuid()).associatedGossip.put((Object)arg.type, arg.value));
    }

    private static int max(int i, int j) {
        return Math.max(i, j);
    }

    private int mergeReputation(VillageGossipType arg, int i, int j) {
        int k = i + j;
        return k > arg.maxValue ? Math.max(arg.maxValue, i) : k;
    }

    static class Reputation {
        private final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap();

        private Reputation() {
        }

        public int getValueFor(Predicate<VillageGossipType> predicate) {
            return this.associatedGossip.object2IntEntrySet().stream().filter(entry -> predicate.test((VillageGossipType)((Object)entry.getKey()))).mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier).sum();
        }

        public Stream<GossipEntry> entriesFor(UUID uUID) {
            return this.associatedGossip.object2IntEntrySet().stream().map(entry -> new GossipEntry(uUID, (VillageGossipType)((Object)((Object)entry.getKey())), entry.getIntValue()));
        }

        public void decay() {
            ObjectIterator objectIterator = this.associatedGossip.object2IntEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
                int i = entry.getIntValue() - ((VillageGossipType)entry.getKey()).decay;
                if (i < 2) {
                    objectIterator.remove();
                    continue;
                }
                entry.setValue(i);
            }
        }

        public boolean isObsolete() {
            return this.associatedGossip.isEmpty();
        }

        public void clamp(VillageGossipType arg) {
            int i = this.associatedGossip.getInt((Object)arg);
            if (i > arg.maxValue) {
                this.associatedGossip.put((Object)arg, arg.maxValue);
            }
            if (i < 2) {
                this.remove(arg);
            }
        }

        public void remove(VillageGossipType arg) {
            this.associatedGossip.removeInt((Object)arg);
        }
    }

    static class GossipEntry {
        public final DynamicSerializableUuid target;
        public final VillageGossipType type;
        public final int value;

        public GossipEntry(UUID uUID, VillageGossipType arg, int i) {
            this(new DynamicSerializableUuid(uUID), arg, i);
        }

        public GossipEntry(DynamicSerializableUuid arg, VillageGossipType arg2, int i) {
            this.target = arg;
            this.type = arg2;
            this.value = i;
        }

        public int getValue() {
            return this.value * this.type.multiplier;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + (Object)((Object)this.type) + ", value=" + this.value + '}';
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
            return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Target"), DynamicSerializableUuid.field_25122.encodeStart(dynamicOps, (Object)this.target).result().orElseThrow(RuntimeException::new), (Object)dynamicOps.createString("Type"), (Object)dynamicOps.createString(this.type.key), (Object)dynamicOps.createString("Value"), (Object)dynamicOps.createInt(this.value))));
        }

        public static DataResult<GossipEntry> deserialize(Dynamic<?> dynamic) {
            return DataResult.unbox((App)DataResult.instance().group((App)dynamic.get("Target").read(DynamicSerializableUuid.field_25122), (App)dynamic.get("Type").asString().map(VillageGossipType::byKey), (App)dynamic.get("Value").asNumber().map(Number::intValue)).apply((Applicative)DataResult.instance(), GossipEntry::new));
        }
    }
}

