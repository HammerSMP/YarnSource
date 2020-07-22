/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.openal.AL10
 */
package net.minecraft.client.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class Source {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int pointer;
    private final AtomicBoolean playing = new AtomicBoolean(true);
    private int bufferSize = 16384;
    @Nullable
    private AudioStream stream;

    @Nullable
    static Source create() {
        int[] is = new int[1];
        AL10.alGenSources((int[])is);
        if (AlUtil.checkErrors("Allocate new source")) {
            return null;
        }
        return new Source(is[0]);
    }

    private Source(int pointer) {
        this.pointer = pointer;
    }

    public void close() {
        if (this.playing.compareAndSet(true, false)) {
            AL10.alSourceStop((int)this.pointer);
            AlUtil.checkErrors("Stop");
            if (this.stream != null) {
                try {
                    this.stream.close();
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to close audio stream", (Throwable)iOException);
                }
                this.removeProcessedBuffers();
                this.stream = null;
            }
            AL10.alDeleteSources((int[])new int[]{this.pointer});
            AlUtil.checkErrors("Cleanup");
        }
    }

    public void play() {
        AL10.alSourcePlay((int)this.pointer);
    }

    private int getSourceState() {
        if (!this.playing.get()) {
            return 4116;
        }
        return AL10.alGetSourcei((int)this.pointer, (int)4112);
    }

    public void pause() {
        if (this.getSourceState() == 4114) {
            AL10.alSourcePause((int)this.pointer);
        }
    }

    public void resume() {
        if (this.getSourceState() == 4115) {
            AL10.alSourcePlay((int)this.pointer);
        }
    }

    public void stop() {
        if (this.playing.get()) {
            AL10.alSourceStop((int)this.pointer);
            AlUtil.checkErrors("Stop");
        }
    }

    public boolean isStopped() {
        return this.getSourceState() == 4116;
    }

    public void setPosition(Vec3d arg) {
        AL10.alSourcefv((int)this.pointer, (int)4100, (float[])new float[]{(float)arg.x, (float)arg.y, (float)arg.z});
    }

    public void setPitch(float f) {
        AL10.alSourcef((int)this.pointer, (int)4099, (float)f);
    }

    public void setLooping(boolean bl) {
        AL10.alSourcei((int)this.pointer, (int)4103, (int)(bl ? 1 : 0));
    }

    public void setVolume(float f) {
        AL10.alSourcef((int)this.pointer, (int)4106, (float)f);
    }

    public void disableAttenuation() {
        AL10.alSourcei((int)this.pointer, (int)53248, (int)0);
    }

    public void setAttenuation(float f) {
        AL10.alSourcei((int)this.pointer, (int)53248, (int)53251);
        AL10.alSourcef((int)this.pointer, (int)4131, (float)f);
        AL10.alSourcef((int)this.pointer, (int)4129, (float)1.0f);
        AL10.alSourcef((int)this.pointer, (int)4128, (float)0.0f);
    }

    public void setRelative(boolean bl) {
        AL10.alSourcei((int)this.pointer, (int)514, (int)(bl ? 1 : 0));
    }

    public void setBuffer(StaticSound arg) {
        arg.getStreamBufferPointer().ifPresent(i -> AL10.alSourcei((int)this.pointer, (int)4105, (int)i));
    }

    public void setStream(AudioStream stream) {
        this.stream = stream;
        AudioFormat audioFormat = stream.getFormat();
        this.bufferSize = Source.getBufferSize(audioFormat, 1);
        this.method_19640(4);
    }

    private static int getBufferSize(AudioFormat format, int time) {
        return (int)((float)(time * format.getSampleSizeInBits()) / 8.0f * (float)format.getChannels() * format.getSampleRate());
    }

    private void method_19640(int i2) {
        if (this.stream != null) {
            try {
                for (int j = 0; j < i2; ++j) {
                    ByteBuffer byteBuffer = this.stream.getBuffer(this.bufferSize);
                    if (byteBuffer == null) continue;
                    new StaticSound(byteBuffer, this.stream.getFormat()).takeStreamBufferPointer().ifPresent(i -> AL10.alSourceQueueBuffers((int)this.pointer, (int[])new int[]{i}));
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to read from audio stream", (Throwable)iOException);
            }
        }
    }

    public void tick() {
        if (this.stream != null) {
            int i = this.removeProcessedBuffers();
            this.method_19640(i);
        }
    }

    private int removeProcessedBuffers() {
        int i = AL10.alGetSourcei((int)this.pointer, (int)4118);
        if (i > 0) {
            int[] is = new int[i];
            AL10.alSourceUnqueueBuffers((int)this.pointer, (int[])is);
            AlUtil.checkErrors("Unqueue buffers");
            AL10.alDeleteBuffers((int[])is);
            AlUtil.checkErrors("Remove processed buffers");
        }
        return i;
    }
}

