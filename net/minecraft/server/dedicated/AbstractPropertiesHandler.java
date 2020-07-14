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

    private static <V extends Number> Function<String, V> wrapNumberParser(Function<String, V> parser) {
        return string -> {
            try {
                return (Number)parser.apply((String)string);
            }
            catch (NumberFormatException numberFormatException) {
                return null;
            }
        };
    }

    protected static <V> Function<String, V> combineParser(IntFunction<V> intParser, Function<String, V> fallbackParser) {
        return string -> {
            try {
                return intParser.apply(Integer.parseInt(string));
            }
            catch (NumberFormatException numberFormatException) {
                return fallbackParser.apply((String)string);
            }
        };
    }

    @Nullable
    private String getStringValue(String key) {
        return (String)this.properties.get(key);
    }

    @Nullable
    protected <V> V getDeprecated(String key, Function<String, V> stringifier) {
        String string2 = this.getStringValue(key);
        if (string2 == null) {
            return null;
        }
        this.properties.remove(key);
        return stringifier.apply(string2);
    }

    protected <V> V get(String key, Function<String, V> parser, Function<V, String> stringifier, V fallback) {
        String string2 = this.getStringValue(key);
        Object object2 = MoreObjects.firstNonNull(string2 != null ? parser.apply(string2) : null, fallback);
        this.properties.put(key, stringifier.apply(object2));
        return (V)object2;
    }

    protected <V> PropertyAccessor<V> accessor(String key, Function<String, V> parser, Function<V, String> stringifier, V fallback) {
        String string2 = this.getStringValue(key);
        Object object2 = MoreObjects.firstNonNull(string2 != null ? parser.apply(string2) : null, fallback);
        this.properties.put(key, stringifier.apply(object2));
        return new PropertyAccessor(key, object2, stringifier);
    }

    protected <V> V get(String key, Function<String, V> parser, UnaryOperator<V> parsedTransformer, Function<V, String> stringifier, V fallback) {
        return (V)this.get(key, string -> {
            Object object = parser.apply((String)string);
            return object != null ? parsedTransformer.apply(object) : null;
        }, stringifier, fallback);
    }

    protected <V> V get(String key, Function<String, V> parser, V fallback) {
        return (V)this.get(key, parser, Objects::toString, fallback);
    }

    protected <V> PropertyAccessor<V> accessor(String key, Function<String, V> parser, V fallback) {
        return this.accessor(key, parser, Objects::toString, fallback);
    }

    protected String getString(String key, String fallback) {
        return this.get(key, Function.identity(), Function.identity(), fallback);
    }

    @Nullable
    protected String getDeprecatedString(String key) {
        return (String)this.getDeprecated(key, Function.identity());
    }

    protected int getInt(String key, int fallback) {
        return this.get(key, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), fallback);
    }

    protected PropertyAccessor<Integer> intAccessor(String key, int fallback) {
        return this.accessor(key, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), fallback);
    }

    protected int transformedParseInt(String key, UnaryOperator<Integer> transformer, int fallback) {
        return this.get(key, AbstractPropertiesHandler.wrapNumberParser(Integer::parseInt), transformer, Objects::toString, fallback);
    }

    protected long parseLong(String key, long fallback) {
        return this.get(key, AbstractPropertiesHandler.wrapNumberParser(Long::parseLong), fallback);
    }

    protected boolean parseBoolean(String key, boolean fallback) {
        return this.get(key, Boolean::valueOf, fallback);
    }

    protected PropertyAccessor<Boolean> booleanAccessor(String key, boolean fallback) {
        return this.accessor(key, Boolean::valueOf, fallback);
    }

    @Nullable
    protected Boolean getDeprecatedBoolean(String key) {
        return this.getDeprecated(key, Boolean::valueOf);
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

        private PropertyAccessor(String key, V value, Function<V, String> stringifier) {
            this.key = key;
            this.value = value;
            this.stringifier = stringifier;
        }

        @Override
        public V get() {
            return this.value;
        }

        public T set(V value) {
            Properties properties = AbstractPropertiesHandler.this.copyProperties();
            properties.put(this.key, this.stringifier.apply(value));
            return AbstractPropertiesHandler.this.create(properties);
        }
    }
}

