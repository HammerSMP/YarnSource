/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IdentifierSearchableContainer<T>
implements SearchableContainer<T> {
    protected SuffixArray<T> byNamespace = new SuffixArray();
    protected SuffixArray<T> byPath = new SuffixArray();
    private final Function<T, Stream<Identifier>> identifierFinder;
    private final List<T> entries = Lists.newArrayList();
    private final Object2IntMap<T> entryIds = new Object2IntOpenHashMap();

    public IdentifierSearchableContainer(Function<T, Stream<Identifier>> function) {
        this.identifierFinder = function;
    }

    @Override
    public void reload() {
        this.byNamespace = new SuffixArray();
        this.byPath = new SuffixArray();
        for (T object : this.entries) {
            this.index(object);
        }
        this.byNamespace.sort();
        this.byPath.sort();
    }

    @Override
    public void add(T object) {
        this.entryIds.put(object, this.entries.size());
        this.entries.add(object);
        this.index(object);
    }

    @Override
    public void clear() {
        this.entries.clear();
        this.entryIds.clear();
    }

    protected void index(T object) {
        this.identifierFinder.apply(object).forEach(arg -> {
            this.byNamespace.add(object, arg.getNamespace().toLowerCase(Locale.ROOT));
            this.byPath.add(object, arg.getPath().toLowerCase(Locale.ROOT));
        });
    }

    protected int compare(T object, T object2) {
        return Integer.compare(this.entryIds.getInt(object), this.entryIds.getInt(object2));
    }

    @Override
    public List<T> findAll(String string) {
        int i = string.indexOf(58);
        if (i == -1) {
            return this.byPath.findAll(string);
        }
        List<T> list = this.byNamespace.findAll(string.substring(0, i).trim());
        String string2 = string.substring(i + 1).trim();
        List<T> list2 = this.byPath.findAll(string2);
        return Lists.newArrayList(new Iterator<T>(list.iterator(), list2.iterator(), (arg_0, arg_1) -> this.compare(arg_0, arg_1)));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Iterator<T>
    extends AbstractIterator<T> {
        private final PeekingIterator<T> field_5490;
        private final PeekingIterator<T> field_5491;
        private final Comparator<T> field_5492;

        public Iterator(java.util.Iterator<T> iterator, java.util.Iterator<T> iterator2, Comparator<T> comparator) {
            this.field_5490 = Iterators.peekingIterator(iterator);
            this.field_5491 = Iterators.peekingIterator(iterator2);
            this.field_5492 = comparator;
        }

        protected T computeNext() {
            while (this.field_5490.hasNext() && this.field_5491.hasNext()) {
                int i = this.field_5492.compare(this.field_5490.peek(), this.field_5491.peek());
                if (i == 0) {
                    this.field_5491.next();
                    return (T)this.field_5490.next();
                }
                if (i < 0) {
                    this.field_5490.next();
                    continue;
                }
                this.field_5491.next();
            }
            return (T)this.endOfData();
        }
    }
}

