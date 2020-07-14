/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
    protected final List<Entry<U>> entries;
    private final Random random = new Random();

    public WeightedList() {
        this(Lists.newArrayList());
    }

    private WeightedList(List<Entry<U>> list) {
        this.entries = Lists.newArrayList(list);
    }

    public static <U> Codec<WeightedList<U>> method_28338(Codec<U> codec) {
        return Entry.method_28341(codec).listOf().xmap(WeightedList::new, arg -> arg.entries);
    }

    public WeightedList<U> add(U item, int weight) {
        this.entries.add(new Entry(item, weight));
        return this;
    }

    public WeightedList<U> shuffle() {
        return this.shuffle(this.random);
    }

    public WeightedList<U> shuffle(Random random) {
        this.entries.forEach(arg -> ((Entry)arg).setShuffledOrder(random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(object -> ((Entry)object).getShuffledOrder()));
        return this;
    }

    public boolean method_28339() {
        return this.entries.isEmpty();
    }

    public Stream<U> stream() {
        return this.entries.stream().map(Entry::getElement);
    }

    public U pickRandom(Random random) {
        return this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString() {
        return "WeightedList[" + this.entries + "]";
    }

    public static class Entry<T> {
        private final T item;
        private final int weight;
        private double shuffledOrder;

        private Entry(T object, int i) {
            this.weight = i;
            this.item = object;
        }

        private double getShuffledOrder() {
            return this.shuffledOrder;
        }

        private void setShuffledOrder(float random) {
            this.shuffledOrder = -Math.pow(random, 1.0f / (float)this.weight);
        }

        public T getElement() {
            return this.item;
        }

        public String toString() {
            return "" + this.weight + ":" + this.item;
        }

        public static <E> Codec<Entry<E>> method_28341(final Codec<E> codec) {
            return new Codec<Entry<E>>(){

                public <T> DataResult<Pair<Entry<E>, T>> decode(DynamicOps<T> dynamicOps, T object2) {
                    Dynamic dynamic = new Dynamic(dynamicOps, object2);
                    return dynamic.get("data").flatMap(((Codec)codec)::parse).map(object -> new Entry(object, dynamic.get("weight").asInt(1))).map(arg -> Pair.of((Object)arg, (Object)dynamicOps.empty()));
                }

                public <T> DataResult<T> encode(Entry<E> arg, DynamicOps<T> dynamicOps, T object) {
                    return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(arg.weight)).add("data", codec.encodeStart(dynamicOps, arg.item)).build(object);
                }

                public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                    return this.encode((Entry)object, dynamicOps, object2);
                }
            };
        }
    }
}

