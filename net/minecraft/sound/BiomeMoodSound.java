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
import net.minecraft.sound.SoundEvents;

public class BiomeMoodSound {
    public static final Codec<BiomeMoodSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.field_24628.fieldOf("sound").forGetter(arg -> arg.event), (App)Codec.INT.fieldOf("tick_delay").forGetter(arg -> arg.cultivationTicks), (App)Codec.INT.fieldOf("block_search_extent").forGetter(arg -> arg.spawnRange), (App)Codec.DOUBLE.fieldOf("offset").forGetter(arg -> arg.extraDistance)).apply((Applicative)instance, BiomeMoodSound::new));
    public static final BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
    private SoundEvent event;
    private int cultivationTicks;
    private int spawnRange;
    private double extraDistance;

    public BiomeMoodSound(SoundEvent event, int cultivationTicks, int spawnRange, double extraDistance) {
        this.event = event;
        this.cultivationTicks = cultivationTicks;
        this.spawnRange = spawnRange;
        this.extraDistance = extraDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public int getCultivationTicks() {
        return this.cultivationTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSpawnRange() {
        return this.spawnRange;
    }

    @Environment(value=EnvType.CLIENT)
    public double getExtraDistance() {
        return this.extraDistance;
    }
}

