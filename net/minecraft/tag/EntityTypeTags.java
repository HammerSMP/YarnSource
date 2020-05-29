/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.tag;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class EntityTypeTags {
    private static final GlobalTagAccessor<EntityType<?>> ACCESSOR = new GlobalTagAccessor();
    public static final Tag.Identified<EntityType<?>> SKELETONS = EntityTypeTags.register("skeletons");
    public static final Tag.Identified<EntityType<?>> RAIDERS = EntityTypeTags.register("raiders");
    public static final Tag.Identified<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.register("beehive_inhabitors");
    public static final Tag.Identified<EntityType<?>> ARROWS = EntityTypeTags.register("arrows");
    public static final Tag.Identified<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.register("impact_projectiles");

    private static Tag.Identified<EntityType<?>> register(String string) {
        return ACCESSOR.get(string);
    }

    public static void setContainer(TagContainer<EntityType<?>> arg) {
        ACCESSOR.setContainer(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public static void markReady() {
        ACCESSOR.markReady();
    }

    public static TagContainer<EntityType<?>> getContainer() {
        return ACCESSOR.getContainer();
    }

    public static Set<Identifier> method_29215(TagContainer<EntityType<?>> arg) {
        return ACCESSOR.method_29224(arg);
    }
}

