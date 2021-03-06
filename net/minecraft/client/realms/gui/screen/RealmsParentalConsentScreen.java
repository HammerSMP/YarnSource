/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsParentalConsentScreen
extends RealmsScreen {
    private final Screen parent;

    public RealmsParentalConsentScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        Realms.narrateNow(I18n.translate("mco.account.privacyinfo", new Object[0]));
        TranslatableText lv = new TranslatableText("mco.account.update");
        Text lv2 = ScreenTexts.BACK;
        int i = Math.max(this.textRenderer.getWidth(lv), this.textRenderer.getWidth(lv2)) + 30;
        TranslatableText lv3 = new TranslatableText("mco.account.privacy.info");
        int j = (int)((double)this.textRenderer.getWidth(lv3) * 1.2);
        this.addButton(new ButtonWidget(this.width / 2 - j / 2, RealmsParentalConsentScreen.row(11), j, 20, lv3, arg -> Util.getOperatingSystem().open("https://minecraft.net/privacy/gdpr/")));
        this.addButton(new ButtonWidget(this.width / 2 - (i + 5), RealmsParentalConsentScreen.row(13), i, 20, lv, arg -> Util.getOperatingSystem().open("https://minecraft.net/update-account")));
        this.addButton(new ButtonWidget(this.width / 2 + 5, RealmsParentalConsentScreen.row(13), i, 20, lv2, arg -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        List<StringRenderable> list = this.client.textRenderer.wrapLines(new TranslatableText("mco.account.privacyinfo"), (int)Math.round((double)this.width * 0.9));
        int k = 15;
        for (StringRenderable lv : list) {
            this.drawCenteredText(matrices, this.textRenderer, lv, this.width / 2, k, 0xFFFFFF);
            k += 15;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

