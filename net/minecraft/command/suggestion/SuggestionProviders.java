/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.suggestion;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SuggestionProviders {
    private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY = Maps.newHashMap();
    private static final Identifier ASK_SERVER_NAME = new Identifier("ask_server");
    public static final SuggestionProvider<CommandSource> ASK_SERVER = SuggestionProviders.register(ASK_SERVER_NAME, (SuggestionProvider<CommandSource>)((SuggestionProvider)(commandContext, suggestionsBuilder) -> ((CommandSource)commandContext.getSource()).getCompletions((CommandContext<CommandSource>)commandContext, suggestionsBuilder)));
    public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = SuggestionProviders.register(new Identifier("all_recipes"), (SuggestionProvider<CommandSource>)((SuggestionProvider)(commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getRecipeIds(), suggestionsBuilder)));
    public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(new Identifier("available_sounds"), (SuggestionProvider<CommandSource>)((SuggestionProvider)(commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getSoundIds(), suggestionsBuilder)));
    public static final SuggestionProvider<ServerCommandSource> ALL_BIOMES = SuggestionProviders.register(new Identifier("available_biomes"), (SuggestionProvider<CommandSource>)((SuggestionProvider)(commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(Registry.BIOME.getIds(), suggestionsBuilder)));
    public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES = SuggestionProviders.register(new Identifier("summonable_entities"), (SuggestionProvider<CommandSource>)((SuggestionProvider)(commandContext, suggestionsBuilder) -> CommandSource.suggestFromIdentifier(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), suggestionsBuilder, EntityType::getId, arg -> new TranslatableText(Util.createTranslationKey("entity", EntityType.getId(arg))))));

    public static <S extends CommandSource> SuggestionProvider<S> register(Identifier arg, SuggestionProvider<CommandSource> suggestionProvider) {
        if (REGISTRY.containsKey(arg)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + arg);
        }
        REGISTRY.put(arg, suggestionProvider);
        return new LocalProvider(arg, suggestionProvider);
    }

    public static SuggestionProvider<CommandSource> byId(Identifier arg) {
        return REGISTRY.getOrDefault(arg, ASK_SERVER);
    }

    public static Identifier computeName(SuggestionProvider<CommandSource> suggestionProvider) {
        if (suggestionProvider instanceof LocalProvider) {
            return ((LocalProvider)suggestionProvider).name;
        }
        return ASK_SERVER_NAME;
    }

    public static SuggestionProvider<CommandSource> getLocalProvider(SuggestionProvider<CommandSource> suggestionProvider) {
        if (suggestionProvider instanceof LocalProvider) {
            return suggestionProvider;
        }
        return ASK_SERVER;
    }

    public static class LocalProvider
    implements SuggestionProvider<CommandSource> {
        private final SuggestionProvider<CommandSource> provider;
        private final Identifier name;

        public LocalProvider(Identifier arg, SuggestionProvider<CommandSource> suggestionProvider) {
            this.provider = suggestionProvider;
            this.name = arg;
        }

        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            return this.provider.getSuggestions(commandContext, suggestionsBuilder);
        }
    }
}

