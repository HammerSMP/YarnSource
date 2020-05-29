/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandSource;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockArgumentParser {
    public static final SimpleCommandExceptionType DISALLOWED_TAG_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType INVALID_BLOCK_ID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.block.id.invalid", object));
    public static final Dynamic2CommandExceptionType UNKNOWN_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.block.property.unknown", object, object2));
    public static final Dynamic2CommandExceptionType DUPLICATE_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.block.property.duplicate", object2, object));
    public static final Dynamic3CommandExceptionType INVALID_PROPERTY_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("argument.block.property.invalid", object, object3, object2));
    public static final Dynamic2CommandExceptionType EMPTY_PROPERTY_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.block.property.novalue", object, object2));
    public static final SimpleCommandExceptionType UNCLOSED_PROPERTIES_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.block.property.unclosed"));
    private static final BiFunction<SuggestionsBuilder, TagContainer<Block>, CompletableFuture<Suggestions>> SUGGEST_DEFAULT = (suggestionsBuilder, arg) -> suggestionsBuilder.buildFuture();
    private final StringReader reader;
    private final boolean allowTag;
    private final Map<Property<?>, Comparable<?>> blockProperties = Maps.newHashMap();
    private final Map<String, String> tagProperties = Maps.newHashMap();
    private Identifier blockId = new Identifier("");
    private StateManager<Block, BlockState> stateFactory;
    private BlockState blockState;
    @Nullable
    private CompoundTag data;
    private Identifier tagId = new Identifier("");
    private int cursorPos;
    private BiFunction<SuggestionsBuilder, TagContainer<Block>, CompletableFuture<Suggestions>> suggestions = SUGGEST_DEFAULT;

    public BlockArgumentParser(StringReader stringReader, boolean bl) {
        this.reader = stringReader;
        this.allowTag = bl;
    }

    public Map<Property<?>, Comparable<?>> getBlockProperties() {
        return this.blockProperties;
    }

    @Nullable
    public BlockState getBlockState() {
        return this.blockState;
    }

    @Nullable
    public CompoundTag getNbtData() {
        return this.data;
    }

    @Nullable
    public Identifier getTagId() {
        return this.tagId;
    }

    public BlockArgumentParser parse(boolean bl) throws CommandSyntaxException {
        this.suggestions = (arg_0, arg_1) -> this.suggestBlockOrTagId(arg_0, arg_1);
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.parseTagId();
            this.suggestions = (arg_0, arg_1) -> this.suggestSnbtOrTagProperties(arg_0, arg_1);
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.parseTagProperties();
                this.suggestions = (arg_0, arg_1) -> this.suggestSnbt(arg_0, arg_1);
            }
        } else {
            this.parseBlockId();
            this.suggestions = (arg_0, arg_1) -> this.suggestSnbtOrBlockProperties(arg_0, arg_1);
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.parseBlockProperties();
                this.suggestions = (arg_0, arg_1) -> this.suggestSnbt(arg_0, arg_1);
            }
        }
        if (bl && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = SUGGEST_DEFAULT;
            this.parseSnbt();
        }
        return this;
    }

    private CompletableFuture<Suggestions> suggestBlockPropertiesOrEnd(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestBlockProperties(suggestionsBuilder, arg);
    }

    private CompletableFuture<Suggestions> suggestTagPropertiesOrEnd(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        return this.suggestTagProperties(suggestionsBuilder, arg);
    }

    private CompletableFuture<Suggestions> suggestBlockProperties(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        for (Property<?> lv : this.blockState.getProperties()) {
            if (this.blockProperties.containsKey(lv) || !lv.getName().startsWith(string)) continue;
            suggestionsBuilder.suggest(lv.getName() + '=');
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTagProperties(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        Tag<Block> lv;
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        if (this.tagId != null && !this.tagId.getPath().isEmpty() && (lv = arg.get(this.tagId)) != null) {
            for (Block lv2 : lv.values()) {
                for (Property<?> lv3 : lv2.getStateManager().getProperties()) {
                    if (this.tagProperties.containsKey(lv3.getName()) || !lv3.getName().startsWith(string)) continue;
                    suggestionsBuilder.suggest(lv3.getName() + '=');
                }
            }
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSnbt(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty() && this.hasBlockEntity(arg)) {
            suggestionsBuilder.suggest(String.valueOf('{'));
        }
        return suggestionsBuilder.buildFuture();
    }

    private boolean hasBlockEntity(TagContainer<Block> arg) {
        Tag<Block> lv;
        if (this.blockState != null) {
            return this.blockState.getBlock().hasBlockEntity();
        }
        if (this.tagId != null && (lv = arg.get(this.tagId)) != null) {
            for (Block lv2 : lv.values()) {
                if (!lv2.hasBlockEntity()) continue;
                return true;
            }
        }
        return false;
    }

    private CompletableFuture<Suggestions> suggestEqualsCharacter(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('='));
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }
        if (suggestionsBuilder.getRemaining().isEmpty() && this.blockProperties.size() < this.blockState.getProperties().size()) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        return suggestionsBuilder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder suggestPropertyValues(SuggestionsBuilder suggestionsBuilder, Property<T> arg) {
        for (Comparable comparable : arg.getValues()) {
            if (comparable instanceof Integer) {
                suggestionsBuilder.suggest(((Integer)comparable).intValue());
                continue;
            }
            suggestionsBuilder.suggest(arg.name(comparable));
        }
        return suggestionsBuilder;
    }

    private CompletableFuture<Suggestions> suggestTagPropertyValues(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg, String string) {
        Tag<Block> lv;
        boolean bl = false;
        if (this.tagId != null && !this.tagId.getPath().isEmpty() && (lv = arg.get(this.tagId)) != null) {
            block0: for (Block lv2 : lv.values()) {
                Property<?> lv3 = lv2.getStateManager().getProperty(string);
                if (lv3 != null) {
                    BlockArgumentParser.suggestPropertyValues(suggestionsBuilder, lv3);
                }
                if (bl) continue;
                for (Property<?> lv4 : lv2.getStateManager().getProperties()) {
                    if (this.tagProperties.containsKey(lv4.getName())) continue;
                    bl = true;
                    continue block0;
                }
            }
        }
        if (bl) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }
        suggestionsBuilder.suggest(String.valueOf(']'));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSnbtOrTagProperties(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        Tag<Block> lv;
        if (suggestionsBuilder.getRemaining().isEmpty() && (lv = arg.get(this.tagId)) != null) {
            Block lv2;
            boolean bl = false;
            boolean bl2 = false;
            Iterator<Block> iterator = lv.values().iterator();
            while (!(!iterator.hasNext() || (bl |= !(lv2 = iterator.next()).getStateManager().getProperties().isEmpty()) && (bl2 |= lv2.hasBlockEntity()))) {
            }
            if (bl) {
                suggestionsBuilder.suggest(String.valueOf('['));
            }
            if (bl2) {
                suggestionsBuilder.suggest(String.valueOf('{'));
            }
        }
        return this.suggestIdentifiers(suggestionsBuilder, arg);
    }

    private CompletableFuture<Suggestions> suggestSnbtOrBlockProperties(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            if (!this.blockState.getBlock().getStateManager().getProperties().isEmpty()) {
                suggestionsBuilder.suggest(String.valueOf('['));
            }
            if (this.blockState.getBlock().hasBlockEntity()) {
                suggestionsBuilder.suggest(String.valueOf('{'));
            }
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestIdentifiers(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        return CommandSource.suggestIdentifiers(arg.getKeys(), suggestionsBuilder.createOffset(this.cursorPos).add(suggestionsBuilder));
    }

    private CompletableFuture<Suggestions> suggestBlockOrTagId(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        if (this.allowTag) {
            CommandSource.suggestIdentifiers(arg.getKeys(), suggestionsBuilder, String.valueOf('#'));
        }
        CommandSource.suggestIdentifiers(Registry.BLOCK.getIds(), suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    public void parseBlockId() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        this.blockId = Identifier.fromCommandInput(this.reader);
        Block lv = (Block)Registry.BLOCK.getOrEmpty(this.blockId).orElseThrow(() -> {
            this.reader.setCursor(i);
            return INVALID_BLOCK_ID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString());
        });
        this.stateFactory = lv.getStateManager();
        this.blockState = lv.getDefaultState();
    }

    public void parseTagId() throws CommandSyntaxException {
        if (!this.allowTag) {
            throw DISALLOWED_TAG_EXCEPTION.create();
        }
        this.suggestions = (arg_0, arg_1) -> this.suggestIdentifiers(arg_0, arg_1);
        this.reader.expect('#');
        this.cursorPos = this.reader.getCursor();
        this.tagId = Identifier.fromCommandInput(this.reader);
    }

    public void parseBlockProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = (arg_0, arg_1) -> this.suggestBlockPropertiesOrEnd(arg_0, arg_1);
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String string = this.reader.readString();
            Property<?> lv = this.stateFactory.getProperty(string);
            if (lv == null) {
                this.reader.setCursor(i);
                throw UNKNOWN_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)string);
            }
            if (this.blockProperties.containsKey(lv)) {
                this.reader.setCursor(i);
                throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)string);
            }
            this.reader.skipWhitespace();
            this.suggestions = (arg_0, arg_1) -> this.suggestEqualsCharacter(arg_0, arg_1);
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                throw EMPTY_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)string);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (suggestionsBuilder, arg2) -> BlockArgumentParser.suggestPropertyValues(suggestionsBuilder, lv).buildFuture();
            int j = this.reader.getCursor();
            this.parsePropertyValue(lv, this.reader.readString(), j);
            this.suggestions = (arg_0, arg_1) -> this.suggestCommaOrEnd(arg_0, arg_1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) continue;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = (arg_0, arg_1) -> this.suggestBlockProperties(arg_0, arg_1);
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
    }

    public void parseTagProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = (arg_0, arg_1) -> this.suggestTagPropertiesOrEnd(arg_0, arg_1);
        int i = -1;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int j = this.reader.getCursor();
            String string = this.reader.readString();
            if (this.tagProperties.containsKey(string)) {
                this.reader.setCursor(j);
                throw DUPLICATE_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)string);
            }
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor(j);
                throw EMPTY_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)string);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (suggestionsBuilder, arg) -> this.suggestTagPropertyValues((SuggestionsBuilder)suggestionsBuilder, (TagContainer<Block>)arg, string);
            i = this.reader.getCursor();
            String string2 = this.reader.readString();
            this.tagProperties.put(string, string2);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) continue;
            i = -1;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = (arg_0, arg_1) -> this.suggestTagProperties(arg_0, arg_1);
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            if (i >= 0) {
                this.reader.setCursor(i);
            }
            throw UNCLOSED_PROPERTIES_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
    }

    public void parseSnbt() throws CommandSyntaxException {
        this.data = new StringNbtReader(this.reader).parseCompoundTag();
    }

    private <T extends Comparable<T>> void parsePropertyValue(Property<T> arg, String string, int i) throws CommandSyntaxException {
        Optional<T> optional = arg.parse(string);
        if (!optional.isPresent()) {
            this.reader.setCursor(i);
            throw INVALID_PROPERTY_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)this.blockId.toString(), (Object)arg.getName(), (Object)string);
        }
        this.blockState = (BlockState)this.blockState.with(arg, (Comparable)optional.get());
        this.blockProperties.put(arg, (Comparable<?>)optional.get());
    }

    public static String stringifyBlockState(BlockState arg) {
        StringBuilder stringBuilder = new StringBuilder(Registry.BLOCK.getId(arg.getBlock()).toString());
        if (!arg.getProperties().isEmpty()) {
            stringBuilder.append('[');
            boolean bl = false;
            for (Map.Entry entry : arg.getEntries().entrySet()) {
                if (bl) {
                    stringBuilder.append(',');
                }
                BlockArgumentParser.stringifyProperty(stringBuilder, (Property)entry.getKey(), (Comparable)entry.getValue());
                bl = true;
            }
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }

    private static <T extends Comparable<T>> void stringifyProperty(StringBuilder stringBuilder, Property<T> arg, Comparable<?> comparable) {
        stringBuilder.append(arg.getName());
        stringBuilder.append('=');
        stringBuilder.append(arg.name(comparable));
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder suggestionsBuilder, TagContainer<Block> arg) {
        return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), arg);
    }

    public Map<String, String> getProperties() {
        return this.tagProperties;
    }
}

