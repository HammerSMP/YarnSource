/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.server.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ForceLoadCommand {
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.forceload.toobig", object, object2));
    private static final Dynamic2CommandExceptionType QUERY_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.forceload.query.failure", object, object2));
    private static final SimpleCommandExceptionType ADDED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType REMOVED_FAILURE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.forceload.removed.failure"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("forceload").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), true))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)CommandManager.literal("remove").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), false))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), false))))).then(CommandManager.literal("all").executes(commandContext -> ForceLoadCommand.executeRemoveAll((ServerCommandSource)commandContext.getSource()))))).then(((LiteralArgumentBuilder)CommandManager.literal("query").executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }

    private static int executeQuery(ServerCommandSource arg, ColumnPos arg2) throws CommandSyntaxException {
        ChunkPos lv = new ChunkPos(arg2.x >> 4, arg2.z >> 4);
        RegistryKey<World> lv2 = arg.getWorld().method_27983();
        boolean bl = arg.getMinecraftServer().getWorld(lv2).getForcedChunks().contains(lv.toLong());
        if (bl) {
            arg.sendFeedback(new TranslatableText("commands.forceload.query.success", lv, lv2.getValue()), false);
            return 1;
        }
        throw QUERY_FAILURE_EXCEPTION.create((Object)lv, (Object)lv2.getValue());
    }

    private static int executeQuery(ServerCommandSource arg) {
        RegistryKey<World> lv = arg.getWorld().method_27983();
        LongSet longSet = arg.getMinecraftServer().getWorld(lv).getForcedChunks();
        int i = longSet.size();
        if (i > 0) {
            String string = Joiner.on((String)", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (i == 1) {
                arg.sendFeedback(new TranslatableText("commands.forceload.list.single", lv.getValue(), string), false);
            } else {
                arg.sendFeedback(new TranslatableText("commands.forceload.list.multiple", i, lv.getValue(), string), false);
            }
        } else {
            arg.sendError(new TranslatableText("commands.forceload.added.none", lv.getValue()));
        }
        return i;
    }

    private static int executeRemoveAll(ServerCommandSource arg) {
        RegistryKey<World> lv = arg.getWorld().method_27983();
        ServerWorld lv2 = arg.getMinecraftServer().getWorld(lv);
        LongSet longSet = lv2.getForcedChunks();
        longSet.forEach(l -> lv2.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
        arg.sendFeedback(new TranslatableText("commands.forceload.removed.all", lv.getValue()), true);
        return 0;
    }

    private static int executeChange(ServerCommandSource arg, ColumnPos arg2, ColumnPos arg3, boolean bl) throws CommandSyntaxException {
        int i = Math.min(arg2.x, arg3.x);
        int j = Math.min(arg2.z, arg3.z);
        int k = Math.max(arg2.x, arg3.x);
        int l = Math.max(arg2.z, arg3.z);
        if (i < -30000000 || j < -30000000 || k >= 30000000 || l >= 30000000) {
            throw BlockPosArgumentType.OUT_OF_WORLD_EXCEPTION.create();
        }
        int o = k >> 4;
        int m = i >> 4;
        int p = l >> 4;
        int n = j >> 4;
        long q = ((long)(o - m) + 1L) * ((long)(p - n) + 1L);
        if (q > 256L) {
            throw TOO_BIG_EXCEPTION.create((Object)256, (Object)q);
        }
        RegistryKey<World> lv = arg.getWorld().method_27983();
        ServerWorld lv2 = arg.getMinecraftServer().getWorld(lv);
        ChunkPos lv3 = null;
        int r = 0;
        for (int s = m; s <= o; ++s) {
            for (int t = n; t <= p; ++t) {
                boolean bl2 = lv2.setChunkForced(s, t, bl);
                if (!bl2) continue;
                ++r;
                if (lv3 != null) continue;
                lv3 = new ChunkPos(s, t);
            }
        }
        if (r == 0) {
            throw (bl ? ADDED_FAILURE_EXCEPTION : REMOVED_FAILURE_EXCEPTION).create();
        }
        if (r == 1) {
            arg.sendFeedback(new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".single", lv3, lv.getValue()), true);
        } else {
            ChunkPos lv4 = new ChunkPos(m, n);
            ChunkPos lv5 = new ChunkPos(o, p);
            arg.sendFeedback(new TranslatableText("commands.forceload." + (bl ? "added" : "removed") + ".multiple", r, lv.getValue(), lv4, lv5), true);
        }
        return r;
    }
}

