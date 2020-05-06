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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class SetBlockCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.setblock.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setblock").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("block", BlockStateArgumentType.blockState()).executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, null))).then(CommandManager.literal("destroy").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.DESTROY, null)))).then(CommandManager.literal("keep").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, arg -> arg.getWorld().isAir(arg.getBlockPos()))))).then(CommandManager.literal("replace").executes(commandContext -> SetBlockCommand.execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), BlockStateArgumentType.getBlockState((CommandContext<ServerCommandSource>)commandContext, "block"), Mode.REPLACE, null))))));
    }

    private static int execute(ServerCommandSource arg, BlockPos arg2, BlockStateArgument arg3, Mode arg4, @Nullable Predicate<CachedBlockPosition> predicate) throws CommandSyntaxException {
        boolean bl2;
        ServerWorld lv = arg.getWorld();
        if (predicate != null && !predicate.test(new CachedBlockPosition(lv, arg2, true))) {
            throw FAILED_EXCEPTION.create();
        }
        if (arg4 == Mode.DESTROY) {
            lv.breakBlock(arg2, true);
            boolean bl = !arg3.getBlockState().isAir() || !lv.getBlockState(arg2).isAir();
        } else {
            BlockEntity lv2 = lv.getBlockEntity(arg2);
            Clearable.clear(lv2);
            bl2 = true;
        }
        if (bl2 && !arg3.setBlockState(lv, arg2, 2)) {
            throw FAILED_EXCEPTION.create();
        }
        lv.updateNeighbors(arg2, arg3.getBlockState().getBlock());
        arg.sendFeedback(new TranslatableText("commands.setblock.success", arg2.getX(), arg2.getY(), arg2.getZ()), true);
        return 1;
    }

    public static interface Filter {
        @Nullable
        public BlockStateArgument filter(BlockBox var1, BlockPos var2, BlockStateArgument var3, ServerWorld var4);
    }

    public static enum Mode {
        REPLACE,
        DESTROY;

    }
}

