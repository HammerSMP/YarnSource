/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locatebiome").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("biome", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.ALL_BIOMES).executes(commandContext -> LocateBiomeCommand.execute((ServerCommandSource)commandContext.getSource(), (Identifier)commandContext.getArgument("biome", Identifier.class)))));
    }

    private static int execute(ServerCommandSource source, Identifier arg2) throws CommandSyntaxException {
        Biome lv = (Biome)source.getMinecraftServer().method_30611().method_30530(Registry.BIOME_KEY).getOrEmpty(arg2).orElseThrow(() -> INVALID_EXCEPTION.create((Object)arg2));
        BlockPos lv2 = new BlockPos(source.getPosition());
        BlockPos lv3 = source.getWorld().locateBiome(lv, lv2, 6400, 8);
        String string = arg2.toString();
        if (lv3 == null) {
            throw NOT_FOUND_EXCEPTION.create((Object)string);
        }
        return LocateCommand.sendCoordinates(source, string, lv2, lv3, "commands.locatebiome.success");
    }
}

