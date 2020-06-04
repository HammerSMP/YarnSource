/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMaps
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileLocationInfo;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl
implements ProfileResult {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ProfileLocationInfo EMPTY_INFO = new ProfileLocationInfo(){

        @Override
        public long getTotalTime() {
            return 0L;
        }

        @Override
        public long getVisitCount() {
            return 0L;
        }

        @Override
        public Object2LongMap<String> getCounts() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter SPLITTER = Splitter.on((char)'\u001e');
    private static final Comparator<Map.Entry<String, CounterInfo>> COMPARATOR = Map.Entry.comparingByValue(Comparator.comparingLong(arg -> CounterInfo.method_24265(arg))).reversed();
    private final Map<String, ? extends ProfileLocationInfo> locationInfos;
    private final long startTime;
    private final int startTick;
    private final long endTime;
    private final int endTick;
    private final int tickDuration;

    public ProfileResultImpl(Map<String, ? extends ProfileLocationInfo> map, long l, int i, long m, int j) {
        this.locationInfos = map;
        this.startTime = l;
        this.startTick = i;
        this.endTime = m;
        this.endTick = j;
        this.tickDuration = j - i;
    }

    private ProfileLocationInfo getInfo(String string) {
        ProfileLocationInfo lv = this.locationInfos.get(string);
        return lv != null ? lv : EMPTY_INFO;
    }

    @Override
    public List<ProfilerTiming> getTimings(String string) {
        String string2 = string;
        ProfileLocationInfo lv = this.getInfo("root");
        long l = lv.getTotalTime();
        ProfileLocationInfo lv2 = this.getInfo(string);
        long m = lv2.getTotalTime();
        long n = lv2.getVisitCount();
        ArrayList list = Lists.newArrayList();
        if (!string.isEmpty()) {
            string = string + '\u001e';
        }
        long o = 0L;
        for (String string3 : this.locationInfos.keySet()) {
            if (!ProfileResultImpl.isSubpath(string, string3)) continue;
            o += this.getInfo(string3).getTotalTime();
        }
        float f = o;
        if (o < m) {
            o = m;
        }
        if (l < o) {
            l = o;
        }
        for (String string4 : this.locationInfos.keySet()) {
            if (!ProfileResultImpl.isSubpath(string, string4)) continue;
            ProfileLocationInfo lv3 = this.getInfo(string4);
            long p = lv3.getTotalTime();
            double d = (double)p * 100.0 / (double)o;
            double e = (double)p * 100.0 / (double)l;
            String string5 = string4.substring(string.length());
            list.add(new ProfilerTiming(string5, d, e, lv3.getVisitCount()));
        }
        if ((float)o > f) {
            list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
        }
        Collections.sort(list);
        list.add(0, new ProfilerTiming(string2, 100.0, (double)o * 100.0 / (double)l, n));
        return list;
    }

    private static boolean isSubpath(String string, String string2) {
        return string2.length() > string.length() && string2.startsWith(string) && string2.indexOf(30, string.length() + 1) < 0;
    }

    private Map<String, CounterInfo> setupCounters() {
        TreeMap map = Maps.newTreeMap();
        this.locationInfos.forEach((string, arg) -> {
            Object2LongMap<String> object2LongMap = arg.getCounts();
            if (!object2LongMap.isEmpty()) {
                List list = SPLITTER.splitToList((CharSequence)string);
                object2LongMap.forEach((string2, long_) -> map.computeIfAbsent(string2, string -> new CounterInfo()).add(list.iterator(), (long)long_));
            }
        });
        return map;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public int getStartTick() {
        return this.startTick;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public int getEndTick() {
        return this.endTick;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean save(File file) {
        boolean bl;
        file.getParentFile().mkdirs();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
            bl = true;
        }
        catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save profiler results to {}", (Object)file, (Object)throwable);
                bl2 = false;
            }
            catch (Throwable throwable2) {
                IOUtils.closeQuietly(writer);
                throw throwable2;
            }
            IOUtils.closeQuietly((Writer)writer);
            return bl2;
        }
        IOUtils.closeQuietly((Writer)writer);
        return bl;
    }

    protected String asString(long l, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Profiler Results ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(ProfileResultImpl.generateWittyComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
        stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
        stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
        stringBuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", Float.valueOf((float)i / ((float)l / 1.0E9f)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendTiming(0, "root", stringBuilder);
        stringBuilder.append("--- END PROFILE DUMP ---\n\n");
        Map<String, CounterInfo> map = this.setupCounters();
        if (!map.isEmpty()) {
            stringBuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.appendCounterDump(map, stringBuilder, i);
            stringBuilder.append("--- END COUNTER DUMP ---\n\n");
        }
        return stringBuilder.toString();
    }

    private static StringBuilder indent(StringBuilder stringBuilder, int i) {
        stringBuilder.append(String.format("[%02d] ", i));
        for (int j = 0; j < i; ++j) {
            stringBuilder.append("|   ");
        }
        return stringBuilder;
    }

    private void appendTiming(int i, String string2, StringBuilder stringBuilder) {
        List<ProfilerTiming> list = this.getTimings(string2);
        Object2LongMap<String> object2LongMap = this.locationInfos.get(string2).getCounts();
        object2LongMap.forEach((string, long_) -> ProfileResultImpl.indent(stringBuilder, i).append('#').append((String)string).append(' ').append(long_).append('/').append(long_ / (long)this.tickDuration).append('\n'));
        if (list.size() < 3) {
            return;
        }
        for (int j = 1; j < list.size(); ++j) {
            ProfilerTiming lv = list.get(j);
            ProfileResultImpl.indent(stringBuilder, i).append(lv.name).append('(').append(lv.visitCount).append('/').append(String.format(Locale.ROOT, "%.0f", Float.valueOf((float)lv.visitCount / (float)this.tickDuration))).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", lv.parentSectionUsagePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", lv.totalUsagePercentage)).append("%\n");
            if ("unspecified".equals(lv.name)) continue;
            try {
                this.appendTiming(i + 1, string2 + '\u001e' + lv.name, stringBuilder);
                continue;
            }
            catch (Exception exception) {
                stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
            }
        }
    }

    private void appendCounter(int i, String string, CounterInfo arg, int j, StringBuilder stringBuilder) {
        ProfileResultImpl.indent(stringBuilder, i).append(string).append(" total:").append(arg.selfTime).append('/').append(arg.totalTime).append(" average: ").append(arg.selfTime / (long)j).append('/').append(arg.totalTime / (long)j).append('\n');
        arg.subCounters.entrySet().stream().sorted(COMPARATOR).forEach(entry -> this.appendCounter(i + 1, (String)entry.getKey(), (CounterInfo)entry.getValue(), j, stringBuilder));
    }

    private void appendCounterDump(Map<String, CounterInfo> map, StringBuilder stringBuilder, int i) {
        map.forEach((string, arg) -> {
            stringBuilder.append("-- Counter: ").append((String)string).append(" --\n");
            this.appendCounter(0, "root", (CounterInfo)((CounterInfo)arg).subCounters.get("root"), i, stringBuilder);
            stringBuilder.append("\n\n");
        });
    }

    private static String generateWittyComment() {
        String[] strings = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return strings[(int)(Util.getMeasuringTimeNano() % (long)strings.length)];
        }
        catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int getTickSpan() {
        return this.tickDuration;
    }

    static class CounterInfo {
        private long selfTime;
        private long totalTime;
        private final Map<String, CounterInfo> subCounters = Maps.newHashMap();

        private CounterInfo() {
        }

        public void add(Iterator<String> iterator, long l) {
            this.totalTime += l;
            if (!iterator.hasNext()) {
                this.selfTime += l;
            } else {
                this.subCounters.computeIfAbsent(iterator.next(), string -> new CounterInfo()).add(iterator, l);
            }
        }
    }
}

