/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class NbtCompoundTagArgumentType
implements ArgumentType<CompoundTag> {
    private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

    private NbtCompoundTagArgumentType() {
    }

    public static NbtCompoundTagArgumentType nbtCompound() {
        return new NbtCompoundTagArgumentType();
    }

    public static <S> CompoundTag getCompoundTag(CommandContext<S> commandContext, String string) {
        return (CompoundTag)commandContext.getArgument(string, CompoundTag.class);
    }

    public CompoundTag parse(StringReader stringReader) throws CommandSyntaxException {
        return new StringNbtReader(stringReader).parseCompoundTag();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

