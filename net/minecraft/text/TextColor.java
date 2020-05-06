/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public final class TextColor {
    private static final Map<Formatting, TextColor> FORMATTING_TO_COLOR = (Map)Stream.of(Formatting.values()).filter(Formatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), arg -> new TextColor(arg.getColorValue(), arg.getName())));
    private static final Map<String, TextColor> BY_NAME = (Map)FORMATTING_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap(arg -> arg.name, Function.identity()));
    private final int rgb;
    @Nullable
    private final String name;

    private TextColor(int i, String string) {
        this.rgb = i;
        this.name = string;
    }

    private TextColor(int i) {
        this.rgb = i;
        this.name = null;
    }

    @Environment(value=EnvType.CLIENT)
    public int getRgb() {
        return this.rgb;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        }
        return this.getHexCode();
    }

    private String getHexCode() {
        return String.format("#%06X", this.rgb);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TextColor lv = (TextColor)object;
        return this.rgb == lv.rgb;
    }

    public int hashCode() {
        return Objects.hash(this.rgb, this.name);
    }

    public String toString() {
        return this.name != null ? this.name : this.getHexCode();
    }

    @Nullable
    public static TextColor fromFormatting(Formatting arg) {
        return FORMATTING_TO_COLOR.get((Object)arg);
    }

    public static TextColor fromRgb(int i) {
        return new TextColor(i);
    }

    @Nullable
    public static TextColor parse(String string) {
        if (string.startsWith("#")) {
            try {
                int i = Integer.parseInt(string.substring(1), 16);
                return TextColor.fromRgb(i);
            }
            catch (NumberFormatException numberFormatException) {
                return null;
            }
        }
        return BY_NAME.get(string);
    }
}

