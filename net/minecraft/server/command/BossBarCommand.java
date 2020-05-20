/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class BossBarCommand {
    private static final DynamicCommandExceptionType CREATE_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.bossbar.create.failed", object));
    private static final DynamicCommandExceptionType UNKNOWN_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.bossbar.unknown", object));
    private static final SimpleCommandExceptionType SET_PLAYERS_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType SET_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType SET_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType SET_STYLE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType SET_VALUE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType SET_MAX_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getBossBarManager().getIds(), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("bossbar").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("add").then(CommandManager.argument("id", IdentifierArgumentType.identifier()).then(CommandManager.argument("name", TextArgumentType.text()).executes(commandContext -> BossBarCommand.addBossBar((ServerCommandSource)commandContext.getSource(), IdentifierArgumentType.getIdentifier((CommandContext<ServerCommandSource>)commandContext, "id"), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "name"))))))).then(CommandManager.literal("remove").then(CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> BossBarCommand.removeBossBar((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext)))))).then(CommandManager.literal("list").executes(commandContext -> BossBarCommand.listBossBars((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(CommandManager.literal("name").then(CommandManager.argument("name", TextArgumentType.text()).executes(commandContext -> BossBarCommand.setName((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), TextArgumentType.getTextArgument((CommandContext<ServerCommandSource>)commandContext, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("color").then(CommandManager.literal("pink").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.PINK)))).then(CommandManager.literal("blue").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.BLUE)))).then(CommandManager.literal("red").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.RED)))).then(CommandManager.literal("green").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.GREEN)))).then(CommandManager.literal("yellow").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.YELLOW)))).then(CommandManager.literal("purple").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.PURPLE)))).then(CommandManager.literal("white").executes(commandContext -> BossBarCommand.setColor((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Color.WHITE))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("style").then(CommandManager.literal("progress").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.PROGRESS)))).then(CommandManager.literal("notched_6").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.NOTCHED_6)))).then(CommandManager.literal("notched_10").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.NOTCHED_10)))).then(CommandManager.literal("notched_12").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.NOTCHED_12)))).then(CommandManager.literal("notched_20").executes(commandContext -> BossBarCommand.setStyle((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BossBar.Style.NOTCHED_20))))).then(CommandManager.literal("value").then(CommandManager.argument("value", IntegerArgumentType.integer((int)0)).executes(commandContext -> BossBarCommand.setValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))).then(CommandManager.literal("max").then(CommandManager.argument("max", IntegerArgumentType.integer((int)1)).executes(commandContext -> BossBarCommand.setMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"max")))))).then(CommandManager.literal("visible").then(CommandManager.argument("visible", BoolArgumentType.bool()).executes(commandContext -> BossBarCommand.setVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), BoolArgumentType.getBool((CommandContext)commandContext, (String)"visible")))))).then(((LiteralArgumentBuilder)CommandManager.literal("players").executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), Collections.emptyList()))).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> BossBarCommand.setPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext), EntityArgumentType.getOptionalPlayers((CommandContext<ServerCommandSource>)commandContext, "targets")))))))).then(CommandManager.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(CommandManager.literal("value").executes(commandContext -> BossBarCommand.getValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("max").executes(commandContext -> BossBarCommand.getMaxValue((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("visible").executes(commandContext -> BossBarCommand.isVisible((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext))))).then(CommandManager.literal("players").executes(commandContext -> BossBarCommand.getPlayers((ServerCommandSource)commandContext.getSource(), BossBarCommand.getBossBar((CommandContext<ServerCommandSource>)commandContext)))))));
    }

    private static int getValue(ServerCommandSource arg, CommandBossBar arg2) {
        arg.sendFeedback(new TranslatableText("commands.bossbar.get.value", arg2.toHoverableText(), arg2.getValue()), true);
        return arg2.getValue();
    }

    private static int getMaxValue(ServerCommandSource arg, CommandBossBar arg2) {
        arg.sendFeedback(new TranslatableText("commands.bossbar.get.max", arg2.toHoverableText(), arg2.getMaxValue()), true);
        return arg2.getMaxValue();
    }

    private static int isVisible(ServerCommandSource arg, CommandBossBar arg2) {
        if (arg2.isVisible()) {
            arg.sendFeedback(new TranslatableText("commands.bossbar.get.visible.visible", arg2.toHoverableText()), true);
            return 1;
        }
        arg.sendFeedback(new TranslatableText("commands.bossbar.get.visible.hidden", arg2.toHoverableText()), true);
        return 0;
    }

    private static int getPlayers(ServerCommandSource arg, CommandBossBar arg2) {
        if (arg2.getPlayers().isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.bossbar.get.players.none", arg2.toHoverableText()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.bossbar.get.players.some", arg2.toHoverableText(), arg2.getPlayers().size(), Texts.join(arg2.getPlayers(), PlayerEntity::getDisplayName)), true);
        }
        return arg2.getPlayers().size();
    }

    private static int setVisible(ServerCommandSource arg, CommandBossBar arg2, boolean bl) throws CommandSyntaxException {
        if (arg2.isVisible() == bl) {
            if (bl) {
                throw SET_VISIBILITY_UNCHANGED_VISIBLE_EXCEPTION.create();
            }
            throw SET_VISIBILITY_UNCHANGED_HIDDEN_EXCEPTION.create();
        }
        arg2.setVisible(bl);
        if (bl) {
            arg.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.visible", arg2.toHoverableText()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.bossbar.set.visible.success.hidden", arg2.toHoverableText()), true);
        }
        return 0;
    }

    private static int setValue(ServerCommandSource arg, CommandBossBar arg2, int i) throws CommandSyntaxException {
        if (arg2.getValue() == i) {
            throw SET_VALUE_UNCHANGED_EXCEPTION.create();
        }
        arg2.setValue(i);
        arg.sendFeedback(new TranslatableText("commands.bossbar.set.value.success", arg2.toHoverableText(), i), true);
        return i;
    }

    private static int setMaxValue(ServerCommandSource arg, CommandBossBar arg2, int i) throws CommandSyntaxException {
        if (arg2.getMaxValue() == i) {
            throw SET_MAX_UNCHANGED_EXCEPTION.create();
        }
        arg2.setMaxValue(i);
        arg.sendFeedback(new TranslatableText("commands.bossbar.set.max.success", arg2.toHoverableText(), i), true);
        return i;
    }

    private static int setColor(ServerCommandSource arg, CommandBossBar arg2, BossBar.Color arg3) throws CommandSyntaxException {
        if (arg2.getColor().equals((Object)arg3)) {
            throw SET_COLOR_UNCHANGED_EXCEPTION.create();
        }
        arg2.setColor(arg3);
        arg.sendFeedback(new TranslatableText("commands.bossbar.set.color.success", arg2.toHoverableText()), true);
        return 0;
    }

    private static int setStyle(ServerCommandSource arg, CommandBossBar arg2, BossBar.Style arg3) throws CommandSyntaxException {
        if (arg2.getOverlay().equals((Object)arg3)) {
            throw SET_STYLE_UNCHANGED_EXCEPTION.create();
        }
        arg2.setOverlay(arg3);
        arg.sendFeedback(new TranslatableText("commands.bossbar.set.style.success", arg2.toHoverableText()), true);
        return 0;
    }

    private static int setName(ServerCommandSource arg, CommandBossBar arg2, Text arg3) throws CommandSyntaxException {
        MutableText lv = Texts.parse(arg, arg3, null, 0);
        if (arg2.getName().equals(lv)) {
            throw SET_NAME_UNCHANGED_EXCEPTION.create();
        }
        arg2.setName(lv);
        arg.sendFeedback(new TranslatableText("commands.bossbar.set.name.success", arg2.toHoverableText()), true);
        return 0;
    }

    private static int setPlayers(ServerCommandSource arg, CommandBossBar arg2, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
        boolean bl = arg2.addPlayers(collection);
        if (!bl) {
            throw SET_PLAYERS_UNCHANGED_EXCEPTION.create();
        }
        if (arg2.getPlayers().isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.bossbar.set.players.success.none", arg2.toHoverableText()), true);
        } else {
            arg.sendFeedback(new TranslatableText("commands.bossbar.set.players.success.some", arg2.toHoverableText(), collection.size(), Texts.join(collection, PlayerEntity::getDisplayName)), true);
        }
        return arg2.getPlayers().size();
    }

    private static int listBossBars(ServerCommandSource arg) {
        Collection<CommandBossBar> collection = arg.getMinecraftServer().getBossBarManager().getAll();
        if (collection.isEmpty()) {
            arg.sendFeedback(new TranslatableText("commands.bossbar.list.bars.none"), false);
        } else {
            arg.sendFeedback(new TranslatableText("commands.bossbar.list.bars.some", collection.size(), Texts.join(collection, CommandBossBar::toHoverableText)), false);
        }
        return collection.size();
    }

    private static int addBossBar(ServerCommandSource arg, Identifier arg2, Text arg3) throws CommandSyntaxException {
        BossBarManager lv = arg.getMinecraftServer().getBossBarManager();
        if (lv.get(arg2) != null) {
            throw CREATE_FAILED_EXCEPTION.create((Object)arg2.toString());
        }
        CommandBossBar lv2 = lv.add(arg2, Texts.parse(arg, arg3, null, 0));
        arg.sendFeedback(new TranslatableText("commands.bossbar.create.success", lv2.toHoverableText()), true);
        return lv.getAll().size();
    }

    private static int removeBossBar(ServerCommandSource arg, CommandBossBar arg2) {
        BossBarManager lv = arg.getMinecraftServer().getBossBarManager();
        arg2.clearPlayers();
        lv.remove(arg2);
        arg.sendFeedback(new TranslatableText("commands.bossbar.remove.success", arg2.toHoverableText()), true);
        return lv.getAll().size();
    }

    public static CommandBossBar getBossBar(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        Identifier lv = IdentifierArgumentType.getIdentifier(commandContext, "id");
        CommandBossBar lv2 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getBossBarManager().get(lv);
        if (lv2 == null) {
            throw UNKNOWN_EXCEPTION.create((Object)lv.toString());
        }
        return lv2;
    }
}

