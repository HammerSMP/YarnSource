/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class MessageArgumentType
implements ArgumentType<MessageFormat> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

    public static MessageArgumentType message() {
        return new MessageArgumentType();
    }

    public static Text getMessage(CommandContext<ServerCommandSource> command, String name) throws CommandSyntaxException {
        return ((MessageFormat)command.getArgument(name, MessageFormat.class)).format((ServerCommandSource)command.getSource(), ((ServerCommandSource)command.getSource()).hasPermissionLevel(2));
    }

    public MessageFormat parse(StringReader stringReader) throws CommandSyntaxException {
        return MessageFormat.parse(stringReader, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class MessageSelector {
        private final int start;
        private final int end;
        private final EntitySelector selector;

        public MessageSelector(int i, int j, EntitySelector arg) {
            this.start = i;
            this.end = j;
            this.selector = arg;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        @Nullable
        public Text format(ServerCommandSource arg) throws CommandSyntaxException {
            return EntitySelector.getNames(this.selector.getEntities(arg));
        }
    }

    public static class MessageFormat {
        private final String contents;
        private final MessageSelector[] selectors;

        public MessageFormat(String string, MessageSelector[] args) {
            this.contents = string;
            this.selectors = args;
        }

        public Text format(ServerCommandSource arg, boolean bl) throws CommandSyntaxException {
            if (this.selectors.length == 0 || !bl) {
                return new LiteralText(this.contents);
            }
            LiteralText lv = new LiteralText(this.contents.substring(0, this.selectors[0].getStart()));
            int i = this.selectors[0].getStart();
            for (MessageSelector lv2 : this.selectors) {
                Text lv3 = lv2.format(arg);
                if (i < lv2.getStart()) {
                    lv.append(this.contents.substring(i, lv2.getStart()));
                }
                if (lv3 != null) {
                    lv.append(lv3);
                }
                i = lv2.getEnd();
            }
            if (i < this.contents.length()) {
                lv.append(this.contents.substring(i, this.contents.length()));
            }
            return lv;
        }

        /*
         * WARNING - void declaration
         */
        public static MessageFormat parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
            String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
            if (!bl) {
                stringReader.setCursor(stringReader.getTotalLength());
                return new MessageFormat(string, new MessageSelector[0]);
            }
            ArrayList list = Lists.newArrayList();
            int i = stringReader.getCursor();
            while (stringReader.canRead()) {
                if (stringReader.peek() == '@') {
                    void lv3;
                    int j = stringReader.getCursor();
                    try {
                        EntitySelectorReader lv = new EntitySelectorReader(stringReader);
                        EntitySelector lv2 = lv.read();
                    }
                    catch (CommandSyntaxException commandSyntaxException) {
                        if (commandSyntaxException.getType() == EntitySelectorReader.MISSING_EXCEPTION || commandSyntaxException.getType() == EntitySelectorReader.UNKNOWN_SELECTOR_EXCEPTION) {
                            stringReader.setCursor(j + 1);
                            continue;
                        }
                        throw commandSyntaxException;
                    }
                    list.add(new MessageSelector(j - i, stringReader.getCursor() - i, (EntitySelector)lv3));
                    continue;
                }
                stringReader.skip();
            }
            return new MessageFormat(string, list.toArray(new MessageSelector[list.size()]));
        }
    }
}

