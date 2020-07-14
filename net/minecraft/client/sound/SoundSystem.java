/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraft.client.sound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Listener;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Environment(value=EnvType.CLIENT)
public class SoundSystem {
    private static final Marker MARKER = MarkerManager.getMarker((String)"SOUNDS");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Identifier> unknownSounds = Sets.newHashSet();
    private final SoundManager loader;
    private final GameOptions settings;
    private boolean started;
    private final SoundEngine soundEngine = new SoundEngine();
    private final Listener listener = this.soundEngine.getListener();
    private final SoundLoader soundLoader;
    private final SoundExecutor taskQueue = new SoundExecutor();
    private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
    private int ticks;
    private final Map<SoundInstance, Channel.SourceManager> sources = Maps.newHashMap();
    private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> startTicks = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundEndTicks = Maps.newHashMap();
    private final List<SoundInstanceListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> soundsToPlayNextTick = Lists.newArrayList();
    private final List<Sound> preloadedSounds = Lists.newArrayList();

    public SoundSystem(SoundManager loader, GameOptions settings, ResourceManager arg3) {
        this.loader = loader;
        this.settings = settings;
        this.soundLoader = new SoundLoader(arg3);
    }

    public void reloadSounds() {
        unknownSounds.clear();
        for (SoundEvent lv : Registry.SOUND_EVENT) {
            Identifier lv2 = lv.getId();
            if (this.loader.get(lv2) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getId(lv));
            unknownSounds.add(lv2);
        }
        this.stop();
        this.start();
    }

