/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  javax.annotation.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), null, null))).then(CommandManager.literal("*").then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), null, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound")))));
        for (SoundCategory lv : SoundCategory.values()) {
            requiredArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(lv.getName()).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), lv, null))).then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes(commandContext -> StopSoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), lv, IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound")))));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stopsound").requires(arg -> arg.hasPermissionLevel(2))).then((ArgumentBuilder)requiredArgumentBuilder));
    }

    private static int execute(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, @Nullable SoundCategory arg2, @Nullable Identifier arg3) {
        StopSoundS2CPacket lv = new StopSoundS2CPacket(arg3, arg2);
        for (ServerPlayerEntity lv2 : collection) {
            lv2.networkHandler.sendPacket(lv);
        }
        if (arg2 != null) {
            if (arg3 != null) {
                arg.sendFeedback(new TranslatableText("commands.stopsound.success.source.sound", arg3, arg2.getName()), true);
            } else {
                arg.sendFeedback(new TranslatableText("commands.stopsound.success.source.any", arg2.getName()), true);
            }
        } else if (arg3 != null) {
            arg.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.sound", arg3), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.stopsound.success.sourceless.any"), true);
        }
        return collection.size();
    }
}

