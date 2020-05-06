/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class AdvancementCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        Collection<Advancement> collection = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements();
        return CommandSource.suggestIdentifiers(collection.stream().map(Advancement::getId), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.GRANT, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements())))))).then(CommandManager.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.ONLY)))).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder)).executes(commandContext -> AdvancementCommand.executeCriterion((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), StringArgumentType.getString((CommandContext)commandContext, (String)"criterion"))))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.FROM)))))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.UNTIL)))))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, AdvancementCommand.select(IdentifierArgumentType.getAdvancementArgument((CommandContext<ServerCommandSource>)commandContext, "advancement"), Selection.THROUGH)))))).then(CommandManager.literal("everything").executes(commandContext -> AdvancementCommand.executeAdvancement((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers((CommandContext<ServerCommandSource>)commandContext, "targets"), Operation.REVOKE, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().getAdvancements()))))));
    }

    private static int executeAdvancement(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, Operation arg2, Collection<Advancement> collection2) {
        int i = 0;
        for (ServerPlayerEntity lv : collection) {
            i += arg2.processAll(lv, collection2);
        }
        if (i == 0) {
            if (collection2.size() == 1) {
                if (collection.size() == 1) {
                    throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".one.to.one.failure", collection2.iterator().next().toHoverableText(), collection.iterator().next().getDisplayName()));
                }
                throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".one.to.many.failure", collection2.iterator().next().toHoverableText(), collection.size()));
            }
            if (collection.size() == 1) {
                throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".many.to.one.failure", collection2.size(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".many.to.many.failure", collection2.size(), collection.size()));
        }
        if (collection2.size() == 1) {
            if (collection.size() == 1) {
                arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".one.to.one.success", collection2.iterator().next().toHoverableText(), collection.iterator().next().getDisplayName()), true);
            } else {
                arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".one.to.many.success", collection2.iterator().next().toHoverableText(), collection.size()), true);
            }
        } else if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".many.to.one.success", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".many.to.many.success", collection2.size(), collection.size()), true);
        }
        return i;
    }

    private static int executeCriterion(ServerCommandSource arg, Collection<ServerPlayerEntity> collection, Operation arg2, Advancement arg3, String string) {
        int i = 0;
        if (!arg3.getCriteria().containsKey(string)) {
            throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", arg3.toHoverableText(), string));
        }
        for (ServerPlayerEntity lv : collection) {
            if (!arg2.processEachCriterion(lv, arg3, string)) continue;
            ++i;
        }
        if (i == 0) {
            if (collection.size() == 1) {
                throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".criterion.to.one.failure", string, arg3.toHoverableText(), collection.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslatableText(arg2.getCommandPrefix() + ".criterion.to.many.failure", string, arg3.toHoverableText(), collection.size()));
        }
        if (collection.size() == 1) {
            arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".criterion.to.one.success", string, arg3.toHoverableText(), collection.iterator().next().getDisplayName()), true);
        } else {
            arg.sendFeedback(new TranslatableText(arg2.getCommandPrefix() + ".criterion.to.many.success", string, arg3.toHoverableText(), collection.size()), true);
        }
        return i;
    }

    private static List<Advancement> select(Advancement arg, Selection arg2) {
        ArrayList list = Lists.newArrayList();
        if (arg2.before) {
            for (Advancement lv = arg.getParent(); lv != null; lv = lv.getParent()) {
                list.add(lv);
            }
        }
        list.add(arg);
        if (arg2.after) {
            AdvancementCommand.addChildrenRecursivelyToList(arg, list);
        }
        return list;
    }

    private static void addChildrenRecursivelyToList(Advancement arg, List<Advancement> list) {
        for (Advancement lv : arg.getChildren()) {
            list.add(lv);
            AdvancementCommand.addChildrenRecursivelyToList(lv, list);
        }
    }

    static enum Selection {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean before;
        private final boolean after;

        private Selection(boolean bl, boolean bl2) {
            this.before = bl;
            this.after = bl2;
        }
    }

    static enum Operation {
        GRANT("grant"){

            @Override
            protected boolean processEach(ServerPlayerEntity arg, Advancement arg2) {
                AdvancementProgress lv = arg.getAdvancementTracker().getProgress(arg2);
                if (lv.isDone()) {
                    return false;
                }
                for (String string : lv.getUnobtainedCriteria()) {
                    arg.getAdvancementTracker().grantCriterion(arg2, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity arg, Advancement arg2, String string) {
                return arg.getAdvancementTracker().grantCriterion(arg2, string);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean processEach(ServerPlayerEntity arg, Advancement arg2) {
                AdvancementProgress lv = arg.getAdvancementTracker().getProgress(arg2);
                if (!lv.isAnyObtained()) {
                    return false;
                }
                for (String string : lv.getObtainedCriteria()) {
                    arg.getAdvancementTracker().revokeCriterion(arg2, string);
                }
                return true;
            }

            @Override
            protected boolean processEachCriterion(ServerPlayerEntity arg, Advancement arg2, String string) {
                return arg.getAdvancementTracker().revokeCriterion(arg2, string);
            }
        };

        private final String commandPrefix;

        private Operation(String string2) {
            this.commandPrefix = "commands.advancement." + string2;
        }

        public int processAll(ServerPlayerEntity arg, Iterable<Advancement> iterable) {
            int i = 0;
            for (Advancement lv : iterable) {
                if (!this.processEach(arg, lv)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean processEach(ServerPlayerEntity var1, Advancement var2);

        protected abstract boolean processEachCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

        protected String getCommandPrefix() {
            return this.commandPrefix;
        }
    }
}

