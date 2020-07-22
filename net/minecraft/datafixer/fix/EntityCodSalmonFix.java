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
import net.minecraft.datafixer.fix.EntityRenameFix;

public class EntityCodSalmonFix
extends EntityRenameFix {
    public static final Map<String, String> ENTITIES = ImmutableMap.builder().put((Object)"minecraft:salmon_mob", (Object)"minecraft:salmon").put((Object)"minecraft:cod_mob", (Object)"minecraft:cod").build();
    public static final Map<String, String> SPAWN_EGGS = ImmutableMap.builder().put((Object)"minecraft:salmon_mob_spawn_egg", (Object)"minecraft:salmon_spawn_egg").put((Object)"minecraft:cod_mob_spawn_egg", (Object)"minecraft:cod_spawn_egg").build();

    public EntityCodSalmonFix(Schema outputSchema, boolean changesType) {
        super("EntityCodSalmonFix", outputSchema, changesType);
    }

    @Override
    protected String rename(String oldName) {
        return ENTITIES.getOrDefault(oldName, oldName);
    }
}

