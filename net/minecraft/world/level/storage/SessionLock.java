/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.level.storage;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SessionLock
implements AutoCloseable {
    private final FileChannel channel;
    private final FileLock lock;
    private static final ByteBuffer field_25353;

    public static SessionLock create(Path path) throws IOException {
        Path path2 = path.resolve("session.lock");
        if (!Files.isDirectory(path, new LinkOption[0])) {
            Files.createDirectories(path, new FileAttribute[0]);
        }
        FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE);
        try {
            fileChannel.write(field_25353.duplicate());
            fileChannel.force(true);
            FileLock fileLock = fileChannel.tryLock();
            if (fileLock == null) {
                throw AlreadyLockedException.create(path2);
            }
            return new SessionLock(fileChannel, fileLock);
        }
        catch (IOException iOException) {
            try {
                fileChannel.close();
            }
            catch (IOException iOException2) {
                iOException.addSuppressed(iOException2);
            }
            throw iOException;
        }
    }

    private SessionLock(FileChannel fileChannel, FileLock fileLock) {
        this.channel = fileChannel;
        this.lock = fileLock;
    }

    @Override
    public void close() throws IOException {
        try {
            if (this.lock.isValid()) {
                this.lock.release();
            }
        }
        finally {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
        }
    }

    public boolean isValid() {
        return this.lock.isValid();
    }

    /*
     * Exception decompiling
     */
    @Environment(value=EnvType.CLIENT)
    public static boolean isLocked(Path path) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[TRYBLOCK]
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

    static {
        byte[] bs = "\u2603".getBytes(Charsets.UTF_8);
        field_25353 = ByteBuffer.allocateDirect(bs.length);
        field_25353.put(bs);
        field_25353.flip();
    }

    public static class AlreadyLockedException
    extends IOException {
        private AlreadyLockedException(Path path, String string) {
            super(path.toAbsolutePath() + ": " + string);
        }

        public static AlreadyLockedException create(Path path) {
            return new AlreadyLockedException(path, "already locked (possibly by other Minecraft instance?)");
        }
    }
}

