/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;

public class Style {
    public static final Identifier DEFAULT_FONT_ID = new Identifier("minecraft", "default");
    public static final Style EMPTY = new Style(null, null, null, null, null, null, null, null, null, null);
    @Nullable
    private final TextColor color;
    @Nullable
    private final Boolean bold;
    @Nullable
    private final Boolean italic;
    @Nullable
    private final Boolean underlined;
    @Nullable
    private final Boolean strikethrough;
    @Nullable
    private final Boolean obfuscated;
    @Nullable
    private final ClickEvent clickEvent;
    @Nullable
    private final HoverEvent hoverEvent;
    @Nullable
    private final String insertion;
    @Nullable
    private final Identifier font;

    private Style(@Nullable TextColor arg, @Nullable Boolean boolean_, @Nullable Boolean boolean2, @Nullable Boolean boolean3, @Nullable Boolean boolean4, @Nullable Boolean boolean5, @Nullable ClickEvent arg2, @Nullable HoverEvent arg3, @Nullable String string, @Nullable Identifier arg4) {
        this.color = arg;
        this.bold = boolean_;
        this.italic = boolean2;
        this.underlined = boolean3;
        this.strikethrough = boolean4;
        this.obfuscated = boolean5;
        this.clickEvent = arg2;
        this.hoverEvent = arg3;
        this.insertion = string;
        this.font = arg4;
    }

    @Nullable
    public TextColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public Identifier getFont() {
        return this.font != null ? this.font : DEFAULT_FONT_ID;
    }

    public Style withColor(@Nullable TextColor arg) {
        return new Style(arg, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withColor(@Nullable Formatting arg) {
        return this.withColor(arg != null ? TextColor.fromFormatting(arg) : null);
    }

    public Style withBold(@Nullable Boolean boolean_) {
        return new Style(this.color, boolean_, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withItalic(@Nullable Boolean boolean_) {
        return new Style(this.color, this.bold, boolean_, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withClickEvent(@Nullable ClickEvent arg) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, arg, this.hoverEvent, this.insertion, this.font);
    }

    public Style setHoverEvent(@Nullable HoverEvent arg) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, arg, this.insertion, this.font);
    }

    public Style withInsertion(@Nullable String string) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, string, this.font);
    }

