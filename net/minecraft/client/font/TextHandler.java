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
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class TextHandler {
    private final WidthRetriever widthRetriever;

    public TextHandler(WidthRetriever widthRetriever) {
        this.widthRetriever = widthRetriever;
    }

    public float getWidth(@Nullable String text) {
        if (text == null) {
            return 0.0f;
        }
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted(text, Style.EMPTY, (i, arg, j) -> {
            mutableFloat.add(this.widthRetriever.getWidth(j, arg));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float getWidth(StringRenderable text) {
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted(text, Style.EMPTY, (i, arg, j) -> {
            mutableFloat.add(this.widthRetriever.getWidth(j, arg));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public int getTrimmedLength(String text, int maxWidth, Style style) {
        WidthLimitingVisitor lv = new WidthLimitingVisitor(maxWidth);
        TextVisitFactory.visitForwards(text, style, lv);
        return lv.getLength();
    }

    public String trimToWidth(String text, int maxWidth, Style style) {
        return text.substring(0, this.getTrimmedLength(text, maxWidth, style));
    }

    public String trimToWidthBackwards(String text, int maxWidth, Style style) {
        MutableFloat mutableFloat = new MutableFloat();
        MutableInt mutableInt = new MutableInt(text.length());
        TextVisitFactory.visitBackwards(text, style, (j, arg, k) -> {
            float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(k, arg));
            if (f > (float)maxWidth) {
                return false;
            }
            mutableInt.setValue(j);
            return true;
        });
        return text.substring(mutableInt.intValue());
    }

    @Nullable
    public Style trimToWidth(StringRenderable text, int maxWidth) {
        WidthLimitingVisitor lv = new WidthLimitingVisitor(maxWidth);
        return text.visit((arg2, string) -> TextVisitFactory.visitFormatted(string, arg2, (TextVisitFactory.CharacterVisitor)lv) ? Optional.empty() : Optional.of(arg2), Style.EMPTY).orElse(null);
    }

    public StringRenderable trimToWidth(StringRenderable text, int width, Style style) {
        final WidthLimitingVisitor lv = new WidthLimitingVisitor(width);
        return text.visit(new StringRenderable.StyledVisitor<StringRenderable>(){
            private final TextCollector collector = new TextCollector();

            @Override
            public Optional<StringRenderable> accept(Style arg, String string) {
                lv.resetLength();
                if (!TextVisitFactory.visitFormatted(string, arg, (TextVisitFactory.CharacterVisitor)lv)) {
                    String string2 = string.substring(0, lv.getLength());
                    if (!string2.isEmpty()) {
                        this.collector.add(StringRenderable.styled(string2, arg));
                    }
                    return Optional.of(this.collector.getCombined());
                }
                if (!string.isEmpty()) {
                    this.collector.add(StringRenderable.styled(string, arg));
                }
                return Optional.empty();
            }
        }, style).orElse(text);
    }

    public static int moveCursorByWords(String text, int offset, int cursor, boolean consumeSpaceOrBreak) {
        int k = cursor;
        boolean bl2 = offset < 0;
        int l = Math.abs(offset);
        for (int m = 0; m < l; ++m) {
            if (bl2) {
                while (consumeSpaceOrBreak && k > 0 && (text.charAt(k - 1) == ' ' || text.charAt(k - 1) == '\n')) {
                    --k;
                }
                while (k > 0 && text.charAt(k - 1) != ' ' && text.charAt(k - 1) != '\n') {
                    --k;
                }
                continue;
            }
            int n = text.length();
            int o = text.indexOf(32, k);
            int p = text.indexOf(10, k);
            k = o == -1 && p == -1 ? -1 : (o != -1 && p != -1 ? Math.min(o, p) : (o != -1 ? o : p));
            if (k == -1) {
                k = n;
                continue;
            }
            while (consumeSpaceOrBreak && k < n && (text.charAt(k) == ' ' || text.charAt(k) == '\n')) {
                ++k;
            }
        }
        return k;
    }

    public void wrapLines(String text, int maxWidth, Style style, boolean retainTrailingWordSplit, LineWrappingConsumer consumer) {
        int j = 0;
        int k = text.length();
        Style lv = style;
        while (j < k) {
            LineBreakingVisitor lv2 = new LineBreakingVisitor(maxWidth);
            boolean bl2 = TextVisitFactory.visitFormatted(text, j, lv, style, lv2);
            if (bl2) {
                consumer.accept(lv, j, k);
                break;
            }
            int l = lv2.getEndingIndex();
            char c = text.charAt(l);
            int m = c == '\n' || c == ' ' ? l + 1 : l;
            consumer.accept(lv, j, retainTrailingWordSplit ? m : l);
            j = m;
            lv = lv2.getEndingStyle();
        }
    }

    public List<StringRenderable> wrapLines(String text, int maxWidth, Style style) {
        ArrayList list = Lists.newArrayList();
        this.wrapLines(text, maxWidth, style, false, (arg, i, j) -> list.add(StringRenderable.styled(text.substring(i, j), arg)));
        return list;
    }

    public List<StringRenderable> wrapLines(StringRenderable arg, int maxWidth, Style arg2) {
        return this.method_29971(arg, maxWidth, arg2, null);
    }

    public List<StringRenderable> method_29971(StringRenderable arg2, int i, Style arg22, @Nullable StringRenderable arg3) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        arg2.visit((arg, string) -> {
            if (!string.isEmpty()) {
                list2.add(new StyledString(string, arg));
            }
            return Optional.empty();
        }, arg22);
        LineWrappingCollector lv = new LineWrappingCollector(list2);
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;
        block0 : while (bl) {
            bl = false;
            LineBreakingVisitor lv2 = new LineBreakingVisitor(i);
            for (StyledString lv3 : lv.parts) {
                boolean bl4 = TextVisitFactory.visitFormatted(lv3.literal, 0, lv3.style, arg22, lv2);
                if (!bl4) {
                    int j = lv2.getEndingIndex();
                    Style lv4 = lv2.getEndingStyle();
                    char c = lv.charAt(j);
                    boolean bl5 = c == '\n';
                    boolean bl6 = bl5 || c == ' ';
                    bl2 = bl5;
                    StringRenderable lv5 = lv.collectLine(j, bl6 ? 1 : 0, lv4);
                    list.add(this.method_29972(lv5, bl3, arg3));
                    bl3 = !bl5;
                    bl = true;
                    continue block0;
                }
                lv2.offset(lv3.literal.length());
            }
        }
        StringRenderable lv6 = lv.collectRemainers();
        if (lv6 != null) {
            list.add(this.method_29972(lv6, bl3, arg3));
        } else if (bl2) {
            list.add(StringRenderable.EMPTY);
        }
        return list;
    }

    private StringRenderable method_29972(StringRenderable arg, boolean bl, StringRenderable arg2) {
        if (bl && arg2 != null) {
            return StringRenderable.concat(arg2, arg);
        }
        return arg;
    }

    @Environment(value=EnvType.CLIENT)
    static class LineWrappingCollector {
        private final List<StyledString> parts;
        private String joined;

        public LineWrappingCollector(List<StyledString> parts) {
            this.parts = parts;
            this.joined = parts.stream().map(arg -> ((StyledString)arg).literal).collect(Collectors.joining());
        }

        public char charAt(int index) {
            return this.joined.charAt(index);
        }

        public StringRenderable collectLine(int lineLength, int skippedLength, Style style) {
            TextCollector lv = new TextCollector();
            ListIterator<StyledString> listIterator = this.parts.listIterator();
            int k = lineLength;
            boolean bl = false;
            while (listIterator.hasNext()) {
                StyledString lv2 = listIterator.next();
                String string = lv2.literal;
                int l = string.length();
                if (!bl) {
                    if (k > l) {
                        lv.add(lv2);
                        listIterator.remove();
                        k -= l;
                    } else {
                        String string2 = string.substring(0, k);
                        if (!string2.isEmpty()) {
                            lv.add(StringRenderable.styled(string2, lv2.style));
                        }
                        k += skippedLength;
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
                listIterator.set(new StyledString(string3, style));
                break;
            }
            this.joined = this.joined.substring(lineLength + skippedLength);
            return lv.getCombined();
        }

        @Nullable
        public StringRenderable collectRemainers() {
            TextCollector lv = new TextCollector();
            this.parts.forEach(lv::add);
            this.parts.clear();
            return lv.getRawCombined();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StyledString
    implements StringRenderable {
        private final String literal;
        private final Style style;

        public StyledString(String literal, Style style) {
            this.literal = literal;
            this.style = style;
        }

        @Override
        public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
            return visitor.accept(this.literal);
        }

        @Override
        public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
            return styledVisitor.accept(this.style.withParent(style), this.literal);
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

        public LineBreakingVisitor(float maxWidth) {
            this.maxWidth = Math.max(maxWidth, 1.0f);
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

        private boolean breakLine(int finishIndex, Style finishStyle) {
            this.endIndex = finishIndex;
            this.endStyle = finishStyle;
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

        public void offset(int extraOffset) {
            this.startOffset += extraOffset;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WidthLimitingVisitor
    implements TextVisitFactory.CharacterVisitor {
        private float widthLeft;
        private int length;

        public WidthLimitingVisitor(float maxWidth) {
            this.widthLeft = maxWidth;
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

