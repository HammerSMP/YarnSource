/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.text;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public abstract class BaseText
implements MutableText {
    protected final List<Text> siblings = Lists.newArrayList();
    private Style style = Style.EMPTY;

    @Override
    public MutableText append(Text arg) {
        this.siblings.add(arg);
        return this;
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public List<Text> getSiblings() {
        return this.siblings;
    }

    @Override
    public MutableText setStyle(Style arg) {
        this.style = arg;
        return this;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    @Override
    public abstract BaseText copy();

    @Override
    public final MutableText shallowCopy() {
        BaseText lv = this.copy();
        lv.siblings.addAll(this.siblings);
        lv.setStyle(this.style);
        return lv;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BaseText) {
            BaseText lv = (BaseText)object;
            return this.siblings.equals(lv.siblings) && Objects.equals(this.getStyle(), lv.getStyle());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.getStyle(), this.siblings);
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

