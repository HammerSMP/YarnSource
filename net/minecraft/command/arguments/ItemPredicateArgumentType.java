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
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ItemPredicateArgumentType
implements ArgumentType<ItemPredicateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.item.tag.unknown", object));

    public static ItemPredicateArgumentType itemPredicate() {
        return new ItemPredicateArgumentType();
    }

    public ItemPredicateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        ItemStringReader lv = new ItemStringReader(stringReader, true).consume();
        if (lv.getItem() != null) {
            ItemPredicate lv2 = new ItemPredicate(lv.getItem(), lv.getTag());
            return commandContext -> lv2;
        }
        Identifier lv3 = lv.getId();
        return commandContext -> {
            Tag<Item> lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getTagManager().getItems().getTag(lv3);
            if (lv == null) {
                throw UNKNOWN_TAG_EXCEPTION.create((Object)lv3.toString());
            }
            return new TagPredicate(lv, lv.getTag());
        };
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return ((ItemPredicateArgument)context.getArgument(name, ItemPredicateArgument.class)).create(context);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ItemStringReader lv = new ItemStringReader(stringReader, true);
        try {
            lv.consume();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return lv.getSuggestions(builder, ItemTags.getTagGroup());
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static class TagPredicate
    implements Predicate<ItemStack> {
        private final Tag<Item> tag;
        @Nullable
        private final CompoundTag compound;

        public TagPredicate(Tag<Item> arg, @Nullable CompoundTag arg2) {
            this.tag = arg;
            this.compound = arg2;
        }

        @Override
        public boolean test(ItemStack arg) {
            return this.tag.contains(arg.getItem()) && NbtHelper.matches(this.compound, arg.getTag(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((ItemStack)context);
        }
    }

    static class ItemPredicate
    implements Predicate<ItemStack> {
        private final Item item;
        @Nullable
        private final CompoundTag compound;

        public ItemPredicate(Item arg, @Nullable CompoundTag arg2) {
            this.item = arg;
            this.compound = arg2;
        }

        @Override
        public boolean test(ItemStack arg) {
            return arg.getItem() == this.item && NbtHelper.matches(this.compound, arg.getTag(), true);
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((ItemStack)object);
        }
    }

    public static interface ItemPredicateArgument {
        public Predicate<ItemStack> create(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
    }
}

