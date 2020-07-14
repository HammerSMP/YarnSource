/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CheckboxWidget
extends AbstractPressableButtonWidget {
    private static final Identifier TEXTURE = new Identifier("textures/gui/checkbox.png");
    private boolean checked;
    private final boolean field_24253;

    public CheckboxWidget(int x, int y, int width, int height, Text arg, boolean checked) {
        this(x, y, width, height, arg, checked, true);
    }

    public CheckboxWidget(int i, int j, int k, int l, Text arg, boolean bl, boolean bl2) {
        super(i, j, k, l, arg);
        this.checked = bl;
        this.field_24253 = bl2;
    }

    @Override
    public void onPress() {
        this.checked = !this.checked;
    }

    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.enableDepthTest();
        TextRenderer lv2 = lv.textRenderer;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        CheckboxWidget.drawTexture(matrices, this.x, this.y, this.isFocused() ? 20.0f : 0.0f, this.checked ? 20.0f : 0.0f, 20, this.height, 64, 64);
        this.renderBg(matrices, lv, mouseX, mouseY);
        if (this.field_24253) {
            this.drawTextWithShadow(matrices, lv2, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xE0E0E0 | MathHelper.ceil(this.alpha * 255.0f) << 24);
        }
    }
}

