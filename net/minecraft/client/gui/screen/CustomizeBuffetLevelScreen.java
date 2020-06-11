/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private final Screen field_24562;
    private final Consumer<Biome> field_24563;
    private BuffetBiomesListWidget biomeSelectionList;
    private Biome field_25040;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen arg, Consumer<Biome> consumer, Biome arg2) {
        super(new TranslatableText("createWorld.customize.buffet.title"));
        this.field_24562 = arg;
        this.field_24563 = consumer;
        this.field_25040 = arg2;
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.field_24562);
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(arg -> Objects.equals(((BuffetBiomesListWidget.BuffetBiomeItem)arg).field_24564, this.field_25040)).findFirst().orElse(null));
        this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, arg -> {
            this.field_24563.accept(this.field_25040);
            this.client.openScreen(this.field_24562);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.field_24562)));
        this.refreshConfirmButton();
    }

    private void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.biomeSelectionList.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("createWorld.customize.buffet.biome", new Object[0]), this.width / 2, 28, 0xA0A0A0);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 40, CustomizeBuffetLevelScreen.this.height - 37, 16);
            Registry.BIOME.stream().sorted(Comparator.comparing(arg -> arg.getName().getString())).forEach(arg -> this.addEntry(new BuffetBiomeItem((Biome)arg)));
        }

        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable BuffetBiomeItem arg) {
            super.setSelected(arg);
            if (arg != null) {
                CustomizeBuffetLevelScreen.this.field_25040 = arg.field_24564;
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", arg.field_24564.getName().getString()).getString());
            }
        }

        @Override
        protected void moveSelection(EntryListWidget.class_5403 arg) {
            super.moveSelection(arg);
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            private final Biome field_24564;

            public BuffetBiomeItem(Biome arg2) {
                this.field_24564 = arg2;
            }

            @Override
            public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                BuffetBiomesListWidget.this.drawStringWithShadow(arg, CustomizeBuffetLevelScreen.this.textRenderer, this.field_24564.getName().getString(), k + 5, j + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    BuffetBiomesListWidget.this.setSelected(this);
                    CustomizeBuffetLevelScreen.this.refreshConfirmButton();
                    return true;
                }
                return false;
            }
        }
    }
}

