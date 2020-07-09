/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.server.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.class_5455;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface CommandSource {
    public Collection<String> getPlayerNames();

    default public Collection<String> getEntitySuggestions() {
        return Collections.emptyList();
    }

    public Collection<String> getTeamNames();

    public Collection<Identifier> getSoundIds();

    public Stream<Identifier> getRecipeIds();

    public CompletableFuture<Suggestions> getCompletions(CommandContext<CommandSource> var1, SuggestionsBuilder var2);

    default public Collection<RelativePosition> getBlockPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    default public Collection<RelativePosition> getPositionSuggestions() {
        return Collections.singleton(RelativePosition.ZERO_WORLD);
    }

    public Set<RegistryKey<World>> getWorldKeys();

    public class_5455 method_30497();

    public boolean hasPermissionLevel(int var1);

    public static <T> void forEachMatching(Iterable<T> iterable, String string, Function<T, Identifier> function, Consumer<T> consumer) {
        boolean bl = string.indexOf(58) > -1;
        for (T object : iterable) {
            Identifier lv = function.apply(object);
            if (bl) {
                String string2 = lv.toString();
                if (!CommandSource.method_27136(string, string2)) continue;
                consumer.accept(object);
                continue;
            }
            if (!CommandSource.method_27136(string, lv.getNamespace()) && (!lv.getNamespace().equals("minecraft") || !CommandSource.method_27136(string, lv.getPath()))) continue;
            consumer.accept(object);
        }
    }

    public static <T> void forEachMatching(Iterable<T> iterable, String string, String string2, Function<T, Identifier> function, Consumer<T> consumer) {
        if (string.isEmpty()) {
            iterable.forEach(consumer);
        } else {
            String string3 = Strings.commonPrefix((CharSequence)string, (CharSequence)string2);
            if (!string3.isEmpty()) {
                String string4 = string.substring(string3.length());
                CommandSource.forEachMatching(iterable, string4, function, consumer);
            }
        }
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder, String string) {
        String string2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string2, string, arg -> arg, arg -> suggestionsBuilder.suggest(string + arg));
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<Identifier> iterable, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string, arg -> arg, arg -> suggestionsBuilder.suggest(arg.toString()));
        return suggestionsBuilder.buildFuture();
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Iterable<T> iterable, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(iterable, string, function, object -> suggestionsBuilder.suggest(((Identifier)function.apply(object)).toString(), (Message)function2.apply(object)));
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestIdentifiers(Stream<Identifier> stream, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestIdentifiers(stream::iterator, suggestionsBuilder);
    }

    public static <T> CompletableFuture<Suggestions> suggestFromIdentifier(Stream<T> stream, SuggestionsBuilder suggestionsBuilder, Function<T, Identifier> function, Function<T, Message> function2) {
        return CommandSource.suggestFromIdentifier(stream::iterator, suggestionsBuilder, function, function2);
    }

    public static CompletableFuture<Suggestions> suggestPositions(String string, Collection<RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate) {
        ArrayList list;
        block4: {
            String[] strings;
            block5: {
                block3: {
                    list = Lists.newArrayList();
                    if (!Strings.isNullOrEmpty((String)string)) break block3;
                    for (RelativePosition lv : collection) {
                        String string2 = lv.x + " " + lv.y + " " + lv.z;
                        if (!predicate.test(string2)) continue;
                        list.add(lv.x);
                        list.add(lv.x + " " + lv.y);
                        list.add(string2);
                    }
                    break block4;
                }
                strings = string.split(" ");
                if (strings.length != 1) break block5;
                for (RelativePosition lv2 : collection) {
                    String string3 = strings[0] + " " + lv2.y + " " + lv2.z;
                    if (!predicate.test(string3)) continue;
                    list.add(strings[0] + " " + lv2.y);
                    list.add(string3);
                }
                break block4;
            }
            if (strings.length != 2) break block4;
            for (RelativePosition lv3 : collection) {
                String string4 = strings[0] + " " + strings[1] + " " + lv3.z;
                if (!predicate.test(string4)) continue;
                list.add(string4);
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestColumnPositions(String string, Collection<RelativePosition> collection, SuggestionsBuilder suggestionsBuilder, Predicate<String> predicate) {
        ArrayList list;
        block3: {
            block2: {
                list = Lists.newArrayList();
                if (!Strings.isNullOrEmpty((String)string)) break block2;
                for (RelativePosition lv : collection) {
                    String string2 = lv.x + " " + lv.z;
                    if (!predicate.test(string2)) continue;
                    list.add(lv.x);
                    list.add(string2);
                }
                break block3;
            }
            String[] strings = string.split(" ");
            if (strings.length != 1) break block3;
            for (RelativePosition lv2 : collection) {
                String string3 = strings[0] + " " + lv2.z;
                if (!predicate.test(string3)) continue;
                list.add(string3);
            }
        }
        return CommandSource.suggestMatching(list, suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestMatching(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : iterable) {
            if (!CommandSource.method_27136(string, string2.toLowerCase(Locale.ROOT))) continue;
            suggestionsBuilder.suggest(string2);
        }
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(Stream<String> stream, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        stream.filter(string2 -> CommandSource.method_27136(string, string2.toLowerCase(Locale.ROOT))).forEach(((SuggestionsBuilder)suggestionsBuilder)::suggest);
        return suggestionsBuilder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggestMatching(String[] strings, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (String string2 : strings) {
            if (!CommandSource.method_27136(string, string2.toLowerCase(Locale.ROOT))) continue;
            suggestionsBuilder.suggest(string2);
        }
        return suggestionsBuilder.buildFuture();
    }

    public static boolean method_27136(String string, String string2) {
        int i = 0;
        while (!string2.startsWith(string, i)) {
            if ((i = string2.indexOf(95, i)) < 0) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static class RelativePosition {
        public static final RelativePosition ZERO_LOCAL = new RelativePosition("^", "^", "^");
        public static final RelativePosition ZERO_WORLD = new RelativePosition("~", "~", "~");
        public final String x;
        public final String y;
        public final String z;

        public RelativePosition(String string, String string2, String string3) {
            this.x = string;
            this.y = string2;
            this.z = string3;
        }
    }
}

