/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class TextRenderingUtils {
    @VisibleForTesting
    protected static List<String> lineBreak(String string) {
        return Arrays.asList(string.split("\\n"));
    }

    public static List<Line> decompose(String string, LineSegment ... args) {
        return TextRenderingUtils.decompose(string, Arrays.asList(args));
    }

    private static List<Line> decompose(String string, List<LineSegment> list) {
        List<String> list2 = TextRenderingUtils.lineBreak(string);
        return TextRenderingUtils.insertLinks(list2, list);
    }

    private static List<Line> insertLinks(List<String> list, List<LineSegment> list2) {
        int i = 0;
        ArrayList list3 = Lists.newArrayList();
        for (String string : list) {
            ArrayList list4 = Lists.newArrayList();
            List<String> list5 = TextRenderingUtils.split(string, "%link");
            for (String string2 : list5) {
                if ("%link".equals(string2)) {
                    list4.add(list2.get(i++));
                    continue;
                }
                list4.add(LineSegment.text(string2));
            }
            list3.add(new Line(list4));
        }
        return list3;
    }

    public static List<String> split(String string, String string2) {
        int j;
        if (string2.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be the empty string");
        }
        ArrayList list = Lists.newArrayList();
        int i = 0;
        while ((j = string.indexOf(string2, i)) != -1) {
            if (j > i) {
                list.add(string.substring(i, j));
            }
            list.add(string2);
            i = j + string2.length();
        }
        if (i < string.length()) {
            list.add(string.substring(i));
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public static class LineSegment {
        private final String fullText;
        private final String linkTitle;
        private final String linkUrl;

        private LineSegment(String string) {
            this.fullText = string;
            this.linkTitle = null;
            this.linkUrl = null;
        }

        private LineSegment(String string, String string2, String string3) {
            this.fullText = string;
            this.linkTitle = string2;
            this.linkUrl = string3;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            LineSegment lv = (LineSegment)object;
            return Objects.equals(this.fullText, lv.fullText) && Objects.equals(this.linkTitle, lv.linkTitle) && Objects.equals(this.linkUrl, lv.linkUrl);
        }

        public int hashCode() {
            return Objects.hash(this.fullText, this.linkTitle, this.linkUrl);
        }

        public String toString() {
            return "Segment{fullText='" + this.fullText + '\'' + ", linkTitle='" + this.linkTitle + '\'' + ", linkUrl='" + this.linkUrl + '\'' + '}';
        }

        public String renderedText() {
            return this.isLink() ? this.linkTitle : this.fullText;
        }

        public boolean isLink() {
            return this.linkTitle != null;
        }

        public String getLinkUrl() {
            if (!this.isLink()) {
                throw new IllegalStateException("Not a link: " + this);
            }
            return this.linkUrl;
        }

        public static LineSegment link(String string, String string2) {
            return new LineSegment(null, string, string2);
        }

        @VisibleForTesting
        protected static LineSegment text(String string) {
            return new LineSegment(string);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Line {
        public final List<LineSegment> segments;

        Line(List<LineSegment> list) {
            this.segments = list;
        }

        public String toString() {
            return "Line{segments=" + this.segments + '}';
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Line lv = (Line)object;
            return Objects.equals(this.segments, lv.segments);
        }

        public int hashCode() {
            return Objects.hash(this.segments);
        }
    }
}

