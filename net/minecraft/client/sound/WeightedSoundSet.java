/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WeightedSoundSet
implements SoundContainer<Sound> {
    private final List<SoundContainer<Sound>> sounds = Lists.newArrayList();
    private final Random random = new Random();
    private final Identifier id;
    @Nullable
    private final Text subtitle;

    public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
        this.id = id;
        this.subtitle = subtitle == null ? null : new TranslatableText(subtitle);
    }

    @Override
    public int getWeight() {
        int i = 0;
        for (SoundContainer<Sound> lv : this.sounds) {
            i += lv.getWeight();
        }
        return i;
    }

    @Override
    public Sound getSound() {
        int i = this.getWeight();
        if (this.sounds.isEmpty() || i == 0) {
            return SoundManager.MISSING_SOUND;
        }
        int j = this.random.nextInt(i);
        for (SoundContainer<Sound> lv : this.sounds) {
            if ((j -= lv.getWeight()) >= 0) continue;
            return lv.getSound();
        }
        return SoundManager.MISSING_SOUND;
    }

    public void add(SoundContainer<Sound> arg) {
        this.sounds.add(arg);
    }

    @Nullable
    public Text getSubtitle() {
        return this.subtitle;
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        for (SoundContainer<Sound> lv : this.sounds) {
            lv.preload(soundSystem);
        }
    }

    @Override
    public /* synthetic */ Object getSound() {
        return this.getSound();
    }
}

