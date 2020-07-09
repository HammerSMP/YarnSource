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
import java.util.Map;
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
import net.minecraft.world.gen.feature.StructureFeature;

public class LocateCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("commands.locate.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("locate").requires(arg -> arg.hasPermissionLevel(2));
        for (Map.Entry entry : StructureFeature.STRUCTURES.entrySet()) {
            literalArgumentBuilder = (LiteralArgumentBuilder)literalArgumentBuilder.then(CommandManager.literal((String)entry.getKey()).executes(commandContext -> LocateCommand.execute((ServerCommandSource)commandContext.getSource(), (StructureFeature)entry.getValue())));
        }
        commandDispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource arg, StructureFeature<?> arg2) throws CommandSyntaxException {
        BlockPos lv = new BlockPos(arg.getPosition());
        BlockPos lv2 = arg.getWorld().locateStructure(arg2, lv, 100, false);
        if (lv2 == null) {
            throw FAILED_EXCEPTION.create();
        }
        return LocateCommand.sendCoordinates(arg, arg2.getName(), lv, lv2, "commands.locate.success");
    }

    public static int sendCoordinates(ServerCommandSource arg, String string, BlockPos arg22, BlockPos arg3, String string2) {
        int i = MathHelper.floor(LocateCommand.getDistance(arg22.getX(), arg22.getZ(), arg3.getX(), arg3.getZ()));
        MutableText lv = Texts.bracketed(new TranslatableText("chat.coordinates", arg3.getX(), "~", arg3.getZ())).styled(arg2 -> arg2.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + arg3.getX() + " ~ " + arg3.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
        arg.sendFeedback(new TranslatableText(string2, string, lv, i), false);
        return i;
    }

    private static float getDistance(int i, int j, int k, int l) {
        int m = k - i;
        int n = l - j;
        return MathHelper.sqrt(m * m + n * n);
    }
}

