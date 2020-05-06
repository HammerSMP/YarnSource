/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;

public class CrashReportSection {
    private final CrashReport report;
    private final String title;
    private final List<Element> elements = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportSection(CrashReport arg, String string) {
        this.report = arg;
        this.title = string;
    }

    @Environment(value=EnvType.CLIENT)
    public static String createPositionString(double d, double e, double f) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, e, f, CrashReportSection.createPositionString(new BlockPos(d, e, f)));
    }

    public static String createPositionString(BlockPos arg) {
        return CrashReportSection.createPositionString(arg.getX(), arg.getY(), arg.getZ());
    }

    public static String createPositionString(int i, int j, int k) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
        }
        catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        stringBuilder.append(", ");
        try {
            int l = i >> 4;
            int m = k >> 4;
            int n = i & 0xF;
            int o = j >> 4;
            int p = k & 0xF;
            int q = l << 4;
            int r = m << 4;
            int s = (l + 1 << 4) - 1;
            int t = (m + 1 << 4) - 1;
            stringBuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n, o, p, l, m, q, r, s, t));
        }
        catch (Throwable throwable2) {
            stringBuilder.append("(Error finding chunk loc)");
        }
        stringBuilder.append(", ");
        try {
            int u = i >> 9;
            int v = k >> 9;
            int w = u << 5;
            int x = v << 5;
            int y = (u + 1 << 5) - 1;
            int z = (v + 1 << 5) - 1;
            int aa = u << 9;
            int ab = v << 9;
            int ac = (u + 1 << 9) - 1;
            int ad = (v + 1 << 9) - 1;
            stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", u, v, w, x, y, z, aa, ab, ac, ad));
        }
        catch (Throwable throwable3) {
            stringBuilder.append("(Error finding world loc)");
        }
        return stringBuilder.toString();
    }

    public CrashReportSection add(String string, CrashCallable<String> arg) {
        try {
            this.add(string, arg.call());
        }
        catch (Throwable throwable) {
            this.add(string, throwable);
        }
        return this;
    }

    public CrashReportSection add(String string, Object object) {
        this.elements.add(new Element(string, object));
        return this;
    }

    public void add(String string, Throwable throwable) {
        this.add(string, (Object)throwable);
    }

    public int initStackTrace(int i) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - i];
        System.arraycopy(stackTraceElements, 3 + i, this.stackTrace, 0, this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean method_584(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
        if (this.stackTrace.length == 0 || stackTraceElement == null) {
            return false;
        }
        StackTraceElement stackTraceElement3 = this.stackTrace[0];
        if (!(stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod() && stackTraceElement3.getClassName().equals(stackTraceElement.getClassName()) && stackTraceElement3.getFileName().equals(stackTraceElement.getFileName()) && stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName()))) {
            return false;
        }
        if (stackTraceElement2 != null != this.stackTrace.length > 1) {
            return false;
        }
        if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
            return false;
        }
        this.stackTrace[0] = stackTraceElement;
        return true;
    }

    public void trimStackTraceEnd(int i) {
        StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - i];
        System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
        this.stackTrace = stackTraceElements;
    }

    public void addStackTrace(StringBuilder stringBuilder) {
        stringBuilder.append("-- ").append(this.title).append(" --\n");
        stringBuilder.append("Details:");
        for (Element lv : this.elements) {
            stringBuilder.append("\n\t");
            stringBuilder.append(lv.getName());
            stringBuilder.append(": ");
            stringBuilder.append(lv.getDetail());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringBuilder.append("\nStacktrace:");
            for (StackTraceElement stackTraceElement : this.stackTrace) {
                stringBuilder.append("\n\tat ");
                stringBuilder.append(stackTraceElement);
            }
        }
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public static void addBlockInfo(CrashReportSection arg, BlockPos arg2, @Nullable BlockState arg3) {
        if (arg3 != null) {
            arg.add("Block", arg3::toString);
        }
        arg.add("Block location", () -> CrashReportSection.createPositionString(arg2));
    }

    static class Element {
        private final String name;
        private final String detail;

        public Element(String string, Object object) {
            this.name = string;
            if (object == null) {
                this.detail = "~~NULL~~";
            } else if (object instanceof Throwable) {
                Throwable throwable = (Throwable)object;
                this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.detail = object.toString();
            }
        }

        public String getName() {
            return this.name;
        }

        public String getDetail() {
            return this.detail;
        }
    }
}

