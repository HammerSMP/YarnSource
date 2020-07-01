/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import net.minecraft.class_5413;
import net.minecraft.class_5414;
import net.minecraft.class_5415;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class EntityTypeTags {
    protected static final GlobalTagAccessor<EntityType<?>> ACCESSOR = class_5413.method_30201(new Identifier("entity_type"), class_5415::method_30221);
    public static final Tag.Identified<EntityType<?>> SKELETONS = EntityTypeTags.register("skeletons");
    public static final Tag.Identified<EntityType<?>> RAIDERS = EntityTypeTags.register("raiders");
    public static final Tag.Identified<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.register("beehive_inhabitors");
    public static final Tag.Identified<EntityType<?>> ARROWS = EntityTypeTags.register("arrows");
    public static final Tag.Identified<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.register("impact_projectiles");

    private static Tag.Identified<EntityType<?>> register(String string) {
        return ACCESSOR.get(string);
    }

    public static class_5414<EntityType<?>> getContainer() {
        return ACCESSOR.getContainer();
    }
}

