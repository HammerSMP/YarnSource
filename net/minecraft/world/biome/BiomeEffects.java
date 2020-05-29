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
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.BiomeParticleConfig;

public class BiomeEffects {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("fog_color").forGetter(arg -> arg.fogColor), (App)Codec.INT.fieldOf("water_color").forGetter(arg -> arg.waterColor), (App)Codec.INT.fieldOf("water_fog_color").forGetter(arg -> arg.waterFogColor), (App)BiomeParticleConfig.CODEC.optionalFieldOf("particle").forGetter(arg -> arg.particleConfig), (App)SoundEvent.field_24628.optionalFieldOf("ambient_sound").forGetter(arg -> arg.loopSound), (App)BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter(arg -> arg.moodSound), (App)BiomeAdditionsSound.field_24673.optionalFieldOf("additions_sound").forGetter(arg -> arg.additionsSound), (App)MusicSound.CODEC.optionalFieldOf("music").forGetter(arg -> arg.music)).apply((Applicative)instance, BiomeEffects::new));
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final Optional<BiomeParticleConfig> particleConfig;
    private final Optional<SoundEvent> loopSound;
    private final Optional<BiomeMoodSound> moodSound;
    private final Optional<BiomeAdditionsSound> additionsSound;
    private final Optional<MusicSound> music;

    private BiomeEffects(int i, int j, int k, Optional<BiomeParticleConfig> optional, Optional<SoundEvent> optional2, Optional<BiomeMoodSound> optional3, Optional<BiomeAdditionsSound> optional4, Optional<MusicSound> optional5) {
        this.fogColor = i;
        this.waterColor = j;
        this.waterFogColor = k;
        this.particleConfig = optional;
        this.loopSound = optional2;
        this.moodSound = optional3;
        this.additionsSound = optional4;
        this.music = optional5;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFogColor() {
        return this.fogColor;
    }

    @Environment(value=EnvType.CLIENT)
    public int getWaterColor() {
        return this.waterColor;
    }

    @Environment(value=EnvType.CLIENT)
    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.particleConfig;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<SoundEvent> getLoopSound() {
        return this.loopSound;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeMoodSound> getMoodSound() {
        return this.moodSound;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.additionsSound;
    }

    @Environment(value=EnvType.CLIENT)
    public Optional<MusicSound> method_27345() {
        return this.music;
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
        private Optional<SoundEvent> loopSound = Optional.empty();
        private Optional<BiomeMoodSound> moodSound = Optional.empty();
        private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
        private Optional<MusicSound> musicSound = Optional.empty();

        public Builder fogColor(int i) {
            this.fogColor = OptionalInt.of(i);
            return this;
        }

        public Builder waterColor(int i) {
            this.waterColor = OptionalInt.of(i);
            return this;
        }

        public Builder waterFogColor(int i) {
            this.waterFogColor = OptionalInt.of(i);
            return this;
        }

        public Builder particleConfig(BiomeParticleConfig arg) {
            this.particleConfig = Optional.of(arg);
            return this;
        }

        public Builder loopSound(SoundEvent arg) {
            this.loopSound = Optional.of(arg);
            return this;
        }

        public Builder moodSound(BiomeMoodSound arg) {
            this.moodSound = Optional.of(arg);
            return this;
        }

        public Builder additionsSound(BiomeAdditionsSound arg) {
            this.additionsSound = Optional.of(arg);
            return this;
        }

        public Builder music(MusicSound arg) {
            this.musicSound = Optional.of(arg);
            return this;
        }

        public BiomeEffects build() {
            return new BiomeEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.particleConfig, this.loopSound, this.moodSound, this.additionsSound, this.musicSound);
        }
    }
}

