/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class MemoryExpiryDataFix
extends ChoiceFix {
    public MemoryExpiryDataFix(Schema schema, String string) {
        super(schema, false, "Memory expiry data fix (" + string + ")", TypeReferences.ENTITY, string);
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::updateBrain);
    }

    public Dynamic<?> updateBrain(Dynamic<?> dynamic) {
        return dynamic.update("Brain", this::updateMemories);
    }

    private Dynamic<?> updateMemories(Dynamic<?> dynamic) {
        return dynamic.update("memories", this::updateMemoryMap);
    }

    private Dynamic<?> updateMemoryMap(Dynamic<?> dynamic) {
        return dynamic.updateMapValues(this::updateMemoryMapValues);
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateMemoryMapValues(Pair<Dynamic<?>, Dynamic<?>> pair) {
        return pair.mapSecond(this::updateMemoryMapValueEntry);
    }

    private Dynamic<?> updateMemoryMapValueEntry(Dynamic<?> dynamic) {
        return dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("value"), dynamic));
    }
}

