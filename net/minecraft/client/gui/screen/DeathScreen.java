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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    private int ticksSinceDeath;
    private final Text message;
    private final boolean isHardcore;

    public DeathScreen(@Nullable Text arg, boolean bl) {
        super(new TranslatableText(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.message = arg;
        this.isHardcore = bl;
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

    private void onConfirmQuit(boolean bl) {
        if (bl) {
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
    public void render(MatrixStack arg, int i, int j, float f) {
        this.fillGradient(arg, 0, 0, this.width, this.height, 0x60500000, -1602211792);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2 / 2, 30, 0xFFFFFF);
        RenderSystem.popMatrix();
        if (this.message != null) {
            this.drawStringWithShadow(arg, this.textRenderer, this.message, this.width / 2, 85, 0xFFFFFF);
        }
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("deathScreen.score", new Object[0]) + ": " + (Object)((Object)Formatting.YELLOW) + this.client.player.getScore(), this.width / 2, 100, 0xFFFFFF);
        if (this.message != null && j > 85) {
            this.textRenderer.getClass();
            if (j < 85 + 9) {
                Text lv = this.getTextComponentUnderMouse(i);
                this.renderTextHoverEffect(arg, lv, i, j);
            }
        }
        super.render(arg, i, j, f);
    }

    @Nullable
    public Text getTextComponentUnderMouse(int i) {
        if (this.message == null) {
            return null;
        }
        int j = this.client.textRenderer.getStringWidth(this.message);
        int k = this.width / 2 - j / 2;
        int l = this.width / 2 + j / 2;
        if (i < k || i > l) {
            return null;
        }
        return this.client.textRenderer.getTextHandler().trimToWidth(this.message, i - k);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.message != null && e > 85.0) {
            Text lv;
            this.textRenderer.getClass();
            if (e < (double)(85 + 9) && (lv = this.getTextComponentUnderMouse((int)d)) != null && lv.getStyle().getClickEvent() != null && lv.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                this.handleTextClick(lv);
                return false;
            }
        }
        return super.mouseClicked(d, e, i);
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

