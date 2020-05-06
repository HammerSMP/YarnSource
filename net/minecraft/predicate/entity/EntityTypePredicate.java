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
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> arg) {
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

    public static EntityTypePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        String string = JsonHelper.asString(jsonElement, "type");
        if (string.startsWith("#")) {
            Identifier lv = new Identifier(string.substring(1));
            return new Tagged(EntityTypeTags.getContainer().getOrCreate(lv));
        }
        Identifier lv2 = new Identifier(string);
        EntityType lv3 = (EntityType)Registry.ENTITY_TYPE.getOrEmpty(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + lv2 + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.getIds())));
        return new Single(lv3);
    }

    public static EntityTypePredicate create(EntityType<?> arg) {
        return new Single(arg);
    }

    public static EntityTypePredicate create(Tag<EntityType<?>> arg) {
        return new Tagged(arg);
    }

    static class Tagged
    extends EntityTypePredicate {
        private final Tag<EntityType<?>> tag;

        public Tagged(Tag<EntityType<?>> arg) {
            this.tag = arg;
        }

        @Override
        public boolean matches(EntityType<?> arg) {
            return this.tag.contains(arg);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + EntityTypeTags.getContainer().checkId(this.tag));
        }
    }

    static class Single
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public Single(EntityType<?> arg) {
            this.type = arg;
        }

        @Override
        public boolean matches(EntityType<?> arg) {
            return this.type == arg;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Registry.ENTITY_TYPE.getId(this.type).toString());
        }
    }
}

