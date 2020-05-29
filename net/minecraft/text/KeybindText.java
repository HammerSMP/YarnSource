/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class KeybindText
extends BaseText {
    private static Function<String, Supplier<Text>> translator = string -> () -> new LiteralText((String)string);
    private final String key;
    private Supplier<Text> translated;

    public KeybindText(String string) {
        this.key = string;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setTranslator(Function<String, Supplier<Text>> function) {
        translator = function;
    }

    private Text getTranslated() {
        if (this.translated == null) {
            this.translated = translator.apply(this.key);
        }
        return this.translated.get();
    }

    @Override
    public <T> Optional<T> visitSelf(class_5348.Visitor<T> arg) {
        return this.getTranslated().visit(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visitSelf(class_5348.StyledVisitor<T> arg, Style arg2) {
        return this.getTranslated().visit(arg, arg2);
    }

    @Override
    public KeybindText copy() {
        return new KeybindText(this.key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof KeybindText) {
            KeybindText lv = (KeybindText)object;
            return this.key.equals(lv.key) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.key + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey() {
        return this.key;
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

