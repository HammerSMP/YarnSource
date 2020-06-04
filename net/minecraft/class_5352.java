/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface class_5352 {
    public static final class_5352 field_25347 = class_5352.method_29485();
    public static final class_5352 PACK_SOURCE_BUILTIN = class_5352.method_29486("pack.source.builtin");
    public static final class_5352 PACK_SOURCE_WORLD = class_5352.method_29486("pack.source.world");
    public static final class_5352 PACK_SOURCE_SERVER = class_5352.method_29486("pack.source.server");

    public Text decorate(Text var1);

    public static class_5352 method_29485() {
        return arg -> arg;
    }

    public static class_5352 method_29486(String string) {
        TranslatableText lv = new TranslatableText(string);
        return arg2 -> new TranslatableText("pack.nameAndSource", arg2, lv);
    }
}

