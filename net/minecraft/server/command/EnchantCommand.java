/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
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
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemEnchantmentArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class EnchantCommand {
    private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.enchant.failed.entity", object));
    private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.enchant.failed.itemless", object));
    private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.enchant.failed.incompatible", object));
    private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.enchant.failed.level", object, object2));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.enchant.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("enchant").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.entities()).then(((RequiredArgumentBuilder)CommandManager.argument("enchantment", ItemEnchantmentArgumentType.itemEnchantment()).executes(commandContext -> EnchantCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemEnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)commandContext, "enchantment"), 1))).then(CommandManager.argument("level", IntegerArgumentType.integer((int)0)).executes(commandContext -> EnchantCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities((CommandContext<ServerCommandSource>)commandContext, "targets"), ItemEnchantmentArgumentType.getEnchantment((CommandContext<ServerCommandSource>)commandContext, "enchantment"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"level")))))));
    }

    private static int execute(ServerCommandSource arg, Collection<? extends Entity> collection, Enchantment arg2, int i) throws CommandSyntaxException {
        if (i > arg2.getMaxLevel()) {
            throw FAILED_LEVEL_EXCEPTION.create((Object)i, (Object)arg2.getMaxLevel());
        }
        int j = 0;
        for (Entity entity : collection) {
            if (entity instanceof LivingEntity) {
                LivingEntity lv2 = (LivingEntity)entity;
                ItemStack lv3 = lv2.getMainHandStack();
                if (!lv3.isEmpty()) {
                    if (arg2.isAcceptableItem(lv3) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(lv3).keySet(), arg2)) {
                        lv3.addEnchantment(arg2, i);
                        ++j;
                        continue;
                    }
                    if (collection.size() != 1) continue;
                    throw FAILED_INCOMPATIBLE_EXCEPTION.create((Object)lv3.getItem().getName(lv3).getString());
                }
                if (collection.size() != 1) continue;
                throw FAILED_ITEMLESS_EXCEPTION.create((Object)lv2.getName().getString());
            }
            if (collection.size() != 1) continue;
            throw FAILED_ENTITY_EXCEPTION.create((Object)entity.getName().getString());
        }
        if (j == 0) {
            throw FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText("commands.enchant.success.single", arg2.getName(i), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.enchant.success.multiple", arg2.getName(i), collection.size()), true);
        }
        return j;
    }
}

