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
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;

public class TranslatableText
extends BaseText
implements ParsableText {
    private static final Object[] EMPTY_ARGUMENTS = new Object[0];
    private static final StringRenderable LITERAL_PERCENT_SIGN = StringRenderable.plain("%");
    private static final StringRenderable NULL_ARGUMENT = StringRenderable.plain("null");
    private final String key;
    private final Object[] args;
    @Nullable
    private Language languageCache;
    private final List<StringRenderable> translations = Lists.newArrayList();
    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TranslatableText(String key) {
        this.key = key;
        this.args = EMPTY_ARGUMENTS;
    }

    public TranslatableText(String key, Object ... args) {
        this.key = key;
        this.args = args;
    }

    private void updateTranslations() {
        Language lv = Language.getInstance();
        if (lv == this.languageCache) {
            return;
        }
        this.languageCache = lv;
        this.translations.clear();
        String string = lv.get(this.key);
        try {
            this.setTranslation(lv.reorder(string, true), lv);
        }
        catch (TranslationException lv2) {
            this.translations.clear();
            this.translations.add(StringRenderable.plain(string));
        }
    }

    private void setTranslation(String translation, Language language) {
        Matcher matcher = ARG_FORMAT.matcher(translation);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    String string2 = translation.substring(j, k);
                    if (string2.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    this.translations.add(StringRenderable.plain(string2));
                }
                String string3 = matcher.group(2);
                String string4 = translation.substring(k, l);
                if ("%".equals(string3) && "%%".equals(string4)) {
                    this.translations.add(LITERAL_PERCENT_SIGN);
                } else if ("s".equals(string3)) {
                    int m;
                    String string5 = matcher.group(1);
                    int n = m = string5 != null ? Integer.parseInt(string5) - 1 : i++;
                    if (m < this.args.length) {
                        this.translations.add(this.method_29434(m, language));
                    }
                } else {
                    throw new TranslationException(this, "Unsupported format: '" + string4 + "'");
                }
                j = l;
            }
            if (j < translation.length()) {
                String string6 = translation.substring(j);
                if (string6.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                this.translations.add(StringRenderable.plain(string6));
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TranslationException(this, (Throwable)illegalArgumentException);
        }
    }

    private StringRenderable method_29434(int i, Language arg) {
        if (i >= this.args.length) {
            throw new TranslationException(this, i);
        }
        Object object = this.args[i];
        if (object instanceof Text) {
            return (Text)object;
        }
        return object == null ? NULL_ARGUMENT : StringRenderable.plain(arg.reorder(object.toString(), false));
    }

    @Override
    public TranslatableText copy() {
        return new TranslatableText(this.key, this.args);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visitSelf(StringRenderable.StyledVisitor<T> visitor, Style style) {
        this.updateTranslations();
        for (StringRenderable lv : this.translations) {
            Optional<T> optional = lv.visit(visitor, style);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visitSelf(StringRenderable.Visitor<T> visitor) {
        this.updateTranslations();
        for (StringRenderable lv : this.translations) {
            Optional<T> optional = lv.visit(visitor);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];
        for (int j = 0; j < objects.length; ++j) {
            Object object = this.args[j];
            objects[j] = object instanceof Text ? Texts.parse(source, (Text)object, sender, depth) : object;
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

