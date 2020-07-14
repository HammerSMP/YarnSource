/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.PropertiesMap;
import net.minecraft.state.property.Property;

public abstract class BlockStateVariantMap {
    private final Map<PropertiesMap, List<BlockStateVariant>> variants = Maps.newHashMap();

    protected void register(PropertiesMap condition, List<BlockStateVariant> possibleVariants) {
        List<BlockStateVariant> list2 = this.variants.put(condition, possibleVariants);
        if (list2 != null) {
            throw new IllegalStateException("Value " + condition + " is already defined");
        }
    }

    Map<PropertiesMap, List<BlockStateVariant>> getVariants() {
        this.checkAllPropertyDefinitions();
        return ImmutableMap.copyOf(this.variants);
    }

    private void checkAllPropertyDefinitions() {
        List<Property<?>> list = this.getProperties();
        Stream<PropertiesMap> stream = Stream.of(PropertiesMap.empty());
        for (Property<?> lv : list) {
            stream = stream.flatMap(arg2 -> lv.method_30043().map(arg2::method_25819));
        }
        List list2 = stream.filter(arg -> !this.variants.containsKey(arg)).collect(Collectors.toList());
        if (!list2.isEmpty()) {
            throw new IllegalStateException("Missing definition for properties: " + list2);
        }
    }

    abstract List<Property<?>> getProperties();

