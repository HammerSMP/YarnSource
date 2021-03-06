/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.brigadier.context.SuggestionContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(value=EnvType.CLIENT)
public class CommandSuggestor {
    private static final Pattern BACKSLASH_S_PATTERN = Pattern.compile("(\\s+)");
    private static final Style field_25885 = Style.EMPTY.withColor(Formatting.RED);
    private static final Style field_25886 = Style.EMPTY.withColor(Formatting.GRAY);
    private static final List<Style> field_25887 = (List)Stream.of(new Formatting[]{Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD}).map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
    private final MinecraftClient client;
    private final Screen owner;
    private final TextFieldWidget textField;
    private final TextRenderer textRenderer;
    private final boolean slashRequired;
    private final boolean suggestingWhenEmpty;
    private final int inWindowIndexOffset;
    private final int maxSuggestionSize;
    private final boolean chatScreenSized;
    private final int color;
    private final List<StringRenderable> messages = Lists.newArrayList();
    private int x;
    private int width;
    private ParseResults<CommandSource> parse;
    private CompletableFuture<Suggestions> pendingSuggestions;
    private SuggestionWindow window;
    private boolean windowActive;
    private boolean completingSuggestions;

    public CommandSuggestor(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, boolean slashRequired, boolean suggestingWhenEmpty, int inWindowIndexOffset, int maxSuggestionSize, boolean chatScreenSized, int color) {
        this.client = client;
        this.owner = owner;
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.slashRequired = slashRequired;
        this.suggestingWhenEmpty = suggestingWhenEmpty;
        this.inWindowIndexOffset = inWindowIndexOffset;
        this.maxSuggestionSize = maxSuggestionSize;
        this.chatScreenSized = chatScreenSized;
        this.color = color;
        textField.setRenderTextProvider((arg_0, arg_1) -> this.provideRenderText(arg_0, arg_1));
    }

