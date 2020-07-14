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

    public static When anyOf(When ... conditions) {
        return new LogicalCondition(LogicalOperator.OR, Arrays.asList(conditions));
    }

    public static class PropertyCondition
    implements When {
        private final Map<Property<?>, String> properties = Maps.newHashMap();

        private static <T extends Comparable<T>> String name(Property<T> property, Stream<T> valueStream) {
            return valueStream.map(property::name).collect(Collectors.joining("|"));
        }

        private static <T extends Comparable<T>> String name(Property<T> property, T value, T[] otherValues) {
            return PropertyCondition.name(property, Stream.concat(Stream.of(value), Stream.of(otherValues)));
        }

        private <T extends Comparable<T>> void set(Property<T> property, String value) {
            String string2 = this.properties.put(property, value);
            if (string2 != null) {
                throw new IllegalStateException("Tried to replace " + property + " value from " + string2 + " to " + value);
            }
        }

        public final <T extends Comparable<T>> PropertyCondition set(Property<T> property, T value) {
            this.set(property, property.name(value));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PropertyCondition set(Property<T> property, T value, T ... otherValues) {
            this.set(property, PropertyCondition.name(property, value, otherValues));
            return this;
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.properties.forEach((arg, string) -> jsonObject.addProperty(arg.getName(), string));
            return jsonObject;
        }

        @Override
        public void validate(StateManager<?, ?> stateManager) {
            List list = this.properties.keySet().stream().filter(arg2 -> stateManager.getProperty(arg2.getName()) != arg2).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalStateException("Properties " + list + " are missing from " + stateManager);
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

        private LogicalCondition(LogicalOperator operator, List<When> components) {
            this.operator = operator;
            this.components = components;
        }

        @Override
        public void validate(StateManager<?, ?> stateManager) {
            this.components.forEach(arg2 -> arg2.validate(stateManager));
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

        private LogicalOperator(String name) {
            this.name = name;
        }
    }
}

