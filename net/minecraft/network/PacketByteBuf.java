/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ByteProcessor
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PacketByteBuf
extends ByteBuf {
    private final ByteBuf parent;

    public PacketByteBuf(ByteBuf byteBuf) {
        this.parent = byteBuf;
    }

    public static int getVarIntSizeBytes(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) != 0) continue;
            return j;
        }
        return 5;
    }

    public PacketByteBuf writeByteArray(byte[] bs) {
        this.writeVarInt(bs.length);
        this.writeBytes(bs);
        return this;
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public byte[] readByteArray(int i) {
        int j = this.readVarInt();
        if (j > i) {
            throw new DecoderException("ByteArray with size " + j + " is bigger than allowed " + i);
        }
        byte[] bs = new byte[j];
        this.readBytes(bs);
        return bs;
    }

    public PacketByteBuf writeIntArray(int[] is) {
        this.writeVarInt(is.length);
        for (int i : is) {
            this.writeVarInt(i);
        }
        return this;
    }

    public int[] readIntArray() {
        return this.readIntArray(this.readableBytes());
    }

    public int[] readIntArray(int i) {
        int j = this.readVarInt();
        if (j > i) {
            throw new DecoderException("VarIntArray with size " + j + " is bigger than allowed " + i);
        }
        int[] is = new int[j];
        for (int k = 0; k < is.length; ++k) {
            is[k] = this.readVarInt();
        }
        return is;
    }

    public PacketByteBuf writeLongArray(long[] ls) {
        this.writeVarInt(ls.length);
        for (long l : ls) {
            this.writeLong(l);
        }
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public long[] readLongArray(@Nullable long[] ls) {
        return this.readLongArray(ls, this.readableBytes() / 8);
    }

    @Environment(value=EnvType.CLIENT)
    public long[] readLongArray(@Nullable long[] ls, int i) {
        int j = this.readVarInt();
        if (ls == null || ls.length != j) {
            if (j > i) {
                throw new DecoderException("LongArray with size " + j + " is bigger than allowed " + i);
            }
            ls = new long[j];
        }
        for (int k = 0; k < ls.length; ++k) {
            ls[k] = this.readLong();
        }
        return ls;
    }

    public BlockPos readBlockPos() {
        return BlockPos.fromLong(this.readLong());
    }

    public PacketByteBuf writeBlockPos(BlockPos arg) {
        this.writeLong(arg.asLong());
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public ChunkSectionPos readChunkSectionPos() {
        return ChunkSectionPos.from(this.readLong());
    }

    public Text readText() {
        return Text.Serializer.fromJson(this.readString(262144));
    }

    public PacketByteBuf writeText(Text arg) {
        return this.writeString(Text.Serializer.toJson(arg), 262144);
    }

    public <T extends Enum<T>> T readEnumConstant(Class<T> arg) {
        return (T)((Enum[])arg.getEnumConstants())[this.readVarInt()];
    }

    public PacketByteBuf writeEnumConstant(Enum<?> arg) {
        return this.writeVarInt(arg.ordinal());
    }

    public int readVarInt() {
        byte b;
        int i = 0;
        int j = 0;
        do {
            b = this.readByte();
            i |= (b & 0x7F) << j++ * 7;
            if (j <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((b & 0x80) == 128);
        return i;
    }

    public long readVarLong() {
        byte b;
        long l = 0L;
        int i = 0;
        do {
            b = this.readByte();
            l |= (long)(b & 0x7F) << i++ * 7;
            if (i <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((b & 0x80) == 128);
        return l;
    }

    public PacketByteBuf writeUuid(UUID uUID) {
        this.writeLong(uUID.getMostSignificantBits());
        this.writeLong(uUID.getLeastSignificantBits());
        return this;
    }

    public UUID readUuid() {
        return new UUID(this.readLong(), this.readLong());
    }

    public PacketByteBuf writeVarInt(int i) {
        do {
            if ((i & 0xFFFFFF80) == 0) {
                this.writeByte(i);
                return this;
            }
            this.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        } while (true);
    }

    public PacketByteBuf writeVarLong(long l) {
        do {
            if ((l & 0xFFFFFFFFFFFFFF80L) == 0L) {
                this.writeByte((int)l);
                return this;
            }
            this.writeByte((int)(l & 0x7FL) | 0x80);
            l >>>= 7;
        } while (true);
    }

    public PacketByteBuf writeCompoundTag(@Nullable CompoundTag arg) {
        if (arg == null) {
            this.writeByte(0);
        } else {
            try {
                NbtIo.write(arg, (DataOutput)new ByteBufOutputStream((ByteBuf)this));
            }
            catch (IOException iOException) {
                throw new EncoderException((Throwable)iOException);
            }
        }
        return this;
    }

    @Nullable
    public CompoundTag readCompoundTag() {
        int i = this.readerIndex();
        byte b = this.readByte();
        if (b == 0) {
            return null;
        }
        this.readerIndex(i);
        try {
            return NbtIo.read((DataInput)new ByteBufInputStream((ByteBuf)this), new PositionTracker(0x200000L));
        }
        catch (IOException iOException) {
            throw new EncoderException((Throwable)iOException);
        }
    }

    public PacketByteBuf writeItemStack(ItemStack arg) {
        if (arg.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item lv = arg.getItem();
            this.writeVarInt(Item.getRawId(lv));
            this.writeByte(arg.getCount());
            CompoundTag lv2 = null;
            if (lv.isDamageable() || lv.shouldSyncTagToClient()) {
                lv2 = arg.getTag();
            }
            this.writeCompoundTag(lv2);
        }
        return this;
    }

    public ItemStack readItemStack() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        }
        int i = this.readVarInt();
        byte j = this.readByte();
        ItemStack lv = new ItemStack(Item.byRawId(i), j);
        lv.setTag(this.readCompoundTag());
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public String readString() {
        return this.readString(32767);
    }

    public String readString(int i) {
        int j = this.readVarInt();
        if (j > i * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i * 4 + ")");
        }
        if (j < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        String string = this.toString(this.readerIndex(), j, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + j);
        if (string.length() > i) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
        }
        return string;
    }

    public PacketByteBuf writeString(String string) {
        return this.writeString(string, 32767);
    }

    public PacketByteBuf writeString(String string, int i) {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        if (bs.length > i) {
            throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
        }
        this.writeVarInt(bs.length);
        this.writeBytes(bs);
        return this;
    }

    public Identifier readIdentifier() {
        return new Identifier(this.readString(32767));
    }

    public PacketByteBuf writeIdentifier(Identifier arg) {
        this.writeString(arg.toString());
        return this;
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public PacketByteBuf writeDate(Date date) {
        this.writeLong(date.getTime());
        return this;
    }

    public BlockHitResult readBlockHitResult() {
        BlockPos lv = this.readBlockPos();
        Direction lv2 = this.readEnumConstant(Direction.class);
        float f = this.readFloat();
        float g = this.readFloat();
        float h = this.readFloat();
        boolean bl = this.readBoolean();
        return new BlockHitResult(new Vec3d((float)lv.getX() + f, (float)lv.getY() + g, (float)lv.getZ() + h), lv2, lv, bl);
    }

    public void writeBlockHitResult(BlockHitResult arg) {
        BlockPos lv = arg.getBlockPos();
        this.writeBlockPos(lv);
        this.writeEnumConstant(arg.getSide());
        Vec3d lv2 = arg.getPos();
        this.writeFloat((float)(lv2.x - (double)lv.getX()));
        this.writeFloat((float)(lv2.y - (double)lv.getY()));
        this.writeFloat((float)(lv2.z - (double)lv.getZ()));
        this.writeBoolean(arg.isInsideBlock());
    }

    public int capacity() {
        return this.parent.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.parent.capacity(i);
    }

    public int maxCapacity() {
        return this.parent.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.parent.alloc();
    }

    public ByteOrder order() {
        return this.parent.order();
    }

    public ByteBuf order(ByteOrder byteOrder) {
        return this.parent.order(byteOrder);
    }

    public ByteBuf unwrap() {
        return this.parent.unwrap();
    }

    public boolean isDirect() {
        return this.parent.isDirect();
    }

    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.parent.asReadOnly();
    }

    public int readerIndex() {
        return this.parent.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.parent.readerIndex(i);
    }

    public int writerIndex() {
        return this.parent.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.parent.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.parent.setIndex(i, j);
    }

    public int readableBytes() {
        return this.parent.readableBytes();
    }

    public int writableBytes() {
        return this.parent.writableBytes();
    }

    public int maxWritableBytes() {
        return this.parent.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.parent.isReadable();
    }

    public boolean isReadable(int i) {
        return this.parent.isReadable(i);
    }

    public boolean isWritable() {
        return this.parent.isWritable();
    }

    public boolean isWritable(int i) {
        return this.parent.isWritable(i);
    }

    public ByteBuf clear() {
        return this.parent.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.parent.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.parent.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.parent.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.parent.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.parent.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.parent.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.parent.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean bl) {
        return this.parent.ensureWritable(i, bl);
    }

    public boolean getBoolean(int i) {
        return this.parent.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.parent.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.parent.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.parent.getShort(i);
    }

    public short getShortLE(int i) {
        return this.parent.getShortLE(i);
    }

    public int getUnsignedShort(int i) {
        return this.parent.getUnsignedShort(i);
    }

    public int getUnsignedShortLE(int i) {
        return this.parent.getUnsignedShortLE(i);
    }

    public int getMedium(int i) {
        return this.parent.getMedium(i);
    }

    public int getMediumLE(int i) {
        return this.parent.getMediumLE(i);
    }

    public int getUnsignedMedium(int i) {
        return this.parent.getUnsignedMedium(i);
    }

    public int getUnsignedMediumLE(int i) {
        return this.parent.getUnsignedMediumLE(i);
    }

    public int getInt(int i) {
        return this.parent.getInt(i);
    }

    public int getIntLE(int i) {
        return this.parent.getIntLE(i);
    }

    public long getUnsignedInt(int i) {
        return this.parent.getUnsignedInt(i);
    }

    public long getUnsignedIntLE(int i) {
        return this.parent.getUnsignedIntLE(i);
    }

    public long getLong(int i) {
        return this.parent.getLong(i);
    }

    public long getLongLE(int i) {
        return this.parent.getLongLE(i);
    }

    public char getChar(int i) {
        return this.parent.getChar(i);
    }

    public float getFloat(int i) {
        return this.parent.getFloat(i);
    }

    public double getDouble(int i) {
        return this.parent.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf) {
        return this.parent.getBytes(i, byteBuf);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
        return this.parent.getBytes(i, byteBuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
        return this.parent.getBytes(i, byteBuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] bs) {
        return this.parent.getBytes(i, bs);
    }

    public ByteBuf getBytes(int i, byte[] bs, int j, int k) {
        return this.parent.getBytes(i, bs, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
        return this.parent.getBytes(i, byteBuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
        return this.parent.getBytes(i, outputStream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
        return this.parent.getBytes(i, gatheringByteChannel, j);
    }

    public int getBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
        return this.parent.getBytes(i, fileChannel, l, j);
    }

    public CharSequence getCharSequence(int i, int j, Charset charset) {
        return this.parent.getCharSequence(i, j, charset);
    }

    public ByteBuf setBoolean(int i, boolean bl) {
        return this.parent.setBoolean(i, bl);
    }

    public ByteBuf setByte(int i, int j) {
        return this.parent.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.parent.setShort(i, j);
    }

    public ByteBuf setShortLE(int i, int j) {
        return this.parent.setShortLE(i, j);
    }

    public ByteBuf setMedium(int i, int j) {
        return this.parent.setMedium(i, j);
    }

    public ByteBuf setMediumLE(int i, int j) {
        return this.parent.setMediumLE(i, j);
    }

    public ByteBuf setInt(int i, int j) {
        return this.parent.setInt(i, j);
    }

    public ByteBuf setIntLE(int i, int j) {
        return this.parent.setIntLE(i, j);
    }

    public ByteBuf setLong(int i, long l) {
        return this.parent.setLong(i, l);
    }

    public ByteBuf setLongLE(int i, long l) {
        return this.parent.setLongLE(i, l);
    }

    public ByteBuf setChar(int i, int j) {
        return this.parent.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.parent.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d) {
        return this.parent.setDouble(i, d);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf) {
        return this.parent.setBytes(i, byteBuf);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
        return this.parent.setBytes(i, byteBuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
        return this.parent.setBytes(i, byteBuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] bs) {
        return this.parent.setBytes(i, bs);
    }

    public ByteBuf setBytes(int i, byte[] bs, int j, int k) {
        return this.parent.setBytes(i, bs, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
        return this.parent.setBytes(i, byteBuffer);
    }

    public int setBytes(int i, InputStream inputStream, int j) throws IOException {
        return this.parent.setBytes(i, inputStream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
        return this.parent.setBytes(i, scatteringByteChannel, j);
    }

    public int setBytes(int i, FileChannel fileChannel, long l, int j) throws IOException {
        return this.parent.setBytes(i, fileChannel, l, j);
    }

    public ByteBuf setZero(int i, int j) {
        return this.parent.setZero(i, j);
    }

    public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
        return this.parent.setCharSequence(i, charSequence, charset);
    }

    public boolean readBoolean() {
        return this.parent.readBoolean();
    }

    public byte readByte() {
        return this.parent.readByte();
    }

    public short readUnsignedByte() {
        return this.parent.readUnsignedByte();
    }

    public short readShort() {
        return this.parent.readShort();
    }

    public short readShortLE() {
        return this.parent.readShortLE();
    }

    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.parent.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.parent.readMedium();
    }

    public int readMediumLE() {
        return this.parent.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.parent.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.parent.readInt();
    }

    public int readIntLE() {
        return this.parent.readIntLE();
    }

    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.parent.readUnsignedIntLE();
    }

    public long readLong() {
        return this.parent.readLong();
    }

    public long readLongLE() {
        return this.parent.readLongLE();
    }

    public char readChar() {
        return this.parent.readChar();
    }

    public float readFloat() {
        return this.parent.readFloat();
    }

    public double readDouble() {
        return this.parent.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.parent.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.parent.readSlice(i);
    }

    public ByteBuf readRetainedSlice(int i) {
        return this.parent.readRetainedSlice(i);
    }

    public ByteBuf readBytes(ByteBuf byteBuf) {
        return this.parent.readBytes(byteBuf);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int i) {
        return this.parent.readBytes(byteBuf, i);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
        return this.parent.readBytes(byteBuf, i, j);
    }

    public ByteBuf readBytes(byte[] bs) {
        return this.parent.readBytes(bs);
    }

    public ByteBuf readBytes(byte[] bs, int i, int j) {
        return this.parent.readBytes(bs, i, j);
    }

    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return this.parent.readBytes(byteBuffer);
    }

    public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
        return this.parent.readBytes(outputStream, i);
    }

    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return this.parent.readBytes(gatheringByteChannel, i);
    }

    public CharSequence readCharSequence(int i, Charset charset) {
        return this.parent.readCharSequence(i, charset);
    }

    public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.parent.readBytes(fileChannel, l, i);
    }

    public ByteBuf skipBytes(int i) {
        return this.parent.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean bl) {
        return this.parent.writeBoolean(bl);
    }

    public ByteBuf writeByte(int i) {
        return this.parent.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.parent.writeShort(i);
    }

    public ByteBuf writeShortLE(int i) {
        return this.parent.writeShortLE(i);
    }

    public ByteBuf writeMedium(int i) {
        return this.parent.writeMedium(i);
    }

    public ByteBuf writeMediumLE(int i) {
        return this.parent.writeMediumLE(i);
    }

    public ByteBuf writeInt(int i) {
        return this.parent.writeInt(i);
    }

    public ByteBuf writeIntLE(int i) {
        return this.parent.writeIntLE(i);
    }

    public ByteBuf writeLong(long l) {
        return this.parent.writeLong(l);
    }

    public ByteBuf writeLongLE(long l) {
        return this.parent.writeLongLE(l);
    }

    public ByteBuf writeChar(int i) {
        return this.parent.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.parent.writeFloat(f);
    }

    public ByteBuf writeDouble(double d) {
        return this.parent.writeDouble(d);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return this.parent.writeBytes(byteBuf);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return this.parent.writeBytes(byteBuf, i);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
        return this.parent.writeBytes(byteBuf, i, j);
    }

    public ByteBuf writeBytes(byte[] bs) {
        return this.parent.writeBytes(bs);
    }

    public ByteBuf writeBytes(byte[] bs, int i, int j) {
        return this.parent.writeBytes(bs, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return this.parent.writeBytes(byteBuffer);
    }

    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return this.parent.writeBytes(inputStream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return this.parent.writeBytes(scatteringByteChannel, i);
    }

    public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return this.parent.writeBytes(fileChannel, l, i);
    }

    public ByteBuf writeZero(int i) {
        return this.parent.writeZero(i);
    }

    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return this.parent.writeCharSequence(charSequence, charset);
    }

    public int indexOf(int i, int j, byte b) {
        return this.parent.indexOf(i, j, b);
    }

    public int bytesBefore(byte b) {
        return this.parent.bytesBefore(b);
    }

    public int bytesBefore(int i, byte b) {
        return this.parent.bytesBefore(i, b);
    }

    public int bytesBefore(int i, int j, byte b) {
        return this.parent.bytesBefore(i, j, b);
    }

    public int forEachByte(ByteProcessor byteProcessor) {
        return this.parent.forEachByte(byteProcessor);
    }

    public int forEachByte(int i, int j, ByteProcessor byteProcessor) {
        return this.parent.forEachByte(i, j, byteProcessor);
    }

    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(byteProcessor);
    }

    public int forEachByteDesc(int i, int j, ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(i, j, byteProcessor);
    }

    public ByteBuf copy() {
        return this.parent.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.parent.copy(i, j);
    }

    public ByteBuf slice() {
        return this.parent.slice();
    }

    public ByteBuf retainedSlice() {
        return this.parent.retainedSlice();
    }

    public ByteBuf slice(int i, int j) {
        return this.parent.slice(i, j);
    }

    public ByteBuf retainedSlice(int i, int j) {
        return this.parent.retainedSlice(i, j);
    }

    public ByteBuf duplicate() {
        return this.parent.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.parent.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.parent.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.parent.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.parent.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.parent.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.parent.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.parent.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.parent.hasArray();
    }

    public byte[] array() {
        return this.parent.array();
    }

    public int arrayOffset() {
        return this.parent.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.parent.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.parent.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.parent.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.parent.toString(i, j, charset);
    }

    public int hashCode() {
        return this.parent.hashCode();
    }

    public boolean equals(Object object) {
        return this.parent.equals(object);
    }

    public int compareTo(ByteBuf byteBuf) {
        return this.parent.compareTo(byteBuf);
    }

    public String toString() {
        return this.parent.toString();
    }

    public ByteBuf retain(int i) {
        return this.parent.retain(i);
    }

    public ByteBuf retain() {
        return this.parent.retain();
    }

    public ByteBuf touch() {
        return this.parent.touch();
    }

    public ByteBuf touch(Object object) {
        return this.parent.touch(object);
    }

    public int refCnt() {
        return this.parent.refCnt();
    }

    public boolean release() {
        return this.parent.release();
    }

    public boolean release(int i) {
        return this.parent.release(i);
    }
}

