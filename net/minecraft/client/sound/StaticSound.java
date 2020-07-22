/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.openal.AL10
 */
package net.minecraft.client.sound;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class StaticSound {
    @Nullable
    private ByteBuffer sample;
    private final AudioFormat format;
    private boolean hasBuffer;
    private int streamBufferPointer;

    public StaticSound(ByteBuffer sample, AudioFormat format) {
        this.sample = sample;
        this.format = format;
    }

    OptionalInt getStreamBufferPointer() {
        if (!this.hasBuffer) {
            if (this.sample == null) {
                return OptionalInt.empty();
            }
            int i = AlUtil.getFormatId(this.format);
            int[] is = new int[1];
            AL10.alGenBuffers((int[])is);
            if (AlUtil.checkErrors("Creating buffer")) {
                return OptionalInt.empty();
            }
            AL10.alBufferData((int)is[0], (int)i, (ByteBuffer)this.sample, (int)((int)this.format.getSampleRate()));
            if (AlUtil.checkErrors("Assigning buffer data")) {
                return OptionalInt.empty();
            }
            this.streamBufferPointer = is[0];
            this.hasBuffer = true;
            this.sample = null;
        }
        return OptionalInt.of(this.streamBufferPointer);
    }

    public void close() {
        if (this.hasBuffer) {
            AL10.alDeleteBuffers((int[])new int[]{this.streamBufferPointer});
            if (AlUtil.checkErrors("Deleting stream buffers")) {
                return;
            }
        }
        this.hasBuffer = false;
    }

    public OptionalInt takeStreamBufferPointer() {
        OptionalInt optionalInt = this.getStreamBufferPointer();
        this.hasBuffer = false;
        return optionalInt;
    }
}

