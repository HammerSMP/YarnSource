/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.text;

import java.util.function.UnaryOperator;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface MutableText
extends Text {
    public MutableText setStyle(Style var1);

    default public MutableText append(String string) {
        return this.append(new LiteralText(string));
    }

    public MutableText append(Text var1);

    default public MutableText styled(UnaryOperator<Style> unaryOperator) {
        this.setStyle((Style)unaryOperator.apply(this.getStyle()));
        return this;
    }

    default public MutableText fillStyle(Style arg) {
        this.setStyle(arg.withParent(this.getStyle()));
        return this;
    }

    default public MutableText formatted(Formatting ... args) {
        this.setStyle(this.getStyle().withFormatting(args));
        return this;
    }

    default public MutableText formatted(Formatting arg) {
        this.setStyle(this.getStyle().withFormatting(arg));
        return this;
    }
}

