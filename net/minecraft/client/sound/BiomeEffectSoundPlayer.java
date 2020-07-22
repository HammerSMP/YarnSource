/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;

@Environment(value=EnvType.CLIENT)
public class BiomeEffectSoundPlayer
implements ClientPlayerTickable {
    private final ClientPlayerEntity player;
    private final SoundManager soundManager;
    private final BiomeAccess biomeAccess;
    private final Random random;
    private Object2ObjectArrayMap<Biome, MusicLoop> soundLoops = new Object2ObjectArrayMap();
    private Optional<BiomeMoodSound> moodSound = Optional.empty();
    private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
    private float moodPercentage;
    private Biome activeBiome;

    public BiomeEffectSoundPlayer(ClientPlayerEntity player, SoundManager soundManager, BiomeAccess biomeAccess) {
        this.random = player.world.getRandom();
        this.player = player;
        this.soundManager = soundManager;
        this.biomeAccess = biomeAccess;
    }

    public float getMoodPercentage() {
        return this.moodPercentage;
    }

    @Override
    public void tick() {
        this.soundLoops.values().removeIf(MovingSoundInstance::isDone);
        Biome lv = this.biomeAccess.getBiome(this.player.getX(), this.player.getY(), this.player.getZ());
        if (lv != this.activeBiome) {
            this.activeBiome = lv;
            this.moodSound = lv.getMoodSound();
            this.additionsSound = lv.getAdditionsSound();
            this.soundLoops.values().forEach(MusicLoop::fadeOut);
            lv.getLoopSound().ifPresent(arg22 -> {
                MusicLoop cfr_ignored_0 = (MusicLoop)this.soundLoops.compute((Object)lv, (arg2, arg3) -> {
                    if (arg3 == null) {
                        arg3 = new MusicLoop((SoundEvent)arg22);
                        this.soundManager.play((SoundInstance)arg3);
                    }
                    arg3.fadeIn();
                    return arg3;
                });
            });
        }
        this.additionsSound.ifPresent(arg -> {
            if (this.random.nextDouble() < arg.getChance()) {
                this.soundManager.play(PositionedSoundInstance.ambient(arg.getEvent()));
            }
        });
        this.moodSound.ifPresent(arg -> {
            World lv = this.player.world;
            int i = arg.getSpawnRange() * 2 + 1;
            BlockPos lv2 = new BlockPos(this.player.getX() + (double)this.random.nextInt(i) - (double)arg.getSpawnRange(), this.player.getEyeY() + (double)this.random.nextInt(i) - (double)arg.getSpawnRange(), this.player.getZ() + (double)this.random.nextInt(i) - (double)arg.getSpawnRange());
            int j = lv.getLightLevel(LightType.SKY, lv2);
            this.moodPercentage = j > 0 ? (this.moodPercentage -= (float)j / (float)lv.getMaxLightLevel() * 0.001f) : (this.moodPercentage -= (float)(lv.getLightLevel(LightType.BLOCK, lv2) - 1) / (float)arg.getCultivationTicks());
            if (this.moodPercentage >= 1.0f) {
                double d = (double)lv2.getX() + 0.5;
                double e = (double)lv2.getY() + 0.5;
                double f = (double)lv2.getZ() + 0.5;
                double g = d - this.player.getX();
                double h = e - this.player.getEyeY();
                double k = f - this.player.getZ();
                double l = MathHelper.sqrt(g * g + h * h + k * k);
                double m = l + arg.getExtraDistance();
                PositionedSoundInstance lv3 = PositionedSoundInstance.ambient(arg.getEvent(), this.player.getX() + g / l * m, this.player.getEyeY() + h / l * m, this.player.getZ() + k / l * m);
                this.soundManager.play(lv3);
                this.moodPercentage = 0.0f;
            } else {
                this.moodPercentage = Math.max(this.moodPercentage, 0.0f);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class MusicLoop
    extends MovingSoundInstance {
        private int delta;
        private int strength;

        public MusicLoop(SoundEvent sound) {
            super(sound, SoundCategory.AMBIENT);
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.looping = true;
        }

        @Override
        public void tick() {
            if (this.strength < 0) {
                this.setDone();
            }
            this.strength += this.delta;
            this.volume = MathHelper.clamp((float)this.strength / 40.0f, 0.0f, 1.0f);
        }

        public void fadeOut() {
            this.strength = Math.min(this.strength, 40);
            this.delta = -1;
        }

        public void fadeIn() {
            this.strength = Math.max(0, this.strength);
            this.delta = 1;
        }
    }
}

