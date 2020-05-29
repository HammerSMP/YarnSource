/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.util.Unit;

public interface class_5348 {
    public static final Optional<Unit> field_25309 = Optional.of(Unit.INSTANCE);
    public static final class_5348 field_25310 = new class_5348(){

        @Override
        public <T> Optional<T> visit(Visitor<T> arg) {
            return Optional.empty();
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public <T> Optional<T> visit(StyledVisitor<T> arg, Style arg2) {
            return Optional.empty();
        }
    };

    public <T> Optional<T> visit(Visitor<T> var1);

    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visit(StyledVisitor<T> var1, Style var2);

    public static class_5348 method_29430(final String string) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> arg) {
                return arg.accept(string);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public <T> Optional<T> visit(StyledVisitor<T> arg, Style arg2) {
                return arg.accept(arg2, string);
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29431(final String string, final Style arg) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> arg2) {
                return arg2.accept(string);
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> arg3, Style arg2) {
                return arg3.accept(arg.withParent(arg2), string);
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29433(class_5348 ... args) {
        return class_5348.method_29432((List<class_5348>)ImmutableList.copyOf((Object[])args));
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29432(final List<class_5348> list) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> arg) {
                for (class_5348 lv : list) {
                    Optional<T> optional = lv.visit(arg);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> arg, Style arg2) {
                for (class_5348 lv : list) {
                    Optional<T> optional = lv.visit(arg, arg2);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }
        };
    }

    public static interface Visitor<T> {
        public Optional<T> accept(String var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface StyledVisitor<T> {
        public Optional<T> accept(Style var1, String var2);
    }
}

