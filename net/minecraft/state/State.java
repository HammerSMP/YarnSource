/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.state.property.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface State<C> {
    public static final Logger LOGGER = LogManager.getLogger();

    public <T extends Comparable<T>> T get(Property<T> var1);

    public <T extends Comparable<T>, V extends T> C with(Property<T> var1, V var2);

    public ImmutableMap<Property<?>, Comparable<?>> getEntries();

    public static <T extends Comparable<T>> String nameValue(Property<T> arg, Comparable<?> comparable) {
        return arg.name(comparable);
    }

    public static <S extends State<S>, T extends Comparable<T>> S tryRead(S arg, Property<T> arg2, String string, String string2, String string3) {
        Optional<T> optional = arg2.parse(string3);
        if (optional.isPresent()) {
            return (S)((State)arg.with(arg2, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for input: {}", (Object)string, (Object)string3, (Object)string2);
        return arg;
    }
}

