/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_5321<T> {
    private static final Map<String, class_5321<?>> field_25136 = Collections.synchronizedMap(Maps.newIdentityHashMap());
    private final Identifier field_25137;
    private final Identifier field_25138;

    public static <T> class_5321<T> method_29179(class_5321<Registry<T>> arg, Identifier arg2) {
        return class_5321.method_29181(arg.field_25138, arg2);
    }

    public static <T> class_5321<Registry<T>> method_29180(Identifier arg) {
        return class_5321.method_29181(Registry.field_25100, arg);
    }

    private static <T> class_5321<T> method_29181(Identifier arg, Identifier arg2) {
        String string2 = (arg + ":" + arg2).intern();
        return field_25136.computeIfAbsent(string2, string -> new class_5321(arg, arg2));
    }

    private class_5321(Identifier arg, Identifier arg2) {
        this.field_25137 = arg;
        this.field_25138 = arg2;
    }

    public String toString() {
        return "ResourceKey[" + this.field_25137 + " / " + this.field_25138 + ']';
    }

    public Identifier method_29177() {
        return this.field_25138;
    }

    public static <T> Function<Identifier, class_5321<T>> method_29178(class_5321<Registry<T>> arg) {
        return arg2 -> class_5321.method_29179(arg, arg2);
    }
}

