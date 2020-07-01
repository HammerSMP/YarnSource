/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.class_5415;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BlockPredicateArgumentType
implements ArgumentType<BlockPredicate> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "#stone", "#stone[foo=bar]{baz=nbt}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.block.tag.unknown", object));

    public static BlockPredicateArgumentType blockPredicate() {
        return new BlockPredicateArgumentType();
    }

    public BlockPredicate parse(StringReader stringReader) throws CommandSyntaxException {
        BlockArgumentParser lv = new BlockArgumentParser(stringReader, true).parse(true);
        if (lv.getBlockState() != null) {
            StatePredicate lv2 = new StatePredicate(lv.getBlockState(), lv.getBlockProperties().keySet(), lv.getNbtData());
            return arg2 -> lv2;
        }
        Identifier lv3 = lv.getTagId();
        return arg3 -> {
            Tag<Block> lv = arg3.method_30215().method_30210(lv3);
            if (lv == null) {
                throw UNKNOWN_TAG_EXCEPTION.create((Object)lv3.toString());
            }
            return new TagPredicate(lv, lv.getProperties(), lv.getNbtData());
        };
    }

    public static Predicate<CachedBlockPosition> getBlockPredicate(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return ((BlockPredicate)commandContext.getArgument(string, BlockPredicate.class)).create(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager());
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
        stringReader.setCursor(suggestionsBuilder.getStart());
        BlockArgumentParser lv = new BlockArgumentParser(stringReader, true);
        try {
            lv.parse(true);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return lv.getSuggestions(suggestionsBuilder, BlockTags.getContainer());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class TagPredicate
    implements Predicate<CachedBlockPosition> {
        private final Tag<Block> tag;
        @Nullable
        private final CompoundTag nbt;
        private final Map<String, String> properties;

        private TagPredicate(Tag<Block> arg, Map<String, String> map, @Nullable CompoundTag arg2) {
            this.tag = arg;
            this.properties = map;
            this.nbt = arg2;
        }

        @Override
        public boolean test(CachedBlockPosition arg) {
            BlockState lv = arg.getBlockState();
            if (!lv.isIn(this.tag)) {
                return false;
            }
            for (Map.Entry<String, String> entry : this.properties.entrySet()) {
                Property<?> lv2 = lv.getBlock().getStateManager().getProperty(entry.getKey());
                if (lv2 == null) {
                    return false;
                }
                Comparable comparable = lv2.parse(entry.getValue()).orElse(null);
                if (comparable == null) {
                    return false;
                }
                if (lv.get(lv2) == comparable) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity lv3 = arg.getBlockEntity();
                return lv3 != null && NbtHelper.matches(this.nbt, lv3.toTag(new CompoundTag()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((CachedBlockPosition)object);
        }
    }

    static class StatePredicate
    implements Predicate<CachedBlockPosition> {
        private final BlockState state;
        private final Set<Property<?>> properties;
        @Nullable
        private final CompoundTag nbt;

        public StatePredicate(BlockState arg, Set<Property<?>> set, @Nullable CompoundTag arg2) {
            this.state = arg;
            this.properties = set;
            this.nbt = arg2;
        }

        @Override
        public boolean test(CachedBlockPosition arg) {
            BlockState lv = arg.getBlockState();
            if (!lv.isOf(this.state.getBlock())) {
                return false;
            }
            for (Property<?> lv2 : this.properties) {
                if (lv.get(lv2) == this.state.get(lv2)) continue;
                return false;
            }
            if (this.nbt != null) {
                BlockEntity lv3 = arg.getBlockEntity();
                return lv3 != null && NbtHelper.matches(this.nbt, lv3.toTag(new CompoundTag()), true);
            }
            return true;
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((CachedBlockPosition)object);
        }
    }

    public static interface BlockPredicate {
        public Predicate<CachedBlockPosition> create(class_5415 var1) throws CommandSyntaxException;
    }
}

