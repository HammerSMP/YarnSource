/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LocateCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.locate.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("locate").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.literal("Pillager_Outpost").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Pillager_Outpost")))).then(CommandManager.literal("Mineshaft").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Mineshaft")))).then(CommandManager.literal("Mansion").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Mansion")))).then(CommandManager.literal("Igloo").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Igloo")))).then(CommandManager.literal("Ruined_Portal").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Ruined_Portal")))).then(CommandManager.literal("Desert_Pyramid").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Desert_Pyramid")))).then(CommandManager.literal("Jungle_Pyramid").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Jungle_Pyramid")))).then(CommandManager.literal("Swamp_Hut").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Swamp_Hut")))).then(CommandManager.literal("Stronghold").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Stronghold")))).then(CommandManager.literal("Monument").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Monument")))).then(CommandManager.literal("Fortress").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Fortress")))).then(CommandManager.literal("EndCity").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "EndCity")))).then(CommandManager.literal("Ocean_Ruin").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Ocean_Ruin")))).then(CommandManager.literal("Buried_Treasure").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Buried_Treasure")))).then(CommandManager.literal("Shipwreck").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Shipwreck")))).then(CommandManager.literal("Village").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Village")))).then(CommandManager.literal("Nether_Fossil").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Nether_Fossil")))).then(CommandManager.literal("Bastion_Remnant").executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), "Bastion_Remnant"))));
    }

    private static int execute(ServerCommandSource arg, String string) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(arg.getPosition());
        BlockPos lv2 = arg.getWorld().locateStructure(string, lv, 100, false);
        if (lv2 == null) {
            throw FAILED_EXCEPTION.create();
        }
        return LocateCommand.sendCoordinates(arg, string, lv, lv2, "commands.locate.success");
    }

    public static int sendCoordinates(ServerCommandSource arg, String string, BlockPos arg22, BlockPos arg3, String string2) {
        int i = MathHelper.floor(LocateCommand.getDistance(arg22.getX(), arg22.getZ(), arg3.getX(), arg3.getZ()));
        MutableText lv = Texts.bracketed(new TranslatableText("chat.coordinates", arg3.getX(), "~", arg3.getZ())).styled(arg2 -> arg2.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + arg3.getX() + " ~ " + arg3.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
        arg.sendFeedback(new TranslatableText(string2, string, lv, i), false);
        return i;
    }

    private static float getDistance(int i, int j, int k, int l) {
        int m = k - i;
        int n = l - j;
        return MathHelper.sqrt(m * m + n * n);
    }
}

