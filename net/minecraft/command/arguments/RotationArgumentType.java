/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.arguments.CoordinateArgument;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class RotationArgumentType
implements ArgumentType<PosArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
    public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.rotation.incomplete"));

    public static RotationArgumentType rotation() {
        return new RotationArgumentType();
    }

    public static PosArgument getRotation(CommandContext<ServerCommandSource> context, String name) {
        return (PosArgument)context.getArgument(name, PosArgument.class);
    }

    public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        CoordinateArgument lv = CoordinateArgument.parse(stringReader, false);
        if (!stringReader.canRead() || stringReader.peek() != ' ') {
            stringReader.setCursor(i);
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        stringReader.skip();
        CoordinateArgument lv2 = CoordinateArgument.parse(stringReader, false);
        return new DefaultPosArgument(lv2, lv, new CoordinateArgument(true, 0.0));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

