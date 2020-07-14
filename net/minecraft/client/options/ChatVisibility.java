/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

public enum ChatVisibility {
    FULL(0, "options.chat.visibility.full"),
    SYSTEM(1, "options.chat.visibility.system"),
    HIDDEN(2, "options.chat.visibility.hidden");

    private static final ChatVisibility[] field_7534;
    private final int id;
    private final String key;

    private ChatVisibility(int id, String key) {
        this.id = id;
        this.key = key;
    }

    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public String getTranslationKey() {
        return this.key;
    }

    @Environment(value=EnvType.CLIENT)
    public static ChatVisibility byId(int i) {
        return field_7534[MathHelper.floorMod(i, field_7534.length)];
    }

    static {
        field_7534 = (ChatVisibility[])Arrays.stream(ChatVisibility.values()).sorted(Comparator.comparingInt(ChatVisibility::getId)).toArray(ChatVisibility[]::new);
    }
}

