/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.util.Unit;

public interface StringRenderable {
    public static final Optional<Unit> TERMINATE_VISIT = Optional.of(Unit.INSTANCE);
    public static final StringRenderable EMPTY = new StringRenderable(){

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

    public static StringRenderable plain(final String string) {
        return new StringRenderable(){

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
    public static StringRenderable styled(final String string, final Style arg) {
        return new StringRenderable(){

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
    public static StringRenderable concat(StringRenderable ... args) {
        return StringRenderable.concat((List<StringRenderable>)ImmutableList.copyOf((Object[])args));
    }

    @Environment(value=EnvType.CLIENT)
    public static StringRenderable concat(final List<StringRenderable> list) {
        return new StringRenderable(){

            @Override
            public <T> Optional<T> visit(Visitor<T> arg) {
                for (StringRenderable lv : list) {
                    Optional<T> optional = lv.visit(arg);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> arg, Style arg2) {
                for (StringRenderable lv : list) {
                    Optional<T> optional = lv.visit(arg, arg2);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }
        };
    }

    default public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            stringBuilder.append(string);
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    public static interface Visitor<T> {
        public Optional<T> accept(String var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface StyledVisitor<T> {
        public Optional<T> accept(Style var1, String var2);
    }
}

