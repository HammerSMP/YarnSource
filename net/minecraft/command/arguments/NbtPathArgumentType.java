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

    public static NbtPath getNbtPath(CommandContext<ServerCommandSource> commandContext, String string) {
        return (NbtPath)commandContext.getArgument(string, NbtPath.class);
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

    private static PathNode parseNode(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        switch (stringReader.peek()) {
            case '{': {
                if (!bl) {
                    throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
                }
                CompoundTag lv = new StringNbtReader(stringReader).parseCompoundTag();
                return new FilteredRootNode(lv);
            }
            case '[': {
                stringReader.skip();
                char i = stringReader.peek();
                if (i == '{') {
                    CompoundTag lv2 = new StringNbtReader(stringReader).parseCompoundTag();
                    stringReader.expect(']');
                    return new FilteredListElementNode(lv2);
                }
                if (i == ']') {
                    stringReader.skip();
                    return AllListElementNode.INSTANCE;
                }
                int j = stringReader.readInt();
                stringReader.expect(']');
                return new IndexedListElementNode(j);
            }
            case '\"': {
                String string = stringReader.readString();
                return NbtPathArgumentType.readCompoundChildNode(stringReader, string);
            }
        }
        String string2 = NbtPathArgumentType.readName(stringReader);
        return NbtPathArgumentType.readCompoundChildNode(stringReader, string2);
    }

    private static PathNode readCompoundChildNode(StringReader stringReader, String string) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '{') {
            CompoundTag lv = new StringNbtReader(stringReader).parseCompoundTag();
            return new FilteredNamedNode(string, lv);
        }
        return new NamedNode(string);
    }

    private static String readName(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && NbtPathArgumentType.isNameCharacter(stringReader.peek())) {
            stringReader.skip();
        }
        if (stringReader.getCursor() == i) {
            throw INVALID_PATH_NODE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        return stringReader.getString().substring(i, stringReader.getCursor());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isNameCharacter(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> getPredicate(CompoundTag arg) {
        return arg2 -> NbtHelper.matches(arg, arg2, true);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class FilteredRootNode
    implements PathNode {
        private final Predicate<Tag> matcher;

        public FilteredRootNode(CompoundTag arg) {
            this.matcher = NbtPathArgumentType.getPredicate(arg);
        }

        @Override
        public void get(Tag arg, List<Tag> list) {
            if (arg instanceof CompoundTag && this.matcher.test(arg)) {
                list.add(arg);
            }
        }

        @Override
        public void getOrInit(Tag arg, Supplier<Tag> supplier, List<Tag> list) {
            this.get(arg, list);
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            return 0;
        }

        @Override
        public int clear(Tag arg) {
            return 0;
        }
    }

    static class FilteredNamedNode
    implements PathNode {
        private final String name;
        private final CompoundTag filter;
        private final Predicate<Tag> predicate;

        public FilteredNamedNode(String string, CompoundTag arg) {
            this.name = string;
            this.filter = arg;
            this.predicate = NbtPathArgumentType.getPredicate(arg);
        }

        @Override
        public void get(Tag arg, List<Tag> list) {
            Tag lv;
            if (arg instanceof CompoundTag && this.predicate.test(lv = ((CompoundTag)arg).get(this.name))) {
                list.add(lv);
            }
        }

        @Override
        public void getOrInit(Tag arg, Supplier<Tag> supplier, List<Tag> list) {
            if (arg instanceof CompoundTag) {
                CompoundTag lv = (CompoundTag)arg;
                Tag lv2 = lv.get(this.name);
                if (lv2 == null) {
                    lv2 = this.filter.copy();
                    lv.put(this.name, lv2);
                    list.add(lv2);
                } else if (this.predicate.test(lv2)) {
                    list.add(lv2);
                }
            }
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            Tag lv3;
            CompoundTag lv;
            Tag lv2;
            if (arg instanceof CompoundTag && this.predicate.test(lv2 = (lv = (CompoundTag)arg).get(this.name)) && !(lv3 = supplier.get()).equals(lv2)) {
                lv.put(this.name, lv3);
                return 1;
            }
            return 0;
        }

        @Override
        public int clear(Tag arg) {
            CompoundTag lv;
            Tag lv2;
            if (arg instanceof CompoundTag && this.predicate.test(lv2 = (lv = (CompoundTag)arg).get(this.name))) {
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
        public void get(Tag arg, List<Tag> list) {
            if (arg instanceof AbstractListTag) {
                list.addAll((AbstractListTag)arg);
            }
        }

        @Override
        public void getOrInit(Tag arg, Supplier<Tag> supplier, List<Tag> list) {
            if (arg instanceof AbstractListTag) {
                AbstractListTag lv = (AbstractListTag)arg;
                if (lv.isEmpty()) {
                    Tag lv2 = supplier.get();
                    if (lv.addTag(0, lv2)) {
                        list.add(lv2);
                    }
                } else {
                    list.addAll(lv);
                }
            }
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            if (arg instanceof AbstractListTag) {
                AbstractListTag lv = (AbstractListTag)arg;
                int i = lv.size();
                if (i == 0) {
                    lv.addTag(0, supplier.get());
                    return 1;
                }
                Tag lv2 = supplier.get();
                int j = i - (int)lv.stream().filter(lv2::equals).count();
                if (j == 0) {
                    return 0;
                }
                lv.clear();
                if (!lv.addTag(0, lv2)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    lv.addTag(k, supplier.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int clear(Tag arg) {
            AbstractListTag lv;
            int i;
            if (arg instanceof AbstractListTag && (i = (lv = (AbstractListTag)arg).size()) > 0) {
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

        public FilteredListElementNode(CompoundTag arg) {
            this.filter = arg;
            this.predicate = NbtPathArgumentType.getPredicate(arg);
        }

        @Override
        public void get(Tag arg, List<Tag> list) {
            if (arg instanceof ListTag) {
                ListTag lv = (ListTag)arg;
                lv.stream().filter(this.predicate).forEach(list::add);
            }
        }

        @Override
        public void getOrInit(Tag arg2, Supplier<Tag> supplier, List<Tag> list) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (arg2 instanceof ListTag) {
                ListTag lv = (ListTag)arg2;
                lv.stream().filter(this.predicate).forEach(arg -> {
                    list.add((Tag)arg);
                    mutableBoolean.setTrue();
                });
                if (mutableBoolean.isFalse()) {
                    CompoundTag lv2 = this.filter.copy();
                    lv.add(lv2);
                    list.add(lv2);
                }
            }
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            int i = 0;
            if (arg instanceof ListTag) {
                ListTag lv = (ListTag)arg;
                int j = lv.size();
                if (j == 0) {
                    lv.add(supplier.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        Tag lv3;
                        Tag lv2 = lv.get(k);
                        if (!this.predicate.test(lv2) || (lv3 = supplier.get()).equals(lv2) || !lv.setTag(k, lv3)) continue;
                        ++i;
                    }
                }
            }
            return i;
        }

        @Override
        public int clear(Tag arg) {
            int i = 0;
            if (arg instanceof ListTag) {
                ListTag lv = (ListTag)arg;
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

        public IndexedListElementNode(int i) {
            this.index = i;
        }

        @Override
        public void get(Tag arg, List<Tag> list) {
            if (arg instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)arg;
                int i = lv.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    list.add((Tag)lv.get(j));
                }
            }
        }

        @Override
        public void getOrInit(Tag arg, Supplier<Tag> supplier, List<Tag> list) {
            this.get(arg, list);
        }

        @Override
        public Tag init() {
            return new ListTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            if (arg instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)arg;
                int i = lv.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    Tag lv2 = (Tag)lv.get(j);
                    Tag lv3 = supplier.get();
                    if (!lv3.equals(lv2) && lv.setTag(j, lv3)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int clear(Tag arg) {
            if (arg instanceof AbstractListTag) {
                int j;
                AbstractListTag lv = (AbstractListTag)arg;
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

        public NamedNode(String string) {
            this.name = string;
        }

        @Override
        public void get(Tag arg, List<Tag> list) {
            Tag lv;
            if (arg instanceof CompoundTag && (lv = ((CompoundTag)arg).get(this.name)) != null) {
                list.add(lv);
            }
        }

        @Override
        public void getOrInit(Tag arg, Supplier<Tag> supplier, List<Tag> list) {
            if (arg instanceof CompoundTag) {
                Tag lv3;
                CompoundTag lv = (CompoundTag)arg;
                if (lv.contains(this.name)) {
                    Tag lv2 = lv.get(this.name);
                } else {
                    lv3 = supplier.get();
                    lv.put(this.name, lv3);
                }
                list.add(lv3);
            }
        }

        @Override
        public Tag init() {
            return new CompoundTag();
        }

        @Override
        public int set(Tag arg, Supplier<Tag> supplier) {
            if (arg instanceof CompoundTag) {
                Tag lv3;
                CompoundTag lv = (CompoundTag)arg;
                Tag lv2 = supplier.get();
                if (!lv2.equals(lv3 = lv.put(this.name, lv2))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int clear(Tag arg) {
            CompoundTag lv;
            if (arg instanceof CompoundTag && (lv = (CompoundTag)arg).contains(this.name)) {
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

        default public List<Tag> get(List<Tag> list) {
            return this.process(list, (arg_0, arg_1) -> this.get(arg_0, arg_1));
        }

        default public List<Tag> getOrInit(List<Tag> list2, Supplier<Tag> supplier) {
            return this.process(list2, (arg, list) -> this.getOrInit((Tag)arg, supplier, (List<Tag>)list));
        }

        default public List<Tag> process(List<Tag> list, BiConsumer<Tag, List<Tag>> biConsumer) {
            ArrayList list2 = Lists.newArrayList();
            for (Tag lv : list) {
                biConsumer.accept(lv, list2);
            }
            return list2;
        }
    }

    public static class NbtPath {
        private final String string;
        private final Object2IntMap<PathNode> nodeEndIndices;
        private final PathNode[] nodes;

        public NbtPath(String string, PathNode[] args, Object2IntMap<PathNode> object2IntMap) {
            this.string = string;
            this.nodes = args;
            this.nodeEndIndices = object2IntMap;
        }

        public List<Tag> get(Tag arg) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(arg);
            for (PathNode lv : this.nodes) {
                if (!(list = lv.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(lv);
            }
            return list;
        }

        public int count(Tag arg) {
            List<Tag> list = Collections.singletonList(arg);
            for (PathNode lv : this.nodes) {
                if (!(list = lv.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getTerminals(Tag arg) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(arg);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                PathNode lv = this.nodes[i];
                int j = i + 1;
                if (!(list = lv.getOrInit(list, this.nodes[j]::init)).isEmpty()) continue;
                throw this.createNothingFoundException(lv);
            }
            return list;
        }

        public List<Tag> getOrInit(Tag arg, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(arg);
            PathNode lv = this.nodes[this.nodes.length - 1];
            return lv.getOrInit(list, supplier);
        }

        private static int forEach(List<Tag> list, Function<Tag, Integer> function) {
            return list.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
        }

        public int put(Tag arg, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(arg);
            PathNode lv = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, arg2 -> lv.set((Tag)arg2, supplier));
        }

        public int remove(Tag arg) {
            List<Tag> list = Collections.singletonList(arg);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            PathNode lv = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, lv::clear);
        }

        private CommandSyntaxException createNothingFoundException(PathNode arg) {
            int i = this.nodeEndIndices.getInt((Object)arg);
            return NOTHING_FOUND_EXCEPTION.create((Object)this.string.substring(0, i));
        }

        public String toString() {
            return this.string;
        }
    }
}

