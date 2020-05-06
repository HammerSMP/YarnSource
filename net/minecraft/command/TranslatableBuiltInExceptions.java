/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableText;

public class TranslatableBuiltInExceptions
implements BuiltInExceptionProvider {
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_LOW = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.double.low", object2, object));
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_HIGH = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.double.big", object2, object));
    private static final Dynamic2CommandExceptionType FLOAT_TOO_LOW = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.float.low", object2, object));
    private static final Dynamic2CommandExceptionType FLOAT_TOO_HIGH = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.float.big", object2, object));
    private static final Dynamic2CommandExceptionType INTEGER_TOO_LOW = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.integer.low", object2, object));
    private static final Dynamic2CommandExceptionType INTEGER_TOO_HIGH = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.integer.big", object2, object));
    private static final Dynamic2CommandExceptionType LONG_TOO_LOW = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.long.low", object2, object));
    private static final Dynamic2CommandExceptionType LONG_TOO_HIGH = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("argument.long.big", object2, object));
    private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType(object -> new TranslatableText("argument.literal.incorrect", object));
    private static final SimpleCommandExceptionType READER_EXPECTED_START_QUOTE = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.quote.expected.start"));
    private static final SimpleCommandExceptionType READER_EXPECTED_END_QUOTE = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.quote.expected.end"));
    private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.quote.escape", object));
    private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.bool.invalid", object));
    private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.int.invalid", object));
    private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.int.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.long.invalid", object));
    private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.long.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.double.invalid", object));
    private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.double.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.float.invalid", object));
    private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.float.expected"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType((Message)new TranslatableText("parsing.bool.expected"));
    private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType(object -> new TranslatableText("parsing.expected", object));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType((Message)new TranslatableText("command.unknown.command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType((Message)new TranslatableText("command.unknown.argument"));
    private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType((Message)new TranslatableText("command.expected.separator"));
    private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("command.exception", object));

    public Dynamic2CommandExceptionType doubleTooLow() {
        return DOUBLE_TOO_LOW;
    }

    public Dynamic2CommandExceptionType doubleTooHigh() {
        return DOUBLE_TOO_HIGH;
    }

    public Dynamic2CommandExceptionType floatTooLow() {
        return FLOAT_TOO_LOW;
    }

    public Dynamic2CommandExceptionType floatTooHigh() {
        return FLOAT_TOO_HIGH;
    }

    public Dynamic2CommandExceptionType integerTooLow() {
        return INTEGER_TOO_LOW;
    }

    public Dynamic2CommandExceptionType integerTooHigh() {
        return INTEGER_TOO_HIGH;
    }

    public Dynamic2CommandExceptionType longTooLow() {
        return LONG_TOO_LOW;
    }

    public Dynamic2CommandExceptionType longTooHigh() {
        return LONG_TOO_HIGH;
    }

    public DynamicCommandExceptionType literalIncorrect() {
        return LITERAL_INCORRECT;
    }

    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return READER_EXPECTED_START_QUOTE;
    }

    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return READER_EXPECTED_END_QUOTE;
    }

    public DynamicCommandExceptionType readerInvalidEscape() {
        return READER_INVALID_ESCAPE;
    }

    public DynamicCommandExceptionType readerInvalidBool() {
        return READER_INVALID_BOOL;
    }

    public DynamicCommandExceptionType readerInvalidInt() {
        return READER_INVALID_INT;
    }

    public SimpleCommandExceptionType readerExpectedInt() {
        return READER_EXPECTED_INT;
    }

    public DynamicCommandExceptionType readerInvalidLong() {
        return READER_INVALID_LONG;
    }

    public SimpleCommandExceptionType readerExpectedLong() {
        return READER_EXPECTED_LONG;
    }

    public DynamicCommandExceptionType readerInvalidDouble() {
        return READER_INVALID_DOUBLE;
    }

    public SimpleCommandExceptionType readerExpectedDouble() {
        return READER_EXPECTED_DOUBLE;
    }

    public DynamicCommandExceptionType readerInvalidFloat() {
        return READER_INVALID_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedFloat() {
        return READER_EXPECTED_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedBool() {
        return READER_EXPECTED_BOOL;
    }

    public DynamicCommandExceptionType readerExpectedSymbol() {
        return READER_EXPECTED_SYMBOL;
    }

    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return DISPATCHER_UNKNOWN_COMMAND;
    }

    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return DISPATCHER_UNKNOWN_ARGUMENT;
    }

    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
    }

    public DynamicCommandExceptionType dispatcherParseException() {
        return DISPATCHER_PARSE_EXCEPTION;
    }
}

