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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MobEffectArgumentType
implements ArgumentType<StatusEffect> {
    private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
    public static final DynamicCommandExceptionType INVALID_EFFECT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("effect.effectNotFound", object));

    public static MobEffectArgumentType mobEffect() {
        return new MobEffectArgumentType();
    }

    public static StatusEffect getMobEffect(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        return (StatusEffect)commandContext.getArgument(string, StatusEffect.class);
    }

    public StatusEffect parse(StringReader stringReader) throws CommandSyntaxException {
        Identifier lv = Identifier.fromCommandInput(stringReader);
        return Registry.STATUS_EFFECT.getOrEmpty(lv).orElseThrow(() -> INVALID_EFFECT_EXCEPTION.create((Object)lv));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registry.STATUS_EFFECT.getIds(), builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

