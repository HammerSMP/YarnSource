/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> type) {
            return true;
        }

        @Override
        public JsonElement toJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on((String)", ");

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement toJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        String string = JsonHelper.asString(json, "type");
        if (string.startsWith("#")) {
            Identifier lv = new Identifier(string.substring(1));
            return new Tagged(ServerTagManagerHolder.getTagManager().getEntityTypes().getTagOrEmpty(lv));
        }
        Identifier lv2 = new Identifier(string);
        EntityType<?> lv3 = Registry.ENTITY_TYPE.getOrEmpty(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + lv2 + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds())));
        return new Single(lv3);
    }

    public static EntityTypePredicate create(EntityType<?> type) {
        return new Single(type);
    }

    public static EntityTypePredicate create(Tag<EntityType<?>> tag) {
        return new Tagged(tag);
    }

    static class Tagged
    extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;

        public Tagged(Tag<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean matches(EntityType<?> type) {
            return this.tag.contains(type);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + ServerTagManagerHolder.getTagManager().getEntityTypes().getTagId(this.tag));
        }
    }

    static class Single
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public Single(EntityType<?> type) {
            this.type = type;
        }

        @Override
        public boolean matches(EntityType<?> type) {
            return this.type == type;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.type).toString());
        }
    }
}

