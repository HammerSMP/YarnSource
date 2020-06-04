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
        this(arg, arg2, f, g, (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5);
    }

    public static PositionedSoundInstance master(SoundEvent arg, float f) {
        return PositionedSoundInstance.master(arg, f, 0.25f);
    }

    public static PositionedSoundInstance master(SoundEvent arg, float f, float g) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.MASTER, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance music(SoundEvent arg) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance record(SoundEvent arg, double d, double e, double f) {
        return new PositionedSoundInstance(arg, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, f);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg, float f, float g) {
        return new PositionedSoundInstance(arg.getId(), SoundCategory.AMBIENT, g, f, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg) {
        return PositionedSoundInstance.ambient(arg, 1.0f, 1.0f);
    }

    public static PositionedSoundInstance ambient(SoundEvent arg, double d, double e, double f) {
        return new PositionedSoundInstance(arg, SoundCategory.AMBIENT, 1.0f, 1.0f, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, f);
    }

    public PositionedSoundInstance(SoundEvent arg, SoundCategory arg2, float f, float g, double d, double e, double h) {
        this(arg, arg2, f, g, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, h);
    }

    private PositionedSoundInstance(SoundEvent arg, SoundCategory arg2, float f, float g, boolean bl, int i, SoundInstance.AttenuationType arg3, double d, double e, double h) {
        this(arg.getId(), arg2, f, g, bl, i, arg3, d, e, h, false);
    }

    public PositionedSoundInstance(Identifier arg, SoundCategory arg2, float f, float g, boolean bl, int i, SoundInstance.AttenuationType arg3, double d, double e, double h, boolean bl2) {
        super(arg, arg2);
        this.volume = f;
        this.pitch = g;
        this.x = d;
        this.y = e;
        this.z = h;
        this.repeat = bl;
        this.repeatDelay = i;
        this.attenuationType = arg3;
        this.looping = bl2;
    }
}

