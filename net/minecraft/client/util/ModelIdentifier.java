/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelIdentifier
extends Identifier {
    private final String variant;

    protected ModelIdentifier(String[] strings) {
        super(strings);
        this.variant = strings[2].toLowerCase(Locale.ROOT);
    }

    public ModelIdentifier(String string) {
        this(ModelIdentifier.split(string));
    }

    public ModelIdentifier(Identifier arg, String string) {
        this(arg.toString(), string);
    }

    public ModelIdentifier(String string, String string2) {
        this(ModelIdentifier.split(string + '#' + string2));
    }

    protected static String[] split(String string) {
        String[] strings = new String[]{null, string, ""};
        int i = string.indexOf(35);
        String string2 = string;
        if (i >= 0) {
            strings[2] = string.substring(i + 1, string.length());
            if (i > 1) {
                string2 = string.substring(0, i);
            }
        }
        System.arraycopy(Identifier.split(string2, ':'), 0, strings, 0, 2);
        return strings;
    }

    public String getVariant() {
        return this.variant;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ModelIdentifier && super.equals(object)) {
            ModelIdentifier lv = (ModelIdentifier)object;
            return this.variant.equals(lv.variant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + '#' + this.variant;
    }
}

