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

    default public MutableText append(String text) {
        return this.append(new LiteralText(text));
    }

    public MutableText append(Text var1);

    default public MutableText styled(UnaryOperator<Style> styleUpdater) {
        this.setStyle((Style)styleUpdater.apply(this.getStyle()));
        return this;
    }

    default public MutableText fillStyle(Style styleOverride) {
        this.setStyle(styleOverride.withParent(this.getStyle()));
        return this;
    }

    default public MutableText formatted(Formatting ... formattings) {
        this.setStyle(this.getStyle().withFormatting(formattings));
        return this;
    }

    default public MutableText formatted(Formatting formatting) {
        this.setStyle(this.getStyle().withFormatting(formatting));
        return this;
    }
}

