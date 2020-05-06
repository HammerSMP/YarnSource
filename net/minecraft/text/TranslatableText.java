/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;

public class TranslatableText
extends BaseText
implements ParsableText {
    private static final Language EMPTY_LANGUAGE = new Language();
    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
    private static final Language LANGUAGE = Language.getInstance();
    private static final LiteralText LITERAL_PERCENT_SIGN = new LiteralText("%");
    private static final LiteralText NULL_ARGUMENT = new LiteralText("null");
    private final String key;
    private final Object[] args;
    private long languageReloadTimestamp = -1L;
    private final List<Text> translations = Lists.newArrayList();
    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TranslatableText(String string) {
        this.key = string;
        this.args = EMPTY_ARGUMENTS;
    }

    public TranslatableText(String string, Object ... objects) {
        this.key = string;
        this.args = objects;
    }

    private synchronized void updateTranslations() {
        long l = LANGUAGE.getTimeLoaded();
        if (l == this.languageReloadTimestamp) {
            return;
        }
        this.languageReloadTimestamp = l;
        this.translations.clear();
        String string = LANGUAGE.translate(this.key);
        try {
            this.setTranslation(string);
        }
        catch (TranslationException lv) {
            this.translations.clear();
            this.translations.add(new LiteralText(string));
        }
    }

    private void setTranslation(String string) {
        Matcher matcher = ARG_FORMAT.matcher(string);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    String string2 = string.substring(j, k);
                    if (string2.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    this.translations.add(new LiteralText(string2));
                }
                String string3 = matcher.group(2);
                String string4 = string.substring(k, l);
                if ("%".equals(string3) && "%%".equals(string4)) {
                    this.translations.add(LITERAL_PERCENT_SIGN);
                } else if ("s".equals(string3)) {
                    int m;
                    String string5 = matcher.group(1);
                    int n = m = string5 != null ? Integer.parseInt(string5) - 1 : i++;
                    if (m < this.args.length) {
                        this.translations.add(this.getArg(m));
                    }
                } else {
                    throw new TranslationException(this, "Unsupported format: '" + string4 + "'");
                }
                j = l;
            }
            if (j < string.length()) {
                String string6 = string.substring(j);
                if (string6.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                this.translations.add(new LiteralText(string6));
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TranslationException(this, (Throwable)illegalArgumentException);
        }
    }

    private Text getArg(int i) {
        LiteralText lv2;
        if (i >= this.args.length) {
            throw new TranslationException(this, i);
        }
        Object object = this.args[i];
        if (object instanceof Text) {
            Text lv = (Text)object;
        } else {
            lv2 = object == null ? NULL_ARGUMENT : new LiteralText(object.toString());
        }
        return lv2;
    }

    @Override
    public TranslatableText copy() {
        return new TranslatableText(this.key, this.args);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visitSelf(Text.StyledVisitor<T> arg, Style arg2) {
        this.updateTranslations();
        for (Text lv : this.translations) {
            Optional<T> optional = lv.visit(arg, arg2);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visitSelf(Text.Visitor<T> arg) {
        this.updateTranslations();
        for (Text lv : this.translations) {
            Optional<T> optional = lv.visit(arg);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource arg, @Nullable Entity arg2, int i) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];
        for (int j = 0; j < objects.length; ++j) {
            Object object = this.args[j];
            objects[j] = object instanceof Text ? Texts.parse(arg, (Text)object, arg2, i) : object;
        }
        return new TranslatableText(this.key, objects);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof TranslatableText) {
            TranslatableText lv = (TranslatableText)object;
            return Arrays.equals(this.args, lv.args) && this.key.equals(lv.key) && super.equals(object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.args);
        return i;
    }

    @Override
    public String toString() {
        return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public /* synthetic */ BaseText copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

