/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoverEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Action<?> action;
    private final Object contents;

    public <T> HoverEvent(Action<T> arg, T object) {
        this.action = arg;
        this.contents = object;
    }

    public Action<?> getAction() {
        return this.action;
    }

    @Nullable
    public <T> T getValue(Action<T> arg) {
        if (this.action == arg) {
            return (T)((Action)arg).cast(this.contents);
        }
        return null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        HoverEvent lv = (HoverEvent)object;
        return this.action == lv.action && Objects.equals(this.contents, lv.contents);
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.contents + '\'' + '}';
    }

    public int hashCode() {
        int i = this.action.hashCode();
        i = 31 * i + (this.contents != null ? this.contents.hashCode() : 0);
        return i;
    }

    @Nullable
    public static HoverEvent fromJson(JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "action", null);
        if (string == null) {
            return null;
        }
        Action lv = Action.byName(string);
        if (lv == null) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get("contents");
        if (jsonElement != null) {
            return lv.buildHoverEvent(jsonElement);
        }
        MutableText lv2 = Text.Serializer.fromJson(jsonObject.get("value"));
        if (lv2 != null) {
            return lv.buildHoverEvent(lv2);
        }
        return null;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", this.action.getName());
        jsonObject.add("contents", this.action.contentsToJson(this.contents));
        return jsonObject;
    }

    public static class Action<T> {
        public static final Action<Text> SHOW_TEXT = new Action<Text>("show_text", true, Text.Serializer::fromJson, Text.Serializer::toJsonTree, Function.identity());
        public static final Action<ItemStackContent> SHOW_ITEM = new Action<ItemStackContent>("show_item", true, jsonElement -> ItemStackContent.method_27684(jsonElement), object -> ItemStackContent.method_27686((ItemStackContent)object), arg -> ItemStackContent.method_27685(arg));
        public static final Action<EntityContent> SHOW_ENTITY = new Action<EntityContent>("show_entity", true, EntityContent::parse, EntityContent::toJson, EntityContent::parse);
        private static final Map<String, Action> BY_NAME = (Map)Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(Action::getName, arg -> arg));
        private final String name;
        private final boolean parsable;
        private final Function<JsonElement, T> deserializer;
        private final Function<T, JsonElement> serializer;
        private final Function<Text, T> legacyDeserializer;

        public Action(String string, boolean bl, Function<JsonElement, T> function, Function<T, JsonElement> function2, Function<Text, T> function3) {
            this.name = string;
            this.parsable = bl;
            this.deserializer = function;
            this.serializer = function2;
            this.legacyDeserializer = function3;
        }

        public boolean isParsable() {
            return this.parsable;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Action byName(String string) {
            return BY_NAME.get(string);
        }

        private T cast(Object object) {
            return (T)object;
        }

        public HoverEvent buildHoverEvent(JsonElement jsonElement) {
            T object = this.deserializer.apply(jsonElement);
            return new HoverEvent(this, object);
        }

        public HoverEvent buildHoverEvent(Text arg) {
            T object = this.legacyDeserializer.apply(arg);
            return new HoverEvent(this, object);
        }

        public JsonElement contentsToJson(Object object) {
            return this.serializer.apply(this.cast(object));
        }

        public String toString() {
            return "<action " + this.name + ">";
        }
    }

    public static class ItemStackContent {
        private final Item item;
        private final int count;
        @Nullable
        private final CompoundTag tag;
        @Nullable
        @Environment(value=EnvType.CLIENT)
        private ItemStack stack;

        ItemStackContent(Item arg, int i, @Nullable CompoundTag arg2) {
            this.item = arg;
            this.count = i;
            this.tag = arg2;
        }

        public ItemStackContent(ItemStack arg) {
            this(arg.getItem(), arg.getCount(), arg.getTag() != null ? arg.getTag().copy() : null);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ItemStackContent lv = (ItemStackContent)object;
            return this.count == lv.count && this.item.equals(lv.item) && Objects.equals(this.tag, lv.tag);
        }

        public int hashCode() {
            int i = this.item.hashCode();
            i = 31 * i + this.count;
            i = 31 * i + (this.tag != null ? this.tag.hashCode() : 0);
            return i;
        }

        @Environment(value=EnvType.CLIENT)
        public ItemStack asStack() {
            if (this.stack == null) {
                this.stack = new ItemStack(this.item, this.count);
                if (this.tag != null) {
                    this.stack.setTag(this.tag);
                }
            }
            return this.stack;
        }

        private static ItemStackContent parse(JsonElement jsonElement) {
            if (jsonElement.isJsonPrimitive()) {
                return new ItemStackContent(Registry.ITEM.get(new Identifier(jsonElement.getAsString())), 1, null);
            }
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "item");
            Item lv = Registry.ITEM.get(new Identifier(JsonHelper.getString(jsonObject, "id")));
            int i = JsonHelper.getInt(jsonObject, "count", 1);
            if (jsonObject.has("tag")) {
                String string = JsonHelper.getString(jsonObject, "tag");
                try {
                    CompoundTag lv2 = StringNbtReader.parse(string);
                    return new ItemStackContent(lv, i, lv2);
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.warn("Failed to parse tag: {}", (Object)string, (Object)commandSyntaxException);
                }
            }
            return new ItemStackContent(lv, i, null);
        }

        @Nullable
        private static ItemStackContent parse(Text arg) {
            try {
                CompoundTag lv = StringNbtReader.parse(arg.getString());
                return new ItemStackContent(ItemStack.fromTag(lv));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                LOGGER.warn("Failed to parse item tag: {}", (Object)arg, (Object)commandSyntaxException);
                return null;
            }
        }

        private JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", Registry.ITEM.getId(this.item).toString());
            if (this.count != 1) {
                jsonObject.addProperty("count", (Number)this.count);
            }
            if (this.tag != null) {
                jsonObject.addProperty("tag", this.tag.toString());
            }
            return jsonObject;
        }
    }

    public static class EntityContent {
        public final EntityType<?> entityType;
        public final UUID uuid;
        @Nullable
        public final Text name;
        @Nullable
        @Environment(value=EnvType.CLIENT)
        private List<Text> tooltip;

        public EntityContent(EntityType<?> arg, UUID uUID, @Nullable Text arg2) {
            this.entityType = arg;
            this.uuid = uUID;
            this.name = arg2;
        }

        @Nullable
        public static EntityContent parse(JsonElement jsonElement) {
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            EntityType<?> lv = Registry.ENTITY_TYPE.get(new Identifier(JsonHelper.getString(jsonObject, "type")));
            UUID uUID = UUID.fromString(JsonHelper.getString(jsonObject, "id"));
            MutableText lv2 = Text.Serializer.fromJson(jsonObject.get("name"));
            return new EntityContent(lv, uUID, lv2);
        }

        @Nullable
        public static EntityContent parse(Text arg) {
            try {
                CompoundTag lv = StringNbtReader.parse(arg.getString());
                MutableText lv2 = Text.Serializer.fromJson(lv.getString("name"));
                EntityType<?> lv3 = Registry.ENTITY_TYPE.get(new Identifier(lv.getString("type")));
                UUID uUID = UUID.fromString(lv.getString("id"));
                return new EntityContent(lv3, uUID, lv2);
            }
            catch (JsonSyntaxException | CommandSyntaxException exception) {
                return null;
            }
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", Registry.ENTITY_TYPE.getId(this.entityType).toString());
            jsonObject.addProperty("id", this.uuid.toString());
            if (this.name != null) {
                jsonObject.add("name", Text.Serializer.toJsonTree(this.name));
            }
            return jsonObject;
        }

        @Environment(value=EnvType.CLIENT)
        public List<Text> asTooltip() {
            if (this.tooltip == null) {
                this.tooltip = Lists.newArrayList();
                if (this.name != null) {
                    this.tooltip.add(this.name);
                }
                this.tooltip.add(new TranslatableText("gui.entity_tooltip.type", this.entityType.getName()));
                this.tooltip.add(new LiteralText(this.uuid.toString()));
            }
            return this.tooltip;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            EntityContent lv = (EntityContent)object;
            return this.entityType.equals(lv.entityType) && this.uuid.equals(lv.uuid) && Objects.equals(this.name, lv.name);
        }

        public int hashCode() {
            int i = this.entityType.hashCode();
            i = 31 * i + this.uuid.hashCode();
            i = 31 * i + (this.name != null ? this.name.hashCode() : 0);
            return i;
        }
    }
}

