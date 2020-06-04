/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand {
    private static final Logger field_25343 = LogManager.getLogger();

    public static void method_29480(Collection<String> collection, ServerCommandSource arg) {
        arg.getMinecraftServer().reloadResources(collection).exceptionally(throwable -> {
            field_25343.warn("Failed to execute reload", throwable);
            arg.sendError(new TranslatableText("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> method_29478(ResourcePackManager<?> arg, SaveProperties arg2, Collection<String> collection) {
        arg.scanPacks();
        ArrayList collection2 = Lists.newArrayList(collection);
        List<String> collection3 = arg2.method_29589().method_29550();
        for (String string : arg.method_29206()) {
            if (collection3.contains(string) || collection2.contains(string)) continue;
            collection2.add(string);
        }
        return collection2;
    }

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(arg -> arg.hasPermissionLevel(2))).executes(commandContext -> {
            ServerCommandSource lv = (ServerCommandSource)commandContext.getSource();
            MinecraftServer minecraftServer = lv.getMinecraftServer();
            ResourcePackManager<ResourcePackProfile> lv2 = minecraftServer.getDataPackManager();
            SaveProperties lv3 = minecraftServer.getSaveProperties();
            Collection<String> collection = lv2.method_29210();
            Collection<String> collection2 = ReloadCommand.method_29478(lv2, lv3, collection);
            lv.sendFeedback(new TranslatableText("commands.reload.success"), true);
            ReloadCommand.method_29480(collection2, lv);
            return 0;
        }));
    }
}

