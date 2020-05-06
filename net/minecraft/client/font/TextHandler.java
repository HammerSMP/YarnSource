/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class TextHandler {
    private final WidthRetriever widthRetriever;

    public TextHandler(WidthRetriever arg) {
        this.widthRetriever = arg;
    }

    public float getWidth(@Nullable String string) {
        if (string == null) {
            return 0.0f;
        }
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted(string, Style.EMPTY, (i, arg, j) -> {
            mutableFloat.add(this.widthRetriever.getWidth(j, arg));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float getWidth(Text arg2) {
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted(arg2, Style.EMPTY, (i, arg, j) -> {
            mutableFloat.add(this.widthRetriever.getWidth(j, arg));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public int getTrimmedLength(String string, int i, Style arg) {
        WidthLimitingVisitor lv = new WidthLimitingVisitor(i);
        TextVisitFactory.visitForwards(string, arg, lv);
        return lv.getLength();
    }

    public String trimToWidth(String string, int i, Style arg) {
        return string.substring(0, this.getTrimmedLength(string, i, arg));
    }

    public String trimToWidthBackwards(String string, int i, Style arg2) {
        MutableFloat mutableFloat = new MutableFloat();
        MutableInt mutableInt = new MutableInt(string.length());
        TextVisitFactory.visitBackwards(string, arg2, (j, arg, k) -> {
            float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(k, arg));
            if (f > (float)i) {
                return false;
            }
            mutableInt.setValue(j);
            return true;
        });
        return string.substring(mutableInt.intValue());
    }

    @Nullable
    public Text trimToWidth(Text arg, int i) {
        WidthLimitingVisitor lv = new WidthLimitingVisitor(i);
        return arg.visit((arg2, string) -> {
            if (!TextVisitFactory.visitFormatted(string, arg2, (TextVisitFactory.CharacterVisitor)lv)) {
                return Optional.of(new LiteralText(string).setStyle(arg2));
            }
            return Optional.empty();
        }, Style.EMPTY).orElse(null);
    }

    public MutableText trimToWidth(Text arg, int i, Style arg2) {
        final WidthLimitingVisitor lv = new WidthLimitingVisitor(i);
        return arg.visit(new Text.StyledVisitor<MutableText>(){
            private final TextCollector collector = new TextCollector();

            @Override
            public Optional<MutableText> accept(Style arg, String string) {
                lv.resetLength();
                if (!TextVisitFactory.visitFormatted(string, arg, (TextVisitFactory.CharacterVisitor)lv)) {
                    String string2 = string.substring(0, lv.getLength());
                    if (!string2.isEmpty()) {
                        this.collector.add(new LiteralText(string2).fillStyle(arg));
                    }
                    return Optional.of(this.collector.getCombined());
                }
                if (!string.isEmpty()) {
                    this.collector.add(new LiteralText(string).fillStyle(arg));
                }
                return Optional.empty();
            }
        }, arg2).orElseGet(arg::shallowCopy);
    }

    public static int moveCursorByWords(String string, int i, int j, boolean bl) {
        int k = j;
        boolean bl2 = i < 0;
        int l = Math.abs(i);
        for (int m = 0; m < l; ++m) {
            if (bl2) {
                while (bl && k > 0 && (string.charAt(k - 1) == ' ' || string.charAt(k - 1) == '\n')) {
                    --k;
                }
                while (k > 0 && string.charAt(k - 1) != ' ' && string.charAt(k - 1) != '\n') {
                    --k;
                }
                continue;
            }
            int n = string.length();
            int o = string.indexOf(32, k);
            int p = string.indexOf(10, k);
            k = o == -1 && p == -1 ? -1 : (o != -1 && p != -1 ? Math.min(o, p) : (o != -1 ? o : p));
            if (k == -1) {
                k = n;
                continue;
            }
            while (bl && k < n && (string.charAt(k) == ' ' || string.charAt(k) == '\n')) {
                ++k;
            }
        }
        return k;
    }

    public void wrapLines(String string, int i, Style arg, boolean bl, LineWrappingConsumer arg2) {
        int j = 0;
        int k = string.length();
        Style lv = arg;
        while (j < k) {
            LineBreakingVisitor lv2 = new LineBreakingVisitor(i);
            boolean bl2 = TextVisitFactory.visitFormatted(string, j, lv, arg, lv2);
            if (bl2) {
                arg2.accept(lv, j, k);
                break;
            }
            int l = lv2.getEndingIndex();
            char c = string.charAt(l);
            int m = c == '\n' || c == ' ' ? l + 1 : l;
            arg2.accept(lv, j, bl ? m : l);
            j = m;
            lv = lv2.getEndingStyle();
        }
    }

    public List<Text> wrapLines(String string, int i2, Style arg2) {
        ArrayList list = Lists.newArrayList();
        this.wrapLines(string, i2, arg2, false, (arg, i, j) -> list.add(new LiteralText(string.substring(i, j)).setStyle(arg)));
        return list;
    }

    public List<Text> wrapLines(Text arg2, int i, Style arg22) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        arg2.visit((arg, string) -> {
            if (!string.isEmpty()) {
                list2.add(new FormattedString(string, arg));
            }
            return Optional.empty();
        }, arg22);
        LineWrappingCollector lv = new LineWrappingCollector(list2);
        boolean bl = true;
        boolean bl2 = false;
        block0 : while (bl) {
            bl = false;
            LineBreakingVisitor lv2 = new LineBreakingVisitor(i);
            for (FormattedString lv3 : lv.parts) {
                boolean bl3 = TextVisitFactory.visitFormatted(lv3.text, 0, lv3.style, arg22, lv2);
                if (!bl3) {
                    int j = lv2.getEndingIndex();
                    Style lv4 = lv2.getEndingStyle();
                    char c = lv.charAt(j);
                    boolean bl4 = c == '\n';
                    boolean bl5 = bl4 || c == ' ';
                    bl2 = bl4;
                    list.add(lv.collectLine(j, bl5 ? 1 : 0, lv4));
                    bl = true;
                    continue block0;
                }
                lv2.offset(lv3.text.length());
            }
        }
        Text lv5 = lv.collectRemainers();
        if (lv5 != null) {
            list.add(lv5);
        } else if (bl2) {
            list.add(new LiteralText("").fillStyle(arg22));
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    static class LineWrappingCollector {
        private final List<FormattedString> parts;
        private String joined;

        public LineWrappingCollector(List<FormattedString> list) {
            this.parts = list;
            this.joined = list.stream().map(arg -> ((FormattedString)arg).text).collect(Collectors.joining());
        }

        public char charAt(int i) {
            return this.joined.charAt(i);
        }

        public Text collectLine(int i, int j, Style arg) {
            TextCollector lv = new TextCollector();
            ListIterator<FormattedString> listIterator = this.parts.listIterator();
            int k = i;
            boolean bl = false;
            while (listIterator.hasNext()) {
                FormattedString lv2 = listIterator.next();
                String string = lv2.text;
                int l = string.length();
                if (!bl) {
                    if (k > l) {
                        lv.add(lv2.getText());
                        listIterator.remove();
                        k -= l;
                    } else {
                        String string2 = string.substring(0, k);
                        if (!string2.isEmpty()) {
                            lv.add(new LiteralText(string2).setStyle(lv2.style));
                        }
                        k += j;
                        bl = true;
                    }
                }
                if (!bl) continue;
                if (k > l) {
                    listIterator.remove();
                    k -= l;
                    continue;
                }
                String string3 = string.substring(k);
                if (string3.isEmpty()) {
                    listIterator.remove();
                    break;
                }
                listIterator.set(new FormattedString(string3, arg));
                break;
            }
            this.joined = this.joined.substring(i + j);
            return lv.getCombined();
        }

        @Nullable
        public Text collectRemainers() {
            TextCollector lv = new TextCollector();
            this.parts.forEach(arg2 -> lv.add(arg2.getText()));
            this.parts.clear();
            return lv.getRawCombined();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class FormattedString {
        private final String text;
        private final Style style;

        public FormattedString(String string, Style arg) {
            this.text = string;
            this.style = arg;
        }

        public MutableText getText() {
            return new LiteralText(this.text).setStyle(this.style);
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface LineWrappingConsumer {
        public void accept(Style var1, int var2, int var3);
    }

    @Environment(value=EnvType.CLIENT)
    class LineBreakingVisitor
    implements TextVisitFactory.CharacterVisitor {
        private final float maxWidth;
        private int endIndex = -1;
        private Style endStyle = Style.EMPTY;
        private boolean nonEmpty;
        private float totalWidth;
        private int lastSpaceBreak = -1;
        private Style lastSpaceStyle = Style.EMPTY;
        private int count;
        private int startOffset;

        public LineBreakingVisitor(float f) {
            this.maxWidth = Math.max(f, 1.0f);
        }

        @Override
        public boolean onChar(int i, Style arg, int j) {
            int k = i + this.startOffset;
            switch (j) {
                case 10: {
                    return this.breakLine(k, arg);
                }
                case 32: {
                    this.lastSpaceBreak = k;
                    this.lastSpaceStyle = arg;
                }
            }
            float f = TextHandler.this.widthRetriever.getWidth(j, arg);
            this.totalWidth += f;
            if (this.nonEmpty && this.totalWidth > this.maxWidth) {
                if (this.lastSpaceBreak != -1) {
                    return this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle);
                }
                return this.breakLine(k, arg);
            }
            this.nonEmpty |= f != 0.0f;
            this.count = k + Character.charCount(j);
            return true;
        }

        private boolean breakLine(int i, Style arg) {
            this.endIndex = i;
            this.endStyle = arg;
            return false;
        }

        private boolean hasLineBreak() {
            return this.endIndex != -1;
        }

        public int getEndingIndex() {
            return this.hasLineBreak() ? this.endIndex : this.count;
        }

        public Style getEndingStyle() {
            return this.endStyle;
        }

        public void offset(int i) {
            this.startOffset += i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WidthLimitingVisitor
    implements TextVisitFactory.CharacterVisitor {
        private float widthLeft;
        private int length;

        public WidthLimitingVisitor(float f) {
            this.widthLeft = f;
        }

        @Override
        public boolean onChar(int i, Style arg, int j) {
            this.widthLeft -= TextHandler.this.widthRetriever.getWidth(j, arg);
            if (this.widthLeft >= 0.0f) {
                this.length = i + Character.charCount(j);
                return true;
            }
            return false;
        }

        public int getLength() {
            return this.length;
        }

        public void resetLength() {
            this.length = 0;
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface WidthRetriever {
        public float getWidth(int var1, Style var2);
    }
}

