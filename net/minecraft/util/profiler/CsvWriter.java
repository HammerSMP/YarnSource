/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class CsvWriter {
    private final Writer writer;
    private final int column;

    private CsvWriter(Writer writer, List<String> headers) throws IOException {
        this.writer = writer;
        this.column = headers.size();
        this.printRow(headers.stream());
    }

    public static Header makeHeader() {
        return new Header();
    }

    public void printRow(Object ... columns) throws IOException {
        if (columns.length != this.column) {
            throw new IllegalArgumentException("Invalid number of columns, expected " + this.column + ", but got " + columns.length);
        }
        this.printRow(Stream.of(columns));
    }

    private void printRow(Stream<?> columns) throws IOException {
        this.writer.write(columns.map(CsvWriter::print).collect(Collectors.joining(",")) + "\r\n");
    }

    private static String print(@Nullable Object o) {
        return StringEscapeUtils.escapeCsv((String)(o != null ? o.toString() : "[null]"));
    }

    public static class Header {
        private final List<String> columns = Lists.newArrayList();

        public Header addColumn(String name) {
            this.columns.add(name);
            return this;
        }

        public CsvWriter startBody(Writer writer) throws IOException {
            return new CsvWriter(writer, this.columns);
        }
    }
}

