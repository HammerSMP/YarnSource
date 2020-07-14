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

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("forceload").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), true))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)CommandManager.literal("remove").then(((RequiredArgumentBuilder)CommandManager.argument("from", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), false))).then(CommandManager.argument("to", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeChange((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "from"), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "to"), false))))).then(CommandManager.literal("all").executes(commandContext -> ForceLoadCommand.executeRemoveAll((ServerCommandSource)commandContext.getSource()))))).then(((LiteralArgumentBuilder)CommandManager.literal("query").executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos()).executes(commandContext -> ForceLoadCommand.executeQuery((ServerCommandSource)commandContext.getSource(), ColumnPosArgumentType.getColumnPos((CommandContext<ServerCommandSource>)commandContext, "pos"))))));
    }

    private static int executeQuery(ServerCommandSource source, ColumnPos pos) throws CommandSyntaxException {
        ChunkPos lv = new ChunkPos(pos.x >> 4, pos.z >> 4);
        ServerWorld lv2 = source.getWorld();
        RegistryKey<World> lv3 = lv2.getRegistryKey();
        boolean bl = lv2.getForcedChunks().contains(lv.toLong());
        if (bl) {
            source.sendFeedback(new TranslatableText("commands.forceload.query.success", lv, lv3.getValue()), false);
            return 1;
        }
        throw QUERY_FAILURE_EXCEPTION.create((Object)lv, (Object)lv3.getValue());
    }

    private static int executeQuery(ServerCommandSource source) {
        ServerWorld lv = source.getWorld();
        RegistryKey<World> lv2 = lv.getRegistryKey();
        LongSet longSet = lv.getForcedChunks();
        int i = longSet.size();
        if (i > 0) {
            String string = Joiner.on((String)", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (i == 1) {
                source.sendFeedback(new TranslatableText("commands.forceload.list.single", lv2.getValue(), string), false);
            } else {
                source.sendFeedback(new TranslatableText("commands.forceload.list.multiple", i, lv2.getValue(), string), false);
            }
        } else {
            source.sendError(new TranslatableText("commands.forceload.added.none", lv2.getValue()));
        }
        return i;
    }

    private static int executeRemoveAll(ServerCommandSource source) {
        ServerWorld lv = source.getWorld();
        RegistryKey<World> lv2 = lv.getRegistryKey();
        LongSet longSet = lv.getForcedChunks();
        longSet.forEach(l -> lv.setChunkForced(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), false));
        source.sendFeedback(new TranslatableText("commands.forceload.removed.all", lv2.getValue()), true);
        return 0;
    }

    private static int executeChange(ServerCommandSource source, ColumnPos from, ColumnPos to, boolean forceLoaded) throws CommandSyntaxException {
        int i = Math.min(from.x, to.x);
        int j = Math.min(from.z, to.z);
        int k = Math.max(from.x, to.x);
        int l = Math.max(from.z, to.z);
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
        ServerWorld lv = source.getWorld();
        RegistryKey<World> lv2 = lv.getRegistryKey();
        ChunkPos lv3 = null;
        int r = 0;
        for (int s = m; s <= o; ++s) {
            for (int t = n; t <= p; ++t) {
                boolean bl2 = lv.setChunkForced(s, t, forceLoaded);
                if (!bl2) continue;
                ++r;
                if (lv3 != null) continue;
                lv3 = new ChunkPos(s, t);
            }
        }
        if (r == 0) {
            throw (forceLoaded ? ADDED_FAILURE_EXCEPTION : REMOVED_FAILURE_EXCEPTION).create();
        }
        if (r == 1) {
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".single", lv3, lv2.getValue()), true);
        } else {
            ChunkPos lv4 = new ChunkPos(m, n);
            ChunkPos lv5 = new ChunkPos(o, p);
            source.sendFeedback(new TranslatableText("commands.forceload." + (forceLoaded ? "added" : "removed") + ".multiple", r, lv2.getValue(), lv4, lv5), true);
        }
        return r;
    }
}

