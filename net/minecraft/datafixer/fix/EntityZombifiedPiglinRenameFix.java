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

public class EntityZombifiedPiglinRenameFix
extends EntityRenameFix {
    public static final Map<String, String> RENAMES = ImmutableMap.builder().put((Object)"minecraft:zombie_pigman_spawn_egg", (Object)"minecraft:zombified_piglin_spawn_egg").build();

    public EntityZombifiedPiglinRenameFix(Schema schema) {
        super("EntityZombifiedPiglinRenameFix", schema, true);
    }

    @Override
    protected String rename(String string) {
        return Objects.equals("minecraft:zombie_pigman", string) ? "minecraft:zombified_piglin" : string;
    }
}

