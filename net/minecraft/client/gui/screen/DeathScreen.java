/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    private int ticksSinceDeath;
    private final Text message;
    private final boolean isHardcore;

    public DeathScreen(@Nullable Text message, boolean isHardcore) {
        super(new TranslatableText(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.message = message;
        this.isHardcore = isHardcore;
    }

    @Override
    protected void init() {
        this.ticksSinceDeath = 0;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, this.isHardcore ? new TranslatableText("deathScreen.spectate") : new TranslatableText("deathScreen.respawn"), arg -> {
            this.client.player.requestRespawn();
            this.client.openScreen(null);
        }));
        ButtonWidget lv = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslatableText("deathScreen.titleScreen"), arg -> {
            if (this.isHardcore) {
                this.quitLevel();
                return;
            }
            ConfirmScreen lv = new ConfirmScreen(this::onConfirmQuit, new TranslatableText("deathScreen.quit.confirm"), LiteralText.EMPTY, new TranslatableText("deathScreen.titleScreen"), new TranslatableText("deathScreen.respawn"));
            this.client.openScreen(lv);
            lv.disableButtons(20);
        }));
        if (!this.isHardcore && this.client.getSession() == null) {
            lv.active = false;
        }
        for (AbstractButtonWidget lv2 : this.buttons) {
            lv2.active = false;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void onConfirmQuit(boolean quit) {
        if (quit) {
            this.quitLevel();
        } else {
            this.client.player.requestRespawn();
            this.client.openScreen(null);
        }
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect();
        }
        this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        this.client.openScreen(new TitleScreen());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, 0x60500000, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 0xFFFFFF);
        RenderSystem.popMatrix();
        if (this.message != null) {
            this.drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 0xFFFFFF);
        }
        this.drawCenteredString(matrices, this.textRenderer, I18n.translate("deathScreen.score", new Object[0]) + ": " + (Object)((Object)Formatting.YELLOW) + this.client.player.getScore(), this.width / 2, 100, 0xFFFFFF);
        if (this.message != null && mouseY > 85) {
            this.textRenderer.getClass();
            if (mouseY < 85 + 9) {
                Style lv = this.getTextComponentUnderMouse(mouseX);
                this.renderTextHoverEffect(matrices, lv, mouseX, mouseY);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.message == null) {
            return null;
        }
        int j = this.client.textRenderer.getWidth(this.message);
        int k = this.width / 2 - j / 2;
        int l = this.width / 2 + j / 2;
        if (mouseX < k || mouseX > l) {
            return null;
        }
        return this.client.textRenderer.getTextHandler().trimToWidth(this.message, mouseX - k);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.message != null && mouseY > 85.0) {
            Style lv;
            this.textRenderer.getClass();
            if (mouseY < (double)(85 + 9) && (lv = this.getTextComponentUnderMouse((int)mouseX)) != null && lv.getClickEvent() != null && lv.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleTextClick(lv);
                return false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath == 20) {
            for (AbstractButtonWidget lv : this.buttons) {
                lv.active = true;
            }
        }
    }
}

