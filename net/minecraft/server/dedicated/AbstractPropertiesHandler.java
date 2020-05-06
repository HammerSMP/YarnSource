/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPropertiesHandler<T extends AbstractPropertiesHandler<T>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Properties properties;

    public AbstractPropertiesHandler(Properties properties) {
        this.properties = properties;
    }

    public static Properties loadProperties(Path path) {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);){
            properties.load(inputStream);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to load properties from file: " + path);
        }
        return properties;
    }

    public void saveProperties(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path, new OpenOption[0]);){
            this.properties.store(outputStream, "Minecraft server properties");
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to store properties to file: " + path);
        }
    }

    private static <V extends Number> Function<String, V> wrapNumberParser(Function<String, V> function) {
        return string -> {
            try {
                return (Number)function.apply((String)string);
            }
            catch (NumberFormatException numberFormatException) {
                return null;
            }
        };
    }

    protected static <V> Function<String, V> combineParser(IntFunction<V> intFunction, Function<String, V> function) {
        return string -> {
            try {
                return intFunction.apply(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException) {
                return function.apply((String)string);
            }
        };
    }

    @Nullable
    private String getStringValue(String string) {
        return (String)this.properties.get(string);
    }

    @Nullable
    protected <V> V getDeprecated(String string, Function<String, V> function) {
        String string2 = this.getStringValue(string);
        if (string2 == null) {
            return null;
        }
        this.properties.remove(string);
        return function.apply(string2);
    }

    protected <V> V get(String string, Function<String, V> function, Function<V, String> function2, V object) {
        String string2 = this.getStringValue(string);
        Object object2 = MoreObjects.firstNonNull(string2 != null ? function.apply(string2) : null, object);
        this.properties.put(string, function2.apply(object2));
        return (V)object2;
    }

    protected <V> PropertyAccessor<V> accessor(String string, Function<String, V> function, Function<V, String> function2, V object) {
        String string2 = this.getStringValue(string);
        Object object2 = MoreObjects.firstNonNull(string2 != null ? function.apply(string2) : null, object);
        this.properties.put(string, function2.apply(object2));
        return new PropertyAccessor(string, object2, function2);
    }

    protected <V> V get(String string2, Function<String, V> function, UnaryOperator<V> unaryOperator, Function<V, String> function2, V object) {
        return (V)this.get(string2, string -> {
            Object object = function.apply((String)string);
            return object != null ? unaryOperator.apply(object) : null;
        }, function2, object);
    }

    protected <V> V get(String string, Function<String, V> function, V object) {
        return (V)this.get(string, function, Objects::toString, object);
    }

    protected <V> PropertyAccessor<V> accessor(String string, Function<String, V> function, V object) {
        return this.accessor(string, function, Objects::toString, object);
    }

    protected String getString(String string, String string2) {
        return this.get(string, Function.identity(), Function.identity(), string2);
    }

    @Nullable
    protected String getDeprecatedString(String string) {
        return (String)this.getDeprecated(string, Function.identity());
    }

    protected int getInt(String string, int i) {
        return this.get(string, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), i);
    }

    protected PropertyAccessor<Integer> intAccessor(String string, int i) {
        return this.accessor(string, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), i);
    }

    protected int transformedParseInt(String string, UnaryOperator<Integer> unaryOperator, int i) {
        return this.get(string, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), unaryOperator, Objects::toString, i);
    }

    protected long parseLong(String string, long l) {
        return this.get(string, AbstractPropertiesHandler.wrapNumberParser(Long::parseLong), l);
    }

    protected boolean parseBoolean(String string, boolean bl) {
        return this.get(string, Boolean::valueOf, bl);
    }

    protected PropertyAccessor<Boolean> booleanAccessor(String string, boolean bl) {
        return this.accessor(string, Boolean::valueOf, bl);
    }

    @Nullable
    protected Boolean getDeprecatedBoolean(String string) {
        return this.getDeprecated(string, Boolean::valueOf);
    }

    protected Properties copyProperties() {
        Properties properties = new Properties();
        properties.putAll((Map<?, ?>)this.properties);
        return properties;
    }

    protected abstract T create(Properties var1);

    public class PropertyAccessor<V>
    implements Supplier<V> {
        private final String key;
        private final V value;
        private final Function<V, String> stringifier;

        private PropertyAccessor(String string, V object, Function<V, String> function) {
            this.key = string;
            this.value = object;
            this.stringifier = function;
        }

        @Override
        public V get() {
            return this.value;
        }

        public T set(V object) {
            Properties properties = AbstractPropertiesHandler.this.copyProperties();
            properties.put(this.key, this.stringifier.apply(object));
            return AbstractPropertiesHandler.this.create(properties);
        }
    }
}

