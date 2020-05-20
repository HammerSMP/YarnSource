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
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class CloneCommand {
    private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.clone.toobig", object, object2));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.clone.failed"));
    public static final Predicate<CachedBlockPosition> IS_AIR_PREDICATE = arg -> !arg.getBlockState().isAir();

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clone").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("begin", BlockPosArgumentType.blockPos()).then(CommandManager.argument("end", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("destination", BlockPosArgumentType.blockPos()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), arg -> true, Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replace").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), arg -> true, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), arg -> true, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), arg -> true, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), arg -> true, Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("masked").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), IS_AIR_PREDICATE, Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), IS_AIR_PREDICATE, Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), IS_AIR_PREDICATE, Mode.NORMAL))))).then(CommandManager.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("filter", BlockPredicateArgumentType.blockPredicate()).executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.NORMAL))).then(CommandManager.literal("force").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.FORCE)))).then(CommandManager.literal("move").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.MOVE)))).then(CommandManager.literal("normal").executes(commandContext -> CloneCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "begin"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "end"), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "destination"), BlockPredicateArgumentType.getBlockPredicate((CommandContext<ServerCommandSource>)commandContext, "filter"), Mode.NORMAL)))))))));
    }

    private static int execute(ServerCommandSource arg, BlockPos arg2, BlockPos arg3, BlockPos arg4, Predicate<CachedBlockPosition> predicate, Mode arg5) throws CommandSyntaxException {
        BlockBox lv = new BlockBox(arg2, arg3);
        BlockPos lv2 = arg4.add(lv.getDimensions());
        BlockBox lv3 = new BlockBox(arg4, lv2);
        if (!arg5.allowsOverlap() && lv3.intersects(lv)) {
            throw OVERLAP_EXCEPTION.create();
        }
        int i = lv.getBlockCountX() * lv.getBlockCountY() * lv.getBlockCountZ();
        if (i > 32768) {
            throw TOO_BIG_EXCEPTION.create((Object)32768, (Object)i);
        }
        ServerWorld lv4 = arg.getWorld();
        if (!lv4.isRegionLoaded(arg2, arg3) || !lv4.isRegionLoaded(arg4, lv2)) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        LinkedList deque = Lists.newLinkedList();
        BlockPos lv5 = new BlockPos(lv3.minX - lv.minX, lv3.minY - lv.minY, lv3.minZ - lv.minZ);
        for (int j = lv.minZ; j <= lv.maxZ; ++j) {
            for (int k = lv.minY; k <= lv.maxY; ++k) {
                for (int l = lv.minX; l <= lv.maxX; ++l) {
                    BlockPos lv6 = new BlockPos(l, k, j);
                    BlockPos lv7 = lv6.add(lv5);
                    CachedBlockPosition lv8 = new CachedBlockPosition(lv4, lv6, false);
                    BlockState lv9 = lv8.getBlockState();
                    if (!predicate.test(lv8)) continue;
                    BlockEntity lv10 = lv4.getBlockEntity(lv6);
                    if (lv10 != null) {
                        CompoundTag lv11 = lv10.toTag(new CompoundTag());
                        list2.add(new BlockInfo(lv7, lv9, lv11));
                        deque.addLast(lv6);
                        continue;
                    }
                    if (lv9.isOpaqueFullCube(lv4, lv6) || lv9.isFullCube(lv4, lv6)) {
                        list.add(new BlockInfo(lv7, lv9, null));
                        deque.addLast(lv6);
                        continue;
                    }
                    list3.add(new BlockInfo(lv7, lv9, null));
                    deque.addFirst(lv6);
                }
            }
        }
        if (arg5 == Mode.MOVE) {
            for (BlockPos lv12 : deque) {
                BlockEntity lv13 = lv4.getBlockEntity(lv12);
                Clearable.clear(lv13);
                lv4.setBlockState(lv12, Blocks.BARRIER.getDefaultState(), 2);
            }
            for (BlockPos lv14 : deque) {
                lv4.setBlockState(lv14, Blocks.AIR.getDefaultState(), 3);
            }
        }
        ArrayList list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        List list5 = Lists.reverse((List)list4);
        for (BlockInfo lv15 : list5) {
            BlockEntity lv16 = lv4.getBlockEntity(lv15.pos);
            Clearable.clear(lv16);
            lv4.setBlockState(lv15.pos, Blocks.BARRIER.getDefaultState(), 2);
        }
        int m = 0;
        for (BlockInfo lv17 : list4) {
            if (!lv4.setBlockState(lv17.pos, lv17.state, 2)) continue;
            ++m;
        }
        for (BlockInfo lv18 : list2) {
            BlockEntity lv19 = lv4.getBlockEntity(lv18.pos);
            if (lv18.blockEntityTag != null && lv19 != null) {
                lv18.blockEntityTag.putInt("x", lv18.pos.getX());
                lv18.blockEntityTag.putInt("y", lv18.pos.getY());
                lv18.blockEntityTag.putInt("z", lv18.pos.getZ());
                lv19.fromTag(lv18.state, lv18.blockEntityTag);
                lv19.markDirty();
            }
            lv4.setBlockState(lv18.pos, lv18.state, 2);
        }
        for (BlockInfo lv20 : list5) {
            lv4.updateNeighbors(lv20.pos, lv20.state.getBlock());
        }
        ((ServerTickScheduler)lv4.getBlockTickScheduler()).copyScheduledTicks(lv, lv5);
        if (m == 0) {
            throw FAILED_EXCEPTION.create();
        }
        arg.sendFeedback(new TranslatableText("commands.clone.success", m), true);
        return m;
    }

    static class BlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag blockEntityTag;

        public BlockInfo(BlockPos arg, BlockState arg2, @Nullable CompoundTag arg3) {
            this.pos = arg;
            this.state = arg2;
            this.blockEntityTag = arg3;
        }
    }

    static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);

        private final boolean allowsOverlap;

        private Mode(boolean bl) {
            this.allowsOverlap = bl;
        }

        public boolean allowsOverlap() {
            return this.allowsOverlap;
        }
    }
}

