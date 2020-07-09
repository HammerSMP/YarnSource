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
    public static MutableText setStyleIfAbsent(MutableText arg, Style arg2) {
        if (arg2.isEmpty()) {
            return arg;
        }
        Style lv = arg.getStyle();
        if (lv.isEmpty()) {
            return arg.setStyle(arg2);
        }
        if (lv.equals(arg2)) {
            return arg;
        }
        return arg.setStyle(lv.withParent(arg2));
    }

    public static MutableText parse(@Nullable ServerCommandSource arg, Text arg2, @Nullable Entity arg3, int i) throws CommandSyntaxException {
        if (i > 100) {
            return arg2.shallowCopy();
        }
        MutableText lv = arg2 instanceof ParsableText ? ((ParsableText)((Object)arg2)).parse(arg, arg3, i + 1) : arg2.copy();
        for (Text lv2 : arg2.getSiblings()) {
            lv.append(Texts.parse(arg, lv2, arg3, i + 1));
        }
        return lv.fillStyle(Texts.method_27663(arg, arg2.getStyle(), arg3, i));
    }

    private static Style method_27663(@Nullable ServerCommandSource arg, Style arg2, @Nullable Entity arg3, int i) throws CommandSyntaxException {
        Text lv2;
        HoverEvent lv = arg2.getHoverEvent();
        if (lv != null && (lv2 = lv.getValue(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent lv3 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Texts.parse(arg, lv2, arg3, i + 1));
            return arg2.setHoverEvent(lv3);
        }
        return arg2;
    }

    public static Text toText(GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new LiteralText(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new LiteralText(gameProfile.getId().toString());
        }
        return new LiteralText("(unknown)");
    }

    public static Text joinOrdered(Collection<String> collection) {
        return Texts.joinOrdered(collection, string -> new LiteralText((String)string).formatted(Formatting.GREEN));
    }

    public static <T extends Comparable<T>> Text joinOrdered(Collection<T> collection, Function<T, Text> function) {
        if (collection.isEmpty()) {
            return LiteralText.EMPTY;
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        ArrayList list = Lists.newArrayList(collection);
        list.sort(Comparable::compareTo);
        return Texts.join(list, function);
    }

    public static <T> MutableText join(Collection<T> collection, Function<T, Text> function) {
        if (collection.isEmpty()) {
            return new LiteralText("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next()).shallowCopy();
        }
        LiteralText lv = new LiteralText("");
        boolean bl = true;
        for (T object : collection) {
            if (!bl) {
                lv.append(new LiteralText(", ").formatted(Formatting.GRAY));
            }
            lv.append(function.apply(object));
            bl = false;
        }
        return lv;
    }

    public static MutableText bracketed(Text arg) {
        return new TranslatableText("chat.square_brackets", arg);
    }

    public static Text toText(Message message) {
        if (message instanceof Text) {
            return (Text)message;
        }
        return new LiteralText(message.getString());
    }
}

