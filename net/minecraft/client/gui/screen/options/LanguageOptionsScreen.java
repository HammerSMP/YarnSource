/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.options;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen
extends GameOptionsScreen {
    private LanguageSelectionListWidget languageSelectionList;
    private final LanguageManager languageManager;
    private OptionButtonWidget forceUnicodeButton;
    private ButtonWidget doneButton;

    public LanguageOptionsScreen(Screen arg, GameOptions arg2, LanguageManager arg3) {
        super(arg, arg2, new TranslatableText("options.language"));
        this.languageManager = arg3;
    }

    @Override
    protected void init() {
        this.languageSelectionList = new LanguageSelectionListWidget(this.client);
        this.children.add(this.languageSelectionList);
        this.forceUnicodeButton = this.addButton(new OptionButtonWidget(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions), arg -> {
            Option.FORCE_UNICODE_FONT.set(this.gameOptions);
            this.gameOptions.write();
            arg.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions));
            this.client.onResolutionChanged();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, ScreenTexts.DONE, arg -> {
            LanguageSelectionListWidget.LanguageEntry lv = (LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelected();
            if (lv != null && !lv.languageDefinition.getCode().equals(this.languageManager.getLanguage().getCode())) {
                this.languageManager.setLanguage(lv.languageDefinition);
                this.gameOptions.language = lv.languageDefinition.getCode();
                this.client.reloadResources();
                this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
                this.doneButton.setMessage(ScreenTexts.DONE);
                this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getDisplayString(this.gameOptions));
                this.gameOptions.write();
            }
            this.client.openScreen(this.parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.languageSelectionList.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 16, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, "(" + I18n.translate("options.languageWarning", new Object[0]) + ")", this.width / 2, this.height - 56, 0x808080);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class LanguageSelectionListWidget
    extends AlwaysSelectedEntryListWidget<LanguageEntry> {
        public LanguageSelectionListWidget(MinecraftClient arg2) {
            super(arg2, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
            for (LanguageDefinition lv : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
                LanguageEntry lv2 = new LanguageEntry(lv);
                this.addEntry(lv2);
                if (!LanguageOptionsScreen.this.languageManager.getLanguage().getCode().equals(lv.getCode())) continue;
                this.setSelected(lv2);
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(@Nullable LanguageEntry arg) {
            super.setSelected(arg);
            if (arg != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", arg.languageDefinition).getString());
            }
        }

        @Override
        protected void renderBackground(MatrixStack arg) {
            LanguageOptionsScreen.this.renderBackground(arg);
        }

        @Override
        protected boolean isFocused() {
            return LanguageOptionsScreen.this.getFocused() == this;
        }

        @Environment(value=EnvType.CLIENT)
        public class LanguageEntry
        extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
            private final LanguageDefinition languageDefinition;

            public LanguageEntry(LanguageDefinition arg2) {
                this.languageDefinition = arg2;
            }

            @Override
            public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                LanguageOptionsScreen.this.textRenderer.setRightToLeft(true);
                LanguageSelectionListWidget.this.drawCenteredString(arg, LanguageOptionsScreen.this.textRenderer, this.languageDefinition.toString(), LanguageSelectionListWidget.this.width / 2, j + 1, 0xFFFFFF);
                LanguageOptionsScreen.this.textRenderer.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    this.onPressed();
                    return true;
                }
                return false;
            }

            private void onPressed() {
                LanguageSelectionListWidget.this.setSelected(this);
            }
        }
    }
}

