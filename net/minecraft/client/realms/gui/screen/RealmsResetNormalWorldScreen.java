/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsResetWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsResetNormalWorldScreen
extends RealmsScreen {
    private final RealmsResetWorldScreen parent;
    private RealmsLabel titleLabel;
    private TextFieldWidget seedEdit;
    private Boolean generateStructures = true;
    private Integer levelTypeIndex = 0;
    private Text[] field_24205 = new Text[]{new TranslatableText("generator.default"), new TranslatableText("generator.flat"), new TranslatableText("generator.large_biomes"), new TranslatableText("generator.amplified")};
    private Text field_24206;

    public RealmsResetNormalWorldScreen(RealmsResetWorldScreen parent, Text arg2) {
        this.parent = parent;
        this.field_24206 = arg2;
    }

    @Override
    public void tick() {
        this.seedEdit.tick();
        super.tick();
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.titleLabel = new RealmsLabel(new TranslatableText("mco.reset.world.generate"), this.width / 2, 17, 0xFFFFFF);
        this.addChild(this.titleLabel);
        this.seedEdit = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, RealmsResetNormalWorldScreen.row(2), 200, 20, null, new TranslatableText("mco.reset.world.seed"));
        this.seedEdit.setMaxLength(32);
        this.addChild(this.seedEdit);
        this.setInitialFocus(this.seedEdit);
        this.addButton(new ButtonWidget(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(4), 205, 20, this.method_27458(), arg -> {
            this.levelTypeIndex = (this.levelTypeIndex + 1) % this.field_24205.length;
            arg.setMessage(this.method_27458());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(6) - 2, 205, 20, this.method_27459(), arg -> {
            this.generateStructures = this.generateStructures == false;
            arg.setMessage(this.method_27459());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 102, RealmsResetNormalWorldScreen.row(12), 97, 20, this.field_24206, arg -> this.parent.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getText(), this.levelTypeIndex, this.generateStructures))));
        this.addButton(new ButtonWidget(this.width / 2 + 8, RealmsResetNormalWorldScreen.row(12), 97, 20, ScreenTexts.BACK, arg -> this.client.openScreen(this.parent)));
        this.narrateLabels();
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.titleLabel.render(this, matrices);
        this.textRenderer.draw(matrices, I18n.translate("mco.reset.world.seed", new Object[0]), (float)(this.width / 2 - 100), (float)RealmsResetNormalWorldScreen.row(1), 0xA0A0A0);
        this.seedEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private Text method_27458() {
        return new TranslatableText("selectWorld.mapType").append(" ").append(this.field_24205[this.levelTypeIndex]);
    }

    private Text method_27459() {
        return ScreenTexts.method_30619(new TranslatableText("selectWorld.mapFeatures"), this.generateStructures);
    }
}

