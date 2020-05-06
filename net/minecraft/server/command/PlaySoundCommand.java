/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.playsound.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        RequiredArgumentBuilder requiredArgumentBuilder = CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
        for (SoundCategory lv : SoundCategory.values()) {
            requiredArgumentBuilder.then(PlaySoundCommand.makeArgumentsForCategory(lv));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("playsound").requires(arg -> arg.hasPermissionLevel(2))).then((ArgumentBuilder)requiredArgumentBuilder));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeArgumentsForCategory(SoundCategory arg) {
        return (LiteralArgumentBuilder)CommandManager.literal(arg.getName()).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), arg, ((ServerCommandSource)commandContext.getSource()).getPosition(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), arg, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("volume", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), arg, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)CommandManager.argument("pitch", FloatArgumentType.floatArg((float)0.0f, (float)2.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), arg, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), ((Float)commandContext.getArgument("pitch", Float.class)).floatValue(), 0.0f))).then(CommandManager.argument("minVolume", FloatArgumentType.floatArg((float)0.0f, (float)1.0f)).executes(commandContext -> PlaySoundCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "sound"), arg, Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), ((Float)commandContext.getArgument("volume", Float.class)).floatValue(), ((Float)commandContext.getArgument("pitch", Float.class)).floatValue(), ((Float)commandContext.getArgument("minVolume", Float.class)).floatValue())))))));
    }

    private static int execute(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, Identifier arg2, SoundCategory arg3, Vec3d arg4, float f, float g, float h) throws CommandSyntaxException {
        double d = Math.pow(f > 1.0f ? (double)(f * 16.0f) : 16.0, 2.0);
        int i = 0;
        for (ServerPlayerEntity lv : collection) {
            double e = arg4.x - lv.getX();
            double j = arg4.y - lv.getY();
            double k = arg4.z - lv.getZ();
            double l = e * e + j * j + k * k;
            Vec3d lv2 = arg4;
            float m = f;
            if (l > d) {
                if (h <= 0.0f) continue;
                double n = MathHelper.sqrt(l);
                lv2 = new Vec3d(lv.getX() + e / n * 2.0, lv.getY() + j / n * 2.0, lv.getZ() + k / n * 2.0);
                m = h;
            }
            lv.networkHandler.sendPacket(new PlaySoundIdS2CPacket(arg2, arg3, lv2, m, g));
            ++i;
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.playsound.success.single", arg2, collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.playsound.success.multiple", arg2, collection.size()), true);
        }
        return i;
    }
}

