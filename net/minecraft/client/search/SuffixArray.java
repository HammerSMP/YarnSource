/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.Arrays
 *  it.unimi.dsi.fastutil.Swapper
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SuffixArray<T> {
    private static final boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
    private static final boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<T> objects = Lists.newArrayList();
    private final IntList characters = new IntArrayList();
    private final IntList suffixStarts = new IntArrayList();
    private IntList suffixIndexToObjectIndex = new IntArrayList();
    private IntList suffixSplits = new IntArrayList();
    private int maxTextLength;

    public void add(T object, String string) {
        this.maxTextLength = Math.max(this.maxTextLength, string.length());
        int i = this.objects.size();
        this.objects.add(object);
        this.suffixStarts.add(this.characters.size());
        for (int j = 0; j < string.length(); ++j) {
            this.suffixIndexToObjectIndex.add(i);
            this.suffixSplits.add(j);
            this.characters.add((int)string.charAt(j));
        }
        this.suffixIndexToObjectIndex.add(i);
        this.suffixSplits.add(string.length());
        this.characters.add(-1);
    }

    public void sort() {
        int i2 = this.characters.size();
        int[] is = new int[i2];
        final int[] js = new int[i2];
        final int[] ks = new int[i2];
        int[] ls = new int[i2];
        IntComparator intComparator = new IntComparator(){

            public int compare(int i, int j) {
                if (js[i] == js[j]) {
                    return Integer.compare(ks[i], ks[j]);
                }
                return Integer.compare(js[i], js[j]);
            }

            public int compare(Integer integer, Integer integer2) {
                return this.compare((int)integer, (int)integer2);
            }
        };
        Swapper swapper = (i, j) -> {
            if (i != j) {
                int k = js[i];
                is[i] = js[j];
                is[j] = k;
                k = ks[i];
                js[i] = ks[j];
                js[j] = k;
                k = ls[i];
                ks[i] = ls[j];
                ks[j] = k;
            }
        };
        for (int j2 = 0; j2 < i2; ++j2) {
            is[j2] = this.characters.getInt(j2);
        }
        int k = 1;
        int l = Math.min(i2, this.maxTextLength);
        while (k * 2 < l) {
            for (int m = 0; m < i2; ++m) {
                js[m] = is[m];
                ks[m] = m + k < i2 ? is[m + k] : -2;
                ls[m] = m;
            }
            it.unimi.dsi.fastutil.Arrays.quickSort((int)0, (int)i2, (IntComparator)intComparator, (Swapper)swapper);
            for (int n = 0; n < i2; ++n) {
                is[ls[n]] = n > 0 && js[n] == js[n - 1] && ks[n] == ks[n - 1] ? is[ls[n - 1]] : n;
            }
            k *= 2;
        }
        IntList intList = this.suffixIndexToObjectIndex;
        IntList intList2 = this.suffixSplits;
        this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
        this.suffixSplits = new IntArrayList(intList2.size());
        for (int o = 0; o < i2; ++o) {
            int p = ls[o];
            this.suffixIndexToObjectIndex.add(intList.getInt(p));
            this.suffixSplits.add(intList2.getInt(p));
        }
        if (PRINT_ARRAY) {
            this.printArray();
        }
    }

    private void printArray() {
        for (int i = 0; i < this.suffixIndexToObjectIndex.size(); ++i) {
            LOGGER.debug("{} {}", (Object)i, (Object)this.getDebugString(i));
        }
        LOGGER.debug("");
    }

    private String getDebugString(int i) {
        int j = this.suffixSplits.getInt(i);
        int k = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(i));
        StringBuilder stringBuilder = new StringBuilder();
        int l = 0;
        while (k + l < this.characters.size()) {
            int m;
            if (l == j) {
                stringBuilder.append('^');
            }
            if ((m = this.characters.get(k + l).intValue()) == -1) break;
            stringBuilder.append((char)m);
            ++l;
        }
        return stringBuilder.toString();
    }

    private int compare(String string, int i) {
        int j = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(i));
        int k = this.suffixSplits.getInt(i);
        for (int l = 0; l < string.length(); ++l) {
            char d;
            int m = this.characters.getInt(j + k + l);
            if (m == -1) {
                return 1;
            }
            char c = string.charAt(l);
            if (c < (d = (char)m)) {
                return -1;
            }
            if (c <= d) continue;
            return 1;
        }
        return 0;
    }

    public List<T> findAll(String string) {
        int i = this.suffixIndexToObjectIndex.size();
        int j = 0;
        int k = i;
        while (j < k) {
            int l = j + (k - j) / 2;
            int m = this.compare(string, l);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", (Object)string, (Object)l, (Object)this.getDebugString(l), (Object)m);
            }
            if (m > 0) {
                j = l + 1;
                continue;
            }
            k = l;
        }
        if (j < 0 || j >= i) {
            return Collections.emptyList();
        }
        int n = j;
        k = i;
        while (j < k) {
            int o = j + (k - j) / 2;
            int p = this.compare(string, o);
            if (PRINT_COMPARISONS) {
                LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", (Object)string, (Object)o, (Object)this.getDebugString(o), (Object)p);
            }
            if (p >= 0) {
                j = o + 1;
                continue;
            }
            k = o;
        }
        int q = j;
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (int r = n; r < q; ++r) {
            intSet.add(this.suffixIndexToObjectIndex.getInt(r));
        }
        int[] is = intSet.toIntArray();
        Arrays.sort(is);
        LinkedHashSet set = Sets.newLinkedHashSet();
        for (int s : is) {
            set.add(this.objects.get(s));
        }
        return Lists.newArrayList((Iterable)set);
    }
}

