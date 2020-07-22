/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.state.State;

public abstract class Property<T extends Comparable<T>> {
    private final Class<T> type;
    private final String name;
    private Integer hashCodeCache;
    private final Codec<T> codec = Codec.STRING.comapFlatMap(value -> this.parse((String)value).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unable to read property: " + this + " with value: " + value))), this::name);
    private final Codec<Value<T>> valueCodec = this.codec.xmap(this::createValue, Value::getValue);

    protected Property(String name, Class<T> type) {
        this.type = type;
        this.name = name;
    }

    public Value<T> createValue(T value) {
        return new Value(this, (Comparable)value, null);
    }

    public Value<T> createValue(State<?, ?> arg) {
        return new Value(this, (Comparable)arg.get(this), null);
    }

    public Stream<Value<T>> stream() {
        return this.getValues().stream().map(this::createValue);
    }

    public Codec<Value<T>> getValueCodec() {
        return this.valueCodec;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return this.type;
    }

    public abstract Collection<T> getValues();

    public abstract String name(T var1);

    public abstract Optional<T> parse(String var1);

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.name).add("clazz", this.type).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Property) {
            Property lv = (Property)object;
            return this.type.equals(lv.type) && this.name.equals(lv.name);
        }
        return false;
    }

    public final int hashCode() {
        if (this.hashCodeCache == null) {
            this.hashCodeCache = this.computeHashCode();
        }
        return this.hashCodeCache;
    }

    public int computeHashCode() {
        return 31 * this.type.hashCode() + this.name.hashCode();
    }

    public static final class Value<T extends Comparable<T>> {
        private final Property<T> property;
        private final T value;

        private Value(Property<T> arg, T value) {
            if (!arg.getValues().contains(value)) {
                throw new IllegalArgumentException("Value " + value + " does not belong to property " + arg);
            }
            this.property = arg;
            this.value = value;
        }

        public Property<T> getProperty() {
            return this.property;
        }

        public T getValue() {
            return this.value;
        }

        public String toString() {
            return this.property.getName() + "=" + this.property.name(this.value);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof Value)) {
                return false;
            }
            Value lv = (Value)object;
            return this.property == lv.property && this.value.equals(lv.value);
        }

        public int hashCode() {
            int i = this.property.hashCode();
            i = 31 * i + this.value.hashCode();
            return i;
        }

        /* synthetic */ Value(Property arg, Comparable comparable, 1 arg2) {
            this(arg, comparable);
        }
    }
}

