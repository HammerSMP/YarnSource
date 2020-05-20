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

public class BiomeAdditionsSound {
    public static final Codec<BiomeAdditionsSound> field_24673 = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.field_24628.fieldOf("sound").forGetter(arg -> arg.event), (App)Codec.DOUBLE.fieldOf("tick_chance").forGetter(arg -> arg.chance)).apply((Applicative)instance, BiomeAdditionsSound::new));
    private SoundEvent event;
    private double chance;

    public BiomeAdditionsSound(SoundEvent arg, double d) {
        this.event = arg;
        this.chance = d;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public double getChance() {
        return this.chance;
    }
}

