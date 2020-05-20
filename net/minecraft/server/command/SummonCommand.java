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
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.EntitySummonArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.summon.failed"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.summon.invalidPosition"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("summon").requires(arg -> arg.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("entity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), ((ServerCommandSource)commandContext.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), new CompoundTag(), true))).then(CommandManager.argument("nbt", NbtCompoundTagArgumentType.nbtCompound()).executes(commandContext -> SummonCommand.execute((ServerCommandSource)commandContext.getSource(), EntitySummonArgumentType.getEntitySummon((CommandContext<ServerCommandSource>)commandContext, "entity"), Vec3ArgumentType.getVec3((CommandContext<ServerCommandSource>)commandContext, "pos"), NbtCompoundTagArgumentType.getCompoundTag(commandContext, "nbt"), false))))));
    }

    private static int execute(ServerCommandSource arg, Identifier arg2, Vec3d arg32, CompoundTag arg4, boolean bl) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(arg32);
        if (!World.method_25953(lv)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        CompoundTag lv2 = arg4.copy();
        lv2.putString("id", arg2.toString());
        if (EntityType.getId(EntityType.LIGHTNING_BOLT).equals(arg2)) {
            LightningEntity lv3 = new LightningEntity(arg.getWorld(), arg32.x, arg32.y, arg32.z, false);
            arg.getWorld().addLightning(lv3);
            arg.sendFeedback(new TranslatableText("commands.summon.success", lv3.getDisplayName()), true);
            return 1;
        }
        ServerWorld lv4 = arg.getWorld();
        Entity lv5 = EntityType.loadEntityWithPassengers(lv2, lv4, arg3 -> {
            arg3.refreshPositionAndAngles(arg.x, arg.y, arg.z, arg3.yaw, arg3.pitch);
            if (!lv4.tryLoadEntity((Entity)arg3)) {
                return null;
            }
            return arg3;
        });
        if (lv5 == null) {
            throw FAILED_EXCEPTION.create();
        }
        if (bl && lv5 instanceof MobEntity) {
            ((MobEntity)lv5).initialize(arg.getWorld(), arg.getWorld().getLocalDifficulty(lv5.getBlockPos()), SpawnReason.COMMAND, null, null);
        }
        arg.sendFeedback(new TranslatableText("commands.summon.success", lv5.getDisplayName()), true);
        return 1;
    }
}

