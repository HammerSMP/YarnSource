/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentStateManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, PersistentState> loadedStates = Maps.newHashMap();
    private final DataFixer dataFixer;
    private final File directory;

    public PersistentStateManager(File directory, DataFixer dataFixer) {
        this.dataFixer = dataFixer;
        this.directory = directory;
    }

    private File getFile(String id) {
        return new File(this.directory, id + ".dat");
    }

    public <T extends PersistentState> T getOrCreate(Supplier<T> factory, String id) {
        T lv = this.get(factory, id);
        if (lv != null) {
            return lv;
        }
        PersistentState lv2 = (PersistentState)factory.get();
        this.set(lv2);
        return (T)lv2;
    }

    @Nullable
    public <T extends PersistentState> T get(Supplier<T> factory, String id) {
        PersistentState lv = this.loadedStates.get(id);
        if (lv == null && !this.loadedStates.containsKey(id)) {
            lv = this.readFromFile(factory, id);
            this.loadedStates.put(id, lv);
        }
        return (T)lv;
    }

    @Nullable
    private <T extends PersistentState> T readFromFile(Supplier<T> factory, String id) {
        try {
            File file = this.getFile(id);
            if (file.exists()) {
                PersistentState lv = (PersistentState)factory.get();
                CompoundTag lv2 = this.readTag(id, SharedConstants.getGameVersion().getWorldVersion());
                lv.fromTag(lv2.getCompound("data"));
                return (T)lv;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)id, (Object)exception);
        }
        return null;
    }

    public void set(PersistentState state) {
        this.loadedStates.put(state.getId(), state);
    }

    /*
     * Exception decompiling
     */
    public CompoundTag readTag(String id, int dataVersion) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 8[TRYBLOCK]
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

    private boolean isCompressed(PushbackInputStream pushbackInputStream) throws IOException {
        int j;
        byte[] bs = new byte[2];
        boolean bl = false;
        int i = pushbackInputStream.read(bs, 0, 2);
        if (i == 2 && (j = (bs[1] & 0xFF) << 8 | bs[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (i != 0) {
            pushbackInputStream.unread(bs, 0, i);
        }
        return bl;
    }

    public void save() {
        for (PersistentState lv : this.loadedStates.values()) {
            if (lv == null) continue;
            lv.save(this.getFile(lv.getId()));
        }
    }
}

