/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider
extends AbstractTagProvider<EntityType<?>> {
    public EntityTypeTagsProvider(DataGenerator arg) {
        super(arg, Registry.ENTITY_TYPE);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        this.getOrCreateTagBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
        this.getOrCreateTagBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        this.getOrCreateTagBuilder(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
    }

    @Override
    protected Path getOutput(Identifier arg) {
        return this.root.getOutput().resolve("data/" + arg.getNamespace() + "/tags/entity_types/" + arg.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Entity Type Tags";
    }
}

