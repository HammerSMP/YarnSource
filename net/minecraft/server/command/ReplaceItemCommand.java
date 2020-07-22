/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ReplaceItemCommand {
    public static final SimpleCommandExceptionType BLOCK_FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.replaceitem.block.failed"));
    public static final DynamicCommandExceptionType SLOT_INAPPLICABLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.replaceitem.slot.inapplicable", object));
    public static final Dynamic2CommandExceptionType ENTITY_FAILED_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.replaceitem.entity.failed", object, object2));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("replaceitem").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("block").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> ReplaceItemCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer((int)1, (int)64)).executes(commandContext -> ReplaceItemCommand.executeBlock((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getLoadedBlockPos((CommandContext<ServerCommandSource>)commandContext, "pos"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true))))))))).then(CommandManager.literal("entity").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> ReplaceItemCommand.executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(1, false)))).then(CommandManager.argument("count", IntegerArgumentType.integer((int)1, (int)64)).executes(commandContext -> ReplaceItemCommand.executeEntity((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemSlotArgumentType.getItemSlot((CommandContext<ServerCommandSource>)commandContext, "slot"), ItemStackArgumentType.getItemStackArgument(commandContext, "item").createStack(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true)))))))));
    }

    private static int executeBlock(ServerCommandSource source, BlockPos pos, int slot, ItemStack item) throws CommandSyntaxException {
        BlockEntity lv = source.getWorld().getBlockEntity(pos);
        if (!(lv instanceof Inventory)) {
            throw BLOCK_FAILED_EXCEPTION.create();
        }
        Inventory lv2 = (Inventory)((Object)lv);
        if (slot < 0 || slot >= lv2.size()) {
            throw SLOT_INAPPLICABLE_EXCEPTION.create((Object)slot);
        }
        lv2.setStack(slot, item);
        source.sendFeedback(new TranslatableText("commands.replaceitem.block.success", pos.getX(), pos.getY(), pos.getZ(), item.toHoverableText()), true);
        return 1;
    }

    private static int executeEntity(ServerCommandSource source, Collection<? extends Entity> targets, int slot, ItemStack item) throws CommandSyntaxException {
        ArrayList list = Lists.newArrayListWithCapacity((int)targets.size());
        for (Entity entity : targets) {
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
            }
            if (!entity.equip(slot, item.copy())) continue;
            list.add(entity);
            if (!(entity instanceof ServerPlayerEntity)) continue;
            ((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
        }
        if (list.isEmpty()) {
            throw ENTITY_FAILED_EXCEPTION.create((Object)item.toHoverableText(), (Object)slot);
        }
        if (list.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.replaceitem.entity.success.single", ((Entity)list.iterator().next()).getDisplayName(), item.toHoverableText()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.replaceitem.entity.success.multiple", list.size(), item.toHoverableText()), true);
        }
        return list.size();
    }
}

