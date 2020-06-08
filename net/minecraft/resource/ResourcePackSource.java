/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface ResourcePackSource {
    public static final ResourcePackSource field_25347 = ResourcePackSource.method_29485();
    public static final ResourcePackSource PACK_SOURCE_BUILTIN = ResourcePackSource.method_29486("pack.source.builtin");
    public static final ResourcePackSource PACK_SOURCE_WORLD = ResourcePackSource.method_29486("pack.source.world");
    public static final ResourcePackSource PACK_SOURCE_SERVER = ResourcePackSource.method_29486("pack.source.server");

    public Text decorate(Text var1);

    public static ResourcePackSource method_29485() {
        return arg -> arg;
    }

    public static ResourcePackSource method_29486(String string) {
        TranslatableText lv = new TranslatableText(string);
        return arg2 -> new TranslatableText("pack.nameAndSource", arg2, lv);
    }
}

