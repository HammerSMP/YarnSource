/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.text;

import net.minecraft.text.BaseText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class LiteralText
extends BaseText {
    public static final Text EMPTY = new LiteralText("");
    private final String string;

    public LiteralText(String string) {
        this.string = string;
    }

    public String getRawString() {
        return this.string;
    }

    @Override
    public String asString() {
        return this.string;
    }

    @Override
    public LiteralText copy() {
        LiteralText lv = new LiteralText(this.string);
        lv.setStyle(this.getStyle());
        return lv;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof LiteralText) {
            LiteralText lv = (LiteralText)object;
            return this.string.equals(lv.getRawString()) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.string + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
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