    public void setWindowActive(boolean windowActive) {
        this.windowActive = windowActive;
        if (!windowActive) {
            this.window = null;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.window != null && this.window.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.owner.getFocused() == this.textField && keyCode == 258) {
            this.showSuggestions(true);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double amount) {
        return this.window != null && this.window.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.window != null && this.window.mouseClicked((int)mouseX, (int)mouseY, button);
    }

    public void showSuggestions(boolean narrateFirstSuggestion) {
        Suggestions suggestions;
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone() && !(suggestions = this.pendingSuggestions.join()).isEmpty()) {
            int i = 0;
            for (Suggestion suggestion : suggestions.getList()) {
                i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()));
            }
            int j = MathHelper.clamp(this.textField.getCharacterX(suggestions.getRange().getStart()), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
            int k = this.chatScreenSized ? this.owner.height - 12 : 72;
            this.window = new SuggestionWindow(j, k, i, this.method_30104(suggestions), narrateFirstSuggestion);
        }
    }

    private List<Suggestion> method_30104(Suggestions suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        int i = CommandSuggestor.getLastPlayerNameStart(string);
        String string2 = string.substring(i).toLowerCase(Locale.ROOT);
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (Suggestion suggestion : suggestions.getList()) {
            if (suggestion.getText().startsWith(string2) || suggestion.getText().startsWith("minecraft:" + string2)) {
                list.add(suggestion);
                continue;
            }
            list2.add(suggestion);
        }
        list.addAll(list2);
        return list;
    }

    public void refresh() {
        boolean bl;
        String string = this.textField.getText();
        if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
            this.parse = null;
        }
        if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
        }
        this.messages.clear();
        StringReader stringReader = new StringReader(string);
        boolean bl2 = bl = stringReader.canRead() && stringReader.peek() == '/';
        if (bl) {
            stringReader.skip();
        }
        boolean bl22 = this.slashRequired || bl;
        int i = this.textField.getCursor();
        if (bl22) {
            int j;
            CommandDispatcher<CommandSource> commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(stringReader, (Object)this.client.player.networkHandler.getCommandSource());
            }
            int n = j = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
            if (!(i < j || this.window != null && this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, i);
                this.pendingSuggestions.thenRun(() -> {
                    if (!this.pendingSuggestions.isDone()) {
                        return;
                    }
                    this.show();
                });
            }
        } else {
            String string2 = string.substring(0, i);
            int k = CommandSuggestor.getLastPlayerNameStart(string2);
            Collection<String> collection = this.client.player.networkHandler.getCommandSource().getPlayerNames();
            this.pendingSuggestions = CommandSource.suggestMatching(collection, new SuggestionsBuilder(string2, k));
        }
    }

    private static int getLastPlayerNameStart(String input) {
        if (Strings.isNullOrEmpty((String)input)) {
            return 0;
        }
        int i = 0;
        Matcher matcher = BACKSLASH_S_PATTERN.matcher(input);
        while (matcher.find()) {
            i = matcher.end();
        }
        return i;
    }

    private static StringRenderable method_30505(CommandSyntaxException commandSyntaxException) {
        Text lv = Texts.toText(commandSyntaxException.getRawMessage());
        String string = commandSyntaxException.getContext();
        if (string == null) {
            return lv;
        }
        return new TranslatableText("command.context.parse_error", lv, commandSyntaxException.getCursor(), string);
    }

    public void show() {
        if (this.textField.getCursor() == this.textField.getText().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
                int i = 0;
                for (Map.Entry entry : this.parse.getExceptions().entrySet()) {
                    CommandSyntaxException commandSyntaxException = (CommandSyntaxException)((Object)entry.getValue());
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++i;
                        continue;
                    }
                    this.messages.add(CommandSuggestor.method_30505(commandSyntaxException));
                }
                if (i > 0) {
                    this.messages.add(CommandSuggestor.method_30505(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else if (this.parse.getReader().canRead()) {
                this.messages.add(CommandSuggestor.method_30505(CommandManager.getException(this.parse)));
            }
        }
        this.x = 0;
        this.width = this.owner.width;
        if (this.messages.isEmpty()) {
            this.showUsages(Formatting.GRAY);
        }
        this.window = null;
        if (this.windowActive && this.client.options.autoSuggestions) {
            this.showSuggestions(false);
        }
    }

    private void showUsages(Formatting formatting) {
        CommandContextBuilder commandContextBuilder = this.parse.getContext();
        SuggestionContext suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
        Map map = this.client.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, (Object)this.client.player.networkHandler.getCommandSource());
        ArrayList list = Lists.newArrayList();
        int i = 0;
        Style lv = Style.EMPTY.withColor(formatting);
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() instanceof LiteralCommandNode) continue;
            list.add(StringRenderable.styled((String)entry.getValue(), lv));
            i = Math.max(i, this.textRenderer.getWidth((String)entry.getValue()));
        }
        if (!list.isEmpty()) {
            this.messages.addAll(list);
            this.x = MathHelper.clamp(this.textField.getCharacterX(suggestionContext.startPos), 0, this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i);
            this.width = i;
        }
    }

    private StringRenderable provideRenderText(String original, int firstCharacterIndex) {
        if (this.parse != null) {
            return CommandSuggestor.highlight(this.parse, original, firstCharacterIndex);
        }
        return StringRenderable.plain(original);
    }

    @Nullable
    private static String getSuggestionSuffix(String original, String suggestion) {
        if (suggestion.startsWith(original)) {
            return suggestion.substring(original.length());
        }
        return null;
    }

    private static StringRenderable highlight(ParseResults<CommandSource> parse, String original, int firstCharacterIndex) {
        int n;
        ArrayList list = Lists.newArrayList();
        int j = 0;
        int k = -1;
        CommandContextBuilder commandContextBuilder = parse.getContext().getLastChild();
        for (ParsedArgument parsedArgument : commandContextBuilder.getArguments().values()) {
            int l;
            if (++k >= field_25887.size()) {
                k = 0;
            }
            if ((l = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0)) >= original.length()) break;
            int m = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
            if (m <= 0) continue;
            list.add(StringRenderable.styled(original.substring(j, l), field_25886));
            list.add(StringRenderable.styled(original.substring(l, m), field_25887.get(k)));
            j = m;
        }
        if (parse.getReader().canRead() && (n = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0)) < original.length()) {
            int o = Math.min(n + parse.getReader().getRemainingLength(), original.length());
            list.add(StringRenderable.styled(original.substring(j, n), field_25886));
            list.add(StringRenderable.styled(original.substring(n, o), field_25885));
            j = o;
        }
        list.add(StringRenderable.styled(original.substring(j), field_25886));
        return StringRenderable.concat(list);
    }

    public void render(MatrixStack arg, int i, int j) {
        if (this.window != null) {
            this.window.render(arg, i, j);
        } else {
            int k = 0;
            for (StringRenderable lv : this.messages) {
                int l = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * k : 72 + 12 * k;
                DrawableHelper.fill(arg, this.x - 1, l, this.x + this.width + 1, l + 12, this.color);
                this.textRenderer.drawWithShadow(arg, lv, (float)this.x, (float)(l + 2), -1);
                ++k;
            }
        }
    }

    public String method_23958() {
        if (this.window != null) {
            return "\n" + this.window.getNarration();
        }
        return "";
    }

    @Environment(value=EnvType.CLIENT)
    public class SuggestionWindow {
        private final Rect2i area;
        private final String typedText;
        private final List<Suggestion> field_25709;
        private int inWindowIndex;
        private int selection;
        private Vec2f mouse = Vec2f.ZERO;
        private boolean completed;
        private int lastNarrationIndex;

        private SuggestionWindow(int x, int y, int width, List<Suggestion> list, boolean narrateFirstSuggestion) {
            int l = x - 1;
            int m = CommandSuggestor.this.chatScreenSized ? y - 3 - Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12 : y;
            this.area = new Rect2i(l, m, width + 1, Math.min(list.size(), CommandSuggestor.this.maxSuggestionSize) * 12);
            this.typedText = CommandSuggestor.this.textField.getText();
            this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
            this.field_25709 = list;
            this.select(0);
        }

        public void render(MatrixStack arg, int i, int j) {
            Message message;
            boolean bl4;
            int k = Math.min(this.field_25709.size(), CommandSuggestor.this.maxSuggestionSize);
            int l = -5592406;
            boolean bl = this.inWindowIndex > 0;
            boolean bl2 = this.field_25709.size() > this.inWindowIndex + k;
            boolean bl3 = bl || bl2;
            boolean bl5 = bl4 = this.mouse.x != (float)i || this.mouse.y != (float)j;
            if (bl4) {
                this.mouse = new Vec2f(i, j);
            }
            if (bl3) {
                DrawableHelper.fill(arg, this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), CommandSuggestor.this.color);
                DrawableHelper.fill(arg, this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, CommandSuggestor.this.color);
                if (bl) {
                    for (int m = 0; m < this.area.getWidth(); ++m) {
                        if (m % 2 != 0) continue;
                        DrawableHelper.fill(arg, this.area.getX() + m, this.area.getY() - 1, this.area.getX() + m + 1, this.area.getY(), -1);
                    }
                }
                if (bl2) {
                    for (int n = 0; n < this.area.getWidth(); ++n) {
                        if (n % 2 != 0) continue;
                        DrawableHelper.fill(arg, this.area.getX() + n, this.area.getY() + this.area.getHeight(), this.area.getX() + n + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                    }
                }
            }
            boolean bl52 = false;
            for (int o = 0; o < k; ++o) {
                Suggestion suggestion = this.field_25709.get(o + this.inWindowIndex);
                DrawableHelper.fill(arg, this.area.getX(), this.area.getY() + 12 * o, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * o + 12, CommandSuggestor.this.color);
                if (i > this.area.getX() && i < this.area.getX() + this.area.getWidth() && j > this.area.getY() + 12 * o && j < this.area.getY() + 12 * o + 12) {
                    if (bl4) {
                        this.select(o + this.inWindowIndex);
                    }
                    bl52 = true;
                }
                CommandSuggestor.this.textRenderer.drawWithShadow(arg, suggestion.getText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * o), o + this.inWindowIndex == this.selection ? -256 : -5592406);
            }
            if (bl52 && (message = this.field_25709.get(this.selection).getTooltip()) != null) {
                CommandSuggestor.this.owner.renderTooltip(arg, Texts.toText(message), i, j);
            }
        }

        public boolean mouseClicked(int x, int y, int button) {
            if (!this.area.contains(x, y)) {
                return false;
            }
            int l = (y - this.area.getY()) / 12 + this.inWindowIndex;
            if (l >= 0 && l < this.field_25709.size()) {
                this.select(l);
                this.complete();
            }
            return true;
        }

        public boolean mouseScrolled(double amount) {
            int j;
            int i = (int)(((CommandSuggestor)CommandSuggestor.this).client.mouse.getX() * (double)CommandSuggestor.this.client.getWindow().getScaledWidth() / (double)CommandSuggestor.this.client.getWindow().getWidth());
            if (this.area.contains(i, j = (int)(((CommandSuggestor)CommandSuggestor.this).client.mouse.getY() * (double)CommandSuggestor.this.client.getWindow().getScaledHeight() / (double)CommandSuggestor.this.client.getWindow().getHeight()))) {
                this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - amount), 0, Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0));
                return true;
            }
            return false;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 265) {
                this.scroll(-1);
                this.completed = false;
                return true;
            }
            if (keyCode == 264) {
                this.scroll(1);
                this.completed = false;
                return true;
            }
            if (keyCode == 258) {
                if (this.completed) {
                    this.scroll(Screen.hasShiftDown() ? -1 : 1);
                }
                this.complete();
                return true;
            }
            if (keyCode == 256) {
                this.discard();
                return true;
            }
            return false;
        }

        public void scroll(int offset) {
            this.select(this.selection + offset);
            int j = this.inWindowIndex;
            int k = this.inWindowIndex + CommandSuggestor.this.maxSuggestionSize - 1;
            if (this.selection < j) {
                this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0));
            } else if (this.selection > k) {
                this.inWindowIndex = MathHelper.clamp(this.selection + CommandSuggestor.this.inWindowIndexOffset - CommandSuggestor.this.maxSuggestionSize, 0, Math.max(this.field_25709.size() - CommandSuggestor.this.maxSuggestionSize, 0));
            }
        }

        public void select(int index) {
            this.selection = index;
            if (this.selection < 0) {
                this.selection += this.field_25709.size();
            }
            if (this.selection >= this.field_25709.size()) {
                this.selection -= this.field_25709.size();
            }
            Suggestion suggestion = this.field_25709.get(this.selection);
            CommandSuggestor.this.textField.setSuggestion(CommandSuggestor.getSuggestionSuffix(CommandSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
            if (NarratorManager.INSTANCE.isActive() && this.lastNarrationIndex != this.selection) {
                NarratorManager.INSTANCE.narrate(this.getNarration());
            }
        }

        public void complete() {
            Suggestion suggestion = this.field_25709.get(this.selection);
            CommandSuggestor.this.completingSuggestions = true;
            CommandSuggestor.this.textField.setText(suggestion.apply(this.typedText));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            CommandSuggestor.this.textField.setSelectionStart(i);
            CommandSuggestor.this.textField.setSelectionEnd(i);
            this.select(this.selection);
            CommandSuggestor.this.completingSuggestions = false;
            this.completed = true;
        }

        private String getNarration() {
            this.lastNarrationIndex = this.selection;
            Suggestion suggestion = this.field_25709.get(this.selection);
            Message message = suggestion.getTooltip();
            if (message != null) {
                return I18n.translate("narration.suggestion.tooltip", this.selection + 1, this.field_25709.size(), suggestion.getText(), message.getString());
            }
            return I18n.translate("narration.suggestion", this.selection + 1, this.field_25709.size(), suggestion.getText());
        }

        public void discard() {
            CommandSuggestor.this.window = null;
        }
    }
}

