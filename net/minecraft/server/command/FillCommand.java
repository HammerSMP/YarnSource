/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class FillCommand {
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.fill.toobig", object, object2));
    private static final BlockStateArgument AIR_BLOCK_ARGUMENT = new BlockStateArgument(Blocks.AIR.getDefaultState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.fill.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("fill").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("from", BlockPosArgumentType.blockPos()).then(CommandManager.argument("to", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, null))).then(CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate()).executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter")))))).then(CommandManager.literal("keep").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, arg -> arg.getWorld().isAir(arg.getBlockPos()))))).then(CommandManager.literal("outline").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.OUTLINE, null)))).then(CommandManager.literal("hollow").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.HOLLOW, null)))).then(CommandManager.literal("destroy").executes(commandContext -> FillCommand.execute((ServerCommandSource)commandContext.getSource(), new BlockBox(BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "from"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "to")), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.DESTROY, null)))))));
    }

    private static int execute(ServerCommandSource source, BlockBox range, BlockStateArgument block, Mode mode, @Nullable Predicate<CachedBlockPosition> filter) throws CommandSyntaxException {
        int i = range.getBlockCountX() * range.getBlockCountY() * range.getBlockCountZ();
        if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create((Object)32768, (Object)i);
        }
        ArrayList list = Lists.newArrayList();
        ServerWorld lv = source.getWorld();
        int j = 0;
        for (BlockPos lv2 : BlockPos.iterate(range.minX, range.minY, range.minZ, range.maxX, range.maxY, range.maxZ)) {
            BlockStateArgument lv3;
            if (filter != null && !filter.test(new CachedBlockPosition(lv, lv2, true)) || (lv3 = mode.filter.filter(range, lv2, block, lv)) == null) continue;
            BlockEntity lv4 = lv.getBlockEntity(lv2);
            Clearable.clear(lv4);
            if (!lv3.setBlockState(lv, lv2, 2)) continue;
            list.add(lv2.toImmutable());
            ++j;
        }
        for (BlockPos lv5 : list) {
            Block lv6 = lv.getBlockState(lv5).getBlock();
            lv.updateNeighbors(lv5, lv6);
        }
        if (j == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslatableText("commands.fill.success", j), true);
        return j;
    }

    static enum Mode {
        REPLACE((arg, arg2, arg3, arg4) -> arg3),
        OUTLINE((arg, arg2, arg3, arg4) -> {
            if (arg2.getX() == arg.minX || arg2.getX() == arg.maxX || arg2.getY() == arg.minY || arg2.getY() == arg.maxY || arg2.getZ() == arg.minZ || arg2.getZ() == arg.maxZ) {
                return arg3;
            }
            return null;
        }),
        HOLLOW((arg, arg2, arg3, arg4) -> {
            if (arg2.getX() == arg.minX || arg2.getX() == arg.maxX || arg2.getY() == arg.minY || arg2.getY() == arg.maxY || arg2.getZ() == arg.minZ || arg2.getZ() == arg.maxZ) {
                return arg3;
            }
            return AIR_BLOCK_ARGUMENT;
        }),
        DESTROY((arg, arg2, arg3, arg4) -> {
            arg4.breakBlock(arg2, true);
            return arg3;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter arg) {
            this.filter = arg;
        }
    }
}

