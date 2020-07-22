/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ParticleArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.particle.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("particle").requires(arg -> arg.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("name", ParticleArgumentType.particle()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), ((ServerCommandSource)commandContext.getSource()).getPosition(), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("delta", Vec3ArgumentType.vec3(false)).then(CommandManager.argument("speed", FloatArgumentType.floatArg((float)0.0f)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("count", IntegerArgumentType.integer((int)0)).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((LiteralArgumentBuilder)CommandManager.literal("force").executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", EntityArgumentType.players()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "viewers")))))).then(((LiteralArgumentBuilder)CommandManager.literal("normal").executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", EntityArgumentType.players()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle((CommandContext<ServerCommandSource>)commandContext, "name"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "viewers")))))))))));
    }

    private static int execute(ServerCommandSource source, ParticleEffect parameters, Vec3d pos, Vec3d delta, float speed, int count, boolean force, Collection<ServerPlayerEntity> viewers) throws CommandSyntaxException {
        int j = 0;
        for (ServerPlayerEntity lv : viewers) {
            if (!source.getWorld().spawnParticles(lv, parameters, force, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed)) continue;
            ++j;
        }
        if (j == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.particle.success", Registry.PARTICLE_TYPE.getId(parameters.getType()).toString()), true);
        return j;
    }
}

