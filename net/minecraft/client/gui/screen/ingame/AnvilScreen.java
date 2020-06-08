/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AnvilScreen
extends ForgingScreen<AnvilScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/anvil.png");
    private TextFieldWidget nameField;

    public AnvilScreen(AnvilScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3, TEXTURE);
        this.titleX = 60;
    }

    @Override
    protected void setup() {
        this.client.keyboard.enableRepeatEvents(true);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, new TranslatableText("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(false);
        this.nameField.setMaxLength(35);
        this.nameField.setChangedListener(this::onRenamed);
        this.children.add(this.nameField);
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.nameField.getText();
        this.init(arg, i, j);
        this.nameField.setText(string);
    }

    @Override
    public void removed() {
        super.removed();
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.player.closeHandledScreen();
        }
        if (this.nameField.keyPressed(i, j, k) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void onRenamed(String string) {
        if (string.isEmpty()) {
            return;
        }
        String string2 = string;
        Slot lv = ((AnvilScreenHandler)this.handler).getSlot(0);
        if (lv != null && lv.hasStack() && !lv.getStack().hasCustomName() && string2.equals(lv.getStack().getName().getString())) {
            string2 = "";
        }
        ((AnvilScreenHandler)this.handler).setNewItemName(string2);
        this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string2));
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        RenderSystem.disableBlend();
        super.drawForeground(arg, i, j);
        int k = ((AnvilScreenHandler)this.handler).getLevelCost();
        if (k > 0) {
            int l = 8453920;
            boolean bl = true;
            String string = I18n.translate("container.repair.cost", k);
            if (k >= 40 && !this.client.player.abilities.creativeMode) {
                string = I18n.translate("container.repair.expensive", new Object[0]);
                l = 0xFF6060;
            } else if (!((AnvilScreenHandler)this.handler).getSlot(2).hasStack()) {
                bl = false;
            } else if (!((AnvilScreenHandler)this.handler).getSlot(2).canTakeItems(this.playerInventory.player)) {
                l = 0xFF6060;
            }
            if (bl) {
                int m = this.backgroundWidth - 8 - this.textRenderer.getWidth(string) - 2;
                int n = 69;
                AnvilScreen.fill(arg, m - 2, 67, this.backgroundWidth - 8, 79, 0x4F000000);
                this.textRenderer.drawWithShadow(arg, string, (float)m, 69.0f, l);
            }
        }
    }

    @Override
    public void renderForeground(MatrixStack arg, int i, int j, float f) {
        this.nameField.render(arg, i, j, f);
    }

    @Override
    public void onSlotUpdate(ScreenHandler arg, int i, ItemStack arg2) {
        if (i == 0) {
            this.nameField.setText(arg2.isEmpty() ? "" : arg2.getName().getString());
            this.nameField.setEditable(!arg2.isEmpty());
            this.setFocused(this.nameField);
        }
    }
}

