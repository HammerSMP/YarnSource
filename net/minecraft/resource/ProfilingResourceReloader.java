/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilingResourceReloader
extends ResourceReloader<Summary> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

    public ProfilingResourceReloader(ResourceManager manager, List<ResourceReloadListener> listeners, Executor prepareExecutor2, Executor applyExecutor2, CompletableFuture<Unit> completableFuture) {
        super(prepareExecutor2, applyExecutor2, manager, listeners, (arg, arg2, arg3, prepareExecutor, applyExecutor) -> {
            AtomicLong atomicLong = new AtomicLong();
            AtomicLong atomicLong2 = new AtomicLong();
            ProfilerSystem lv = new ProfilerSystem(Util.nanoTimeSupplier, () -> 0, false);
            ProfilerSystem lv2 = new ProfilerSystem(Util.nanoTimeSupplier, () -> 0, false);
            CompletableFuture<Void> completableFuture = arg3.reload(arg, arg2, lv, lv2, runnable -> prepareExecutor.execute(() -> {
                long l = Util.getMeasuringTimeNano();
                runnable.run();
                atomicLong.addAndGet(Util.getMeasuringTimeNano() - l);
            }), runnable -> applyExecutor.execute(() -> {
                long l = Util.getMeasuringTimeNano();
                runnable.run();
                atomicLong2.addAndGet(Util.getMeasuringTimeNano() - l);
            }));
            return completableFuture.thenApplyAsync(void_ -> new Summary(arg3.getName(), lv.getResult(), lv2.getResult(), atomicLong, atomicLong2), applyExecutor2);
        }, completableFuture);
        this.reloadTimer.start();
        this.applyStageFuture.thenAcceptAsync(this::finish, applyExecutor2);
    }

    private void finish(List<Summary> summaries) {
        this.reloadTimer.stop();
        int i = 0;
        LOGGER.info("Resource reload finished after " + this.reloadTimer.elapsed(TimeUnit.MILLISECONDS) + " ms");
        for (Summary lv : summaries) {
            ProfileResult lv2 = lv.prepareProfile;
            ProfileResult lv3 = lv.applyProfile;
            int j = (int)((double)lv.prepareTimeMs.get() / 1000000.0);
            int k = (int)((double)lv.applyTimeMs.get() / 1000000.0);
            int l = j + k;
            String string = lv.name;
            LOGGER.info(string + " took approximately " + l + " ms (" + j + " ms preparing, " + k + " ms applying)");
            i += k;
        }
        LOGGER.info("Total blocking time: " + i + " ms");
    }

    public static class Summary {
        private final String name;
        private final ProfileResult prepareProfile;
        private final ProfileResult applyProfile;
        private final AtomicLong prepareTimeMs;
        private final AtomicLong applyTimeMs;

        private Summary(String name, ProfileResult prepareProfile, ProfileResult applyProfile, AtomicLong prepareTimeMs, AtomicLong applyTimeMs) {
            this.name = name;
            this.prepareProfile = prepareProfile;
            this.applyProfile = applyProfile;
            this.prepareTimeMs = prepareTimeMs;
            this.applyTimeMs = applyTimeMs;
        }
    }
}

