/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextArgumentType
implements ArgumentType<Text> {
    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.component.invalid", object));

    private TextArgumentType() {
    }

    public static Text getTextArgument(CommandContext<ServerCommandSource> commandContext, String string) {
        return (Text)commandContext.getArgument(string, Text.class);
    }

    public static TextArgumentType text() {
        return new TextArgumentType();
    }

    public Text parse(StringReader stringReader) throws CommandSyntaxException {
        try {
            MutableText lv = Text.Serializer.fromJson(stringReader);
            if (lv == null) {
                throw INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)"empty");
            }
            return lv;
        }
        catch (JsonParseException jsonParseException) {
            String string = jsonParseException.getCause() != null ? jsonParseException.getCause().getMessage() : jsonParseException.getMessage();
            throw INVALID_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

