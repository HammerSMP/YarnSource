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

    private Collection<GossipEntry> pickGossips(Random random, int count) {
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
        for (int l = 0; l < count; ++l) {
            int m = random.nextInt(j);
            int n = Arrays.binarySearch(is, m);
            set.add(list.get(n < 0 ? -n - 1 : n));
        }
        return set;
    }

    private Reputation getReputationFor(UUID target) {
        return this.entityReputation.computeIfAbsent(target, uUID -> new Reputation());
    }

    public void shareGossipFrom(VillagerGossips from, Random random, int count) {
        Collection<GossipEntry> collection = from.pickGossips(random, count);
        collection.forEach(arg -> {
            int i = arg.value - arg.type.shareDecrement;
            if (i >= 2) {
                this.getReputationFor(arg.target).associatedGossip.mergeInt((Object)arg.type, i, VillagerGossips::max);
            }
        });
    }

    public int getReputationFor(UUID target, Predicate<VillageGossipType> gossipTypeFilter) {
        Reputation lv = this.entityReputation.get(target);
        return lv != null ? lv.getValueFor(gossipTypeFilter) : 0;
    }

    public void startGossip(UUID target, VillageGossipType type, int value) {
        Reputation lv = this.getReputationFor(target);
        lv.associatedGossip.mergeInt((Object)type, value, (integer, integer2) -> this.mergeReputation(type, (int)integer, (int)integer2));
        lv.clamp(type);
        if (lv.isObsolete()) {
            this.entityReputation.remove(target);
        }
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createList(this.entries().map(arg -> arg.serialize(dynamicOps)).map(Dynamic::getValue)));
    }

    public void deserialize(Dynamic<?> dynamic) {
        dynamic.asStream().map(GossipEntry::deserialize).flatMap(dataResult -> Util.stream(dataResult.result())).forEach(arg -> this.getReputationFor(arg.target).associatedGossip.put((Object)arg.type, arg.value));
    }

    private static int max(int left, int right) {
        return Math.max(left, right);
    }

    private int mergeReputation(VillageGossipType type, int left, int right) {
        int k = left + right;
        return k > type.maxValue ? Math.max(type.maxValue, left) : k;
    }

    static class Reputation {
        private final Object2IntMap<VillageGossipType> associatedGossip = new Object2IntOpenHashMap();

        private Reputation() {
        }

        public int getValueFor(Predicate<VillageGossipType> gossipTypeFilter) {
            return this.associatedGossip.object2IntEntrySet().stream().filter(entry -> gossipTypeFilter.test((VillageGossipType)((Object)entry.getKey()))).mapToInt(entry -> entry.getIntValue() * ((VillageGossipType)entry.getKey()).multiplier).sum();
        }

        public Stream<GossipEntry> entriesFor(UUID target) {
            return this.associatedGossip.object2IntEntrySet().stream().map(entry -> new GossipEntry(target, (VillageGossipType)((Object)((Object)entry.getKey())), entry.getIntValue()));
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

        public void clamp(VillageGossipType gossipType) {
            int i = this.associatedGossip.getInt((Object)gossipType);
            if (i > gossipType.maxValue) {
                this.associatedGossip.put((Object)gossipType, gossipType.maxValue);
            }
            if (i < 2) {
                this.remove(gossipType);
            }
        }

        public void remove(VillageGossipType gossipType) {
            this.associatedGossip.removeInt((Object)gossipType);
        }
    }

    static class GossipEntry {
        public final UUID target;
        public final VillageGossipType type;
        public final int value;

        public GossipEntry(UUID uUID, VillageGossipType arg, int i) {
            this.target = uUID;
            this.type = arg;
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

