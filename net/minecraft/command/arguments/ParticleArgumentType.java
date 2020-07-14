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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleArgumentType
implements ArgumentType<ParticleEffect> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("particle.notFound", object));

    public static ParticleArgumentType particle() {
        return new ParticleArgumentType();
    }

    public static ParticleEffect getParticle(CommandContext<ServerCommandSource> context, String name) {
        return (ParticleEffect)context.getArgument(name, ParticleEffect.class);
    }

    public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
        return ParticleArgumentType.readParameters(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static ParticleEffect readParameters(StringReader reader) throws CommandSyntaxException {
        Identifier lv = Identifier.fromCommandInput(reader);
        ParticleType<?> lv2 = Registry.PARTICLE_TYPE.getOrEmpty(lv).orElseThrow(() -> UNKNOWN_PARTICLE_EXCEPTION.create((Object)lv));
        return ParticleArgumentType.readParameters(reader, lv2);
    }

    private static <T extends ParticleEffect> T readParameters(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
        return type.getParametersFactory().read(type, reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(Registry.PARTICLE_TYPE.getIds(), builder);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

