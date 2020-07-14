/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Texts {
    public static MutableText setStyleIfAbsent(MutableText text, Style style) {
        if (style.isEmpty()) {
            return text;
        }
        Style lv = text.getStyle();
        if (lv.isEmpty()) {
            return text.setStyle(style);
        }
        if (lv.equals(style)) {
            return text;
        }
        return text.setStyle(lv.withParent(style));
    }

    public static MutableText parse(@Nullable ServerCommandSource source, Text text, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (depth > 100) {
            return text.shallowCopy();
        }
        MutableText lv = text instanceof ParsableText ? ((ParsableText)((Object)text)).parse(source, sender, depth + 1) : text.copy();
        for (Text lv2 : text.getSiblings()) {
            lv.append(Texts.parse(source, lv2, sender, depth + 1));
        }
        return lv.fillStyle(Texts.method_27663(source, text.getStyle(), sender, depth));
    }

    private static Style method_27663(@Nullable ServerCommandSource arg, Style arg2, @Nullable Entity arg3, int i) throws CommandSyntaxException {
        Text lv2;
        HoverEvent lv = arg2.getHoverEvent();
        if (lv != null && (lv2 = lv.getValue(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent lv3 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Texts.parse(arg, lv2, arg3, i + 1));
            return arg2.withHoverEvent(lv3);
        }
        return arg2;
    }

    public static Text toText(GameProfile profile) {
        if (profile.getName() != null) {
            return new LiteralText(profile.getName());
        }
        if (profile.getId() != null) {
            return new LiteralText(profile.getId().toString());
        }
        return new LiteralText("(unknown)");
    }

    public static Text joinOrdered(Collection<String> strings) {
        return Texts.joinOrdered(strings, string -> new LiteralText((String)string).formatted(Formatting.GREEN));
    }

    public static <T extends Comparable<T>> Text joinOrdered(Collection<T> elements, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return LiteralText.EMPTY;
        }
        if (elements.size() == 1) {
            return transformer.apply(elements.iterator().next());
        }
        ArrayList list = Lists.newArrayList(elements);
        list.sort(Comparable::compareTo);
        return Texts.join(list, transformer);
    }

    public static <T> MutableText join(Collection<T> elements, Function<T, Text> transformer) {
        if (elements.isEmpty()) {
            return new LiteralText("");
        }
        if (elements.size() == 1) {
            return transformer.apply(elements.iterator().next()).shallowCopy();
        }
        LiteralText lv = new LiteralText("");
        boolean bl = true;
        for (T object : elements) {
            if (!bl) {
                lv.append(new LiteralText(", ").formatted(Formatting.GRAY));
            }
            lv.append(transformer.apply(object));
            bl = false;
        }
        return lv;
    }

    public static MutableText bracketed(Text text) {
        return new TranslatableText("chat.square_brackets", text);
    }

    public static Text toText(Message message) {
        if (message instanceof Text) {
            return (Text)message;
        }
        return new LiteralText(message.getString());
    }
}

