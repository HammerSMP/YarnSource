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
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class Sound
implements SoundContainer<Sound> {
    private final Identifier id;
    private final float volume;
    private final float pitch;
    private final int weight;
    private final RegistrationType registrationType;
    private final boolean stream;
    private final boolean preload;
    private final int attenuation;

    public Sound(String string, float f, float g, int i, RegistrationType arg, boolean bl, boolean bl2, int j) {
        this.id = new Identifier(string);
        this.volume = f;
        this.pitch = g;
        this.weight = i;
        this.registrationType = arg;
        this.stream = bl;
        this.preload = bl2;
        this.attenuation = j;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public Identifier getLocation() {
        return new Identifier(this.id.getNamespace(), "sounds/" + this.id.getPath() + ".ogg");
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Sound getSound() {
        return this;
    }

    @Override
    public void preload(SoundSystem arg) {
        if (this.preload) {
            arg.addPreloadedSound(this);
        }
    }

    public RegistrationType getRegistrationType() {
        return this.registrationType;
    }

    public boolean isStreamed() {
        return this.stream;
    }

    public boolean isPreloaded() {
        return this.preload;
    }

    public int getAttenuation() {
        return this.attenuation;
    }

    public String toString() {
        return "Sound[" + this.id + "]";
    }

    @Override
    public /* synthetic */ Object getSound() {
        return this.getSound();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RegistrationType {
        FILE("file"),
        SOUND_EVENT("event");

        private final String name;

        private RegistrationType(String string2) {
            this.name = string2;
        }

        public static RegistrationType getByName(String string) {
            for (RegistrationType lv : RegistrationType.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            return null;
        }
    }
}

