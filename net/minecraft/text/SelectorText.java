/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;

    public SelectorText(String pattern) {
        this.pattern = pattern;
        EntitySelector lv = null;
        try {
            EntitySelectorReader lv2 = new EntitySelectorReader(new StringReader(pattern));
            lv = lv2.read();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.warn("Invalid selector component: {}", (Object)pattern, (Object)commandSyntaxException.getMessage());
        }
        this.selector = lv;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null || this.selector == null) {
            return new LiteralText("");
        }
        return EntitySelector.getNames(this.selector.getEntities(source));
    }

    @Override
    public String asString() {
        return this.pattern;
    }

    @Override
    public SelectorText copy() {
        return new SelectorText(this.pattern);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof SelectorText) {
            SelectorText lv = (SelectorText)object;
            return this.pattern.equals(lv.pattern) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
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

