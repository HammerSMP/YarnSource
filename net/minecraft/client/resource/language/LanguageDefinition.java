/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.Language
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.language;

import com.mojang.bridge.game.Language;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class LanguageDefinition
implements Language,
Comparable<LanguageDefinition> {
    private final String code;
    private final String name;
    private final String region;
    private final boolean rightToLeft;

    public LanguageDefinition(String string, String string2, String string3, boolean bl) {
        this.code = string;
        this.name = string2;
        this.region = string3;
        this.rightToLeft = bl;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.region;
    }

    public String getRegion() {
        return this.name;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof LanguageDefinition)) {
            return false;
        }
        return this.code.equals(((LanguageDefinition)object).code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(LanguageDefinition arg) {
        return this.code.compareTo(arg.code);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((LanguageDefinition)object);
    }
}

