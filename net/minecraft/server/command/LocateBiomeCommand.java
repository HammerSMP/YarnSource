/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class LocateBiomeCommand {
    public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locatebiome.invalid", object));
    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.locatebiome.notFound", object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locatebiome").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("biome", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.ALL_BIOMES).executes(commandContext -> LocateBiomeCommand.execute((ServerCommandSource)commandContext.getSource(), LocateBiomeCommand.getBiome((CommandContext<ServerCommandSource>)commandContext, "biome")))));
    }

    private static int execute(ServerCommandSource arg, Biome arg2) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(arg.getPosition());
        BlockPos lv2 = arg.getWorld().locateBiome(arg2, lv, 6400, 8);
        if (lv2 == null) {
            throw NOT_FOUND_EXCEPTION.create((Object)arg2.getName().getString());
        }
        return LocateCommand.sendCoordinates(arg, arg2.getName().getString(), lv, lv2, "commands.locatebiome.success");
    }

    private static Biome getBiome(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Identifier lv = (Identifier)commandContext.getArgument(string, Identifier.class);
        return Registry.BIOME.getOrEmpty(lv).orElseThrow(() -> INVALID_EXCEPTION.create((Object)lv));
    }
}

