/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public interface TagGroup<T> {
    public Map<Identifier, Tag<T>> getTags();

    @Nullable
    default public Tag<T> getTag(Identifier arg) {
        return this.getTags().get(arg);
    }

    public Tag<T> getTagOrEmpty(Identifier var1);

    @Nullable
    public Identifier getUncheckedTagId(Tag<T> var1);

    default public Identifier getTagId(Tag<T> arg) {
        Identifier lv = this.getUncheckedTagId(arg);
        if (lv == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return lv;
    }

    default public Collection<Identifier> getTagIds() {
        return this.getTags().keySet();
    }

    @Environment(value=EnvType.CLIENT)
    default public Collection<Identifier> getTagsFor(T object) {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.getTags().entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    default public void toPacket(PacketByteBuf arg, DefaultedRegistry<T> arg2) {
        Map<Identifier, Tag<T>> map = this.getTags();
        arg.writeVarInt(map.size());
        for (Map.Entry<Identifier, Tag<T>> entry : map.entrySet()) {
            arg.writeIdentifier(entry.getKey());
            arg.writeVarInt(entry.getValue().values().size());
            for (T object : entry.getValue().values()) {
                arg.writeVarInt(arg2.getRawId(object));
            }
        }
    }

    public static <T> TagGroup<T> fromPacket(PacketByteBuf arg, Registry<T> arg2) {
        HashMap map = Maps.newHashMap();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier lv = arg.readIdentifier();
            int k = arg.readVarInt();
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (int l = 0; l < k; ++l) {
                builder.add(arg2.get(arg.readVarInt()));
            }
            map.put(lv, Tag.of(builder.build()));
        }
        return TagGroup.create(map);
    }

    public static <T> TagGroup<T> createEmpty() {
        return TagGroup.create(ImmutableBiMap.of());
    }

    public static <T> TagGroup<T> create(Map<Identifier, Tag<T>> map) {
        ImmutableBiMap biMap = ImmutableBiMap.copyOf(map);
        return new TagGroup<T>((BiMap)biMap){
            private final Tag<T> emptyTag = SetTag.empty();
            final /* synthetic */ BiMap tags;
            {
                this.tags = biMap;
            }

            @Override
            public Tag<T> getTagOrEmpty(Identifier arg) {
                return (Tag)this.tags.getOrDefault((Object)arg, this.emptyTag);
            }

            @Override
            @Nullable
            public Identifier getUncheckedTagId(Tag<T> arg) {
                if (arg instanceof Tag.Identified) {
                    return ((Tag.Identified)arg).getId();
                }
                return (Identifier)this.tags.inverse().get(arg);
            }

            @Override
            public Map<Identifier, Tag<T>> getTags() {
                return this.tags;
            }
        };
    }
}

