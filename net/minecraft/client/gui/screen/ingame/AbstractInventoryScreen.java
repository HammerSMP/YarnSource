/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractInventoryScreen<T extends ScreenHandler>
extends HandledScreen<T> {
    protected boolean drawStatusEffects;

    public AbstractInventoryScreen(T arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    protected void init() {
        super.init();
        this.applyStatusEffectOffset();
    }

    protected void applyStatusEffectOffset() {
        if (this.client.player.getStatusEffects().isEmpty()) {
            this.x = (this.width - this.backgroundWidth) / 2;
            this.drawStatusEffects = false;
        } else {
            this.x = 160 + (this.width - this.backgroundWidth - 200) / 2;
            this.drawStatusEffects = true;
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (this.drawStatusEffects) {
            this.drawStatusEffects(matrices);
        }
    }

    private void drawStatusEffects(MatrixStack arg) {
        int i = this.x - 124;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty()) {
            return;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int j = 33;
        if (collection.size() > 5) {
            j = 132 / (collection.size() - 1);
        }
        List iterable = Ordering.natural().sortedCopy(collection);
        this.drawStatusEffectBackgrounds(arg, i, j, iterable);
        this.drawStatusEffectSprites(arg, i, j, iterable);
        this.drawStatusEffectDescriptions(arg, i, j, iterable);
    }

    private void drawStatusEffectBackgrounds(MatrixStack arg, int i, int j, Iterable<StatusEffectInstance> iterable) {
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int k = this.y;
        for (StatusEffectInstance lv : iterable) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(arg, i, k, 0, 166, 140, 32);
            k += j;
        }
    }

    private void drawStatusEffectSprites(MatrixStack arg, int i, int j, Iterable<StatusEffectInstance> iterable) {
        StatusEffectSpriteManager lv = this.client.getStatusEffectSpriteManager();
        int k = this.y;
        for (StatusEffectInstance lv2 : iterable) {
            StatusEffect lv3 = lv2.getEffectType();
            Sprite lv4 = lv.getSprite(lv3);
            this.client.getTextureManager().bindTexture(lv4.getAtlas().getId());
            AbstractInventoryScreen.drawSprite(arg, i + 6, k + 7, this.getZOffset(), 18, 18, lv4);
            k += j;
        }
    }

    private void drawStatusEffectDescriptions(MatrixStack arg, int i, int j, Iterable<StatusEffectInstance> iterable) {
        int k = this.y;
        for (StatusEffectInstance lv : iterable) {
            String string = I18n.translate(lv.getEffectType().getTranslationKey(), new Object[0]);
            if (lv.getAmplifier() >= 1 && lv.getAmplifier() <= 9) {
                string = string + ' ' + I18n.translate("enchantment.level." + (lv.getAmplifier() + 1), new Object[0]);
            }
            this.textRenderer.drawWithShadow(arg, string, (float)(i + 10 + 18), (float)(k + 6), 0xFFFFFF);
            String string2 = StatusEffectUtil.durationToString(lv, 1.0f);
            this.textRenderer.drawWithShadow(arg, string2, (float)(i + 10 + 18), (float)(k + 6 + 10), 0x7F7F7F);
            k += j;
        }
    }
}

