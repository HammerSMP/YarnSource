/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SelectWorldScreen
extends Screen {
    protected final Screen parent;
    private List<Text> tooltipText;
    private ButtonWidget deleteButton;
    private ButtonWidget selectButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    protected TextFieldWidget searchBox;
    private WorldListWidget levelList;

    public SelectWorldScreen(Screen arg) {
        super(new TranslatableText("selectWorld.title"));
        this.parent = arg;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return super.mouseScrolled(d, e, f);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, new TranslatableText("selectWorld.search"));
        this.searchBox.setChangedListener(string -> this.levelList.filter(() -> string, false));
        this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getText(), this.levelList);
        this.children.add(this.searchBox);
        this.children.add(this.levelList);
        this.selectButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 150, 20, new TranslatableText("selectWorld.select"), arg -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::play)));
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 150, 20, new TranslatableText("selectWorld.create"), arg -> this.client.openScreen(new CreateWorldScreen(this))));
        this.editButton = this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 72, 20, new TranslatableText("selectWorld.edit"), arg -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::edit)));
        this.deleteButton = this.addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 72, 20, new TranslatableText("selectWorld.delete"), arg -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::delete)));
        this.recreateButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 72, 20, new TranslatableText("selectWorld.recreate"), arg -> this.levelList.method_20159().ifPresent(WorldListWidget.Entry::recreate)));
        this.addButton(new ButtonWidget(this.width / 2 + 82, this.height - 28, 72, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.worldSelected(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        return this.searchBox.keyPressed(i, j, k);
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.searchBox.charTyped(c, i);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.tooltipText = null;
        this.levelList.render(arg, i, j, f);
        this.searchBox.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(arg, i, j, f);
        if (this.tooltipText != null) {
            this.renderTooltip(arg, this.tooltipText, i, j);
        }
    }

    public void setTooltip(List<Text> list) {
        this.tooltipText = list;
    }

    public void worldSelected(boolean bl) {
        this.selectButton.active = bl;
        this.deleteButton.active = bl;
        this.editButton.active = bl;
        this.recreateButton.active = bl;
    }

    @Override
    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(WorldListWidget.Entry::close);
        }
    }
}

