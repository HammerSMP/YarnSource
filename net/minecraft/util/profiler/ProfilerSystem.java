/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMaps
 *  it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileLocationInfo;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfileResultImpl;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class ProfilerSystem
implements ReadableProfiler {
    private static final long TIMEOUT_NANOSECONDS = Duration.ofMillis(100L).toNanos();
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<String> path = Lists.newArrayList();
    private final LongList timeList = new LongArrayList();
    private final Map<String, LocatedInfo> locationInfos = Maps.newHashMap();
    private final IntSupplier endTickGetter;
    private final LongSupplier timeGetter;
    private final long startTime;
    private final int startTick;
    private String location = "";
    private boolean tickStarted;
    @Nullable
    private LocatedInfo currentInfo;
    private final boolean checkTimeout;

    public ProfilerSystem(LongSupplier longSupplier, IntSupplier intSupplier, boolean bl) {
        this.startTime = longSupplier.getAsLong();
        this.timeGetter = longSupplier;
        this.startTick = intSupplier.getAsInt();
        this.endTickGetter = intSupplier;
        this.checkTimeout = bl;
    }

    @Override
    public void startTick() {
        if (this.tickStarted) {
            LOGGER.error("Profiler tick already started - missing endTick()?");
            return;
        }
        this.tickStarted = true;
        this.location = "";
        this.path.clear();
        this.push("root");
    }

    @Override
    public void endTick() {
        if (!this.tickStarted) {
            LOGGER.error("Profiler tick already ended - missing startTick()?");
            return;
        }
        this.pop();
        this.tickStarted = false;
        if (!this.location.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new Supplier[]{() -> ProfileResult.getHumanReadableName(this.location)});
        }
    }

    @Override
    public void push(String string) {
        if (!this.tickStarted) {
            LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", (Object)string);
            return;
        }
        if (!this.location.isEmpty()) {
            this.location = this.location + '\u001e';
        }
        this.location = this.location + string;
        this.path.add(this.location);
        this.timeList.add(Util.getMeasuringTimeNano());
        this.currentInfo = null;
    }

    @Override
    public void push(java.util.function.Supplier<String> supplier) {
        this.push(supplier.get());
    }

    @Override
    public void pop() {
        if (!this.tickStarted) {
            LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
            return;
        }
        if (this.timeList.isEmpty()) {
            LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
            return;
        }
        long l = Util.getMeasuringTimeNano();
        long m = this.timeList.removeLong(this.timeList.size() - 1);
        this.path.remove(this.path.size() - 1);
        long n = l - m;
        LocatedInfo lv = this.getCurrentInfo();
        lv.time = lv.time + n;
        lv.visits = lv.visits + 1L;
        if (this.checkTimeout && n > TIMEOUT_NANOSECONDS) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[]{() -> ProfileResult.getHumanReadableName(this.location), () -> (double)n / 1000000.0});
        }
        this.location = this.path.isEmpty() ? "" : this.path.get(this.path.size() - 1);
        this.currentInfo = null;
    }

    @Override
    public void swap(String string) {
        this.pop();
        this.push(string);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void swap(java.util.function.Supplier<String> supplier) {
        this.pop();
        this.push(supplier);
    }

    private LocatedInfo getCurrentInfo() {
        if (this.currentInfo == null) {
            this.currentInfo = this.locationInfos.computeIfAbsent(this.location, string -> new LocatedInfo());
        }
        return this.currentInfo;
    }

    @Override
    public void visit(String string) {
        this.getCurrentInfo().counts.addTo((Object)string, 1L);
    }

    @Override
    public void visit(java.util.function.Supplier<String> supplier) {
        this.getCurrentInfo().counts.addTo((Object)supplier.get(), 1L);
    }

    @Override
    public ProfileResult getResult() {
        return new ProfileResultImpl(this.locationInfos, this.startTime, this.startTick, this.timeGetter.getAsLong(), this.endTickGetter.getAsInt());
    }

    static class LocatedInfo
    implements ProfileLocationInfo {
        private long time;
        private long visits;
        private Object2LongOpenHashMap<String> counts = new Object2LongOpenHashMap();

        private LocatedInfo() {
        }

        @Override
        public long getTotalTime() {
            return this.time;
        }

        @Override
        public long getVisitCount() {
            return this.visits;
        }

        @Override
        public Object2LongMap<String> getCounts() {
            return Object2LongMaps.unmodifiable(this.counts);
        }
    }
}

