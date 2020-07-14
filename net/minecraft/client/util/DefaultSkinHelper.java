/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultSkinHelper {
    private static final Identifier STEVE_SKIN = new Identifier("textures/entity/steve.png");
    private static final Identifier ALEX_SKIN = new Identifier("textures/entity/alex.png");

    public static Identifier getTexture() {
        return STEVE_SKIN;
    }

    public static Identifier getTexture(UUID uuid) {
        if (DefaultSkinHelper.shouldUseSlimModel(uuid)) {
            return ALEX_SKIN;
        }
        return STEVE_SKIN;
    }

    public static String getModel(UUID uuid) {
        if (DefaultSkinHelper.shouldUseSlimModel(uuid)) {
            return "slim";
        }
        return "default";
    }

    private static boolean shouldUseSlimModel(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }
}

