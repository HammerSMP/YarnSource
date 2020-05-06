/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.primitives.Floats
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BufferBuilder
extends FixedColorVertexConsumer
implements BufferVertexConsumer {
    private static final Logger LOGGER = LogManager.getLogger();
    private ByteBuffer buffer;
    private final List<DrawArrayParameters> parameters = Lists.newArrayList();
    private int lastParameterIndex = 0;
    private int buildStart = 0;
    private int elementOffset = 0;
    private int nextDrawStart = 0;
    private int vertexCount;
    @Nullable
    private VertexFormatElement currentElement;
    private int currentElementId;
    private int drawMode;
    private VertexFormat format;
    private boolean field_21594;
    private boolean field_21595;
    private boolean building;

    public BufferBuilder(int i) {
        this.buffer = GlAllocationUtils.allocateByteBuffer(i * 4);
    }

    protected void grow() {
        this.grow(this.format.getVertexSize());
    }

    private void grow(int i) {
        if (this.elementOffset + i <= this.buffer.capacity()) {
            return;
        }
        int j = this.buffer.capacity();
        int k = j + BufferBuilder.roundBufferSize(i);
        LOGGER.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", (Object)j, (Object)k);
        ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(k);
        this.buffer.position(0);
        byteBuffer.put(this.buffer);
        byteBuffer.rewind();
        this.buffer = byteBuffer;
    }

    private static int roundBufferSize(int i) {
        int k;
        int j = 0x200000;
        if (i == 0) {
            return j;
        }
        if (i < 0) {
            j *= -1;
        }
        if ((k = i % j) == 0) {
            return i;
        }
        return i + j - k;
    }

    public void sortQuads(float f, float g, float h) {
        this.buffer.clear();
        FloatBuffer floatBuffer = this.buffer.asFloatBuffer();
        int i2 = this.vertexCount / 4;
        float[] fs = new float[i2];
        for (int j2 = 0; j2 < i2; ++j2) {
            fs[j2] = BufferBuilder.getDistanceSq(floatBuffer, f, g, h, this.format.getVertexSizeInteger(), this.buildStart / 4 + j2 * this.format.getVertexSize());
        }
        int[] is = new int[i2];
        for (int k = 0; k < is.length; ++k) {
            is[k] = k;
        }
        IntArrays.mergeSort((int[])is, (i, j) -> Floats.compare((float)fs[j], (float)fs[i]));
        BitSet bitSet = new BitSet();
        FloatBuffer floatBuffer2 = GlAllocationUtils.allocateFloatBuffer(this.format.getVertexSizeInteger() * 4);
        int l = bitSet.nextClearBit(0);
        while (l < is.length) {
            int m = is[l];
            if (m != l) {
                this.method_22628(floatBuffer, m);
                floatBuffer2.clear();
                floatBuffer2.put(floatBuffer);
                int n = m;
                int o = is[n];
                while (n != l) {
                    this.method_22628(floatBuffer, o);
                    FloatBuffer floatBuffer3 = floatBuffer.slice();
                    this.method_22628(floatBuffer, n);
                    floatBuffer.put(floatBuffer3);
                    bitSet.set(n);
                    n = o;
                    o = is[n];
                }
                this.method_22628(floatBuffer, l);
                floatBuffer2.flip();
                floatBuffer.put(floatBuffer2);
            }
            bitSet.set(l);
            l = bitSet.nextClearBit(l + 1);
        }
    }

    private void method_22628(FloatBuffer floatBuffer, int i) {
        int j = this.format.getVertexSizeInteger() * 4;
        floatBuffer.limit(this.buildStart / 4 + (i + 1) * j);
        floatBuffer.position(this.buildStart / 4 + i * j);
    }

    public State popState() {
        this.buffer.limit(this.elementOffset);
        this.buffer.position(this.buildStart);
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.vertexCount * this.format.getVertexSize());
        byteBuffer.put(this.buffer);
        this.buffer.clear();
        return new State(byteBuffer, this.format);
    }

    private static float getDistanceSq(FloatBuffer floatBuffer, float f, float g, float h, int i, int j) {
        float k = floatBuffer.get(j + i * 0 + 0);
        float l = floatBuffer.get(j + i * 0 + 1);
        float m = floatBuffer.get(j + i * 0 + 2);
        float n = floatBuffer.get(j + i * 1 + 0);
        float o = floatBuffer.get(j + i * 1 + 1);
        float p = floatBuffer.get(j + i * 1 + 2);
        float q = floatBuffer.get(j + i * 2 + 0);
        float r = floatBuffer.get(j + i * 2 + 1);
        float s = floatBuffer.get(j + i * 2 + 2);
        float t = floatBuffer.get(j + i * 3 + 0);
        float u = floatBuffer.get(j + i * 3 + 1);
        float v = floatBuffer.get(j + i * 3 + 2);
        float w = (k + n + q + t) * 0.25f - f;
        float x = (l + o + r + u) * 0.25f - g;
        float y = (m + p + s + v) * 0.25f - h;
        return w * w + x * x + y * y;
    }

    public void restoreState(State arg) {
        arg.buffer.clear();
        int i = arg.buffer.capacity();
        this.grow(i);
        this.buffer.limit(this.buffer.capacity());
        this.buffer.position(this.buildStart);
        this.buffer.put(arg.buffer);
        this.buffer.clear();
        VertexFormat lv = arg.format;
        this.method_23918(lv);
        this.vertexCount = i / lv.getVertexSize();
        this.elementOffset = this.buildStart + this.vertexCount * lv.getVertexSize();
    }

    public void begin(int i, VertexFormat arg) {
        if (this.building) {
            throw new IllegalStateException("Already building!");
        }
        this.building = true;
        this.drawMode = i;
        this.method_23918(arg);
        this.currentElement = (VertexFormatElement)arg.getElements().get(0);
        this.currentElementId = 0;
        this.buffer.clear();
    }

    private void method_23918(VertexFormat arg) {
        if (this.format == arg) {
            return;
        }
        this.format = arg;
        boolean bl = arg == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
        boolean bl2 = arg == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        this.field_21594 = bl || bl2;
        this.field_21595 = bl;
    }

    public void end() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
        this.building = false;
        this.parameters.add(new DrawArrayParameters(this.format, this.vertexCount, this.drawMode));
        this.buildStart += this.vertexCount * this.format.getVertexSize();
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
    }

    @Override
    public void putByte(int i, byte b) {
        this.buffer.put(this.elementOffset + i, b);
    }

    @Override
    public void putShort(int i, short s) {
        this.buffer.putShort(this.elementOffset + i, s);
    }

    @Override
    public void putFloat(int i, float f) {
        this.buffer.putFloat(this.elementOffset + i, f);
    }

    @Override
    public void next() {
        if (this.currentElementId != 0) {
            throw new IllegalStateException("Not filled all elements of the vertex");
        }
        ++this.vertexCount;
        this.grow();
    }

    @Override
    public void nextElement() {
        VertexFormatElement lv;
        ImmutableList<VertexFormatElement> immutableList = this.format.getElements();
        this.currentElementId = (this.currentElementId + 1) % immutableList.size();
        this.elementOffset += this.currentElement.getSize();
        this.currentElement = lv = (VertexFormatElement)immutableList.get(this.currentElementId);
        if (lv.getType() == VertexFormatElement.Type.PADDING) {
            this.nextElement();
        }
        if (this.colorFixed && this.currentElement.getType() == VertexFormatElement.Type.COLOR) {
            BufferVertexConsumer.super.color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha);
        }
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        return BufferVertexConsumer.super.color(i, j, k, l);
    }

    @Override
    public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        if (this.field_21594) {
            int u;
            this.putFloat(0, f);
            this.putFloat(4, g);
            this.putFloat(8, h);
            this.putByte(12, (byte)(i * 255.0f));
            this.putByte(13, (byte)(j * 255.0f));
            this.putByte(14, (byte)(k * 255.0f));
            this.putByte(15, (byte)(l * 255.0f));
            this.putFloat(16, m);
            this.putFloat(20, n);
            if (this.field_21595) {
                this.putShort(24, (short)(o & 0xFFFF));
                this.putShort(26, (short)(o >> 16 & 0xFFFF));
                int t = 28;
            } else {
                u = 24;
            }
            this.putShort(u + 0, (short)(p & 0xFFFF));
            this.putShort(u + 2, (short)(p >> 16 & 0xFFFF));
            this.putByte(u + 4, BufferVertexConsumer.method_24212(q));
            this.putByte(u + 5, BufferVertexConsumer.method_24212(r));
            this.putByte(u + 6, BufferVertexConsumer.method_24212(s));
            this.elementOffset += u + 8;
            this.next();
            return;
        }
        super.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
    }

    public Pair<DrawArrayParameters, ByteBuffer> popData() {
        DrawArrayParameters lv = this.parameters.get(this.lastParameterIndex++);
        this.buffer.position(this.nextDrawStart);
        this.nextDrawStart += lv.getCount() * lv.getVertexFormat().getVertexSize();
        this.buffer.limit(this.nextDrawStart);
        if (this.lastParameterIndex == this.parameters.size() && this.vertexCount == 0) {
            this.clear();
        }
        ByteBuffer byteBuffer = this.buffer.slice();
        this.buffer.clear();
        return Pair.of((Object)lv, (Object)byteBuffer);
    }

    public void clear() {
        if (this.buildStart != this.nextDrawStart) {
            LOGGER.warn("Bytes mismatch " + this.buildStart + " " + this.nextDrawStart);
        }
        this.reset();
    }

    public void reset() {
        this.buildStart = 0;
        this.nextDrawStart = 0;
        this.elementOffset = 0;
        this.parameters.clear();
        this.lastParameterIndex = 0;
    }

    @Override
    public VertexFormatElement getCurrentElement() {
        if (this.currentElement == null) {
            throw new IllegalStateException("BufferBuilder not started");
        }
        return this.currentElement;
    }

    public boolean isBuilding() {
        return this.building;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawArrayParameters {
        private final VertexFormat vertexFormat;
        private final int count;
        private final int mode;

        private DrawArrayParameters(VertexFormat arg, int i, int j) {
            this.vertexFormat = arg;
            this.count = i;
            this.mode = j;
        }

        public VertexFormat getVertexFormat() {
            return this.vertexFormat;
        }

        public int getCount() {
            return this.count;
        }

        public int getMode() {
            return this.mode;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        private final ByteBuffer buffer;
        private final VertexFormat format;

        private State(ByteBuffer byteBuffer, VertexFormat arg) {
            this.buffer = byteBuffer;
            this.format = arg;
        }
    }
}

