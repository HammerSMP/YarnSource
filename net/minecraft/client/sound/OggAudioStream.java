/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.stb.STBVorbis
 *  org.lwjgl.stb.STBVorbisInfo
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class OggAudioStream
implements AudioStream {
    private long pointer;
    private final AudioFormat format;
    private final InputStream inputStream;
    private ByteBuffer buffer = MemoryUtil.memAlloc((int)8192);

    public OggAudioStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.buffer.limit(0);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            while (this.pointer == 0L) {
                if (!this.readHeader()) {
                    throw new IOException("Failed to find Ogg header");
                }
                int i = this.buffer.position();
                this.buffer.position(0);
                this.pointer = STBVorbis.stb_vorbis_open_pushdata((ByteBuffer)this.buffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, null);
                this.buffer.position(i);
                int j = intBuffer2.get(0);
                if (j == 1) {
                    this.increaseBufferSize();
                    continue;
                }
                if (j == 0) continue;
                throw new IOException("Failed to read Ogg file " + j);
            }
            this.buffer.position(this.buffer.position() + intBuffer.get(0));
            STBVorbisInfo sTBVorbisInfo = STBVorbisInfo.mallocStack((MemoryStack)memoryStack);
            STBVorbis.stb_vorbis_get_info((long)this.pointer, (STBVorbisInfo)sTBVorbisInfo);
            this.format = new AudioFormat(sTBVorbisInfo.sample_rate(), 16, sTBVorbisInfo.channels(), true, false);
        }
    }

    private boolean readHeader() throws IOException {
        int i = this.buffer.limit();
        int j = this.buffer.capacity() - i;
        if (j == 0) {
            return true;
        }
        byte[] bs = new byte[j];
        int k = this.inputStream.read(bs);
        if (k == -1) {
            return false;
        }
        int l = this.buffer.position();
        this.buffer.limit(i + k);
        this.buffer.position(i);
        this.buffer.put(bs, 0, k);
        this.buffer.position(l);
        return true;
    }

    private void increaseBufferSize() {
        boolean bl2;
        boolean bl = this.buffer.position() == 0;
        boolean bl3 = bl2 = this.buffer.position() == this.buffer.limit();
        if (bl2 && !bl) {
            this.buffer.position(0);
            this.buffer.limit(0);
        } else {
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)(bl ? 2 * this.buffer.capacity() : this.buffer.capacity()));
            byteBuffer.put(this.buffer);
            MemoryUtil.memFree((Buffer)this.buffer);
            byteBuffer.flip();
            this.buffer = byteBuffer;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private boolean readOggFile(ChannelList arg) throws IOException {
        if (this.pointer == 0L) {
            return false;
        }
        memoryStack = MemoryStack.stackPush();
        var3_3 = null;
        try {
            pointerBuffer = memoryStack.mallocPointer(1);
            intBuffer = memoryStack.mallocInt(1);
            intBuffer2 = memoryStack.mallocInt(1);
            do lbl-1000:
            // 3 sources

            {
                block21: {
                    i = STBVorbis.stb_vorbis_decode_frame_pushdata((long)this.pointer, (ByteBuffer)this.buffer, (IntBuffer)intBuffer, (PointerBuffer)pointerBuffer, (IntBuffer)intBuffer2);
                    this.buffer.position(this.buffer.position() + i);
                    j = STBVorbis.stb_vorbis_get_error((long)this.pointer);
                    if (j != 1) break block21;
                    this.increaseBufferSize();
                    if (this.readHeader()) ** GOTO lbl-1000
                    var7_8 = false;
                    return var7_8;
                }
                if (j == 0) continue;
                throw new IOException("Failed to read Ogg file " + j);
            } while ((k = intBuffer2.get(0)) == 0);
            l = intBuffer.get(0);
            pointerBuffer2 = pointerBuffer.getPointerBuffer(l);
            if (l == 1) {
                this.readChannels(pointerBuffer2.getFloatBuffer(0, k), arg);
                var12_14 = true;
                return var12_14;
            }
            if (l != 2) throw new IllegalStateException("Invalid number of channels: " + l);
            this.readChannels(pointerBuffer2.getFloatBuffer(0, k), pointerBuffer2.getFloatBuffer(1, k), arg);
            var12_15 = true;
            return var12_15;
        }
        catch (Throwable var4_5) {
            var3_3 = var4_5;
            throw var4_5;
        }
        finally {
            if (memoryStack != null) {
                if (var3_3 != null) {
                    try {
                        memoryStack.close();
                    }
                    catch (Throwable var13_16) {
                        var3_3.addSuppressed(var13_16);
                    }
                } else {
                    memoryStack.close();
                }
            }
        }
    }

    private void readChannels(FloatBuffer floatBuffer, ChannelList arg) {
        while (floatBuffer.hasRemaining()) {
            arg.addChannel(floatBuffer.get());
        }
    }

    private void readChannels(FloatBuffer floatBuffer, FloatBuffer floatBuffer2, ChannelList arg) {
        while (floatBuffer.hasRemaining() && floatBuffer2.hasRemaining()) {
            arg.addChannel(floatBuffer.get());
            arg.addChannel(floatBuffer2.get());
        }
    }

    @Override
    public void close() throws IOException {
        if (this.pointer != 0L) {
            STBVorbis.stb_vorbis_close((long)this.pointer);
            this.pointer = 0L;
        }
        MemoryUtil.memFree((Buffer)this.buffer);
        this.inputStream.close();
    }

    @Override
    public AudioFormat getFormat() {
        return this.format;
    }

    @Override
    public ByteBuffer getBuffer(int size) throws IOException {
        ChannelList lv = new ChannelList(size + 8192);
        while (this.readOggFile(lv) && lv.currentBufferSize < size) {
        }
        return lv.getBuffer();
    }

    public ByteBuffer getBuffer() throws IOException {
        ChannelList lv = new ChannelList(16384);
        while (this.readOggFile(lv)) {
        }
        return lv.getBuffer();
    }

    @Environment(value=EnvType.CLIENT)
    static class ChannelList {
        private final List<ByteBuffer> buffers = Lists.newArrayList();
        private final int size;
        private int currentBufferSize;
        private ByteBuffer buffer;

        public ChannelList(int size) {
            this.size = size + 1 & 0xFFFFFFFE;
            this.init();
        }

        private void init() {
            this.buffer = BufferUtils.createByteBuffer((int)this.size);
        }

        public void addChannel(float f) {
            if (this.buffer.remaining() == 0) {
                this.buffer.flip();
                this.buffers.add(this.buffer);
                this.init();
            }
            int i = MathHelper.clamp((int)(f * 32767.5f - 0.5f), -32768, 32767);
            this.buffer.putShort((short)i);
            this.currentBufferSize += 2;
        }

        public ByteBuffer getBuffer() {
            this.buffer.flip();
            if (this.buffers.isEmpty()) {
                return this.buffer;
            }
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)this.currentBufferSize);
            this.buffers.forEach(byteBuffer::put);
            byteBuffer.put(this.buffer);
            byteBuffer.flip();
            return byteBuffer;
        }
    }
}

