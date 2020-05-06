/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.client.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public interface When
extends Supplier<JsonElement> {
    public void validate(StateManager<?, ?> var1);

    public static PropertyCondition create() {
        return new PropertyCondition();
    }

    public static When anyOf(When ... args) {
        return new LogicalCondition(LogicalOperator.OR, Arrays.asList(args));
    }

    public static class PropertyCondition
    implements When {
        private final Map<Property<?>, String> properties = Maps.newHashMap();

        private static <T extends Comparable<T>> String name(Property<T> arg, Stream<T> stream) {
            return stream.map(arg::name).collect(Collectors.joining("|"));
        }

        private static <T extends Comparable<T>> String name(Property<T> arg, T comparable, T[] comparables) {
            return PropertyCondition.name(arg, Stream.concat(Stream.of(comparable), Stream.of(comparables)));
        }

        private <T extends Comparable<T>> void set(Property<T> arg, String string) {
            String string2 = this.properties.put(arg, string);
            if (string2 != null) {
                throw new IllegalStateException("Tried to replace " + arg + " value from " + string2 + " to " + string);
            }
        }

        public final <T extends Comparable<T>> PropertyCondition set(Property<T> arg, T comparable) {
            this.set(arg, arg.name(comparable));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PropertyCondition set(Property<T> arg, T comparable, T ... comparables) {
            this.set(arg, PropertyCondition.name(arg, comparable, comparables));
            return this;
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.properties.forEach((arg, string) -> jsonObject.addProperty(arg.getName(), string));
            return jsonObject;
        }

        @Override
        public void validate(StateManager<?, ?> arg) {
            List list = this.properties.keySet().stream().filter(arg2 -> arg.getProperty(arg2.getName()) != arg2).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalStateException("Properties " + list + " are missing from " + arg);
            }
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static class LogicalCondition
    implements When {
        private final LogicalOperator operator;
        private final List<When> components;

        private LogicalCondition(LogicalOperator arg, List<When> list) {
            this.operator = arg;
            this.components = list;
        }

        @Override
        public void validate(StateManager<?, ?> arg) {
            this.components.forEach(arg2 -> arg2.validate(arg));
        }

        @Override
        public JsonElement get() {
            JsonArray jsonArray = new JsonArray();
            this.components.stream().map(Supplier::get).forEach(((JsonArray)jsonArray)::add);
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(this.operator.name, (JsonElement)jsonArray);
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static enum LogicalOperator {
        AND("AND"),
        OR("OR");

        private final String name;

        private LogicalOperator(String string2) {
            this.name = string2;
        }
    }
}

