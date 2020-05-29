/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.text;

import javax.annotation.Nullable;
import net.minecraft.text.BaseText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

public class LiteralText
extends BaseText {
    public static final Text EMPTY = new LiteralText("");
    private final String string;
    @Nullable
    private Language field_25315;
    private String field_25316;

    public LiteralText(String string) {
        this.string = string;
        this.field_25316 = string;
    }

    public String getRawString() {
        return this.string;
    }

    @Override
    public String asString() {
        if (this.string.isEmpty()) {
            return this.string;
        }
        Language lv = Language.getInstance();
        if (this.field_25315 != lv) {
            this.field_25316 = lv.method_29426(this.string, false);
            this.field_25315 = lv;
        }
        return this.field_25316;
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

