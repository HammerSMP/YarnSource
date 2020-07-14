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
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class DatapackCommand {
    private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.unknown", object));
    private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.enable.failed", object));
    private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.disable.failed", object));
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackManager().getEnabledNames().stream().map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        ResourcePackManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackManager();
        Collection<String> collection = lv.getEnabledNames();
        return CommandSource.suggestMatching(lv.getNames().stream().filter(string -> !collection.contains(string)).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", StringArgumentType.string()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, arg2) -> arg2.getInitialPosition().insert(list, arg2, arg -> arg, false)))).then(CommandManager.literal("after").then(CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, arg) -> list.add(list.indexOf(DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "existing", false)) + 1, arg)))))).then(CommandManager.literal("before").then(CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, arg) -> list.add(list.indexOf(DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "existing", false)), arg)))))).then(CommandManager.literal("last").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), List::add)))).then(CommandManager.literal("first").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", true), (list, arg) -> list.add(0, arg))))))).then(CommandManager.literal("disable").then(CommandManager.argument("name", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeDisable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer((CommandContext<ServerCommandSource>)commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> DatapackCommand.executeList((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("available").executes(commandContext -> DatapackCommand.executeListAvailable((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("enabled").executes(commandContext -> DatapackCommand.executeListEnabled((ServerCommandSource)commandContext.getSource())))));
    }

    private static int executeEnable(ServerCommandSource source, ResourcePackProfile container, PackAdder packAdder) throws CommandSyntaxException {
        ResourcePackManager lv = source.getMinecraftServer().getDataPackManager();
        ArrayList list = Lists.newArrayList(lv.getEnabledProfiles());
        packAdder.apply(list, container);
        source.sendFeedback(new TranslatableText("commands.datapack.modify.enable", container.getInformationText(true)), true);
        ReloadCommand.method_29480(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeDisable(ServerCommandSource source, ResourcePackProfile container) {
        ResourcePackManager lv = source.getMinecraftServer().getDataPackManager();
        ArrayList list = Lists.newArrayList(lv.getEnabledProfiles());
        list.remove(container);
        source.sendFeedback(new TranslatableText("commands.datapack.modify.disable", container.getInformationText(true)), true);
        ReloadCommand.method_29480(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeList(ServerCommandSource source) {
        return DatapackCommand.executeListEnabled(source) + DatapackCommand.executeListAvailable(source);
    }

    private static int executeListAvailable(ServerCommandSource source) {
        ResourcePackManager lv = source.getMinecraftServer().getDataPackManager();
        lv.scanPacks();
        Collection<ResourcePackProfile> collection = lv.getEnabledProfiles();
        Collection<ResourcePackProfile> collection2 = lv.getProfiles();
        List list = collection2.stream().filter(arg -> !collection.contains(arg)).collect(Collectors.toList());
        if (list.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.datapack.list.available.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.datapack.list.available.success", list.size(), Texts.join(list, arg -> arg.getInformationText(false))), false);
        }
        return list.size();
    }

    private static int executeListEnabled(ServerCommandSource source) {
        ResourcePackManager lv = source.getMinecraftServer().getDataPackManager();
        lv.scanPacks();
        Collection<ResourcePackProfile> collection = lv.getEnabledProfiles();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.datapack.list.enabled.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.datapack.list.enabled.success", collection.size(), Texts.join(collection, arg -> arg.getInformationText(true))), false);
        }
        return collection.size();
    }

    private static ResourcePackProfile getPackContainer(CommandContext<ServerCommandSource> context, String name, boolean enable) throws CommandSyntaxException {
        String string2 = StringArgumentType.getString(context, (String)name);
        ResourcePackManager lv = ((ServerCommandSource)context.getSource()).getMinecraftServer().getDataPackManager();
        ResourcePackProfile lv2 = lv.getProfile(string2);
        if (lv2 == null) {
            throw UNKNOWN_DATAPACK_EXCEPTION.create((Object)string2);
        }
        boolean bl2 = lv.getEnabledProfiles().contains(lv2);
        if (enable && bl2) {
            throw ALREADY_ENABLED_EXCEPTION.create((Object)string2);
        }
        if (!enable && !bl2) {
            throw ALREADY_DISABLED_EXCEPTION.create((Object)string2);
        }
        return lv2;
    }

    static interface PackAdder {
        public void apply(List<ResourcePackProfile> var1, ResourcePackProfile var2) throws CommandSyntaxException;
    }
}

