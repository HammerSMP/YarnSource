/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrintStreamLogger
extends PrintStream {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final String name;

    public PrintStreamLogger(String string, OutputStream outputStream) {
        super(outputStream);
        this.name = string;
    }

    @Override
    public void println(@Nullable String string) {
        this.log(string);
    }

    @Override
    public void println(Object object) {
        this.log(String.valueOf(object));
    }

    protected void log(@Nullable String string) {
        LOGGER.info("[{}]: {}", (Object)this.name, (Object)string);
    }
}

