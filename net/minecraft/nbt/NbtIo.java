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
    public static CompoundTag method_30613(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);){
            CompoundTag compoundTag = NbtIo.readCompressed(inputStream);
            return compoundTag;
        }
    }

    public static CompoundTag readCompressed(InputStream stream) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)));){
            CompoundTag compoundTag = NbtIo.read(dataInputStream, PositionTracker.DEFAULT);
            return compoundTag;
        }
    }

    public static void method_30614(CompoundTag arg, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file);){
            NbtIo.writeCompressed(arg, outputStream);
        }
    }

    public static void writeCompressed(CompoundTag tag, OutputStream stream) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));){
            NbtIo.write(tag, (DataOutput)dataOutputStream);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void write(CompoundTag tag, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);){
            NbtIo.write(tag, (DataOutput)dataOutputStream);
        }
    }

    /*
     * Exception decompiling
     */
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static CompoundTag read(File file) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    public static CompoundTag read(DataInput dataInput) throws IOException {
        return NbtIo.read(dataInput, PositionTracker.DEFAULT);
    }

    public static CompoundTag read(DataInput input, PositionTracker tracker) throws IOException {
        Tag lv = NbtIo.read(input, 0, tracker);
        if (lv instanceof CompoundTag) {
            return (CompoundTag)lv;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(CompoundTag tag, DataOutput output) throws IOException {
        NbtIo.write((Tag)tag, output);
    }

    private static void write(Tag tag, DataOutput output) throws IOException {
        output.writeByte(tag.getType());
        if (tag.getType() == 0) {
            return;
        }
        output.writeUTF("");
        tag.write(output);
    }

    private static Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
        byte b = input.readByte();
        if (b == 0) {
            return EndTag.INSTANCE;
        }
        input.readUTF();
        try {
            return TagReaders.of(b).read(input, depth, tracker);
        }
        catch (IOException iOException) {
            CrashReport lv = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection lv2 = lv.addElement("NBT Tag");
            lv2.add("Tag type", b);
            throw new CrashException(lv);
        }
    }
}

