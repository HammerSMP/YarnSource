/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickDurationMonitor {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LongSupplier timeGetter;
    private final long overtime;
    private int tickCount;
    private final File tickResultsDirectory;
    private ReadableProfiler profiler;

    public Profiler nextProfiler() {
        this.profiler = new ProfilerSystem(this.timeGetter, () -> this.tickCount, false);
        ++this.tickCount;
        return this.profiler;
    }

    public void endTick() {
        if (this.profiler == DummyProfiler.INSTANCE) {
            return;
        }
        ProfileResult lv = this.profiler.getResult();
        this.profiler = DummyProfiler.INSTANCE;
        if (lv.getTimeSpan() >= this.overtime) {
            File file = new File(this.tickResultsDirectory, "tick-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
            lv.save(file);
            LOGGER.info("Recorded long tick -- wrote info to: {}", (Object)file.getAbsolutePath());
        }
    }

    @Nullable
    public static TickDurationMonitor create(String name) {
        return null;
    }

    public static Profiler tickProfiler(Profiler profiler, @Nullable TickDurationMonitor monitor) {
        if (monitor != null) {
            return Profiler.union(monitor.nextProfiler(), profiler);
        }
        return profiler;
    }
}

