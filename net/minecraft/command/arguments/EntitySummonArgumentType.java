/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntitySummonArgumentType
implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
    public static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("entity.notFound", object));

    public static EntitySummonArgumentType entitySummon() {
        return new EntitySummonArgumentType();
    }

    public static Identifier getEntitySummon(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return EntitySummonArgumentType.validate((Identifier)commandContext.getArgument(string, Identifier.class));
    }

    private static Identifier validate(Identifier arg) throws CommandSyntaxException {
        Registry.ENTITY_TYPE.getOrEmpty(arg).filter(EntityType::isSummonable).orElseThrow(() -> NOT_FOUND_EXCEPTION.create((Object)arg));
        return arg;
    }

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return EntitySummonArgumentType.validate(Identifier.fromCommandInput(stringReader));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

