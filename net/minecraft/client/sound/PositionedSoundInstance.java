/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class PositionedSoundInstance
extends AbstractSoundInstance {
    public PositionedSoundInstance(SoundEvent arg, SoundCategory arg2, float f, float g, BlockPos arg3) {
        this(arg, arg2, f, g, (float)arg3.getX() + 0.5f, (float)arg3.getY() + 0.5f, (float)arg3.getZ() + 0.5f);
    }

    public static PositionedSoundInstance master(SoundEvent arg, float f) {
        return PositionedSoundInstance.master(arg, f, 0.25f);
    }

    public static PositionedSoundInstance master(SoundEvent arg, float f, float g) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.MASTER, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }

    public static PositionedSoundInstance music(SoundEvent arg) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }

    public static PositionedSoundInstance record(SoundEvent arg, float f, float g, float h) {
        return new PositionedSoundInstance(arg, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg, float f, float g) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.AMBIENT, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0f, 0.0f, 0.0f, true);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg) {
        return PositionedSoundInstance.ambient(arg, 1.0f, 1.0f);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg, float f, float g, float h) {
        return new PositionedSoundInstance(arg, SoundCategory.AMBIENT, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, f, g, h);
    }

    public PositionedSoundInstance(SoundEvent arg, SoundCategory arg2, float f, float g, float h, float i, float j) {
        this(arg, arg2, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, h, i, j);
    }

    private PositionedSoundInstance(SoundEvent arg, SoundCategory arg2, float f, float g, boolean bl, int i, SoundInstance.AttenuationType arg3, float h, float j, float k) {
        this(arg.getId(), arg2, f, g, bl, i, arg3, h, j, k, false);
    }

    public PositionedSoundInstance(Identifier arg, SoundCategory arg2, float f, float g, boolean bl, int i, SoundInstance.AttenuationType arg3, float h, float j, float k, boolean bl2) {
        super(arg, arg2);
        this.volume = f;
        this.pitch = g;
        this.x = h;
        this.y = j;
        this.z = k;
        this.repeat = bl;
        this.repeatDelay = i;
        this.attenuationType = arg3;
        this.looping = bl2;
    }
}

