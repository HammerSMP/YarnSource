/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T>
extends TagContainer<T> {
    private final Registry<T> registry;

    public RegistryTagContainer(Registry<T> arg, String string, String string2) {
        super(arg::getOrEmpty, string, string2);
        this.registry = arg;
    }

    public void toPacket(PacketByteBuf arg) {
        Map map = this.getEntries();
        arg.writeVarInt(map.size());
        for (Map.Entry entry : map.entrySet()) {
            arg.writeIdentifier(entry.getKey());
            arg.writeVarInt(entry.getValue().values().size());
            for (Object object : entry.getValue().values()) {
                arg.writeVarInt(this.registry.getRawId(object));
            }
        }
    }

    public void fromPacket(PacketByteBuf arg) {
        HashMap map = Maps.newHashMap();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier lv = arg.readIdentifier();
            int k = arg.readVarInt();
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (int l = 0; l < k; ++l) {
                builder.add(this.registry.get(arg.readVarInt()));
            }
            map.put(lv, Tag.of(builder.build()));
        }
        this.setEntries(map);
    }
}

