/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.Listener;
import net.minecraft.client.sound.Source;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class SoundEngine {
    private static final Logger LOGGER = LogManager.getLogger();
    private long devicePointer;
    private long contextPointer;
    private static final SourceSet EMPTY_SOURCE_SET = new SourceSet(){

        @Override
        @Nullable
        public Source createSource() {
            return null;
        }

        @Override
        public boolean release(Source arg) {
            return false;
        }

        @Override
        public void close() {
        }

        @Override
        public int getMaxSourceCount() {
            return 0;
        }

        @Override
        public int getSourceCount() {
            return 0;
        }
    };
    private SourceSet streamingSources = EMPTY_SOURCE_SET;
    private SourceSet staticSources = EMPTY_SOURCE_SET;
    private final Listener listener = new Listener();

    public void init() {
        this.devicePointer = SoundEngine.openDevice();
        ALCCapabilities aLCCapabilities = ALC.createCapabilities((long)this.devicePointer);
        if (AlUtil.checkAlcErrors(this.devicePointer, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!aLCCapabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        this.contextPointer = ALC10.alcCreateContext((long)this.devicePointer, (IntBuffer)null);
        ALC10.alcMakeContextCurrent((long)this.contextPointer);
        int i = this.getMonoSourceCount();
        int j = MathHelper.clamp((int)MathHelper.sqrt(i), 2, 8);
        int k = MathHelper.clamp(i - j, 8, 255);
        this.streamingSources = new SourceSetImpl(k);
        this.staticSources = new SourceSetImpl(j);
        ALCapabilities aLCapabilities = AL.createCapabilities((ALCCapabilities)aLCCapabilities);
        AlUtil.checkErrors("Initialization");
        if (!aLCapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable((int)512);
        if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        AlUtil.checkErrors("Enable per-source distance models");
        LOGGER.info("OpenAL initialized.");
    }

    private int getMonoSourceCount() {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = ALC10.alcGetInteger((long)this.devicePointer, (int)4098);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(i);
            ALC10.alcGetIntegerv((long)this.devicePointer, (int)4099, (IntBuffer)intBuffer);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int j = 0;
            while (j < i) {
                int k;
                if ((k = intBuffer.get(j++)) == 0) {
                    break;
                }
                int l = intBuffer.get(j++);
                if (k != 4112) continue;
                int n = l;
                return n;
            }
        }
        return 30;
    }

    private static long openDevice() {
        for (int i = 0; i < 3; ++i) {
            long l = ALC10.alcOpenDevice((ByteBuffer)null);
            if (l == 0L || AlUtil.checkAlcErrors(l, "Open device")) continue;
            return l;
        }
        throw new IllegalStateException("Failed to open OpenAL device");
    }

    public void close() {
        this.streamingSources.close();
        this.staticSources.close();
        ALC10.alcDestroyContext((long)this.contextPointer);
        if (this.devicePointer != 0L) {
            ALC10.alcCloseDevice((long)this.devicePointer);
        }
    }

    public Listener getListener() {
        return this.listener;
    }

    @Nullable
    public Source createSource(RunMode arg) {
        return (arg == RunMode.STREAMING ? this.staticSources : this.streamingSources).createSource();
    }

    public void release(Source arg) {
        if (!this.streamingSources.release(arg) && !this.staticSources.release(arg)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format("Sounds: %d/%d + %d/%d", this.streamingSources.getSourceCount(), this.streamingSources.getMaxSourceCount(), this.staticSources.getSourceCount(), this.staticSources.getMaxSourceCount());
    }

    @Environment(value=EnvType.CLIENT)
    static class SourceSetImpl
    implements SourceSet {
        private final int maxSourceCount;
        private final Set<Source> sources = Sets.newIdentityHashSet();

        public SourceSetImpl(int i) {
            this.maxSourceCount = i;
        }

        @Override
        @Nullable
        public Source createSource() {
            if (this.sources.size() >= this.maxSourceCount) {
                LOGGER.warn("Maximum sound pool size {} reached", (Object)this.maxSourceCount);
                return null;
            }
            Source lv = Source.create();
            if (lv != null) {
                this.sources.add(lv);
            }
            return lv;
        }

        @Override
        public boolean release(Source arg) {
            if (!this.sources.remove(arg)) {
                return false;
            }
            arg.close();
            return true;
        }

        @Override
        public void close() {
            this.sources.forEach(Source::close);
            this.sources.clear();
        }

        @Override
        public int getMaxSourceCount() {
            return this.maxSourceCount;
        }

        @Override
        public int getSourceCount() {
            return this.sources.size();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface SourceSet {
        @Nullable
        public Source createSource();

        public boolean release(Source var1);

        public void close();

        public int getMaxSourceCount();

        public int getSourceCount();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RunMode {
        STATIC,
        STREAMING;

    }
}

