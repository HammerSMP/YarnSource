/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.crash;

import net.minecraft.util.crash.CrashReport;

public class CrashException
extends RuntimeException {
    private final CrashReport report;

    public CrashException(CrashReport report) {
        this.report = report;
    }

    public CrashReport getReport() {
        return this.report;
    }

    @Override
    public Throwable getCause() {
        return this.report.getCause();
    }

    @Override
    public String getMessage() {
        return this.report.getMessage();
    }
}

