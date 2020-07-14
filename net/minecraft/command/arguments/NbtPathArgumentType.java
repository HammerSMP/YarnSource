/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.nbtpath.nothing_found", object));

    public static NbtPathArgumentType nbtPath() {
        return new NbtPathArgumentType();
    }

    public static NbtPath getNbtPath(CommandContext<ServerCommandSource> context, String name) {
        return (NbtPath)context.getArgument(name, NbtPath.class);
    }

    public NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayList();
        int i = stringReader.getCursor();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        boolean bl = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            char c;
            PathNode lv = NbtPathArgumentType.parseNode(stringReader, bl);
            list.add(lv);
            object2IntMap.put((Object)lv, stringReader.getCursor() - i);
            bl = false;
            if (!stringReader.canRead() || (c = stringReader.peek()) == ' ' || c == '[' || c == '{') continue;
            stringReader.expect('.');
        }
        return new NbtPath(stringReader.getString().substring(i, stringReader.getCursor()), list.toArray(new PathNode[0]), (Object2IntMap<PathNode>)object2IntMap);
    }

    private static PathNode parseNode(StringReader reader, boolean root) throws CommandSyntaxException {
        switch (reader.peek()) {
            case '{': {
                if (!root) {
                    throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
                }
                CompoundTag lv = new StringNbtReader(reader).parseCompoundTag();
                return new FilteredRootNode(lv);
            }
            case '[': {
                reader.skip();
                char i = reader.peek();
                if (i == '{') {
                    CompoundTag lv2 = new StringNbtReader(reader).parseCompoundTag();
                    reader.expect(']');
                    return new FilteredListElementNode(lv2);
                }
                if (i == ']') {
                    reader.skip();
                    return AllListElementNode.INSTANCE;
                }
                int j = reader.readInt();
                reader.expect(']');
                return new IndexedListElementNode(j);
            }
            case '\"': {
                String string = reader.readString();
                return NbtPathArgumentType.readCompoundChildNode(reader, string);
            }
        }
        String string2 = NbtPathArgumentType.readName(reader);
        return NbtPathArgumentType.readCompoundChildNode(reader, string2);
    }

    private static PathNode readCompoundChildNode(StringReader reader, String name) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '{') {
            CompoundTag lv = new StringNbtReader(reader).parseCompoundTag();
            return new FilteredNamedNode(name, lv);
        }
        return new NamedNode(name);
    }

    private static String readName(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NbtPathArgumentType.isNameCharacter(reader.peek())) {
            reader.skip();
        }
        if (reader.getCursor() == i) {
            throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        return reader.getString().substring(i, reader.getCursor());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isNameCharacter(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> getPredicate(CompoundTag filter) {
        return arg2 -> NbtHelper.matches(filter, arg2, true);
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    static class FilteredRootNode
    implements PathNode {
        private final Predicate<Tag> matcher;

        public FilteredRootNode(CompoundTag filter) {
            this.matcher = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            if (current instanceof CompoundTag && this.matcher.test(current)) {
                results.add(current);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            this.get(current, results);
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            return 0;
        }

        @Override
        public int clear(Tag current) {
            return 0;
        }
    }

    static class FilteredNamedNode
    implements PathNode {
        private final String name;
        private final CompoundTag filter;
        private final Predicate<Tag> predicate;

        public FilteredNamedNode(String name, CompoundTag filter) {
            this.name = name;
            this.filter = filter;
            this.predicate = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            Tag lv;
            if (current instanceof CompoundTag && this.predicate.test(lv = ((CompoundTag)current).get(this.name))) {
                results.add(lv);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            if (current instanceof CompoundTag) {
                CompoundTag lv = (CompoundTag)current;
                Tag lv2 = lv.get(this.name);
                if (lv2 == null) {
                    lv2 = this.filter.copy();
                    lv.put(this.name, lv2);
                    results.add(lv2);
                } else if (this.predicate.test(lv2)) {
                    results.add(lv2);
                }
            }
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            Tag lv3;
            CompoundTag lv;
            Tag lv2;
            if (current instanceof CompoundTag && this.predicate.test(lv2 = (lv = (CompoundTag)current).get(this.name)) && !(lv3 = source.get()).equals(lv2)) {
                lv.put(this.name, lv3);
                return 1;
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            CompoundTag lv;
            Tag lv2;
            if (current instanceof CompoundTag && this.predicate.test(lv2 = (lv = (CompoundTag)current).get(this.name))) {
                lv.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class AllListElementNode
    implements PathNode {
        public static final AllListElementNode INSTANCE = new AllListElementNode();

        private AllListElementNode() {
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            if (current instanceof AbstractListTag) {
                results.addAll((AbstractListTag)current);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            if (current instanceof AbstractListTag) {
                AbstractListTag lv = (AbstractListTag)current;
                if (lv.isEmpty()) {
                    Tag lv2 = source.get();
                    if (lv.addTag(0, lv2)) {
                        results.add(lv2);
                    }
                } else {
                    results.addAll(lv);
                }
            }
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            if (current instanceof AbstractListTag) {
                AbstractListTag lv = (AbstractListTag)current;
                int i = lv.size();
                if (i == 0) {
                    lv.addTag(0, source.get());
                    return 1;
                }
                Tag lv2 = source.get();
                int j = i - (int)lv.stream().filter(lv2::equals).count();
                if (j == 0) {
                    return 0;
                }
                lv.clear();
                if (!lv.addTag(0, lv2)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    lv.addTag(k, source.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            AbstractListTag lv;
            int i;
            if (current instanceof AbstractListTag && (i = (lv = (AbstractListTag)current).size()) > 0) {
                lv.clear();
                return i;
            }
            return 0;
        }
    }

    static class FilteredListElementNode
    implements PathNode {
        private final CompoundTag filter;
        private final Predicate<Tag> predicate;

        public FilteredListElementNode(CompoundTag filter) {
            this.filter = filter;
            this.predicate = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            if (current instanceof ListTag) {
                ListTag lv = (ListTag)current;
                lv.stream().filter(this.predicate).forEach(results::add);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (current instanceof ListTag) {
                ListTag lv = (ListTag)current;
                lv.stream().filter(this.predicate).forEach(arg -> {
                    results.add((Tag)arg);
                    mutableBoolean.setTrue();
                });
                if (mutableBoolean.isFalse()) {
                    CompoundTag lv2 = this.filter.copy();
                    lv.add(lv2);
                    results.add(lv2);
                }
            }
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            int i = 0;
            if (current instanceof ListTag) {
                ListTag lv = (ListTag)current;
                int j = lv.size();
                if (j == 0) {
                    lv.add(source.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        Tag lv3;
                        Tag lv2 = lv.get(k);
                        if (!this.predicate.test(lv2) || (lv3 = source.get()).equals(lv2) || !lv.setTag(k, lv3)) continue;
                        ++i;
                    }
                }
            }
            return i;
        }

        @Override
        public int clear(Tag current) {
            int i = 0;
            if (current instanceof ListTag) {
                ListTag lv = (ListTag)current;
                for (int j = lv.size() - 1; j >= 0; --j) {
                    if (!this.predicate.test(lv.get(j))) continue;
                    lv.remove(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class IndexedListElementNode
    implements PathNode {
        private final int index;

        public IndexedListElementNode(int index) {
            this.index = index;
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            if (current instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)current;
                int i = lv.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    results.add((Tag)lv.get(j));
                }
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            this.get(current, results);
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            if (current instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)current;
                int i = lv.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    Tag lv2 = (Tag)lv.get(j);
                    Tag lv3 = source.get();
                    if (!lv3.equals(lv2) && lv.setTag(j, lv3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            if (current instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)current;
                int i = lv.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    lv.remove(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class NamedNode
    implements PathNode {
        private final String name;

        public NamedNode(String name) {
            this.name = name;
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            Tag lv;
            if (current instanceof CompoundTag && (lv = ((CompoundTag)current).get(this.name)) != null) {
                results.add(lv);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            if (current instanceof CompoundTag) {
                Tag lv3;
                CompoundTag lv = (CompoundTag)current;
                if (lv.contains(this.name)) {
                    Tag lv2 = lv.get(this.name);
                } else {
                    lv3 = source.get();
                    lv.put(this.name, lv3);
                }
                results.add(lv3);
            }
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag current, Supplier<Tag> source) {
            if (current instanceof CompoundTag) {
                Tag lv3;
                CompoundTag lv = (CompoundTag)current;
                Tag lv2 = source.get();
                if (!lv2.equals(lv3 = lv.put(this.name, lv2))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            CompoundTag lv;
            if (current instanceof CompoundTag && (lv = (CompoundTag)current).contains(this.name)) {
                lv.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static interface PathNode {
        public void get(Tag var1, List<Tag> var2);

        public void getOrInit(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag init();

        public int set(Tag var1, Supplier<Tag> var2);

        public int clear(Tag var1);

        default public List<Tag> get(List<Tag> tags) {
            return this.process(tags, (arg_0, arg_1) -> this.get(arg_0, arg_1));
        }

        default public List<Tag> getOrInit(List<Tag> tags, Supplier<Tag> supplier) {
            return this.process(tags, (current, results) -> this.getOrInit((Tag)current, supplier, (List<Tag>)results));
        }

        default public List<Tag> process(List<Tag> tags, BiConsumer<Tag, List<Tag>> action) {
            ArrayList list2 = Lists.newArrayList();
            for (Tag lv : tags) {
                action.accept(lv, list2);
            }
            return list2;
        }
    }

    public static class NbtPath {
        private final String string;
        private final Object2IntMap<PathNode> nodeEndIndices;
        private final PathNode[] nodes;

        public NbtPath(String string, PathNode[] nodes, Object2IntMap<PathNode> nodeEndIndices) {
            this.string = string;
            this.nodes = nodes;
            this.nodeEndIndices = nodeEndIndices;
        }

        public List<Tag> get(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (PathNode lv : this.nodes) {
                if (!(list = lv.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(lv);
            }
            return list;
        }

        public int count(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (PathNode lv : this.nodes) {
                if (!(list = lv.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getTerminals(Tag start) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(start);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                PathNode lv = this.nodes[i];
                int j = i + 1;
                if (!(list = lv.getOrInit(list, this.nodes[j]::init)).isEmpty()) continue;
                throw this.createNothingFoundException(lv);
            }
            return list;
        }

        public List<Tag> getOrInit(Tag tag, Supplier<Tag> source) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(tag);
            PathNode lv = this.nodes[this.nodes.length - 1];
            return lv.getOrInit(list, source);
        }

        private static int forEach(List<Tag> tags, Function<Tag, Integer> operation) {
            return tags.stream().map(operation).reduce(0, (integer, integer2) -> integer + integer2);
        }

        public int put(Tag tag, Supplier<Tag> source) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(tag);
            PathNode lv = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, arg2 -> lv.set((Tag)arg2, source));
        }

        public int remove(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            PathNode lv = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, lv::clear);
        }

        private CommandSyntaxException createNothingFoundException(PathNode node) {
            int i = this.nodeEndIndices.getInt((Object)node);
            return NOTHING_FOUND_EXCEPTION.create((Object)this.string.substring(0, i));
        }

        public String toString() {
            return this.string;
        }
    }
}

