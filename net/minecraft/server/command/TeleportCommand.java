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
import net.minecraft.entity.mob.PathAwareEntity;
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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("teleport").requires(arg -> arg.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, null))).then(CommandManager.argument("rotation", RotationArgumentType.rotation()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), RotationArgumentType.getRotation((CommandContext<ServerCommandSource>)commandContext, "rotation"), null)))).then(((LiteralArgumentBuilder)CommandManager.literal("facing").then(CommandManager.literal("entity").then(((RequiredArgumentBuilder)CommandManager.argument("facingEntity", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.EntityAnchor.FEET)))).then(CommandManager.argument("facingAnchor", EntityAnchorArgumentType.entityAnchor()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "facingEntity"), EntityAnchorArgumentType.getEntityAnchor((CommandContext<ServerCommandSource>)commandContext, "facingAnchor")))))))).then(CommandManager.argument("facingLocation", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), null, new LookTarget(Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "facingLocation")))))))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))))).then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), ((ServerCommandSource)commandContext.getSource()).getWorld(), Vec3ArgumentType.getPosArgument((CommandContext<ServerCommandSource>)commandContext, "location"), DefaultPosArgument.zero(), null)))).then(CommandManager.argument("destination", EntityArgumentType.entity()).executes(commandContext -> TeleportCommand.execute((ServerCommandSource)commandContext.getSource(), Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow()), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "destination")))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tp").requires(arg -> arg.hasPermissionLevel(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Entity destination) throws CommandSyntaxException {
        for (Entity entity : targets) {
            TeleportCommand.teleport(source, entity, (ServerWorld)destination.world, destination.getX(), destination.getY(), destination.getZ(), EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), destination.yaw, destination.pitch, null);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.teleport.success.entity.single", targets.iterator().next().getDisplayName(), destination.getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.teleport.success.entity.multiple", targets.size(), destination.getDisplayName()), true);
        }
        return targets.size();
    }

    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, ServerWorld world, PosArgument location, @Nullable PosArgument rotation, @Nullable LookTarget facingLocation) throws CommandSyntaxException {
        Vec3d lv = location.toAbsolutePos(source);
        Vec2f lv2 = rotation == null ? null : rotation.toAbsoluteRotation(source);
        EnumSet<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
        if (location.isXRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.X);
        }
        if (location.isYRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.Y);
        }
        if (location.isZRelative()) {
            set.add(PlayerPositionLookS2CPacket.Flag.Z);
        }
        if (rotation == null) {
            set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
        } else {
            if (rotation.isXRelative()) {
                set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
            }
            if (rotation.isYRelative()) {
                set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
            }
        }
        for (Entity entity : targets) {
            if (rotation == null) {
                TeleportCommand.teleport(source, entity, world, lv.x, lv.y, lv.z, set, entity.yaw, entity.pitch, facingLocation);
                continue;
            }
            TeleportCommand.teleport(source, entity, world, lv.x, lv.y, lv.z, set, lv2.y, lv2.x, facingLocation);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.teleport.success.location.single", targets.iterator().next().getDisplayName(), lv.x, lv.y, lv.z), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.teleport.success.location.multiple", targets.size(), lv.x, lv.y, lv.z), true);
        }
        return targets.size();
    }

    private static void teleport(ServerCommandSource source, Entity target, ServerWorld world, double x, double y, double z, Set<PlayerPositionLookS2CPacket.Flag> movementFlags, float yaw, float pitch, @Nullable LookTarget facingLocation) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(x, y, z);
        if (!World.method_25953(lv)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        if (target instanceof ServerPlayerEntity) {
            ChunkPos lv2 = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.field_19347, lv2, 1, target.getEntityId());
            target.stopRiding();
            if (((ServerPlayerEntity)target).isSleeping()) {
                ((ServerPlayerEntity)target).wakeUp(true, true);
            }
            if (world == target.world) {
                ((ServerPlayerEntity)target).networkHandler.teleportRequest(x, y, z, yaw, pitch, movementFlags);
            } else {
                ((ServerPlayerEntity)target).teleport(world, x, y, z, yaw, pitch);
            }
            target.setHeadYaw(yaw);
        } else {
            float i = MathHelper.wrapDegrees(yaw);
            float j = MathHelper.wrapDegrees(pitch);
            j = MathHelper.clamp(j, -90.0f, 90.0f);
            if (world == target.world) {
                target.refreshPositionAndAngles(x, y, z, i, j);
                target.setHeadYaw(i);
            } else {
                target.detach();
                Entity lv3 = target;
                target = lv3.getType().create(world);
                if (target != null) {
                    target.copyFrom(lv3);
                    target.refreshPositionAndAngles(x, y, z, i, j);
                    target.setHeadYaw(i);
                    world.onDimensionChanged(target);
                    lv3.removed = true;
                } else {
                    return;
                }
            }
        }
        if (facingLocation != null) {
            facingLocation.look(source, target);
        }
        if (!(target instanceof LivingEntity) || !((LivingEntity)target).isFallFlying()) {
            target.setVelocity(target.getVelocity().multiply(1.0, 0.0, 1.0));
            target.setOnGround(true);
        }
        if (target instanceof PathAwareEntity) {
            ((PathAwareEntity)target).getNavigation().stop();
        }
    }

    static class LookTarget {
        private final Vec3d targetPos;
        private final Entity target;
        private final EntityAnchorArgumentType.EntityAnchor targetAnchor;

        public LookTarget(Entity target, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
            this.target = target;
            this.targetAnchor = targetAnchor;
            this.targetPos = targetAnchor.positionAt(target);
        }

        public LookTarget(Vec3d targetPos) {
            this.target = null;
            this.targetPos = targetPos;
            this.targetAnchor = null;
        }

        public void look(ServerCommandSource source, Entity entity) {
            if (this.target != null) {
                if (entity instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity).method_14222(source.getEntityAnchor(), this.target, this.targetAnchor);
                } else {
                    entity.lookAt(source.getEntityAnchor(), this.targetPos);
                }
            } else {
                entity.lookAt(source.getEntityAnchor(), this.targetPos);
            }
        }
    }
}

