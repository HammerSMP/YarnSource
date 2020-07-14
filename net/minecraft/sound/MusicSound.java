/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

public class MusicSound {
    public static final Codec<MusicSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.field_24628.fieldOf("sound").forGetter(arg -> arg.event), (App)Codec.INT.fieldOf("min_delay").forGetter(arg -> arg.field_24058), (App)Codec.INT.fieldOf("max_delay").forGetter(arg -> arg.field_24059), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter(arg -> arg.field_24060)).apply((Applicative)instance, MusicSound::new));
    private final SoundEvent event;
    private final int field_24058;
    private final int field_24059;
    private final boolean field_24060;

    public MusicSound(SoundEvent event, int i, int j, boolean bl) {
        this.event = event;
        this.field_24058 = i;
        this.field_24059 = j;
        this.field_24060 = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_27280() {
        return this.field_24058;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_27281() {
        return this.field_24059;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_27282() {
        return this.field_24060;
    }
}