    public static <T1 extends Comparable<T1>> SingleProperty<T1> create(Property<T1> arg) {
        return new SingleProperty(arg);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> DoubleProperty<T1, T2> create(Property<T1> arg, Property<T2> arg2) {
        return new DoubleProperty(arg, arg2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> TripleProperty<T1, T2, T3> create(Property<T1> arg, Property<T2> arg2, Property<T3> arg3) {
        return new TripleProperty(arg, arg2, arg3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> QuadrupleProperty<T1, T2, T3, T4> create(Property<T1> arg, Property<T2> arg2, Property<T3> arg3, Property<T4> arg4) {
        return new QuadrupleProperty(arg, arg2, arg3, arg4);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> QuintupleProperty<T1, T2, T3, T4, T5> create(Property<T1> arg, Property<T2> arg2, Property<T3> arg3, Property<T4> arg4, Property<T5> arg5) {
        return new QuintupleProperty(arg, arg2, arg3, arg4, arg5);
    }

    @FunctionalInterface
    public static interface TriFunction<P1, P2, P3, R> {
        public R apply(P1 var1, P2 var2, P3 var3);
    }

    public static class QuintupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
    extends BlockStateVariantMap {
        private final Property<T1> first;
        private final Property<T2> second;
        private final Property<T3> third;
        private final Property<T4> fourth;
        private final Property<T5> fifth;

        private QuintupleProperty(Property<T1> arg, Property<T2> arg2, Property<T3> arg3, Property<T4> arg4, Property<T5> arg5) {
            this.first = arg;
            this.second = arg2;
            this.third = arg3;
            this.fourth = arg4;
            this.fifth = arg5;
        }

        @Override
        public List<Property<?>> getProperties() {
            return ImmutableList.of(this.first, this.second, this.third, this.fourth, this.fifth);
        }

        public QuintupleProperty<T1, T2, T3, T4, T5> register(T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, T5 comparable5, List<BlockStateVariant> list) {
            PropertiesMap lv = PropertiesMap.method_25821(this.first.method_30042(comparable), this.second.method_30042(comparable2), this.third.method_30042(comparable3), this.fourth.method_30042(comparable4), this.fifth.method_30042(comparable5));
            this.register(lv, list);
            return this;
        }

        public QuintupleProperty<T1, T2, T3, T4, T5> register(T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, T5 comparable5, BlockStateVariant arg) {
            return this.register(comparable, comparable2, comparable3, comparable4, comparable5, Collections.singletonList(arg));
        }
    }

    public static class QuadrupleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    extends BlockStateVariantMap {
        private final Property<T1> first;
        private final Property<T2> second;
        private final Property<T3> third;
        private final Property<T4> fourth;

        private QuadrupleProperty(Property<T1> arg, Property<T2> arg2, Property<T3> arg3, Property<T4> arg4) {
            this.first = arg;
            this.second = arg2;
            this.third = arg3;
            this.fourth = arg4;
        }

        @Override
        public List<Property<?>> getProperties() {
            return ImmutableList.of(this.first, this.second, this.third, this.fourth);
        }

        public QuadrupleProperty<T1, T2, T3, T4> register(T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, List<BlockStateVariant> list) {
            PropertiesMap lv = PropertiesMap.method_25821(this.first.method_30042(comparable), this.second.method_30042(comparable2), this.third.method_30042(comparable3), this.fourth.method_30042(comparable4));
            this.register(lv, list);
            return this;
        }

        public QuadrupleProperty<T1, T2, T3, T4> register(T1 comparable, T2 comparable2, T3 comparable3, T4 comparable4, BlockStateVariant arg) {
            return this.register(comparable, comparable2, comparable3, comparable4, Collections.singletonList(arg));
        }
    }

    public static class TripleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    extends BlockStateVariantMap {
        private final Property<T1> first;
        private final Property<T2> second;
        private final Property<T3> third;

        private TripleProperty(Property<T1> arg, Property<T2> arg2, Property<T3> arg3) {
            this.first = arg;
            this.second = arg2;
            this.third = arg3;
        }

        @Override
        public List<Property<?>> getProperties() {
            return ImmutableList.of(this.first, this.second, this.third);
        }

        public TripleProperty<T1, T2, T3> register(T1 comparable, T2 comparable2, T3 comparable3, List<BlockStateVariant> list) {
            PropertiesMap lv = PropertiesMap.method_25821(this.first.method_30042(comparable), this.second.method_30042(comparable2), this.third.method_30042(comparable3));
            this.register(lv, list);
            return this;
        }

        public TripleProperty<T1, T2, T3> register(T1 comparable, T2 comparable2, T3 comparable3, BlockStateVariant arg) {
            return this.register(comparable, comparable2, comparable3, Collections.singletonList(arg));
        }

        public BlockStateVariantMap register(TriFunction<T1, T2, T3, BlockStateVariant> arg) {
            this.first.getValues().forEach(comparable -> this.second.getValues().forEach(comparable2 -> this.third.getValues().forEach(comparable3 -> this.register(comparable, comparable2, comparable3, (BlockStateVariant)arg.apply(comparable, comparable2, comparable3)))));
            return this;
        }
    }

    public static class DoubleProperty<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    extends BlockStateVariantMap {
        private final Property<T1> first;
        private final Property<T2> second;

        private DoubleProperty(Property<T1> arg, Property<T2> arg2) {
            this.first = arg;
            this.second = arg2;
        }

        @Override
        public List<Property<?>> getProperties() {
            return ImmutableList.of(this.first, this.second);
        }

        public DoubleProperty<T1, T2> register(T1 comparable, T2 comparable2, List<BlockStateVariant> list) {
            PropertiesMap lv = PropertiesMap.method_25821(this.first.method_30042(comparable), this.second.method_30042(comparable2));
            this.register(lv, list);
            return this;
        }

        public DoubleProperty<T1, T2> register(T1 comparable, T2 comparable2, BlockStateVariant arg) {
            return this.register(comparable, comparable2, Collections.singletonList(arg));
        }

        public BlockStateVariantMap register(BiFunction<T1, T2, BlockStateVariant> variantFactory) {
            this.first.getValues().forEach(comparable -> this.second.getValues().forEach(comparable2 -> this.register(comparable, comparable2, (BlockStateVariant)variantFactory.apply(comparable, comparable2))));
            return this;
        }

        public BlockStateVariantMap registerVariants(BiFunction<T1, T2, List<BlockStateVariant>> variantsFactory) {
            this.first.getValues().forEach(comparable -> this.second.getValues().forEach(comparable2 -> this.register(comparable, comparable2, (List)variantsFactory.apply(comparable, comparable2))));
            return this;
        }
    }

    public static class SingleProperty<T1 extends Comparable<T1>>
    extends BlockStateVariantMap {
        private final Property<T1> property;

        private SingleProperty(Property<T1> arg) {
            this.property = arg;
        }

        @Override
        public List<Property<?>> getProperties() {
            return ImmutableList.of(this.property);
        }

        public SingleProperty<T1> register(T1 value, List<BlockStateVariant> variants) {
            PropertiesMap lv = PropertiesMap.method_25821(this.property.method_30042(value));
            this.register(lv, variants);
            return this;
        }

        public SingleProperty<T1> register(T1 value, BlockStateVariant variant) {
            return this.register(value, Collections.singletonList(variant));
        }

        public BlockStateVariantMap register(Function<T1, BlockStateVariant> variantFactory) {
            this.property.getValues().forEach(comparable -> this.register(comparable, (BlockStateVariant)variantFactory.apply(comparable)));
            return this;
        }
    }
}

