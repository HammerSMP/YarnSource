/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.logging;

import java.io.OutputStream;
import net.minecraft.util.logging.PrintStreamLogger;

public class DebugPrintStreamLogger
extends PrintStreamLogger {
    public DebugPrintStreamLogger(String string, OutputStream outputStream) {
        super(string, outputStream);
    }

    @Override
    protected void log(String string) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
        LOGGER.info("[{}]@.({}:{}): {}", (Object)this.name, (Object)stackTraceElement.getFileName(), (Object)stackTraceElement.getLineNumber(), (Object)string);
    }
}

