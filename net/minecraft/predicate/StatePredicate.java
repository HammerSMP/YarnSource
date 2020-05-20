/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;

public class StatePredicate {
    public static final StatePredicate ANY = new StatePredicate((List<Condition>)ImmutableList.of());
    private final List<Condition> conditions;

    private static Condition createPredicate(String string, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            String string2 = jsonElement.getAsString();
            return new ExactValueCondition(string, string2);
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
        String string3 = jsonObject.has("min") ? StatePredicate.asNullableString(jsonObject.get("min")) : null;
        String string4 = jsonObject.has("max") ? StatePredicate.asNullableString(jsonObject.get("max")) : null;
        return string3 != null && string3.equals(string4) ? new ExactValueCondition(string, string3) : new RangedValueCondition(string, string3, string4);
    }

    @Nullable
    private static String asNullableString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        }
        return jsonElement.getAsString();
    }

    private StatePredicate(List<Condition> list) {
        this.conditions = ImmutableList.copyOf(list);
    }

    public <S extends State<?, S>> boolean test(StateManager<?, S> arg, S arg2) {
        for (Condition lv : this.conditions) {
            if (lv.test(arg, arg2)) continue;
            return false;
        }
        return true;
    }

    public boolean test(BlockState arg) {
        return this.test(arg.getBlock().getStateManager(), arg);
    }

    public boolean test(FluidState arg) {
        return this.test(arg.getFluid().getStateManager(), arg);
    }

    public void check(StateManager<?, ?> arg, Consumer<String> consumer) {
        this.conditions.forEach(arg2 -> arg2.reportMissing(arg, consumer));
    }

    public static StatePredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "properties");
        ArrayList list = Lists.newArrayList();
        for (Map.Entry entry : jsonObject.entrySet()) {
            list.add(StatePredicate.createPredicate((String)entry.getKey(), (JsonElement)entry.getValue()));
        }
        return new StatePredicate(list);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!this.conditions.isEmpty()) {
            this.conditions.forEach(arg -> jsonObject.add(arg.getKey(), arg.toJson()));
        }
        return jsonObject;
    }

    public static class Builder {
        private final List<Condition> conditons = Lists.newArrayList();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder exactMatch(Property<?> arg, String string) {
            this.conditons.add(new ExactValueCondition(arg.getName(), string));
            return this;
        }

        public Builder exactMatch(Property<Integer> arg, int i) {
            return this.exactMatch((Property)arg, (Comparable<T> & StringIdentifiable)Integer.toString(i));
        }

        public Builder exactMatch(Property<Boolean> arg, boolean bl) {
            return this.exactMatch((Property)arg, (Comparable<T> & StringIdentifiable)Boolean.toString(bl));
        }

        public <T extends Comparable<T> & StringIdentifiable> Builder exactMatch(Property<T> arg, T comparable) {
            return this.exactMatch(arg, (T)((StringIdentifiable)comparable).asString());
        }

        public StatePredicate build() {
            return new StatePredicate(this.conditons);
        }
    }

    static class RangedValueCondition
    extends Condition {
        @Nullable
        private final String min;
        @Nullable
        private final String max;

        public RangedValueCondition(String string, @Nullable String string2, @Nullable String string3) {
            super(string);
            this.min = string2;
            this.max = string3;
        }

        @Override
        protected <T extends Comparable<T>> boolean test(State<?, ?> arg, Property<T> arg2) {
            Optional<T> optional2;
            Optional<T> optional;
            T comparable = arg.get(arg2);
            if (!(this.min == null || (optional = arg2.parse(this.min)).isPresent() && comparable.compareTo(optional.get()) >= 0)) {
                return false;
            }
            return this.max == null || (optional2 = arg2.parse(this.max)).isPresent() && comparable.compareTo(optional2.get()) <= 0;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.min != null) {
                jsonObject.addProperty("min", this.min);
            }
            if (this.max != null) {
                jsonObject.addProperty("max", this.max);
            }
            return jsonObject;
        }
    }

    static class ExactValueCondition
    extends Condition {
        private final String value;

        public ExactValueCondition(String string, String string2) {
            super(string);
            this.value = string2;
        }

        @Override
        protected <T extends Comparable<T>> boolean test(State<?, ?> arg, Property<T> arg2) {
            T comparable = arg.get(arg2);
            Optional<T> optional = arg2.parse(this.value);
            return optional.isPresent() && comparable.compareTo(optional.get()) == 0;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.value);
        }
    }

    static abstract class Condition {
        private final String key;

        public Condition(String string) {
            this.key = string;
        }

        public <S extends State<?, S>> boolean test(StateManager<?, S> arg, S arg2) {
            Property<?> lv = arg.getProperty(this.key);
            if (lv == null) {
                return false;
            }
            return this.test(arg2, lv);
        }

        protected abstract <T extends Comparable<T>> boolean test(State<?, ?> var1, Property<T> var2);

        public abstract JsonElement toJson();

        public String getKey() {
            return this.key;
        }

        public void reportMissing(StateManager<?, ?> arg, Consumer<String> consumer) {
            Property<?> lv = arg.getProperty(this.key);
            if (lv == null) {
                consumer.accept(this.key);
            }
        }
    }
}

