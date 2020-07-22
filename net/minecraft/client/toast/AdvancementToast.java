/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringRenderable;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AdvancementToast
implements Toast {
    private final Advancement advancement;
    private boolean soundPlayed;

    public AdvancementToast(Advancement advancement) {
        this.advancement = advancement;
    }

    @Override
    public Toast.Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        manager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        AdvancementDisplay lv = this.advancement.getDisplay();
        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        if (lv != null) {
            int i;
            List<StringRenderable> list = manager.getGame().textRenderer.wrapLines(lv.getTitle(), 125);
            int n = i = lv.getFrame() == AdvancementFrame.CHALLENGE ? 0xFF88FF : 0xFFFF00;
            if (list.size() == 1) {
                manager.getGame().textRenderer.draw(matrices, I18n.translate("advancements.toast." + lv.getFrame().getId(), new Object[0]), 30.0f, 7.0f, i | 0xFF000000);
                manager.getGame().textRenderer.draw(matrices, list.get(0), 30.0f, 18.0f, -1);
            } else {
                int j = 1500;
                float f = 300.0f;
                if (startTime < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    manager.getGame().textRenderer.draw(matrices, I18n.translate("advancements.toast." + lv.getFrame().getId(), new Object[0]), 30.0f, 11.0f, i | k);
                } else {
                    int m = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    manager.getGame().textRenderer.getClass();
                    int n2 = this.getHeight() / 2 - list.size() * 9 / 2;
                    for (StringRenderable lv2 : list) {
                        manager.getGame().textRenderer.draw(matrices, lv2, 30.0f, (float)n2, 0xFFFFFF | m);
                        manager.getGame().textRenderer.getClass();
                        n2 += 9;
                    }
                }
            }
            if (!this.soundPlayed && startTime > 0L) {
                this.soundPlayed = true;
                if (lv.getFrame() == AdvancementFrame.CHALLENGE) {
                    manager.getGame().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
                }
            }
            manager.getGame().getItemRenderer().renderInGui(lv.getIcon(), 8, 8);
            return startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
        return Toast.Visibility.HIDE;
    }
}

