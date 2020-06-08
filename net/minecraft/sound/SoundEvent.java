/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class SoundEvent {
    public static final Codec<SoundEvent> field_24628 = Identifier.CODEC.xmap(SoundEvent::new, arg -> arg.id);
    private final Identifier id;

    public SoundEvent(Identifier arg) {
        this.id = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getId() {
        return this.id;
    }
}

