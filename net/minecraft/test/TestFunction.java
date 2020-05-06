/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.test.StartupParameter;

public class TestFunction {
    private final String batchId;
    private final String structurePath;
    private final String structureName;
    private final boolean required;
    private final Consumer<StartupParameter> starter;
    private final int tickLimit;
    private final long duration;

    public void start(StartupParameter arg) {
        this.starter.accept(arg);
    }

    public String getStructurePath() {
        return this.structurePath;
    }

    public String getStructureName() {
        return this.structureName;
    }

    public String toString() {
        return this.structurePath;
    }

    public int getTickLimit() {
        return this.tickLimit;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public long getDuration() {
        return this.duration;
    }
}

