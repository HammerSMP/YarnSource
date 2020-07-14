/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloadListener<SoundList> {
    public static final Sound MISSING_SOUND = new Sound("meta:missing_sound", 1.0f, 1.0f, 1, Sound.RegistrationType.FILE, false, false, 16);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Text.class, (Object)new Text.Serializer()).registerTypeAdapter(SoundEntry.class, (Object)new SoundEntryDeserializer()).create();
    private static final TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>(){};
    private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    private final SoundSystem soundSystem;

    public SoundManager(ResourceManager arg, GameOptions arg2) {
        this.soundSystem = new SoundSystem(this, arg2, arg);
    }

    @Override
    protected SoundList prepare(ResourceManager arg, Profiler arg2) {
        SoundList lv = new SoundList();
        arg2.startTick();
        for (String string : arg.getAllNamespaces()) {
            arg2.push(string);
            try {
                List<Resource> list = arg.getAllResources(new Identifier(string, "sounds.json"));
                for (Resource lv2 : list) {
                    arg2.push(lv2.getResourcePackName());
                    try (InputStream inputStream = lv2.getInputStream();
                         InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
                        arg2.push("parse");
                        Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, (Reader)reader, TYPE);
                        arg2.swap("register");
                        for (Map.Entry<String, SoundEntry> entry : map.entrySet()) {
                            lv.register(new Identifier(string, entry.getKey()), entry.getValue(), arg);
                        }
                        arg2.pop();
                    }
                    catch (RuntimeException runtimeException) {
                        LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", (Object)lv2.getResourcePackName(), (Object)runtimeException);
                    }
                    arg2.pop();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            arg2.pop();
        }
        arg2.endTick();
        return lv;
    }

    @Override
    protected void apply(SoundList arg, ResourceManager arg2, Profiler arg3) {
        arg.addTo(this.sounds, this.soundSystem);
        for (Identifier lv : this.sounds.keySet()) {
            String string;
            WeightedSoundSet lv2 = this.sounds.get(lv);
            if (!(lv2.getSubtitle() instanceof TranslatableText) || I18n.hasTranslation(string = ((TranslatableText)lv2.getSubtitle()).getKey())) continue;
            LOGGER.debug("Missing subtitle {} for event: {}", (Object)string, (Object)lv);
        }
        if (LOGGER.isDebugEnabled()) {
            for (Identifier lv3 : this.sounds.keySet()) {
                if (Registry.SOUND_EVENT.containsId(lv3)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)lv3);
            }
        }
        this.soundSystem.reloadSounds();
    }

    private static boolean isSoundResourcePresent(Sound arg, Identifier arg2, ResourceManager arg3) {
        Identifier lv = arg.getLocation();
        if (!arg3.containsResource(lv)) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)lv, (Object)arg2);
            return false;
        }
        return true;
    }

    @Nullable
    public WeightedSoundSet get(Identifier arg) {
        return this.sounds.get(arg);
    }

    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundSystem.playNextTick(sound);
    }

    public void play(SoundInstance sound) {
        this.soundSystem.play(sound);
    }

    public void play(SoundInstance sound, int delay) {
        this.soundSystem.play(sound, delay);
    }

    public void updateListenerPosition(Camera arg) {
        this.soundSystem.updateListenerPosition(arg);
    }

    public void pauseAll() {
        this.soundSystem.pauseAll();
    }

    public void stopAll() {
        this.soundSystem.stopAll();
    }

    public void close() {
        this.soundSystem.stop();
    }

    public void tick(boolean bl) {
        this.soundSystem.tick(bl);
    }

    public void resumeAll() {
        this.soundSystem.resumeAll();
    }

    public void updateSoundVolume(SoundCategory category, float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0f) {
            this.stopAll();
        }
        this.soundSystem.updateSoundVolume(category, volume);
    }

    public void stop(SoundInstance arg) {
        this.soundSystem.stop(arg);
    }

    public boolean isPlaying(SoundInstance arg) {
        return this.soundSystem.isPlaying(arg);
    }

    public void registerListener(SoundInstanceListener arg) {
        this.soundSystem.registerListener(arg);
    }

    public void unregisterListener(SoundInstanceListener arg) {
        this.soundSystem.unregisterListener(arg);
    }

    public void stopSounds(@Nullable Identifier arg, @Nullable SoundCategory arg2) {
        this.soundSystem.stopSounds(arg, arg2);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SoundList {
        private final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();

        protected SoundList() {
        }

        /*
         * WARNING - void declaration
         */
        private void register(Identifier id, SoundEntry entry, ResourceManager resourceManager) {
            boolean bl;
            WeightedSoundSet lv = this.loadedSounds.get(id);
            boolean bl2 = bl = lv == null;
            if (bl || entry.canReplace()) {
                if (!bl) {
                    LOGGER.debug("Replaced sound event location {}", (Object)id);
                }
                lv = new WeightedSoundSet(id, entry.getSubtitle());
                this.loadedSounds.put(id, lv);
            }
            block4: for (final Sound lv2 : entry.getSounds()) {
                void lv6;
                final Identifier lv3 = lv2.getIdentifier();
                switch (lv2.getRegistrationType()) {
                    case FILE: {
                        if (!SoundManager.isSoundResourcePresent(lv2, id, resourceManager)) continue block4;
                        Sound lv4 = lv2;
                        break;
                    }
                    case SOUND_EVENT: {
                        SoundContainer<Sound> lv5 = new SoundContainer<Sound>(){

                            @Override
                            public int getWeight() {
                                WeightedSoundSet lv = (WeightedSoundSet)loadedSounds.get(lv3);
                                return lv == null ? 0 : lv.getWeight();
                            }

                            @Override
                            public Sound getSound() {
                                Sound lv22;
                                WeightedSoundSet lv = (WeightedSoundSet)loadedSounds.get(lv3);
                                if (lv == null) {
                                    return MISSING_SOUND;
                                }
                                return new Sound(lv22.getIdentifier().toString(), lv22.getVolume() * lv2.getVolume(), lv22.getPitch() * lv2.getPitch(), lv2.getWeight(), Sound.RegistrationType.FILE, (lv22 = lv.getSound()).isStreamed() || lv2.isStreamed(), lv22.isPreloaded(), lv22.getAttenuation());
                            }

                            @Override
                            public void preload(SoundSystem soundSystem) {
                                WeightedSoundSet lv = (WeightedSoundSet)loadedSounds.get(lv3);
                                if (lv == null) {
                                    return;
                                }
                                lv.preload(soundSystem);
                            }

                            @Override
                            public /* synthetic */ Object getSound() {
                                return this.getSound();
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + (Object)((Object)lv2.getRegistrationType()));
                    }
                }
                lv.add((SoundContainer<Sound>)lv6);
            }
        }

        public void addTo(Map<Identifier, WeightedSoundSet> map, SoundSystem arg) {
            map.clear();
            for (Map.Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
                entry.getValue().preload(arg);
            }
        }
    }
}

