/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReusableStream<T> {
    private final List<T> collectedElements = Lists.newArrayList();
    private final Spliterator<T> source;

    public ReusableStream(Stream<T> stream) {
        this.source = stream.spliterator();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 0){
            private int pos;

            @Override
            public boolean tryAdvance(Consumer<? super T> consumer) {
                while (this.pos >= ReusableStream.this.collectedElements.size()) {
                    if (ReusableStream.this.source.tryAdvance(ReusableStream.this.collectedElements::add)) continue;
                    return false;
                }
                consumer.accept(ReusableStream.this.collectedElements.get(this.pos++));
                return true;
            }
        }, false);
    }
}

