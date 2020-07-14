/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.openal.AL10
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class Listener {
    private float volume = 1.0f;
    private Vec3d field_24051 = Vec3d.ZERO;

    public void setPosition(Vec3d position) {
        this.field_24051 = position;
        AL10.alListener3f((int)4100, (float)((float)position.x), (float)((float)position.y), (float)((float)position.z));
    }

    public Vec3d method_27268() {
        return this.field_24051;
    }

    public void setOrientation(Vector3f arg, Vector3f arg2) {
        AL10.alListenerfv((int)4111, (float[])new float[]{arg.getX(), arg.getY(), arg.getZ(), arg2.getX(), arg2.getY(), arg2.getZ()});
    }

    public void setVolume(float volume) {
        AL10.alListenerf((int)4106, (float)volume);
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume;
    }

    public void init() {
        this.setPosition(Vec3d.ZERO);
        this.setOrientation(Vector3f.NEGATIVE_Z, Vector3f.POSITIVE_Y);
    }
}

