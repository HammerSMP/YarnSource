/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.text;

import net.minecraft.text.TranslatableText;

public class TranslationException
extends IllegalArgumentException {
    public TranslationException(TranslatableText arg, String string) {
        super(String.format("Error parsing: %s: %s", arg, string));
    }

    public TranslationException(TranslatableText arg, int i) {
        super(String.format("Invalid index %d requested for %s", i, arg));
    }

    public TranslationException(TranslatableText arg, Throwable throwable) {
        super(String.format("Error while parsing: %s", arg), throwable);
    }
}

