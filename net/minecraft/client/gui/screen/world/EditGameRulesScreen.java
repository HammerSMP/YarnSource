/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

@Environment(value=EnvType.CLIENT)
public class EditGameRulesScreen
extends Screen {
    private final Consumer<Optional<GameRules>> ruleSaver;
    private RuleListWidget ruleListWidget;
    private final Set<AbstractRuleWidget> invalidRuleWidgets = Sets.newHashSet();
    private ButtonWidget doneButton;
    @Nullable
    private List<StringRenderable> tooltip;
    private final GameRules gameRules;

    public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> ruleSaveConsumer) {
        super(new TranslatableText("editGamerule.title"));
        this.gameRules = gameRules;
        this.ruleSaver = ruleSaveConsumer;
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        super.init();
        this.ruleListWidget = new RuleListWidget(this.gameRules);
        this.children.add(this.ruleListWidget);
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.CANCEL, arg -> this.ruleSaver.accept(Optional.empty())));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, ScreenTexts.DONE, arg -> this.ruleSaver.accept(Optional.of(this.gameRules))));
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public void onClose() {
        this.ruleSaver.accept(Optional.empty());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltip = null;
        this.ruleListWidget.render(matrices, mouseX, mouseY, delta);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltip != null) {
            this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
        }
    }

    private void setTooltipDescription(@Nullable List<StringRenderable> description) {
        this.tooltip = description;
    }

    private void updateDoneButton() {
        this.doneButton.active = this.invalidRuleWidgets.isEmpty();
    }

    private void markInvalid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.add(ruleWidget);
        this.updateDoneButton();
    }

    private void markValid(AbstractRuleWidget ruleWidget) {
        this.invalidRuleWidgets.remove(ruleWidget);
        this.updateDoneButton();
    }

    @Environment(value=EnvType.CLIENT)
    public class RuleListWidget
    extends ElementListWidget<AbstractRuleWidget> {
        public RuleListWidget(final GameRules arg2) {
            super(EditGameRulesScreen.this.client, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
            final HashMap map = Maps.newHashMap();
            GameRules.accept(new GameRules.Visitor(){

                @Override
                public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
                    this.createRuleWidget(key, (arg, list, string, arg2) -> new BooleanRuleWidget(arg, list, string, (GameRules.BooleanRule)arg2));
                }

                @Override
                public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
                    this.createRuleWidget(key, (arg, list, string, arg2) -> new IntRuleWidget(arg, list, string, (GameRules.IntRule)arg2));
                }

                private <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.Key<T> key, RuleWidgetFactory<T> widgetFactory) {
                    String string4;
                    ImmutableList list2;
                    TranslatableText lv = new TranslatableText(key.getTranslationKey());
                    MutableText lv2 = new LiteralText(key.getName()).formatted(Formatting.YELLOW);
                    T lv3 = arg2.get(key);
                    String string = ((GameRules.Rule)lv3).serialize();
                    MutableText lv4 = new TranslatableText("editGamerule.default", new LiteralText(string)).formatted(Formatting.GRAY);
                    String string2 = key.getTranslationKey() + ".description";
                    if (I18n.hasTranslation(string2)) {
                        ImmutableList.Builder builder = ImmutableList.builder().add((Object)lv2);
                        TranslatableText lv5 = new TranslatableText(string2);
                        EditGameRulesScreen.this.textRenderer.wrapLines(lv5, 150).forEach(((ImmutableList.Builder)builder)::add);
                        ImmutableList list = builder.add((Object)lv4).build();
                        String string3 = lv5.getString() + "\n" + lv4.getString();
                    } else {
                        list2 = ImmutableList.of((Object)lv2, (Object)lv4);
                        string4 = lv4.getString();
                    }
                    map.computeIfAbsent(key.getCategory(), arg -> Maps.newHashMap()).put(key, widgetFactory.create(lv, (List<StringRenderable>)list2, string4, lv3));
                }
            });
            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry2 -> {
                this.addEntry(new RuleCategoryWidget(new TranslatableText(((GameRules.Category)((Object)((Object)entry2.getKey()))).getCategory()).formatted(Formatting.BOLD, Formatting.YELLOW)));
                ((Map)entry2.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getName))).forEach(entry -> this.addEntry((EntryListWidget.Entry)entry.getValue()));
            });
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            AbstractRuleWidget lv;
            super.render(matrices, mouseX, mouseY, delta);
            if (this.isMouseOver(mouseX, mouseY) && (lv = (AbstractRuleWidget)this.getEntryAtPosition(mouseX, mouseY)) != null) {
                EditGameRulesScreen.this.setTooltipDescription(lv.description);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class IntRuleWidget
    extends NamedRuleWidget {
        private final TextFieldWidget valueWidget;

        public IntRuleWidget(Text name, List<StringRenderable> description, String ruleName, GameRules.IntRule rule) {
            super(description, name);
            this.valueWidget = new TextFieldWidget(((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, 10, 5, 42, 20, name.shallowCopy().append("\n").append(ruleName).append("\n"));
            this.valueWidget.setText(Integer.toString(rule.get()));
            this.valueWidget.setChangedListener(string -> {
                if (rule.validate((String)string)) {
                    this.valueWidget.setEditableColor(0xE0E0E0);
                    EditGameRulesScreen.this.markValid(this);
                } else {
                    this.valueWidget.setEditableColor(0xFF0000);
                    EditGameRulesScreen.this.markInvalid(this);
                }
            });
            this.children.add(this.valueWidget);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.drawName(matrices, y, x);
            this.valueWidget.x = x + entryWidth - 44;
            this.valueWidget.y = y;
            this.valueWidget.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class BooleanRuleWidget
    extends NamedRuleWidget {
        private final ButtonWidget toggleButton;

        public BooleanRuleWidget(final Text name, List<StringRenderable> description, final String ruleName, final GameRules.BooleanRule arg3) {
            super(description, name);
            this.toggleButton = new ButtonWidget(10, 5, 44, 20, ScreenTexts.getToggleText(arg3.get()), arg2 -> {
                boolean bl = !arg3.get();
                arg3.set(bl, null);
                arg2.setMessage(ScreenTexts.getToggleText(arg3.get()));
            }){

                @Override
                protected MutableText getNarrationMessage() {
                    return ScreenTexts.method_30619(name, arg3.get()).append("\n").append(ruleName);
                }
            };
            this.children.add(this.toggleButton);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.drawName(matrices, y, x);
            this.toggleButton.x = x + entryWidth - 45;
            this.toggleButton.y = y;
            this.toggleButton.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public abstract class NamedRuleWidget
    extends AbstractRuleWidget {
        private final List<StringRenderable> name;
        protected final List<Element> children;

        public NamedRuleWidget(@Nullable List<StringRenderable> description, Text name) {
            super(description);
            this.children = Lists.newArrayList();
            this.name = ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.wrapLines(name, 175);
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        protected void drawName(MatrixStack matrices, int x, int y) {
            if (this.name.size() == 1) {
                ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.draw(matrices, this.name.get(0), (float)y, (float)(x + 5), 0xFFFFFF);
            } else if (this.name.size() >= 2) {
                ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.draw(matrices, this.name.get(0), (float)y, (float)x, 0xFFFFFF);
                ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer.draw(matrices, this.name.get(1), (float)y, (float)(x + 10), 0xFFFFFF);
            }
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface RuleWidgetFactory<T extends GameRules.Rule<T>> {
        public AbstractRuleWidget create(Text var1, List<StringRenderable> var2, String var3, T var4);
    }

    @Environment(value=EnvType.CLIENT)
    public class RuleCategoryWidget
    extends AbstractRuleWidget {
        private final Text name;

        public RuleCategoryWidget(Text text) {
            super(null);
            this.name = text;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            EditGameRulesScreen.this.drawCenteredText(matrices, ((EditGameRulesScreen)EditGameRulesScreen.this).client.textRenderer, this.name, x + entryWidth / 2, y + 5, 0xFFFFFF);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public abstract class AbstractRuleWidget
    extends ElementListWidget.Entry<AbstractRuleWidget> {
        @Nullable
        private final List<StringRenderable> description;

        public AbstractRuleWidget(@Nullable List<StringRenderable> description) {
            this.description = description;
        }
    }
}

