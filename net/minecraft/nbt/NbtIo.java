/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReaders;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class NbtIo {
    public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));){
            CompoundTag compoundTag = NbtIo.read(dataInputStream, PositionTracker.DEFAULT);
            return compoundTag;
        }
    }

    public static void writeCompressed(CompoundTag arg, OutputStream outputStream) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));){
            NbtIo.write(arg, (DataOutput)dataOutputStream);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void write(CompoundTag arg, File file) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));){
            NbtIo.write(arg, (DataOutput)dataOutputStream);
        }
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static CompoundTag read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));){
            CompoundTag compoundTag = NbtIo.read(dataInputStream, PositionTracker.DEFAULT);
            return compoundTag;
        }
    }

    public static CompoundTag read(DataInputStream dataInputStream) throws IOException {
        return NbtIo.read(dataInputStream, PositionTracker.DEFAULT);
    }

    public static CompoundTag read(DataInput dataInput, PositionTracker arg) throws IOException {
        Tag lv = NbtIo.read(dataInput, 0, arg);
        if (lv instanceof CompoundTag) {
            return (CompoundTag)lv;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(CompoundTag arg, DataOutput dataOutput) throws IOException {
        NbtIo.write((Tag)arg, dataOutput);
    }

    private static void write(Tag arg, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(arg.getType());
        if (arg.getType() == 0) {
            return;
        }
        dataOutput.writeUTF("");
        arg.write(dataOutput);
    }

    private static Tag read(DataInput dataInput, int i, PositionTracker arg) throws IOException {
        byte b = dataInput.readByte();
        if (b == 0) {
            return EndTag.INSTANCE;
        }
        dataInput.readUTF();
        try {
            return TagReaders.of(b).read(dataInput, i, arg);
        }
        catch (IOException iOException) {
            CrashReport lv = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection lv2 = lv.addElement("NBT Tag");
            lv2.add("Tag type", b);
            throw new CrashException(lv);
        }
    }
}

