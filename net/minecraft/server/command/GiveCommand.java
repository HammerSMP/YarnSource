/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

public class GiveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("give").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), 1))).then(CommandManager.argument("count", IntegerArgumentType.integer((int)1)).executes(commandContext -> GiveCommand.execute((ServerCommandSource)commandContext.getSource(), ItemStackArgumentType.getItemStackArgument(commandContext, "item"), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count")))))));
    }

    private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        for (ServerPlayerEntity lv : targets) {
            int j = count;
            while (j > 0) {
                int k = Math.min(item.getItem().getMaxCount(), j);
                j -= k;
                ItemStack lv2 = item.createStack(k, false);
                boolean bl = lv.inventory.insertStack(lv2);
                if (!bl || !lv2.isEmpty()) {
                    ItemEntity lv3 = lv.dropItem(lv2, false);
                    if (lv3 == null) continue;
                    lv3.resetPickupDelay();
                    lv3.setOwner(lv.getUuid());
                    continue;
                }
                lv2.setCount(1);
                ItemEntity lv4 = lv.dropItem(lv2, false);
                if (lv4 != null) {
                    lv4.setDespawnImmediately();
                }
                lv.world.playSound(null, lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((lv.getRandom().nextFloat() - lv.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                lv.playerScreenHandler.sendContentUpdates();
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }
}

