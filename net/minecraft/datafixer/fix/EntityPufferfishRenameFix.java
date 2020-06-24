/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntityRenameFix;

public class EntityPufferfishRenameFix
extends EntityRenameFix {
    public static final Map<String, String> RENAMED_FISH = ImmutableMap.builder().put((Object)"minecraft:puffer_fish_spawn_egg", (Object)"minecraft:pufferfish_spawn_egg").build();

    public EntityPufferfishRenameFix(Schema schema, boolean bl) {
        super("EntityPufferfishRenameFix", schema, bl);
    }

    @Override
    protected String rename(String string) {
        return Objects.equals("minecraft:puffer_fish", string) ? "minecraft:pufferfish" : string;
    }
}

