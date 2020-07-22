/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class TextVisitFactory {
    private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

    private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
        if (Character.isSurrogate(c)) {
            return visitor.onChar(index, style, 65533);
        }
        return visitor.onChar(index, style, c);
    }

    public static boolean visitForwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = 0; j < i; ++j) {
            char c = text.charAt(j);
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (visitor.onChar(j, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!visitor.onChar(j, style, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (visitor.onChar(j, style, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitBackwards(String text, Style style, CharacterVisitor visitor) {
        int i = text.length();
        for (int j = i - 1; j >= 0; --j) {
            char c = text.charAt(j);
            if (Character.isLowSurrogate(c)) {
                if (j - 1 < 0) {
                    if (visitor.onChar(0, style, 65533)) break;
                    return false;
                }
                char d = text.charAt(j - 1);
                if (!(Character.isHighSurrogate(d) ? !visitor.onChar(--j, style, Character.toCodePoint(d, c)) : !visitor.onChar(j, style, 65533))) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(style, visitor, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(String text, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, 0, style, visitor);
    }

    public static boolean visitFormatted(String text, int startIndex, Style style, CharacterVisitor visitor) {
        return TextVisitFactory.visitFormatted(text, startIndex, style, style, visitor);
    }

    public static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
        int j = text.length();
        Style lv = startingStyle;
        for (int k = startIndex; k < j; ++k) {
            char c = text.charAt(k);
            if (c == '\u00a7') {
                if (k + 1 >= j) break;
                char d = text.charAt(k + 1);
                Formatting lv2 = Formatting.byCode(d);
                if (lv2 != null) {
                    lv = lv2 == Formatting.RESET ? resetStyle : lv.withExclusiveFormatting(lv2);
                }
                ++k;
                continue;
            }
            if (Character.isHighSurrogate(c)) {
                if (k + 1 >= j) {
                    if (visitor.onChar(k, lv, 65533)) break;
                    return false;
                }
                char e = text.charAt(k + 1);
                if (Character.isLowSurrogate(e)) {
                    if (!visitor.onChar(k, lv, Character.toCodePoint(c, e))) {
                        return false;
                    }
                    ++k;
                    continue;
                }
                if (visitor.onChar(k, lv, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(lv, visitor, k, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(StringRenderable text, Style style, CharacterVisitor visitor) {
        return !text.visit((arg2, string) -> TextVisitFactory.visitFormatted(string, 0, arg2, visitor) ? Optional.empty() : VISIT_TERMINATED, style).isPresent();
    }

    public static String validateSurrogates(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitForwards(text, Style.EMPTY, (i, arg, j) -> {
            stringBuilder.appendCodePoint(j);
            return true;
        });
        return stringBuilder.toString();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface CharacterVisitor {
        public boolean onChar(int var1, Style var2, int var3);
    }
}

