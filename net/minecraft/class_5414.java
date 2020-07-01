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
package net.minecraft;

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

public interface class_5414<T> {
    public Map<Identifier, Tag<T>> method_30204();

    @Nullable
    default public Tag<T> method_30210(Identifier arg) {
        return this.method_30204().get(arg);
    }

    public Tag<T> method_30213(Identifier var1);

    @Nullable
    public Identifier method_30205(Tag<T> var1);

    default public Identifier method_30212(Tag<T> arg) {
        Identifier lv = this.method_30205(arg);
        if (lv == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return lv;
    }

    default public Collection<Identifier> method_30211() {
        return this.method_30204().keySet();
    }

    @Environment(value=EnvType.CLIENT)
    default public Collection<Identifier> method_30206(T object) {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.method_30204().entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    default public void method_30208(PacketByteBuf arg, DefaultedRegistry<T> arg2) {
        Map<Identifier, Tag<T>> map = this.method_30204();
        arg.writeVarInt(map.size());
        for (Map.Entry<Identifier, Tag<T>> entry : map.entrySet()) {
            arg.writeIdentifier(entry.getKey());
            arg.writeVarInt(entry.getValue().values().size());
            for (T object : entry.getValue().values()) {
                arg.writeVarInt(arg2.getRawId(object));
            }
        }
    }

    public static <T> class_5414<T> method_30209(PacketByteBuf arg, Registry<T> arg2) {
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
        return class_5414.method_30207(map);
    }

    public static <T> class_5414<T> method_30214() {
        return class_5414.method_30207(ImmutableBiMap.of());
    }

    public static <T> class_5414<T> method_30207(Map<Identifier, Tag<T>> map) {
        ImmutableBiMap biMap = ImmutableBiMap.copyOf(map);
        return new class_5414<T>((BiMap)biMap){
            private final Tag<T> field_25743 = SetTag.empty();
            final /* synthetic */ BiMap field_25742;
            {
                this.field_25742 = biMap;
            }

            @Override
            public Tag<T> method_30213(Identifier arg) {
                return (Tag)this.field_25742.getOrDefault((Object)arg, this.field_25743);
            }

            @Override
            @Nullable
            public Identifier method_30205(Tag<T> arg) {
                if (arg instanceof Tag.Identified) {
                    return ((Tag.Identified)arg).getId();
                }
                return (Identifier)this.field_25742.inverse().get(arg);
            }

            @Override
            public Map<Identifier, Tag<T>> method_30204() {
                return this.field_25742;
            }
        };
    }
}

