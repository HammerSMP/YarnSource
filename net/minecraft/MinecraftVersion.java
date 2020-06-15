/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.bridge.game.GameVersion
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.bridge.game.GameVersion;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion
implements GameVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final GameVersion field_25319 = new MinecraftVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int packVersion;
    private final Date buildTime;
    private final String releaseTarget;

    private MinecraftVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.16-pre6";
        this.stable = false;
        this.worldVersion = 2562;
        this.protocolVersion = 730;
        this.packVersion = 5;
        this.buildTime = new Date();
        this.releaseTarget = "1.16";
    }

    private MinecraftVersion(JsonObject jsonObject) {
        this.id = JsonHelper.getString(jsonObject, "id");
        this.name = JsonHelper.getString(jsonObject, "name");
        this.releaseTarget = JsonHelper.getString(jsonObject, "release_target");
        this.stable = JsonHelper.getBoolean(jsonObject, "stable");
        this.worldVersion = JsonHelper.getInt(jsonObject, "world_version");
        this.protocolVersion = JsonHelper.getInt(jsonObject, "protocol_version");
        this.packVersion = JsonHelper.getInt(jsonObject, "pack_version");
        this.buildTime = Date.from(ZonedDateTime.parse(JsonHelper.getString(jsonObject, "build_time")).toInstant());
    }

    /*
     * Exception decompiling
     */
    public static GameVersion create() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 7[TRYBLOCK]
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

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getReleaseTarget() {
        return this.releaseTarget;
    }

    public int getWorldVersion() {
        return this.worldVersion;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public int getPackVersion() {
        return this.packVersion;
    }

    public Date getBuildTime() {
        return this.buildTime;
    }

    public boolean isStable() {
        return this.stable;
    }
}