    @Environment(value=EnvType.CLIENT)
    public Style withFont(@Nullable Identifier arg) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, arg);
    }

    public Style withFormatting(Formatting arg) {
        TextColor lv = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        switch (arg) {
            case OBFUSCATED: {
                boolean5 = true;
                break;
            }
            case BOLD: {
                boolean_ = true;
                break;
            }
            case STRIKETHROUGH: {
                boolean3 = true;
                break;
            }
            case UNDERLINE: {
                boolean4 = true;
                break;
            }
            case ITALIC: {
                boolean2 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                lv = TextColor.fromFormatting(arg);
            }
        }
        return new Style(lv, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    @Environment(value=EnvType.CLIENT)
    public Style withExclusiveFormatting(Formatting arg) {
        TextColor lv = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        switch (arg) {
            case OBFUSCATED: {
                boolean5 = true;
                break;
            }
            case BOLD: {
                boolean_ = true;
                break;
            }
            case STRIKETHROUGH: {
                boolean3 = true;
                break;
            }
            case UNDERLINE: {
                boolean4 = true;
                break;
            }
            case ITALIC: {
                boolean2 = true;
                break;
            }
            case RESET: {
                return EMPTY;
            }
            default: {
                boolean5 = false;
                boolean_ = false;
                boolean3 = false;
                boolean4 = false;
                boolean2 = false;
                lv = TextColor.fromFormatting(arg);
            }
        }
        return new Style(lv, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withFormatting(Formatting ... args) {
        TextColor lv = this.color;
        Boolean boolean_ = this.bold;
        Boolean boolean2 = this.italic;
        Boolean boolean3 = this.strikethrough;
        Boolean boolean4 = this.underlined;
        Boolean boolean5 = this.obfuscated;
        block8: for (Formatting lv2 : args) {
            switch (lv2) {
                case OBFUSCATED: {
                    boolean5 = true;
                    continue block8;
                }
                case BOLD: {
                    boolean_ = true;
                    continue block8;
                }
                case STRIKETHROUGH: {
                    boolean3 = true;
                    continue block8;
                }
                case UNDERLINE: {
                    boolean4 = true;
                    continue block8;
                }
                case ITALIC: {
                    boolean2 = true;
                    continue block8;
                }
                case RESET: {
                    return EMPTY;
                }
                default: {
                    lv = TextColor.fromFormatting(lv2);
                }
            }
        }
        return new Style(lv, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withParent(Style arg) {
        if (this == EMPTY) {
            return arg;
        }
        if (arg == EMPTY) {
            return this;
        }
        return new Style(this.color != null ? this.color : arg.color, this.bold != null ? this.bold : arg.bold, this.italic != null ? this.italic : arg.italic, this.underlined != null ? this.underlined : arg.underlined, this.strikethrough != null ? this.strikethrough : arg.strikethrough, this.obfuscated != null ? this.obfuscated : arg.obfuscated, this.clickEvent != null ? this.clickEvent : arg.clickEvent, this.hoverEvent != null ? this.hoverEvent : arg.hoverEvent, this.insertion != null ? this.insertion : arg.insertion, this.font != null ? this.font : arg.font);
    }

    public String toString() {
        return "Style{ color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + ", font=" + this.getFont() + '}';
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Style) {
            Style lv = (Style)object;
            return this.isBold() == lv.isBold() && Objects.equals(this.getColor(), lv.getColor()) && this.isItalic() == lv.isItalic() && this.isObfuscated() == lv.isObfuscated() && this.isStrikethrough() == lv.isStrikethrough() && this.isUnderlined() == lv.isUnderlined() && Objects.equals(this.getClickEvent(), lv.getClickEvent()) && Objects.equals(this.getHoverEvent(), lv.getHoverEvent()) && Objects.equals(this.getInsertion(), lv.getInsertion()) && Objects.equals(this.getFont(), lv.getFont());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
    }

    public static class Serializer
    implements JsonDeserializer<Style>,
    JsonSerializer<Style> {
        @Nullable
        public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject == null) {
                    return null;
                }
                Boolean boolean_ = Serializer.parseNullableBoolean(jsonObject, "bold");
                Boolean boolean2 = Serializer.parseNullableBoolean(jsonObject, "italic");
                Boolean boolean3 = Serializer.parseNullableBoolean(jsonObject, "underlined");
                Boolean boolean4 = Serializer.parseNullableBoolean(jsonObject, "strikethrough");
                Boolean boolean5 = Serializer.parseNullableBoolean(jsonObject, "obfuscated");
                TextColor lv = Serializer.parseColor(jsonObject);
                String string = Serializer.parseInsertion(jsonObject);
                ClickEvent lv2 = Serializer.getClickEvent(jsonObject);
                HoverEvent lv3 = Serializer.getHoverEvent(jsonObject);
                Identifier lv4 = Serializer.getFont(jsonObject);
                return new Style(lv, boolean_, boolean2, boolean3, boolean4, boolean5, lv2, lv3, string, lv4);
            }
            return null;
        }

        @Nullable
        private static Identifier getFont(JsonObject jsonObject) {
            if (jsonObject.has("font")) {
                String string = JsonHelper.getString(jsonObject, "font");
                try {
                    return new Identifier(string);
                }
                catch (InvalidIdentifierException lv) {
                    throw new JsonSyntaxException("Invalid font name: " + string);
                }
            }
            return null;
        }

        @Nullable
        private static HoverEvent getHoverEvent(JsonObject jsonObject) {
            JsonObject jsonObject2;
            HoverEvent lv;
            if (jsonObject.has("hoverEvent") && (lv = HoverEvent.fromJson(jsonObject2 = JsonHelper.getObject(jsonObject, "hoverEvent"))) != null && lv.getAction().isParsable()) {
                return lv;
            }
            return null;
        }

        @Nullable
        private static ClickEvent getClickEvent(JsonObject jsonObject) {
            if (jsonObject.has("clickEvent")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "clickEvent");
                String string = JsonHelper.getString(jsonObject2, "action", null);
                ClickEvent.Action lv = string == null ? null : ClickEvent.Action.byName(string);
                String string2 = JsonHelper.getString(jsonObject2, "value", null);
                if (lv != null && string2 != null && lv.isUserDefinable()) {
                    return new ClickEvent(lv, string2);
                }
            }
            return null;
        }

        @Nullable
        private static String parseInsertion(JsonObject jsonObject) {
            return JsonHelper.getString(jsonObject, "insertion", null);
        }

        @Nullable
        private static TextColor parseColor(JsonObject jsonObject) {
            if (jsonObject.has("color")) {
                String string = JsonHelper.getString(jsonObject, "color");
                return TextColor.parse(string);
            }
            return null;
        }

        @Nullable
        private static Boolean parseNullableBoolean(JsonObject jsonObject, String string) {
            if (jsonObject.has(string)) {
                return jsonObject.get(string).getAsBoolean();
            }
            return null;
        }

        @Nullable
        public JsonElement serialize(Style arg, Type type, JsonSerializationContext jsonSerializationContext) {
            if (arg.isEmpty()) {
                return null;
            }
            JsonObject jsonObject = new JsonObject();
            if (arg.bold != null) {
                jsonObject.addProperty("bold", arg.bold);
            }
            if (arg.italic != null) {
                jsonObject.addProperty("italic", arg.italic);
            }
            if (arg.underlined != null) {
                jsonObject.addProperty("underlined", arg.underlined);
            }
            if (arg.strikethrough != null) {
                jsonObject.addProperty("strikethrough", arg.strikethrough);
            }
            if (arg.obfuscated != null) {
                jsonObject.addProperty("obfuscated", arg.obfuscated);
            }
            if (arg.color != null) {
                jsonObject.addProperty("color", arg.color.getName());
            }
            if (arg.insertion != null) {
                jsonObject.add("insertion", jsonSerializationContext.serialize((Object)arg.insertion));
            }
            if (arg.clickEvent != null) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("action", arg.clickEvent.getAction().getName());
                jsonObject2.addProperty("value", arg.clickEvent.getValue());
                jsonObject.add("clickEvent", (JsonElement)jsonObject2);
            }
            if (arg.hoverEvent != null) {
                jsonObject.add("hoverEvent", (JsonElement)arg.hoverEvent.toJson());
            }
            if (arg.font != null) {
                jsonObject.addProperty("font", arg.font.toString());
            }
            return jsonObject;
        }

        @Nullable
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((Style)object, type, jsonSerializationContext);
        }

        @Nullable
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

