/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5414;
import net.minecraft.class_5415;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

public class class_5413 {
    private static final Map<Identifier, GlobalTagAccessor<?>> field_25741 = Maps.newHashMap();

    public static <T> GlobalTagAccessor<T> method_30201(Identifier arg, Function<class_5415, class_5414<T>> function) {
        GlobalTagAccessor<T> lv = new GlobalTagAccessor<T>(function);
        GlobalTagAccessor<T> lv2 = field_25741.putIfAbsent(arg, lv);
        if (lv2 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + arg);
        }
        return lv;
    }

    public static void method_30198(class_5415 arg) {
        field_25741.values().forEach(arg2 -> arg2.setContainer(arg));
    }

    @Environment(value=EnvType.CLIENT)
    public static void method_30196() {
        field_25741.values().forEach(GlobalTagAccessor::markReady);
    }

    public static Multimap<Identifier, Identifier> method_30203(class_5415 arg) {
        HashMultimap multimap = HashMultimap.create();
        field_25741.forEach((arg_0, arg_1) -> class_5413.method_30200((Multimap)multimap, arg, arg_0, arg_1));
        return multimap;
    }

    public static void method_30202() {
        GlobalTagAccessor[] lvs = new GlobalTagAccessor[]{BlockTags.ACCESSOR, ItemTags.ACCESSOR, FluidTags.ACCESSOR, EntityTypeTags.ACCESSOR};
        boolean bl = Stream.of(lvs).anyMatch(arg -> !field_25741.containsValue(arg));
        if (bl) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }

    private static /* synthetic */ void method_30200(Multimap multimap, class_5415 arg, Identifier arg2, GlobalTagAccessor arg3) {
        multimap.putAll((Object)arg2, arg3.method_29224(arg));
    }
}

