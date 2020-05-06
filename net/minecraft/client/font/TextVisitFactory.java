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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class TextVisitFactory {
    private static final Optional<Object> VISIT_TERMINATED = Optional.of(Unit.INSTANCE);

    private static boolean visitRegularCharacter(Style arg, CharacterVisitor arg2, int i, char c) {
        if (Character.isSurrogate(c)) {
            return arg2.onChar(i, arg, 65533);
        }
        return arg2.onChar(i, arg, c);
    }

    public static boolean visitForwards(String string, Style arg, CharacterVisitor arg2) {
        int i = string.length();
        for (int j = 0; j < i; ++j) {
            char c = string.charAt(j);
            if (Character.isHighSurrogate(c)) {
                if (j + 1 >= i) {
                    if (arg2.onChar(j, arg, 65533)) break;
                    return false;
                }
                char d = string.charAt(j + 1);
                if (Character.isLowSurrogate(d)) {
                    if (!arg2.onChar(j, arg, Character.toCodePoint(c, d))) {
                        return false;
                    }
                    ++j;
                    continue;
                }
                if (arg2.onChar(j, arg, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(arg, arg2, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitBackwards(String string, Style arg, CharacterVisitor arg2) {
        int i = string.length();
        for (int j = i - 1; j >= 0; --j) {
            char c = string.charAt(j);
            if (Character.isLowSurrogate(c)) {
                if (j - 1 < 0) {
                    if (arg2.onChar(0, arg, 65533)) break;
                    return false;
                }
                char d = string.charAt(j - 1);
                if (!(Character.isHighSurrogate(d) ? !arg2.onChar(--j, arg, Character.toCodePoint(d, c)) : !arg2.onChar(j, arg, 65533))) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(arg, arg2, j, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(String string, Style arg, CharacterVisitor arg2) {
        return TextVisitFactory.visitFormatted(string, 0, arg, arg2);
    }

    public static boolean visitFormatted(String string, int i, Style arg, CharacterVisitor arg2) {
        return TextVisitFactory.visitFormatted(string, i, arg, arg, arg2);
    }

    public static boolean visitFormatted(String string, int i, Style arg, Style arg2, CharacterVisitor arg3) {
        int j = string.length();
        Style lv = arg;
        for (int k = i; k < j; ++k) {
            char c = string.charAt(k);
            if (c == '\u00a7') {
                if (k + 1 >= j) break;
                char d = string.charAt(k + 1);
                Formatting lv2 = Formatting.byCode(d);
                if (lv2 != null) {
                    lv = lv2 == Formatting.RESET ? arg2 : lv.withExclusiveFormatting(lv2);
                }
                ++k;
                continue;
            }
            if (Character.isHighSurrogate(c)) {
                if (k + 1 >= j) {
                    if (arg3.onChar(k, lv, 65533)) break;
                    return false;
                }
                char e = string.charAt(k + 1);
                if (Character.isLowSurrogate(e)) {
                    if (!arg3.onChar(k, lv, Character.toCodePoint(c, e))) {
                        return false;
                    }
                    ++k;
                    continue;
                }
                if (arg3.onChar(k, lv, 65533)) continue;
                return false;
            }
            if (TextVisitFactory.visitRegularCharacter(lv, arg3, k, c)) continue;
            return false;
        }
        return true;
    }

    public static boolean visitFormatted(Text arg, Style arg22, CharacterVisitor arg3) {
        return !arg.visit((arg2, string) -> TextVisitFactory.visitFormatted(string, 0, arg2, arg3) ? Optional.empty() : VISIT_TERMINATED, arg22).isPresent();
    }

    public static String validateSurrogates(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        TextVisitFactory.visitForwards(string, Style.EMPTY, (i, arg, j) -> {
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

