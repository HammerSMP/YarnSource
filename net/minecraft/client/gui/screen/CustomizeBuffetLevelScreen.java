/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5285;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final class_5285.class_5286[] field_24561 = class_5285.class_5286.values();
    private final Screen field_24562;
    private final Consumer<Pair<class_5285.class_5286, Set<Biome>>> field_24563;
    private BuffetBiomesListWidget biomeSelectionList;
    private int biomeListLength;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen arg, Consumer<Pair<class_5285.class_5286, Set<Biome>>> consumer, Pair<class_5285.class_5286, Set<Biome>> pair) {
        super(new TranslatableText("createWorld.customize.buffet.title"));
        this.field_24562 = arg;
        this.field_24563 = consumer;
        for (int i = 0; i < field_24561.length; ++i) {
            if (!field_24561[i].equals(pair.getFirst())) continue;
            this.biomeListLength = i;
            break;
        }
        for (Biome lv : (Set)pair.getSecond()) {
            this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(arg2 -> Objects.equals(((BuffetBiomesListWidget.BuffetBiomeItem)arg2).field_24564, lv)).findFirst().orElse(null));
        }
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20, field_24561[this.biomeListLength].method_28043(), arg -> {
            ++this.biomeListLength;
            if (this.biomeListLength >= field_24561.length) {
                this.biomeListLength = 0;
            }
            arg.setMessage(field_24561[this.biomeListLength].method_28043());
        }));
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, arg -> {
            this.field_24563.accept((Pair<class_5285.class_5286, Set<Biome>>)Pair.of((Object)((Object)field_24561[this.biomeListLength]), (Object)ImmutableSet.of((Object)((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.getSelected()).field_24564)));
            this.client.openScreen(this.field_24562);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.field_24562)));
        this.refreshConfirmButton();
    }

    public void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.biomeSelectionList.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("createWorld.customize.buffet.generator", new Object[0]), this.width / 2, 30, 0xA0A0A0);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("createWorld.customize.buffet.biome", new Object[0]), this.width / 2, 68, 0xA0A0A0);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 80, CustomizeBuffetLevelScreen.this.height - 37, 16);
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
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", arg.field_24564.getName().getString()).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
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

