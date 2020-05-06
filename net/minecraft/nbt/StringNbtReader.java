/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.text.TranslatableText;

public class StringNbtReader {
    public static final SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType EXPECTED_KEY = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType EXPECTED_VALUE = new SimpleCommandExceptionType((Message)new TranslatableText("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType LIST_MIXED = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.nbt.list.mixed", object, object2));
    public static final Dynamic2CommandExceptionType ARRAY_MIXED = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.nbt.array.mixed", object, object2));
    public static final DynamicCommandExceptionType ARRAY_INVALID = new DynamicCommandExceptionType(object -> new TranslatableText("argument.nbt.array.invalid", object));
    private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static CompoundTag parse(String string) throws CommandSyntaxException {
        return new StringNbtReader(new StringReader(string)).readCompoundTag();
    }

    @VisibleForTesting
    CompoundTag readCompoundTag() throws CommandSyntaxException {
        CompoundTag lv = this.parseCompoundTag();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw TRAILING.createWithContext((ImmutableStringReader)this.reader);
        }
        return lv;
    }

    public StringNbtReader(StringReader stringReader) {
        this.reader = stringReader;
    }

    protected String readString() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.reader.readString();
    }

    protected Tag parseTagPrimitive() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int i = this.reader.getCursor();
        if (StringReader.isQuotedStringStart((char)this.reader.peek())) {
            return StringTag.of(this.reader.readQuotedString());
        }
        String string = this.reader.readUnquotedString();
        if (string.isEmpty()) {
            this.reader.setCursor(i);
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.parsePrimitive(string);
    }

    private Tag parsePrimitive(String string) {
        try {
            if (FLOAT_PATTERN.matcher(string).matches()) {
                return FloatTag.of(Float.parseFloat(string.substring(0, string.length() - 1)));
            }
            if (BYTE_PATTERN.matcher(string).matches()) {
                return ByteTag.of(Byte.parseByte(string.substring(0, string.length() - 1)));
            }
            if (LONG_PATTERN.matcher(string).matches()) {
                return LongTag.of(Long.parseLong(string.substring(0, string.length() - 1)));
            }
            if (SHORT_PATTERN.matcher(string).matches()) {
                return ShortTag.of(Short.parseShort(string.substring(0, string.length() - 1)));
            }
            if (INT_PATTERN.matcher(string).matches()) {
                return IntTag.of(Integer.parseInt(string));
            }
            if (DOUBLE_PATTERN.matcher(string).matches()) {
                return DoubleTag.of(Double.parseDouble(string.substring(0, string.length() - 1)));
            }
            if (DOUBLE_PATTERN_IMPLICIT.matcher(string).matches()) {
                return DoubleTag.of(Double.parseDouble(string));
            }
            if ("true".equalsIgnoreCase(string)) {
                return ByteTag.ONE;
            }
            if ("false".equalsIgnoreCase(string)) {
                return ByteTag.ZERO;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return StringTag.of(string);
    }

    public Tag parseTag() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        char c = this.reader.peek();
        if (c == '{') {
            return this.parseCompoundTag();
        }
        if (c == '[') {
            return this.parseTagArray();
        }
        return this.parseTagPrimitive();
    }

    protected Tag parseTagArray() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart((char)this.reader.peek(1)) && this.reader.peek(2) == ';') {
            return this.parseTagPrimitiveArray();
        }
        return this.parseListTag();
    }

    public CompoundTag parseCompoundTag() throws CommandSyntaxException {
        this.expect('{');
        CompoundTag lv = new CompoundTag();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            int i = this.reader.getCursor();
            String string = this.readString();
            if (string.isEmpty()) {
                this.reader.setCursor(i);
                throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
            this.expect(':');
            lv.put(string, this.parseTag());
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect('}');
        return lv;
    }

    private Tag parseListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        ListTag lv = new ListTag();
        TagReader<?> lv2 = null;
        while (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            Tag lv3 = this.parseTag();
            TagReader<?> lv4 = lv3.getReader();
            if (lv2 == null) {
                lv2 = lv4;
            } else if (lv4 != lv2) {
                this.reader.setCursor(i);
                throw LIST_MIXED.createWithContext((ImmutableStringReader)this.reader, (Object)lv4.getCommandFeedbackName(), (Object)lv2.getCommandFeedbackName());
            }
            lv.add(lv3);
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return lv;
    }

    private Tag parseTagPrimitiveArray() throws CommandSyntaxException {
        this.expect('[');
        int i = this.reader.getCursor();
        char c = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        if (c == 'B') {
            return new ByteArrayTag(this.readArray(ByteArrayTag.READER, ByteTag.READER));
        }
        if (c == 'L') {
            return new LongArrayTag(this.readArray(LongArrayTag.READER, LongTag.READER));
        }
        if (c == 'I') {
            return new IntArrayTag(this.readArray(IntArrayTag.READER, IntTag.READER));
        }
        this.reader.setCursor(i);
        throw ARRAY_INVALID.createWithContext((ImmutableStringReader)this.reader, (Object)String.valueOf(c));
    }

    private <T extends Number> List<T> readArray(TagReader<?> arg, TagReader<?> arg2) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            Tag lv = this.parseTag();
            TagReader<?> lv2 = lv.getReader();
            if (lv2 != arg2) {
                this.reader.setCursor(i);
                throw ARRAY_MIXED.createWithContext((ImmutableStringReader)this.reader, (Object)lv2.getCommandFeedbackName(), (Object)arg.getCommandFeedbackName());
            }
            if (arg2 == ByteTag.READER) {
                list.add(((AbstractNumberTag)lv).getByte());
            } else if (arg2 == LongTag.READER) {
                list.add(((AbstractNumberTag)lv).getLong());
            } else {
                list.add(((AbstractNumberTag)lv).getInt());
            }
            if (!this.readComma()) break;
            if (this.reader.canRead()) continue;
            throw EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return list;
    }

    private boolean readComma() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void expect(char c) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c);
    }
}

