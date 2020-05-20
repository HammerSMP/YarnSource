/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.DefaultPosArgument;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TeleportCommand {
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("teleport").requires(arg -> arg.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, null))).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), RotationArgumentType.getRotation((CommandContext<ServerCommandSource>)commandContext, "rotation"), null)))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("facingEntity", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "facingAnchor")))))))).then(CommandManager.argument("facingLocation", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "facingLocation")))))))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))))).then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), DefaultPosArgument.zero(), null)))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tp").requires(arg -> arg.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int execute(ServerCommandSource arg, Collection<? extends Entity> collection, Entity arg2) throws CommandSyntaxException {
        for (Entity entity : collection) {
            TeleportCommand.teleport(arg, entity, (ServerWorld)arg2.world, arg2.getX(), arg2.getY(), arg2.getZ(), EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), arg2.yaw, arg2.pitch, null);
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.teleport.success.entity.single", collection.iterator().next().getDisplayName(), arg2.getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.teleport.success.entity.multiple", collection.size(), arg2.getDisplayName()), true);
        }
        return collection.size();
    }

    private static int execute(ServerCommandSource arg, Collection<? extends Entity> collection, ServerWorld arg2, PosArgument arg3, @Nullable PosArgument arg4, @Nullable LookTarget arg5) throws CommandSyntaxException {
        Vec3d lv = arg3.toAbsolutePos(arg);
        Vec2f lv2 = arg4 == null ? null : arg4.toAbsoluteRotation(arg);
        EnumSet<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
        if (arg3.isXRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.X);
        }
        if (arg3.isYRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.Y);
        }
        if (arg3.isZRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.Z);
        }
        if (arg4 == null) {
            set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
        } else {
            if (arg4.isXRelative()) {
                set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            }
            if (arg4.isYRelative()) {
                set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
            }
        }
        for (Entity entity : collection) {
            if (arg4 == null) {
                TeleportCommand.teleport(arg, entity, arg2, lv.x, lv.y, lv.z, set, entity.yaw, entity.pitch, arg5);
                continue;
            }
            TeleportCommand.teleport(arg, entity, arg2, lv.x, lv.y, lv.z, set, lv2.y, lv2.x, arg5);
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.teleport.success.location.single", collection.iterator().next().getDisplayName(), lv.x, lv.y, lv.z), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.teleport.success.location.multiple", collection.size(), lv.x, lv.y, lv.z), true);
        }
        return collection.size();
    }

    private static void teleport(ServerCommandSource arg, Entity arg2, ServerWorld arg3, double d, double e, double f, Set<PlayerPositionLookS2CPacket.Flag> set, float g, float h, @Nullable LookTarget arg4) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(d, e, f);
        if (!World.method_25953(lv)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        if (arg2 instanceof ServerPlayerEntity) {
            ChunkPos lv2 = new ChunkPos(new BlockPos(d, e, f));
            arg3.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, lv2, 1, arg2.getEntityId());
            arg2.stopRiding();
            if (((ServerPlayerEntity)arg2).isSleeping()) {
                ((ServerPlayerEntity)arg2).wakeUp(true, true);
            }
            if (arg3 == arg2.world) {
                ((ServerPlayerEntity)arg2).networkHandler.teleportRequest(d, e, f, g, h, set);
            } else {
                ((ServerPlayerEntity)arg2).teleport(arg3, d, e, f, g, h);
            }
            arg2.setHeadYaw(g);
        } else {
            float i = MathHelper.wrapDegrees(g);
            float j = MathHelper.wrapDegrees(h);
            j = MathHelper.clamp(j, -90.0f, 90.0f);
            if (arg3 == arg2.world) {
                arg2.refreshPositionAndAngles(d, e, f, i, j);
                arg2.setHeadYaw(i);
            } else {
                arg2.detach();
                arg2.dimension = arg3.method_27983();
                Entity lv3 = arg2;
                arg2 = lv3.getType().create(arg3);
                if (arg2 != null) {
                    arg2.copyFrom(lv3);
                    arg2.refreshPositionAndAngles(d, e, f, i, j);
                    arg2.setHeadYaw(i);
                    arg3.onDimensionChanged(arg2);
                    lv3.removed = true;
                } else {
                    return;
                }
            }
        }
        if (arg4 != null) {
            arg4.look(arg, arg2);
        }
        if (!(arg2 instanceof LivingEntity) || !((LivingEntity)arg2).isFallFlying()) {
            arg2.setVelocity(arg2.getVelocity().multiply(1.0, 0.0, 1.0));
            arg2.setOnGround(true);
        }
    }

    static class LookTarget {
        private final Vec3d targetPos;
        private final Entity target;
        private final EntityAnchorArgumentType.EntityAnchor targetAnchor;

        public LookTarget(Entity arg, EntityAnchorArgumentType.EntityAnchor arg2) {
            this.target = arg;
            this.targetAnchor = arg2;
            this.targetPos = arg2.positionAt(arg);
        }

        public LookTarget(Vec3d arg) {
            this.target = null;
            this.targetPos = arg;
            this.targetAnchor = null;
        }

        public void look(ServerCommandSource arg, Entity arg2) {
            if (this.target != null) {
                if (arg2 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)arg2).method_14222(arg.getEntityAnchor(), this.target, this.targetAnchor);
                } else {
                    arg2.lookAt(arg.getEntityAnchor(), this.targetPos);
                }
            } else {
                arg2.lookAt(arg.getEntityAnchor(), this.targetPos);
            }
        }
    }
}

