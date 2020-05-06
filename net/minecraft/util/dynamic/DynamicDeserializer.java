/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface DynamicDeserializer<T> {
    public static final Logger LOGGER = LogManager.getLogger();

    public T deserialize(Dynamic<?> var1);

    public static <T, V, U extends DynamicDeserializer<V>> V deserialize(Dynamic<T> dynamic, Registry<U> arg, String string, V object) {
        V object3;
        DynamicDeserializer lv = (DynamicDeserializer)arg.get(new Identifier(dynamic.get(string).asString("")));
        if (lv != null) {
            T object2 = lv.deserialize(dynamic);
        } else {
            LOGGER.error("Unknown type {}, replacing with {}", (Object)dynamic.get(string).asString(""), object);
            object3 = object;
        }
        return object3;
    }
}