    private synchronized void start() {
        if (this.started) {
            return;
        }
        try {
            this.soundEngine.init();
            this.listener.init();
            this.listener.setVolume(this.settings.getSoundVolume(SoundCategory.MASTER));
            this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
            this.started = true;
            LOGGER.info(MARKER, "Sound engine started");
        }
        catch (RuntimeException runtimeException) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException);
        }
    }

    private float getSoundVolume(@Nullable SoundCategory arg) {
        if (arg == null || arg == SoundCategory.MASTER) {
            return 1.0f;
        }
        return this.settings.getSoundVolume(arg);
    }

    public void updateSoundVolume(SoundCategory arg, float volume) {
        if (!this.started) {
            return;
        }
        if (arg == SoundCategory.MASTER) {
            this.listener.setVolume(volume);
            return;
        }
        this.sources.forEach((arg2, arg22) -> {
            float f = this.getAdjustedVolume((SoundInstance)arg2);
            arg22.run(arg -> {
                if (f <= 0.0f) {
                    arg.stop();
                } else {
                    arg.setVolume(f);
                }
            });
        });
    }

    public void stop() {
        if (this.started) {
            this.stopAll();
            this.soundLoader.close();
            this.soundEngine.close();
            this.started = false;
        }
    }

    public void stop(SoundInstance arg) {
        Channel.SourceManager lv;
        if (this.started && (lv = this.sources.get(arg)) != null) {
            lv.run(Source::stop);
        }
    }

    public void stopAll() {
        if (this.started) {
            this.taskQueue.restart();
            this.sources.values().forEach(arg -> arg.run(Source::stop));
            this.sources.clear();
            this.channel.close();
            this.startTicks.clear();
            this.tickingSounds.clear();
            this.sounds.clear();
            this.soundEndTicks.clear();
            this.soundsToPlayNextTick.clear();
        }
    }

    public void registerListener(SoundInstanceListener arg) {
        this.listeners.add(arg);
    }

    public void unregisterListener(SoundInstanceListener arg) {
        this.listeners.remove(arg);
    }

    public void tick(boolean bl) {
        if (!bl) {
            this.tick();
        }
        this.channel.tick();
    }

    private void tick() {
        ++this.ticks;
        this.soundsToPlayNextTick.stream().filter(SoundInstance::canPlay).forEach(this::play);
        this.soundsToPlayNextTick.clear();
        for (TickableSoundInstance lv : this.tickingSounds) {
            if (!lv.canPlay()) {
                this.stop(lv);
            }
            lv.tick();
            if (lv.isDone()) {
                this.stop(lv);
                continue;
            }
            float f = this.getAdjustedVolume(lv);
            float g = this.getAdjustedPitch(lv);
            Vec3d lv2 = new Vec3d(lv.getX(), lv.getY(), lv.getZ());
            Channel.SourceManager lv3 = this.sources.get(lv);
            if (lv3 == null) continue;
            lv3.run(arg2 -> {
                arg2.setVolume(f);
                arg2.setPitch(g);
                arg2.setPosition(lv2);
            });
        }
        Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            int i;
            Map.Entry<SoundInstance, Channel.SourceManager> entry = iterator.next();
            Channel.SourceManager lv4 = entry.getValue();
            SoundInstance lv5 = entry.getKey();
            float h = this.settings.getSoundVolume(lv5.getCategory());
            if (h <= 0.0f) {
                lv4.run(Source::stop);
                iterator.remove();
                continue;
            }
            if (!lv4.isStopped() || (i = this.soundEndTicks.get(lv5).intValue()) > this.ticks) continue;
            if (SoundSystem.isRepeatDelayed(lv5)) {
                this.startTicks.put(lv5, this.ticks + lv5.getRepeatDelay());
            }
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)lv4);
            this.soundEndTicks.remove(lv5);
            try {
                this.sounds.remove((Object)lv5.getCategory(), (Object)lv5);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!(lv5 instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove(lv5);
        }
        Iterator<Map.Entry<SoundInstance, Integer>> iterator2 = this.startTicks.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<SoundInstance, Integer> entry2 = iterator2.next();
            if (this.ticks < entry2.getValue()) continue;
            SoundInstance lv6 = entry2.getKey();
            if (lv6 instanceof TickableSoundInstance) {
                ((TickableSoundInstance)lv6).tick();
            }
            this.play(lv6);
            iterator2.remove();
        }
    }

    private static boolean canRepeatInstantly(SoundInstance arg) {
        return arg.getRepeatDelay() > 0;
    }

    private static boolean isRepeatDelayed(SoundInstance arg) {
        return arg.isRepeatable() && SoundSystem.canRepeatInstantly(arg);
    }

    private static boolean shouldRepeatInstantly(SoundInstance arg) {
        return arg.isRepeatable() && !SoundSystem.canRepeatInstantly(arg);
    }

    public boolean isPlaying(SoundInstance arg) {
        if (!this.started) {
            return false;
        }
        if (this.soundEndTicks.containsKey(arg) && this.soundEndTicks.get(arg) <= this.ticks) {
            return true;
        }
        return this.sources.containsKey(arg);
    }

    public void play(SoundInstance arg) {
        if (!this.started) {
            return;
        }
        if (!arg.canPlay()) {
            return;
        }
        WeightedSoundSet lv = arg.getSoundSet(this.loader);
        Identifier lv2 = arg.getId();
        if (lv == null) {
            if (unknownSounds.add(lv2)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)lv2);
            }
            return;
        }
        Sound lv3 = arg.getSound();
        if (lv3 == SoundManager.MISSING_SOUND) {
            if (unknownSounds.add(lv2)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)lv2);
            }
            return;
        }
        float f = arg.getVolume();
        float g = Math.max(f, 1.0f) * (float)lv3.getAttenuation();
        SoundCategory lv4 = arg.getCategory();
        float h = this.getAdjustedVolume(arg);
        float i = this.getAdjustedPitch(arg);
        SoundInstance.AttenuationType lv5 = arg.getAttenuationType();
        boolean bl = arg.isLooping();
        if (h == 0.0f && !arg.shouldAlwaysPlay()) {
            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)lv3.getIdentifier());
            return;
        }
        Vec3d lv6 = new Vec3d(arg.getX(), arg.getY(), arg.getZ());
        if (!this.listeners.isEmpty()) {
            boolean bl2;
            boolean bl3 = bl2 = bl || lv5 == SoundInstance.AttenuationType.NONE || this.listener.method_27268().squaredDistanceTo(lv6) < (double)(g * g);
            if (bl2) {
                for (SoundInstanceListener lv7 : this.listeners) {
                    lv7.onSoundPlayed(arg, lv);
                }
            } else {
                LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)lv2);
            }
        }
        if (this.listener.getVolume() <= 0.0f) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)lv2);
            return;
        }
        boolean bl3 = SoundSystem.shouldRepeatInstantly(arg);
        boolean bl4 = lv3.isStreamed();
        CompletableFuture<Channel.SourceManager> completableFuture = this.channel.createSource(lv3.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
        Channel.SourceManager lv8 = completableFuture.join();
        if (lv8 == null) {
            LOGGER.warn("Failed to create new sound handle");
            return;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)lv3.getIdentifier(), (Object)lv2);
        this.soundEndTicks.put(arg, this.ticks + 20);
        this.sources.put(arg, lv8);
        this.sounds.put((Object)lv4, (Object)arg);
        lv8.run(arg3 -> {
            arg3.setPitch(i);
            arg3.setVolume(h);
            if (lv5 == SoundInstance.AttenuationType.LINEAR) {
                arg3.setAttenuation(g);
            } else {
                arg3.disableAttenuation();
            }
            arg3.setLooping(bl3 && !bl4);
            arg3.setPosition(lv6);
            arg3.setRelative(bl);
        });
        if (!bl4) {
            this.soundLoader.loadStatic(lv3.getLocation()).thenAccept(arg22 -> lv8.run(arg2 -> {
                arg2.setBuffer((StaticSound)arg22);
                arg2.play();
            }));
        } else {
            this.soundLoader.loadStreamed(lv3.getLocation(), bl3).thenAccept(arg22 -> lv8.run(arg2 -> {
                arg2.setStream((AudioStream)arg22);
                arg2.play();
            }));
        }
        if (arg instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)arg);
        }
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundsToPlayNextTick.add(sound);
    }

    public void addPreloadedSound(Sound sound) {
        this.preloadedSounds.add(sound);
    }

    private float getAdjustedPitch(SoundInstance arg) {
        return MathHelper.clamp(arg.getPitch(), 0.5f, 2.0f);
    }

    private float getAdjustedVolume(SoundInstance arg) {
        return MathHelper.clamp(arg.getVolume() * this.getSoundVolume(arg.getCategory()), 0.0f, 1.0f);
    }

    public void pauseAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::pause));
        }
    }

    public void resumeAll() {
        if (this.started) {
            this.channel.execute(stream -> stream.forEach(Source::resume));
        }
    }

    public void play(SoundInstance sound, int delay) {
        this.startTicks.put(sound, this.ticks + delay);
    }

    public void updateListenerPosition(Camera arg) {
        if (!this.started || !arg.isReady()) {
            return;
        }
        Vec3d lv = arg.getPos();
        Vector3f lv2 = arg.getHorizontalPlane();
        Vector3f lv3 = arg.getVerticalPlane();
        this.taskQueue.execute(() -> {
            this.listener.setPosition(lv);
            this.listener.setOrientation(lv2, lv3);
        });
    }

    public void stopSounds(@Nullable Identifier arg, @Nullable SoundCategory arg2) {
        if (arg2 != null) {
            for (SoundInstance lv : this.sounds.get((Object)arg2)) {
                if (arg != null && !lv.getId().equals(arg)) continue;
                this.stop(lv);
            }
        } else if (arg == null) {
            this.stopAll();
        } else {
            for (SoundInstance lv2 : this.sources.keySet()) {
                if (!lv2.getId().equals(arg)) continue;
                this.stop(lv2);
            }
        }
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }
}

